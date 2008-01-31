/*
 * SocketHelper.java
 *
 */

package sdtconnector;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketHelper {

    static void bindSocket(Socket s, InetSocketAddress sa) throws IOException, InterruptedException {
        s.setReuseAddress(true);
        for (int retries = RETRIES; retries > 0; retries--) {
            try {
                s.bind(sa);
                return;
            } catch (IOException ex) {
                Thread.sleep(RETRY_DELAY_MS);
            }
        }
        throw new IOException();
    }
    
    static void bindSocket(DatagramSocket s, InetSocketAddress sa) throws IOException, InterruptedException {
        s.setReuseAddress(true);
        for (int retries = RETRIES; retries > 0; retries--) {
            try {
                s.bind(sa);
                return;
            } catch (IOException ex) {
                Thread.sleep(RETRY_DELAY_MS);
            }
        }
        throw new IOException();
    }

    static void bindSocket(ServerSocket s, InetSocketAddress sa) throws IOException, InterruptedException {
        s.setReuseAddress(true);
        for (int retries = RETRIES; retries > 0; retries--) {
            try {
                s.bind(sa, 50);
                return;
            } catch (IOException ex) {
                Thread.sleep(RETRY_DELAY_MS);
            }
        }
        throw new IOException();
    }
    
    static final int RETRIES = 10;
    static final int RETRY_DELAY_MS = 100;
}
