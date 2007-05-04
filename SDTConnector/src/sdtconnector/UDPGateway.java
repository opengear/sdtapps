package sdtconnector;


import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;


public class UDPGateway implements Runnable {
    
    public UDPGateway(String localHost, int udpOverTcpPort, int localPort) {
        this.localHost = localHost;
        this.udpOverTcpPort = udpOverTcpPort;
        this.localPort = localPort;
    }
    
    public void start() {
        thread = new Thread(this);
        thread.setDaemon(true);
        thread.start();
    }
    
    public void stop() {
        if (thread != null) {
            thread.interrupt();
        }
    }
    
    public void shutdown() {
        stop();
        uninit();
    }
    
    public void init() throws Exception {
        InetSocketAddress udpSockAddr;
        
        selector = Selector.open();
        tcpChannel = SocketChannel.open();        
        udpChannel = DatagramChannel.open();
        udpSockAddr = new InetSocketAddress(InetAddress.getByName(localHost), udpOverTcpPort);
        udpChannel.configureBlocking(false);
        udpKey = udpChannel.register(selector, SelectionKey.OP_READ);
        SocketHelper.bindSocket(udpChannel.socket(), udpSockAddr);
    }
    
    private void uninit() {
        try {
            if (selector != null) {
                for (Object o : selector.keys()) {
                    SelectionKey key = (SelectionKey) o;
                    key.cancel();
                }
                selector.close();
            }
            if (udpChannel != null) {
                udpChannel.socket().close();
                udpChannel.disconnect();
            }
            if (tcpChannel != null) {
                tcpChannel.socket().close();
                tcpChannel.close();
            }
        } catch (IOException ex) {
        }
    }
    
    public void run() {
        ByteBuffer buffer = ByteBuffer.allocate(BUF_LEN);
        System.out.println("UDP-TCP: Local UDP to TCP gateway starting");
        System.out.println("UDP-TCP: " + localHost + ":" + udpOverTcpPort + " <-> " + localHost + ":" + localPort);
        while (true) {
            try {
                selector.select();
                if (thread.interrupted()) {
                    break;
                }
                buffer.clear();
                keys = selector.selectedKeys();
                for (Iterator it = keys.iterator(); it.hasNext();) {
                    SelectionKey key = (SelectionKey) it.next();
                    it.remove();
                    if (key == udpKey) {
                        if (key.isReadable()) {
                            udpClient = udpChannel.receive(buffer);
                            if (!tcpChannel.isConnected()) {
                                InetSocketAddress tcpSockAddr;

                                System.out.println("UDP-TCP: Received first UDP packet");
                                tcpChannel.configureBlocking(true);
                                
                                tcpSockAddr = new InetSocketAddress(InetAddress.getByName(localHost), localPort);
                                tcpChannel.connect(tcpSockAddr);
                                System.out.println("UDP-TCP: Using TCP transport port " + tcpSockAddr.getPort());
                                tcpChannel.configureBlocking(false);
                                tcpKey = tcpChannel.register(selector, SelectionKey.OP_READ);
                                System.out.println("UDP-TCP: Connected TCP transport channel");
                            }
                            buffer.flip();
                            tcpChannel.write(buffer);
                        }
                    } else if (key == tcpKey) {
                        if (key.isReadable()) {
                            if (tcpChannel.isConnected()) {
                                int len = tcpChannel.read(buffer);
                                if (len > 0) {
                                    buffer.flip();
                                    udpChannel.send(buffer, udpClient);
                                }
                            }
                        }
                    }
                }
            } catch (UnknownHostException ex) {
                System.out.println("UDP-TCP: Unknown host: " + ex.getMessage());
                break;
            } catch (IOException ex) {
                System.out.println("UDP-TCP: IO error: " + ex.getMessage());
                break;
            }
        }
    }

    private DatagramChannel udpChannel;
    private SocketChannel tcpChannel;
    private Selector selector;
    private SelectionKey udpKey;
    private SelectionKey tcpKey;
    private Set keys;
    private SocketAddress udpClient;    
    private String localHost;
    private int udpOverTcpPort;
    private int localPort;
    private Thread thread;
    public static final int BUF_LEN = 1024;
}
