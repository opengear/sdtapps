/*
 * GatewayConnection.java
 *
 * Created on January 20, 2006, 3:50 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sdtconnector;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelDirectTCPIP;
import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UserInfo;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import edu.emory.mathcs.backport.java.util.concurrent.Callable;
import edu.emory.mathcs.backport.java.util.concurrent.ExecutionException;
import edu.emory.mathcs.backport.java.util.concurrent.Executor;
import edu.emory.mathcs.backport.java.util.concurrent.ExecutorService;
import edu.emory.mathcs.backport.java.util.concurrent.Executors;
import edu.emory.mathcs.backport.java.util.concurrent.Future;
import edu.emory.mathcs.backport.java.util.concurrent.FutureTask;
import edu.emory.mathcs.backport.java.util.concurrent.ThreadPoolExecutor;

/**
 *
 * @author wayne
 */
public class GatewayConnection {
    
    /** Creates a new instance of GatewayConnection */
    
    public GatewayConnection(Gateway gw, final Authentication auth) {
        this.gateway = gw;
        this.authentication =  auth;
        this.username = gw.getUsername();
        this.password = gw.getPassword();
        setupSession(username, password);
    }
    
    public void setListener(Listener l) {
        listener = l;
    }
    public Redirector getRedirector(String host, int port) {
        for (Redirector r : redirectors) {
            if (r.getRemoteHost().equals(host) && r.getRemotePort() == port) {
                return r;
            }
        }
        // Create a new redirector
        try {
            Redirector r = new Redirector(this, host, port);
            redirectors.add(r);
            r.start();
            return r;
        } catch (IOException ex) {
            return null;
        }
    }
    
    //
    // Wait for the login to complete - runs on the GatewayConnection thread.
    //
    public boolean login() {
        try {
            Future f = exec.submit(new Callable() {
                public Boolean call() throws Exception {
                    return doConnect();
                }
            });
            return (Boolean) f.get();
        } catch (Exception ex) {
            return false;
        }
    }
    
    private void setupSession(String username, String password) {
        try {
            jsch = new JSch();
            session = jsch.getSession(username, gateway.getAddress(), gateway.getPort());
            session.setUserInfo(userinfo);
            
            Hashtable<String, String> config = new Hashtable<String, String>();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            session.setPassword(password);
        } catch (com.jcraft.jsch.JSchException jsche) {
            System.out.println("Jsch exception " + jsche);
        }
    }
    
    private boolean doConnect()  {
        if (!session.isConnected()) {
            System.out.println("Connecting ...");
            listener.sshLoginStarted();
            try {
                session.connect(5000);
            } catch (JSchException ex) {
                listener.sshLoginFailed();
                // Reset the session
                setupSession(username, password);
                return false;
            }
            listener.sshLoginSucceeded();
            System.out.println("Connected");
        }
        return true;
    }
    public boolean redirectSocket(final String host, final int port, final Socket s) {
        Future f = exec.submit(new Callable() {
            public Channel call() throws Exception {
                ChannelDirectTCPIP channel;
                if (!doConnect()) {
                    throw new JSchException("Failed to connect");
                }
                try {
                    
                    channel  = (ChannelDirectTCPIP) session.openChannel("direct-tcpip");
                    channel.setHost(host);
                    channel.setPort(port);
                    channel.setInputStream(s.getInputStream());
                    channel.setOutputStream(s.getOutputStream());
                    
                    listener.sshTcpChannelStarted(host, port);
                    channel.connect();
                    listener.sshTcpChannelEstablished(host, port);
                    
                    return channel;
                } catch (JSchException ex) {
                    listener.sshTcpChannelFailed(host, port);
                    return null;
                }
            }
        });
        return true;
    }
    public class Redirector implements Runnable {
        public Redirector(GatewayConnection conn, String host, int port) throws IOException {
            listenSocket = new ServerSocket(0, 50, InetAddress.getByName("localhost"));
            this.connection = conn;
            this.host = host;
            this.port = port;
            this.connection = connection;
        }
        public void start() {
            listenThread = new Thread(this);
            listenThread.setDaemon(true);
            listenThread.start();
        }
        public void stop() {
            listenThread.interrupt();
        }
        public void shutdown() {
            stop();
            try {
                listenSocket.close();
            } catch (IOException ex) {}
        }
        public int getLocalPort() {
            return listenSocket.getLocalPort();
        }
        public String getRemoteHost() {
            return host;
        }
        public int getRemotePort() {
            return port;
        }
        public void run() {
            while (true) {
                try {
                    connection.redirectSocket(host, port, listenSocket.accept());
                } catch (IOException ex) {
                    break;
                }
            }
        }
        private ServerSocket listenSocket;
        private Thread listenThread;
        private GatewayConnection connection;
        private String host;
        private int port;
    }
    
    
    public void shutdown() {
        for (Redirector r : redirectors) {
            r.shutdown();
        }
        exec.execute(new Runnable() {
            public void run() {
                session.disconnect();
                exec.shutdown();
            }
        });
    }
    public static void main(String[] arg) {
        System.out.println("GatewayConnection");
    }
    public interface Authentication {
        public boolean promptAuthentication();
        public String getUsername();
        public String getPassword();
    }
    public interface Listener {
        public void sshLoginStarted();
        public void sshLoginSucceeded();
        public void sshLoginFailed();
        public void sshTcpChannelStarted(String host, int port);
        public void sshTcpChannelEstablished(String host, int port);
        public void sshTcpChannelFailed(String host, int port);
    }
    
    private Gateway gateway;
    private JSch jsch;
    private Session session;
    private ExecutorService exec = Executors.newSingleThreadExecutor();
    private List<Redirector> redirectors = new ArrayList<Redirector>();
    private String username = "";
    private String password = "";
    
    private Authentication authentication;
    private Listener listener = new Listener() {
        public void sshLoginStarted() {}
        public void sshLoginSucceeded() {}
        public void sshLoginFailed() {}
        public void sshTcpChannelStarted(String host, int port) {}
        public void sshTcpChannelEstablished(String host, int port) {}
        public void sshTcpChannelFailed(String host, int port) {}
    };
    UserInfo userinfo = new UserInfo() {
        public String getPassphrase() {
            return "";
        }
        public String getPassword() {
            return authentication.getPassword();
        }
        public boolean promptPassphrase(String string) {
            return false;
        }
        public boolean promptPassword(String string) {            
            return authentication.promptAuthentication();            
        }
        public boolean promptYesNo(String string) {
            return false;
        }
        public void showMessage(String string) {
            
        }
    };
    
}

