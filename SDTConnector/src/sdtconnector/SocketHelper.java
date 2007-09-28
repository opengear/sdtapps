/*
 * SocketHelper.java
 *
 * Created on 4 May 2007, 15:39
 */

package sdtconnector;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import sdtconnector.BinderHelper;


public class SocketHelper {

    static void bindSocket(Socket s, InetSocketAddress sa) throws IOException, InterruptedException {
        s.setReuseAddress(true);

        if (sa.getPort() < 1024) {
            BinderHelper.becomeRoot();
        }
        for (int retries = RETRIES; retries > 0; retries--) {
            try {
                s.bind(sa);
                break;
            } catch (IOException ex) {
                Thread.sleep(RETRY_DELAY_MS);
            }
        }
        if (sa.getPort() < 1024) {
            BinderHelper.dropRoot();
        }
        if (s.isBound() == false) {
            throw new IOException();
        }
    }
    
    static void bindSocket(DatagramSocket s, InetSocketAddress sa) throws IOException, InterruptedException {
        s.setReuseAddress(true);
        if (sa.getPort() < 1024) {
            BinderHelper.becomeRoot();
        }
        for (int retries = RETRIES; retries > 0; retries--) {
            try {
                s.bind(sa);
                return;
            } catch (IOException ex) {
                Thread.sleep(RETRY_DELAY_MS);
            }
        }
        if (sa.getPort() < 1024) {
            BinderHelper.dropRoot();
        }
        if (s.isBound() == false) {
            throw new IOException();
        }
    }

    static void bindSocket(ServerSocket s, InetSocketAddress sa) throws IOException, InterruptedException {
        s.setReuseAddress(true);
        if (sa.getPort() < 1024) {
            BinderHelper.becomeRoot();
        }
        for (int retries = RETRIES; retries > 0; retries--) {
            try {
                s.bind(sa, 50);
                return;
            } catch (IOException ex) {
                Thread.sleep(RETRY_DELAY_MS);
            }
        }
        if (sa.getPort() < 1024) {
            BinderHelper.dropRoot();
        }
        if (s.isBound() == false) {
            throw new IOException();
        }
    }
    
    static final int RETRIES = 10;
    static final int RETRY_DELAY_MS = 100;
}
