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
    
    public void setListener(Listener l) {
        listener = l;
    }
    public Redirector getRedirector(String host, int port, String lhost, int lport, int uport) {
        for (Redirector r : redirectors) {
            if (r.getRemoteHost().equals(host) && r.getRemotePort() == port && 
                    (lport == 0 || lport == r.getLocalPort())) {
                return r;
            }
            // shutdown redirector which has local port that required
            if (r.getLocalPort() == lport || (uport != 0 && r.getUDPPort() == uport)) {
                r.shutdown();
                redirectors.remove(r);
                break;
            }
        }

        // Create a new redirector
        try {
            Redirector r = new Redirector(this, host, port, lhost, lport, uport);
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
            listener.oobStarted();
            try {
                Process proc = Runtime.getRuntime().exec(gateway.getOobStart());
                int retVal = proc.waitFor();
                // TODO: failure case
            } catch (IOException ex) {
                listener.oobFailed();
                return false;
            } catch (InterruptedException ex) {
                listener.oobFailed();
                return false;
            }
            listener.oobSucceeded();
        }
        listener.sshLoginStarted();
        try {
            session.connect(5000);
            //activeSession().connect(5000);
        } catch (JSchException ex) {
            listener.sshLoginFailed();
            // Reset the session
            setupSession(username, password);
            return false;
        }
        listener.sshLoginSucceeded();
        return true;
    }
    
    private boolean disconnectSession() {
        session.disconnect();
        //activeSession().disconnect();
        return true;
    }
    
    public void stopOob() {
        try {
            Process proc = Runtime.getRuntime().exec(gateway.getOobStop());
            int retVal = proc.waitFor();
            // TODO: failure case
        } catch (IOException ex) {
            // FIXME
        } catch (InterruptedException ex) {
            // Ignore
        }
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

    public EventList getHosts() {
		Future f = exec.submit(new Callable() {
			public EventList call() {
				EventList hosts = null;
			   
				if (session.isConnected() == false) {
					try {
						session.connect(5000);
					} catch (JSchException ex) {
						// FIXME
						setupSession(username, password);
						return hosts;
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
				return hosts;
			}
		});
		
		EventList hosts = null;
		
		try {
			hosts = (EventList) f.get();
		} catch (ExecutionException ex) {
			ex.printStackTrace();
		} catch (InterruptedException ex) {
			ex.printStackTrace();
		}
		return hosts;
	}
    
    public class Redirector implements Runnable {
        private boolean redirectSocket(final Socket s) {
            Future f = exec.submit(new Callable() {

                private void redirectRemoteUDPSocket() throws Exception {
                    String command = gateway.getUdpgwStartCommand(host, port, uport);
                    String regex = gateway.getUdpgwPidRegex();
                    CharSequence cs;
                    String s;
  
                    System.out.println("TCP-UDP: 127.0.0.1:" + port + " <-> " + host + ":" + uport);
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
                    } else {
                        remoteUDPGatewayPID = 0;
                    }

                    System.out.println("TCP-UDP: Remote UDP gateway started, PID " + remoteUDPGatewayPID);
                }

                private void redirectTCPSocket(final String host) throws Exception {
                    tcpip = (ChannelDirectTCPIP) session.openChannel("direct-tcpip");
                    tcpip.setHost(host);
                    tcpip.setPort(port);
                    tcpip.setInputStream(s.getInputStream());
                    tcpip.setOutputStream(s.getOutputStream());
                    listener.sshTcpChannelStarted(host, port);
                    tcpip.connect();
                    listener.sshTcpChannelEstablished(host, port);
                }

                public Channel call() throws Exception {
                    if (!doConnect()) {
                        throw new JSchException("Failed to connect");
                    }
                    try {
                        if (uport != 0) {
                            redirectRemoteUDPSocket();
                            redirectTCPSocket(lhost);
                        } else {
                            redirectTCPSocket(host);
                        }
                        return tcpip;
                    } catch (JSchException ex) {
                        listener.sshTcpChannelFailed(uport != 0 ? lhost : host, port);
                        return null;
                    } catch (Exception ex) {
                        listener.sshTcpChannelFailed(uport != 0 ? lhost : host, port);
                        return null;
                    }
                }
            });
            return true;
        }

        public Redirector(GatewayConnection conn, String host, int port, String lhost, int lport, int uport) throws IOException {
            listenSocket = new ServerSocket(lport, 50, InetAddress.getByName(lhost));
            this.connection = conn;
            this.host = host;
            this.lhost = lhost;
            this.port = port;
            this.lport = port;
            this.uport = uport;
            this.connection = connection;
        }
        public void start() {
            listenThread = new Thread(this);
            listenThread.setDaemon(true);
            listenThread.start();
            if (uport != 0) {
                localUDPGateway = new UDPGateway(lhost, uport, lport);
                localUDPGateway.start();
            }
        }
        public void stop() {
            listenThread.interrupt();
        }
        
        private void shutdownLocalUDPRedirection() {
            localUDPGateway.shutdown();
        }

        private void shutdownRemoteUDPRedirection() {
            if (remoteUDPGatewayShell != null) {
                if (remoteUDPGatewayPID != 0) {
                    String command = gateway.getUdpgwStopCommand(host, port, uport, remoteUDPGatewayPID);
                    try {
                        PrintStream shOut = new PrintStream(remoteUDPGatewayShell.getOutputStream());
                        BufferedReader shIn = new BufferedReader(new InputStreamReader(remoteUDPGatewayShell.getInputStream()));

                        shellWrite(shOut, "");
                        // Wait for shell to be ready
                        shellRead(shIn, 1);
                        // Send command
                        shellWrite(shOut, command);
                        // Wait for shell to be ready
                        shellRead(shIn, 1);
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
                if (uport != 0) {
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
            return listenSocket.getLocalPort();
        }
        public String getRemoteHost() {
            return host;
        }
        public int getRemotePort() {
            return port;
        }
        private int getUDPPort() {
            return uport;
        }
        public void run() {
            while (true) {
                try {
                    redirectSocket(listenSocket.accept());
                } catch (IOException ex) {
                    break;
                }
            }
        }

        private ServerSocket listenSocket;
        private Thread listenThread;
        private GatewayConnection connection;
        private String host;
        private String lhost;
        private int port;
        private int lport;
        private int uport;
        private UDPGateway localUDPGateway;
        private ChannelShell remoteUDPGatewayShell;
        private int remoteUDPGatewayPID;
        private ChannelDirectTCPIP tcpip;
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

    public interface Authentication {
        public boolean promptAuthentication(String prompt);
        public boolean promptPassphrase(String prompt);
        public String getUsername();
        public String getPassword();
        public String getPassphrase();
    }
    public interface Listener {
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
    
    private Listener listener = new Listener() {
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

