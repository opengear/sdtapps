package sdtconnector;


import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
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
    
    public UDPGateway(String localHost, int udpPort, int tcpPort) {
        this.localHost = localHost;
        this.udpPort = udpPort;
        this.tcpPort = tcpPort;
    }
    
    public void start() {
        thread = new Thread(this);
        thread.setDaemon(true);
        thread.start();
    }
    
    public void stop() {
        thread.interrupt();
    }
    
    public void shutdown() {
        uninit();
        stop();
    }
    
    private void init() {
        try {
            selector = Selector.open();
            udpChannel = DatagramChannel.open();
            udpChannel.socket().bind(new InetSocketAddress(InetAddress.getByName(localHost), udpPort));
            udpChannel.configureBlocking(false);
            udpKey = udpChannel.register(selector, SelectionKey.OP_READ);
            tcpChannel = SocketChannel.open();
        } catch (ClosedChannelException ex) {
            ex.printStackTrace();
        } catch (UnknownHostException ex) {
            ex.printStackTrace();
        } catch (SocketException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }    
    }
    
    private void uninit() {
        try {
            for (Object o : selector.keys()) {
                SelectionKey key = (SelectionKey) o;
                key.cancel();
            }
            selector.close();
            udpChannel.disconnect();
            udpChannel.close();
            tcpChannel.close();
        } catch (IOException ex) {
        }
    }
    
    public void reinit() {
        uninit();
        init();
    }

    public void run() {
        ByteBuffer buffer = ByteBuffer.allocate(BUF_LEN);
        System.out.println("UDP-TCP: " + localHost + ":" + udpPort + " <-> " + localHost + ":" + tcpPort);
        init();
        System.out.println("UDP-TCP: Waiting");
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
                                System.out.println("UDP-TCP: Received first UDP packet");
                                tcpChannel.configureBlocking(true);
                                tcpChannel.connect(new InetSocketAddress(InetAddress.getByName(localHost), tcpPort));
                                tcpChannel.configureBlocking(false);
                                tcpKey = tcpChannel.register(selector, SelectionKey.OP_READ);
                                System.out.println("UDP-TCP: Connected TCP channel");
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
            } catch (IOException ex) {
                System.out.println("UDPGateway: IO error, terminating");
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
    private int udpPort;
    private int tcpPort;
    private Thread thread;
    public static final int BUF_LEN = 1024;
}
