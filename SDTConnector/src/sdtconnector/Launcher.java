/*
 * Launcher.java
 *
 */

package sdtconnector;

import java.io.IOException;
import java.util.Random;


public class Launcher implements Runnable {
    
    /** Creates a new instance of Launcher */
    public Launcher() {
        recordID = SDTManager.nextRecordID();
    }
    public Launcher(int recordID, String localHost, int localPort, int remotePort, int udpPort, int clientID) {
        this.recordID = recordID;
        this.localHost = localHost;
        this.localPort = localPort;
        this.remotePort = remotePort;
        this.udpOverTcpPort = udpPort;
        if (clientID != 0) {
            this.setClient(clientID);
        } else {
            this.client = null;
        }
    }
    public Launcher(int recordID, String localHost, int localPort, int remotePort, int udpPort, Client client) {
        this.recordID = recordID;
        this.localHost = localHost;
        this.localPort = localPort;
        this.remotePort = remotePort;
        this.udpOverTcpPort = udpPort;
        this.client = client;
    }
    public void setLocalHost(String localAddress) {
        this.localHost = localAddress;
    }
    public String getLocalHost() {
        return localHost;
    }
    public void setLocalPort(int localPort) {
        this.localPort = localPort;
    }
    public int getLocalPort() {
        return localPort;
    }
    public void setRemotePort(int remotePort) {
        this.remotePort = remotePort;
    }
    public int getRemotePort() {
        return remotePort;
    }
    public int getUdpPort() {
        return udpOverTcpPort;
    }
    public void setUdpPort(int udpPort) {
        this.udpOverTcpPort = udpPort;
        if (remotePort == 0) {
            //
            // Choose a random, unused ephemeral TCP port between 1024 and 65534
            // to tunnel the UDP traffic across
            //
            Random generator = new Random();
            Service service;
            Launcher launcher;
            int r;
            while (true) {
                r = generator.nextInt(65534 - 1024) + 1024;
                for (Object s : SDTManager.getServiceList()) {
                    service = (Service) s;
                    for (Object l : service.getLauncherList()) {
                        launcher = (Launcher) l;
                        if (r == launcher.getRemotePort()) {
                            continue;
                        }
                    }
                }
                remotePort = r;
                break;
            }
        }
    }
    public void setRecordID(int recordID) {
        this.recordID = recordID;
    }
    public int getRecordID() {
        return recordID;
    }
    public void setClient(int recordID) {
        for (Object o : SDTManager.getClientList()) {
            Client client = (Client) o;
            if (client.getRecordID() == recordID) {
                this.client = client;
                break;
            }
        }
    }
    public void setClient(Client client) {
        this.client = client;
    }
    public Client getClient() {
        return client;
    }
    public String getRemoteHost() {
        return remoteHost;
    }
    void setRemoteHost(String remoteHost) {
        this.remoteHost = remoteHost;
    }
    public boolean equals(Object obj) {
        return (obj != null && recordID == ((Launcher) obj).getRecordID());
    }
    public String toString() {
        StringBuffer sb  =  new StringBuffer("UDP XXXXX over TCP XXXXX -> XXXXX".length());
        
        if (udpOverTcpPort != 0) {
            sb.append("UDP ");
            sb.append(udpOverTcpPort);
            sb.append(" over TCP ");
        } else {
            sb.append("TCP ");
        }
        sb.append((localPort == 0 ? "(any)" : localPort));
        sb.append(" -> ");
        sb.append((remotePort == 0 ? "(any)" : remotePort));
        
        return sb.toString();
    }
        
    public void run() {
        launch();
    }
    public boolean launch() {
        try {
            Runtime.getRuntime().exec( client.getCommand(localHost, localPort) );
            return true;
        } catch (IOException ex) {
            return false;
        }        
    }

    private Client client;
    private String localHost = "localhost";
    private String remoteHost = "";
    private int udpOverTcpPort = 0;
    private int localPort = 0;
    private int remotePort = 0;
    private int recordID;
}

