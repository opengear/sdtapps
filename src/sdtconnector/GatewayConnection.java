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
    public GatewayConnection(Gateway gw, UserInfo ui, ExecutorService callback) {
        this.gateway = gw;
        this.callback = callback;
        try {
            jsch = new JSch();
            session = jsch.getSession(gw.getUsername(), gw.getAddress(), gw.getPort());
            session.setUserInfo(ui);
            
            Hashtable<String, String> config = new Hashtable<String, String>();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            session.setPassword(gw.getPassword());
        } catch (com.jcraft.jsch.JSchException jsche) {
            System.out.println("Jsch exception " + jsche);
        }
    }
    
    public GatewayConnection(Gateway gw, final Authentication auth, ExecutorService exec) {
        this.gateway = gw;
        this.callback = exec;
        this.authentication = auth;
        UserInfo ui = new UserInfo() {
            public String getPassphrase() {
                return "";
            }
            public String getPassword() {
                Future f = callback.submit(new Callable() {
                    public String call() throws Exception {
                        return authentication.getPassword();
                    }
                });
                try {
                    return (String) f.get();
                } catch (Exception ex) { }
                return "";
            }
            public boolean promptPassphrase(String string) {
                return false;
            }
            public boolean promptPassword(String string) {
                Future f = callback.submit(new Callable() {
                    public Boolean call() throws Exception {
                        return authentication.promptAuthentication();
                    }
                });
                try {
                    return (Boolean) f.get();
                } catch (Exception ex) { }
                return false;
            }
            public boolean promptYesNo(String string) {
                return false;
            }
            public void showMessage(String string) {
                
            }
        };
        
        listener = new Listener() {
            public void sshLoginStarted() {
            }
            
            public void sshLoginSucceeded() {
            }
            
            public void sshLoginFailed() {
            }
            
            public void sshTcpChannelStarted(String host, int port) {
            }
            
            public void sshTcpChannelEstablished(String host, int port) {
            }
            
            public void sshTcpChannelFailed(String host, int port) {
            }
            
        };
        
        try {
            jsch = new JSch();
            session = jsch.getSession(gw.getUsername(), gw.getAddress(), gw.getPort());
            session.setUserInfo(ui);
            
            Hashtable<String, String> config = new Hashtable<String, String>();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            session.setPassword(gw.getPassword());
        } catch (com.jcraft.jsch.JSchException jsche) {
            System.out.println("Jsch exception " + jsche);
        }
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
        Future f = exec.submit(new Callable() {
            public Boolean call() throws Exception {
                return doConnect();
            }
        });
        try {
            return (Boolean) f.get();
        } catch (Exception ex) {
            return false;
        }        
    }
    private boolean doConnect()  {
        if (!session.isConnected()) {
            System.out.println("Connecting ...");
            callback.execute(new Runnable() {
                public void run() {
                    listener.sshLoginStarted();
                }
            });
            try {
                session.connect(5000);
            } catch (JSchException ex) {
                callback.execute(new Runnable() {
                    public void run() {
                        listener.sshLoginFailed();
                    }
                });
                return false;
            }
            callback.execute(new Runnable() {
                public void run() {
                    listener.sshLoginSucceeded();
                }
            });
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
                    callback.execute(new Runnable() {
                        public void run() {
                            listener.sshTcpChannelStarted(host, port);
                        }
                    });
                    
                    channel.connect();
                    callback.execute(new Runnable() {
                        public void run() {
                            listener.sshTcpChannelEstablished(host, port);
                        }
                    });
                    
                    return channel;
                } catch (JSchException ex) {
                    callback.execute(new Runnable() {
                        public void run() {
                            listener.sshTcpChannelFailed(host, port);
                        }
                    });
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
    
    class SSHChannel {
        public SSHChannel(Channel channel) throws IOException {
            this.channel = channel;
            outputStream = channel.getOutputStream();
            inputStream = channel.getInputStream();
        }
        public OutputStream getOutputStream() {
            return outputStream;
        }
        
        public InputStream getInputStream() {
            return inputStream;
        }
        
        public Channel getChannel() {
            return channel;
        }
        Channel channel;
        
        
        InputStream inputStream;
        OutputStream outputStream;
        
        
    }
    private Future openShellAsync() {
        return exec.submit(new Callable() {
            public SSHChannel call() throws Exception {
                try {
                    doConnect();
                    System.out.println("Opening shell");
                    ChannelShell shell = (ChannelShell) session.openChannel("shell");
                    System.out.println("Shell opened");
                    return new SSHChannel(shell);
                } catch (JSchException ex) {
                    ex.printStackTrace();
                    return null;
                }
            }
        });
    }
    
    public SSHChannel openTcpStream(final String host, int port) throws IOException {
        try {
            return (SSHChannel) openTcpStreamAsync(host, port).get();
        } catch (ExecutionException ex) {
            throw new InterruptedIOException(ex.getMessage());
        } catch (InterruptedException ex) {
            throw new InterruptedIOException(ex.getMessage());
        }
    }
    public Future openTcpStreamAsync(final String host, final int remoteport) {
        return exec.submit(new Callable() {
            public SSHChannel call() throws Exception  {
                ChannelDirectTCPIP channel;
                try {
                    doConnect();
                    channel  = (ChannelDirectTCPIP) session.openChannel("direct-tcpip");
                    channel.setHost(host);
                    channel.setPort(remoteport);
                    channel.connect();
                    
                    return new SSHChannel(channel);
                } catch (JSchException ex) {
                    ex.printStackTrace();
                    throw ex;
                }
            }
            
        });
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
        Gateway gw = new Gateway("192.168.99.11", "root", "default", "CM41xx");
        GatewayConnection conn = new GatewayConnection(gw, new MyUserInfo(),
                Executors.newSingleThreadExecutor());
        //conn.forwardLocalPort(5000, "192.168.99.2", 80);
        try {
            SSHChannel channel = conn.openTcpStream("192.168.99.2", 80);
            PrintStream ps = new PrintStream(channel.getOutputStream());
            BufferedReader br = new BufferedReader(new InputStreamReader(channel.getInputStream()));
            
            System.out.println("Connected");
            ps.println("GET / HTTP/1.0\n\n");
            ps.flush();
            String s;
            
            while ((s = br.readLine()) != null) {
                System.out.println("read in: " + s);
            }
            
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            //System.exit(0);
        }
        
        Redirector r = conn.getRedirector("192.168.99.2", 80);
        System.out.println("Redirecting port " + r.getLocalPort() + " to 192.168.99.2:80");
//        conn.openShell();
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
    public void openShell() {
        throw new UnsupportedOperationException("Not yet implemented");
    }
    private Gateway gateway;
    private JSch jsch;
    private Session session;
    private ExecutorService exec = Executors.newSingleThreadExecutor();
    private ExecutorService callback;
    private List<Redirector> redirectors = new ArrayList<Redirector>();
    
    private Authentication authentication;
    private Listener listener;
}
class MyUserInfo implements UserInfo {
    public String getPassword() {
        return passwd;
    }
    public boolean promptYesNo(String str) {
        return false;
        
    }
    
    String passwd = "test";
    
    
    public String getPassphrase() {
        System.out.println("getPassphrase");
        return null;
    }
    public boolean promptPassphrase(String message) {
        System.out.println("promptPassphrase");
        return true;
    }
    public boolean promptPassword(String message) {
        System.out.println("promptPassword");
        return false;
        
    }
    
    public void showMessage(String string) {
        System.out.println("showMessage: " + string);
    }
    
}
