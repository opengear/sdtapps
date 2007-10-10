/*
 * GatewayConnection.java
 */

package sdtconnector;
import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelDirectTCPIP;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UserInfo;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
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
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import org.omg.SendingContext.RunTime;


public class GatewayConnection {
    
    /** Creates a new instance of GatewayConnection */
    
    public GatewayConnection(Gateway gw, final Authentication auth) {
        this.gateway = gw;
        this.authentication =  auth;
        this.username = gw.getUsername();
        this.password = gw.getPassword();
        config.put("StrictHostKeyChecking", "no");
        config.put("cipher.s2c", "aes128-cbc,3des-cbc,blowfish-cbc");
        config.put("cipher.c2s", "aes128-cbc,3des-cbc,blowfish-cbc");
        config.put("compression.s2c", "zlib,none");
        config.put("compression.c2s", "zlib,none");
        setupSession(username, password);
    }

    public void setSSHListener(SSHListener l) {
        sshListener = l;
    }
    public void setAutohostsListener(AutohostsListener l) {
        autohostsListener = l;
    }
    public void setStopOobListener(StopOobListener l) {
        stopOobListener = l;
    }
    
    public Redirector getRedirector(String remoteHost, int remotePort, String localHost, int localPort, int udpOverTcpPort) {
        for (Redirector r : redirectors) {
            if (r.getRemoteHost().equals(remoteHost) && r.getRemotePort() == remotePort &&
                    (localPort == 0 || localPort == r.getLocalPort())) {
                return r;
            }
        }
        
        // Create a new redirector
        Redirector redirector = null;
        try {
            redirector = new Redirector(this, remoteHost, remotePort, localHost, localPort, udpOverTcpPort);
            redirector.start();
        } catch (Exception ex) {
            if (redirector != null) {
                redirector.shutdown();
            }
            return null;
        }
        redirectors.add(redirector);
        return redirector;
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
            session = jsch.getSession(username, gateway.getActiveAddress(), gateway.getPort());
            session.setUserInfo(userinfo);
            session.setConfig(config);
            session.setPassword(password);
            // Add any configured private keys
            for (String path : Settings.getPropertyList(Settings.root().node("PrivateKeyPaths"))) {
                jsch.addIdentity(path, "passphrase");
            }
        } catch (com.jcraft.jsch.JSchException jsche) {
            System.out.println("Jsch exception " + jsche);
        }
    }
    
    private boolean doConnect()  {
        if (!session.isConnected()) {
            System.out.println("Connecting ...");
            connectSession();
            System.out.println("Connected");
        }
        return true;
    }
    
    private boolean connectSession() {
        if (gateway.getOob()) {
            sshListener.oobStarted();
            try {
                Process proc = Runtime.getRuntime().exec(gateway.getOobStart());
                int retVal = proc.waitFor();
                // TODO: failure case
            } catch (IOException ex) {
                sshListener.oobFailed();
                return false;
            } catch (InterruptedException ex) {
                sshListener.oobFailed();
                return false;
            }
            sshListener.oobSucceeded();
        }
        sshListener.sshLoginStarted();
        try {
            session.connect(120000);
        } catch (JSchException ex) {
            sshListener.sshLoginFailed();
            // Reset the session
            setupSession(username, password);
            return false;
        }
        sshListener.sshLoginSucceeded();
        return true;
    }
    
    private boolean disconnectSession() {
        session.disconnect();
        return true;
    }
    
    public void stopOob() {
        exec.execute(new Runnable() {
            public void run() {
                Process proc;
                stopOobListener.stopOobStarted();
                try {
                    proc = Runtime.getRuntime().exec(gateway.getOobStop());
                    int retVal = proc.waitFor();
                } catch (IOException ex) {
                    stopOobListener.stopOobFailed();
                } catch (InterruptedException ex) {
                    stopOobListener.stopOobFailed();
                }
                stopOobListener.stopOobSucceeded();
            }
        });
    }
    
    private void shellWrite(PrintStream ps, String line) {
        try {
            Thread.sleep(10);
        } catch (InterruptedException ex) {}
        ps.println(line);
        ps.flush();
        System.out.println("Shell: Sent: " + line);
    }
    
    private String shellRead(BufferedReader br, int timeout) throws Exception {
        int i, len;
        String s;
        StringBuffer sb = new StringBuffer();
        char[] cb = new char[1024];
        
        for (i = timeout / 10; i > 0; i--) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException ex) {}
            if (br.ready() == false) {
                continue;
            }
            len = br.read(cb, 0, 1024);
            sb.append(cb, 0, len);
        }
        
        s = sb.toString();
        
        if (s.equals("")) {
            System.out.println("Shell: Timeout reading from remote shell");
            throw new Exception();
        }
        
        System.out.println("Shell: Received: " + s);
        return s;
    }
    
    public void getHosts() {
        exec.execute(new Runnable() {
            public void run() {
                EventList hosts = null;
                
                autohostsListener.autohostsStarted();
                
                if (session.isConnected() == false) {
                    try {
                        session.connect(120000);
                    } catch (JSchException ex) {
                        // FIXME
                        setupSession(username, password);
                        autohostsListener.autohostsFailed();
                        return;
                    }
                }
                
                try {
                    ChannelShell shell = (ChannelShell) session.openChannel("shell");
                    PrintStream shOut = new PrintStream(shell.getOutputStream());
                    BufferedReader shIn = new BufferedReader(new InputStreamReader(shell.getInputStream()));
                    AutohostsParser parser = new AutohostsParser();
                    
                    shell.connect();
                    
                    shellWrite(shOut, "stty -echo");
                    // Wait for shell to be ready
                    shellRead(shIn, 1000);
                    // Send command
                    shellWrite(shOut, "cat $HOME/.sdt");
                    
                    hosts = parser.parse(shell.getInputStream());
                    
                } catch (JSchException ex) {
                    ex.printStackTrace();
                } catch (IOException ex) {
                    ex.printStackTrace();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                autohostsListener.autohostsSucceeded(hosts);
            }
        });
    }
    
    public class Redirector implements Runnable {
        private boolean redirectSocket(final Socket s) {
            Future f = exec.submit(new Callable() {
                
                private void redirectRemoteUDPSocket() throws Exception {
                    String command = gateway.getUdpgwStartCommand(remoteHost, remotePort, udpOverTcpPort);
                    String regex = gateway.getUdpgwPidRegex();
                    CharSequence cs;
                    String s;
                    
                    System.out.println("TCP-UDP: Remote UDP to TCP gateway starting");
                    System.out.println("TCP-UDP: 127.0.0.1:" + remotePort + " <-> " + remoteHost + ":" + udpOverTcpPort);
                    remoteUDPGatewayShell = (ChannelShell) session.openChannel("shell");
                    PrintStream shOut = new PrintStream(remoteUDPGatewayShell.getOutputStream());
                    BufferedReader shIn = new BufferedReader(new InputStreamReader(remoteUDPGatewayShell.getInputStream()));
                    remoteUDPGatewayShell.connect();
                    System.out.println("TCP-UDP: Shell connected");
                    
                    shellWrite(shOut, "stty -echo");
                    // Wait for shell to be ready
                    shellRead(shIn, 1000);
                    // Send command
                    shellWrite(shOut, command);
                    // Wait for shell to be ready
                    s = shellRead(shIn, 1000);
                    
                    Matcher m = Pattern.compile(regex).matcher(s);
                    if (m.find() == true) {
                        remoteUDPGatewayPID = Integer.parseInt(s.substring(m.start(), m.end()));
                        System.out.println("TCP-UDP: PID " + remoteUDPGatewayPID);
                    } else {
                        System.out.println("TCP-UDP: Warning, no PID returned");
                    }
                }
                
                private void redirectTCPSocket(final String host) throws Exception {
                    tcpip = (ChannelDirectTCPIP) session.openChannel("direct-tcpip");
                    tcpip.setHost(host);
                    tcpip.setPort(remotePort);
                    tcpip.setInputStream(s.getInputStream());
                    tcpip.setOutputStream(s.getOutputStream());
                    sshListener.sshTcpChannelStarted(host, remotePort);
                    tcpip.connect();
                    sshListener.sshTcpChannelEstablished(host, remotePort);
                }
                
                public Channel call() throws Exception {
                    if (!doConnect()) {
                        throw new JSchException("Failed to connect");
                    }
                    try {
                        if (udpOverTcpPort != 0) {
                            redirectRemoteUDPSocket();
                            redirectTCPSocket(localHost);
                        } else {
                            redirectTCPSocket(remoteHost);
                        }
                        return tcpip;
                    } catch (Exception ex) {
                        sshListener.sshTcpChannelFailed(udpOverTcpPort != 0 ? localHost : remoteHost, remotePort);
                        return null;
                    }
                }
            });
            try {
                if (f.get() == null) {
                    return false;
                }
            } catch (ExecutionException ex) {
                    return false;
            } catch (InterruptedException ex) {
                    return false;
            }
            return true;
        }
        
        public Redirector(GatewayConnection conn, String remoteHost, int remotePort, String localHost, int localPort, int udpOverTcpPort) throws InterruptedException, IOException {
            InetSocketAddress listenSockAddr = new InetSocketAddress(InetAddress.getByName(localHost), localPort);

            this.connection = conn;
            this.remoteHost = remoteHost;
            this.localHost = localHost;
            this.remotePort = remotePort;
            this.localPort = localPort;
            this.udpOverTcpPort = udpOverTcpPort;
            this.connection = connection;

            listenSocket = new ServerSocket();
            SocketHelper.bindSocket(listenSocket, listenSockAddr);

            this.localPort = listenSocket.getLocalPort();
        }
        public void start() throws Exception {
            listenThread = new Thread(this);
            listenThread.setDaemon(true);
            listenThread.start();
            if (udpOverTcpPort != 0) {
                localUDPGateway = new UDPGateway(localHost, udpOverTcpPort, localPort);
                localUDPGateway.init();
                localUDPGateway.start();
            }
        }
        public void stop() {
            listenThread.interrupt();
        }
        
        private void shutdownLocalUDPRedirection() {
            if (localUDPGateway != null) {
                localUDPGateway.shutdown();
            }
        }
        
        private void shutdownRemoteUDPRedirection() {
            if (remoteUDPGatewayShell != null) {
                if (remoteUDPGatewayPID != 0) {
                    String command = gateway.getUdpgwStopCommand(remoteHost, remotePort, udpOverTcpPort, remoteUDPGatewayPID);
                    try {
                        PrintStream shOut = new PrintStream(remoteUDPGatewayShell.getOutputStream());
                        BufferedReader shIn = new BufferedReader(new InputStreamReader(remoteUDPGatewayShell.getInputStream()));
                        
                        shellWrite(shOut, "");
                        // Wait for shell to be ready
                        shellRead(shIn, 1000);
                        // Send command
                        shellWrite(shOut, command);
                        // Wait for shell to be ready
                        shellRead(shIn, 1000);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
                remoteUDPGatewayShell.disconnect();
            }
        }
        
        private void shellWrite(PrintStream ps, String line) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException ex) {}
            ps.println(line);
            ps.flush();
            System.out.println("TCP-UDP: Sent: " + line);
        }
        
        private String shellRead(BufferedReader br, int timeout) throws Exception {
            String strbuf = "";
            int i, len;
            char[] cbuf = new char[1024];
            
            for (i = timeout / 10; i > 0; i--) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException ex) {}
                if (br.ready() == false) {
                    continue;
                }
                len = br.read(cbuf, 0, 1024);
                strbuf = strbuf + String.valueOf(cbuf, 0, len);
            }
            
            if (strbuf == "") {
                System.out.println("Shell: Timeout reading from remote shell");
                throw new Exception();
            }
            
            System.out.println("Shell: Received: " + strbuf);
            return strbuf;
        }
        
        public void shutdown() {
            stop();
            try {
                if (udpOverTcpPort != 0) {
                    shutdownLocalUDPRedirection();
                    shutdownRemoteUDPRedirection();
                }
                if (tcpip != null) {
                    tcpip.disconnect();
                }
                listenSocket.close();
            } catch (IOException ex) {
                // Ignore
            }
        }
        public int getLocalPort() {
            return localPort;
        }
        public String getRemoteHost() {
            return remoteHost;
        }
        public int getRemotePort() {
            return remotePort;
        }
        private int getUDPPort() {
            return udpOverTcpPort;
        }
        private String getLocalHost() {
            return localHost;
        }
        public void run() {
            Socket s;

            while (true) {
                try {
                    s = listenSocket.accept();
                    s.setReuseAddress(true);
                    if (redirectSocket(s) == false) {
                        this.shutdown();
                        redirectors.remove(this);
                        break;
                    }
                } catch (IOException ex) {
                    break;
                }
            }
        }
        
        private ServerSocket listenSocket;
        private Thread listenThread;
        private GatewayConnection connection;
        private String remoteHost;
        private String localHost;
        private int remotePort;
        private int localPort;
        private int udpOverTcpPort;
        private UDPGateway localUDPGateway;
        private ChannelShell remoteUDPGatewayShell;
        private ChannelDirectTCPIP tcpip;
        private int remoteUDPGatewayPID = 0;
    }
    
    
    
    public void shutdown() {
        for (Redirector r : redirectors) {
            r.shutdown();
        }
        exec.execute(new Runnable() {
            public void run() {
                disconnectSession();
                exec.shutdown();
            }
        });
    }
    public static void main(String[] arg) {
        System.out.println("GatewayConnection");
    }

    public List<Redirector> getRedirectors() {
        return redirectors;
    }

    public void shutdownConflictingRedirectors(String localHost, int localPort, int udpOverTcpPort) {
        List<Redirector> conflicting = new ArrayList<Redirector>();
        
        for (Redirector r : redirectors) {
            String rLocalHost = r.getLocalHost();
            int rLocalPort = r.getLocalPort();
            int rUdpOverTcpPort = r.getUDPPort();
            
            if (r.getLocalHost().equals(localHost)) {
                if ((localPort != 0 && r.getLocalPort() == localPort)
                    || (udpOverTcpPort != 0 && r.getUDPPort() == udpOverTcpPort))
                {
                    conflicting.add(r);
                }
            }
        }
        for (Redirector r : conflicting) {
            System.out.println("Stopping conflicting redirection " + r.getLocalHost() + ":" +
                    r.getLocalPort() + " -> " + r.getRemoteHost() + ":" + r.getRemotePort());
            r.shutdown();
            redirectors.remove(r);
        }
    }
    
    public interface Authentication {
        public boolean promptAuthentication(String prompt);
        public boolean promptPassphrase(String prompt);
        public String getUsername();
        public String getPassword();
        public String getPassphrase();
    }
    public interface SSHListener {
        public void sshLoginStarted();
        public void sshLoginSucceeded();
        public void sshLoginFailed();
        public void oobStarted();
        public void oobSucceeded();
        public void oobFailed();
        public void sshTcpChannelStarted(String host, int port);
        public void sshTcpChannelEstablished(String host, int port);
        public void sshTcpChannelFailed(String host, int port);
    }
    public interface AutohostsListener {
        public void autohostsStarted();
        public void autohostsSucceeded(EventList hosts);
        public void autohostsFailed();
    }
    public interface StopOobListener {
        public void stopOobStarted();
        public void stopOobSucceeded();
        public void stopOobFailed();
    }
    private Gateway gateway;
    private JSch jsch;
    private Session session;
    private Session oobSession;
    private ExecutorService exec = Executors.newSingleThreadExecutor();
    private List<Redirector> redirectors = new ArrayList<Redirector>();
    private String username = "";
    private String password = "";
    Hashtable<String, String> config = new Hashtable<String, String>();
    private Authentication authentication;
    
    private SSHListener sshListener = new SSHListener() {
        public void sshLoginStarted() {}
        public void sshLoginSucceeded() {}
        public void sshLoginFailed() {}
        public void oobEnabled() {}
        public void oobStarted() {}
        public void oobSucceeded() {}
        public void oobFailed() {}
        public void sshTcpChannelStarted(String host, int port) {}
        public void sshTcpChannelEstablished(String host, int port) {}
        public void sshTcpChannelFailed(String host, int port) {}
    };
    private AutohostsListener autohostsListener = new AutohostsListener() {
        public void autohostsStarted() {}
        public void autohostsSucceeded(EventList hosts) {}
        public void autohostsFailed() {}
    };
    private StopOobListener stopOobListener = new StopOobListener() {
        public void stopOobStarted() {}
        public void stopOobSucceeded() {}
        public void stopOobFailed() {}
    };
    UserInfo userinfo = new UserInfo() {
        public String getPassphrase() {
            return authentication.getPassphrase();
        }
        public String getPassword() {
            return authentication.getPassword();
        }
        public boolean promptPassphrase(String prompt) {
            return authentication.promptPassphrase(prompt);
        }
        public boolean promptPassword(String prompt) {
            return authentication.promptAuthentication(prompt);
        }
        public boolean promptYesNo(String string) {
            return false;
        }
        public void showMessage(String string) {
            
        }
    };
}

