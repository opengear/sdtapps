package sdtconnector;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.ServerSocket;
import java.util.logging.Level;
import java.util.logging.Logger;

class CmdServer implements Runnable {

    private ServerSocket serverSocket = null;

    public CmdServer(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public void run() {
        while (true) {
            try {
                Socket socket = serverSocket.accept();
                System.out.println("CmdServer: Accepted command connection");

                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String url = "", address = "", username = "", name = "", description = "", privatekey = "";
                int sshport = 22;

                String line;
                while ((line = in.readLine()) != null) {
                    if (line.startsWith("sdt://")) {
                        url = line;

                    } else if (line.startsWith("sdt.gateway.address"))  {
                        address = line.substring("sdt.gateway.address ".length());

                    } else if (line.startsWith("sdt.gateway.sshport")) {
                        sshport = new Integer(line.substring("sdt.gateway.sshport ".length()));

                    } else if (line.startsWith("sdt.gateway.username")) {
                        username = line.substring("sdt.gateway.username ".length());

                    } else if (line.startsWith("sdt.gateway.name")) {
                        name = line.substring("sdt.gateway.name ".length());

                    } else if (line.startsWith("sdt.gateway.description")) {
                        description = line.substring("sdt.gateway.description ".length());

                    } else if (line.startsWith("-----BEGIN")) {
                        do {
                            privatekey += line;
                            privatekey += "\n";
                        } while (!line.startsWith("-----END") && (line = in.readLine()) != null);
                    }
                }
                System.out.println("CmdServer: URL " + url);

                SDTManager.addVolatilePrivateKey(System.getProperty("sdt.privatekey"));
                try {
                    SDTURLHelper.setURL(url);

                    Gateway gw = SDTURLHelper.getVolatileGateway();
                    if (gw == null) {
                        gw = new Gateway();
                        gw.setAddress(address);
                        gw.setPort(sshport);
                        gw.setUsername(username);
                        gw.setName(name);
                        gw.setDescription(description);
                        gw.isVolatile(true);
                        SDTManager.addGateway(gw);
                    }
                    System.out.println("CmdServer: Gateway " + gw);
                    Main.getMainWindow().retrieveHosts(gw);
                    Main.getMainWindow().launchSDTURL();

                } catch (Exception ex) {}

                in.close();
                socket.close();
            } catch (IOException ex) {
                Logger.getLogger(CmdServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}
