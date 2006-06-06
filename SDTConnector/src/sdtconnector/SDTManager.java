/*
 * SDTManager.java
 *
 * Created on January 18, 2006, 4:19 PM
 */

package sdtconnector;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import sdtconnector.Gateway;
import sdtconnector.Client;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;


public class SDTManager {
    static {
        gatewayList = new BasicEventList();
        clientList = new BasicEventList();
        serviceList = new BasicEventList();
        systemRecordID = getSystemRecordID();
        load();
    }
    public static void load() {
        // Predefined clients with predefined commands
        Browser httpBrowser = new Browser();
        HTTPSBrowser httpsBrowser = new HTTPSBrowser();
        Telnet telnetClient = new Telnet();
        // Predefined clients without predefined commands
        Client sshClient = null, vncClient = null, rdpClient = null;
        
        clientList.clear();
        clientList.add(httpBrowser);
        clientList.add(httpsBrowser);
        clientList.add(telnetClient);
        // User defined clients
        clientPreferences = Preferences.userRoot().node("opengear/sdtconnector/clients");
        try {
            for (String clientChildName : clientPreferences.childrenNames()) {
                Preferences clientNode = clientPreferences.node(clientChildName);
                String name = clientNode.get("name", "");
                String path = clientNode.get("path", "");
                UserDefinedClient client = new UserDefinedClient(Integer.parseInt(clientChildName), name, path);
                clientList.add(client);
                if (client.getRecordID() == new SSH().getRecordID()) {
                    sshClient = client;
                } else if (client.getRecordID() == new VNCViewer().getRecordID()) {
                    vncClient = client;
                } else if (client.getRecordID() == new RDPViewer().getRecordID()) {
                    rdpClient = client;
                }
            }
        } catch (BackingStoreException ex) {
            ex.printStackTrace();
        }
        if (sshClient == null) {
            sshClient = new SSH();
            clientList.add(sshClient);
        }
        if (vncClient == null) {
            vncClient = new VNCViewer();
            clientList.add(vncClient);
        }
        if (rdpClient == null) {
            rdpClient = new RDPViewer();
            clientList.add(rdpClient);
        }
        
        serviceList.clear();
        // Predefined services with predefined clients
        serviceList.add(new Service(telnetClient.getRecordID(), "Telnet", new Launcher(telnetClient.getRecordID(), "localhost", 0, 23, telnetClient)));
        serviceList.add(new Service(httpBrowser.getRecordID(), "HTTP", new Launcher(httpBrowser.getRecordID(), "localhost", 0, 80, httpBrowser)));
        serviceList.add(new Service(httpsBrowser.getRecordID(), "HTTPS", new Launcher(httpsBrowser.getRecordID(), "localhost", 0, 443, httpsBrowser)));
        serviceList.add(new Service(sshClient.getRecordID(), "SSH", new Launcher(sshClient.getRecordID(), "localhost", 0, 22, sshClient)));
        serviceList.add(new Service(vncClient.getRecordID(), "VNC", new Launcher(vncClient.getRecordID(), "localhost", 0, 3389, vncClient)));
        serviceList.add(new Service(rdpClient.getRecordID(), "RDP", new Launcher(rdpClient.getRecordID(), "localhost", 0, 5000, rdpClient)));

        // FIXME
        // serviceList.add(new Service(getDefaultILOID(), "HP iLO", new Launcher(getDefaultILOID(), "localhost", 0, 443, webBrowser)));
        // serviceList.add(new Service(getDefaultALOMID(), "Sun ALOM", new Launcher(getDefaultILOID(), "localhost", 0, 443, webBrowser)));
        // serviceList.add(new Service(getDefaultDRACID(), "Dell DRAC", new Launcher(getDefaultDRACID(), "localhost", 0, 443, webBrowser)));
        // serviceList.add(new Service(getDefaultRSAID(), "IBM RSA-II", new Launcher(getDefaultRSAID(), "localhost", 0, 443, webBrowser)));
        // User defined clients
        servicePreferences = Preferences.userRoot().node("opengear/sdtconnector/services");
        try {
            for (String serviceChildName : servicePreferences.childrenNames()) {
                Preferences serviceNode = servicePreferences.node(serviceChildName);
                String name = serviceNode.get("name", "");
                Service service = new Service(Integer.parseInt(serviceChildName), name);
                Preferences launcherPrefs = serviceNode.node("launchers");
                // TODO: many launchers per service, for e.g. Web/KVM
                for (String launcherChildName : launcherPrefs.childrenNames()) {
                    Preferences launcherNode = launcherPrefs.node(launcherChildName);
                    String localAddress = launcherNode.get("localAddress", "");
                    int localPort = Integer.valueOf(launcherNode.get("localPort", ""));
                    int remotePort = Integer.valueOf(launcherNode.get("remotePort", ""));
                    int clientID = Integer.valueOf(launcherNode.get("clientID", ""));
                    Launcher launcher = new Launcher(Integer.valueOf(launcherChildName), localAddress, localPort, remotePort, clientID);
                    service.addLauncher(launcher);
                }
                serviceList.add(service);
            }
        } catch (BackingStoreException ex) {
            ex.printStackTrace();
        }
        
        gatewayList.clear();
        gatewayPreferences = Preferences.userRoot().node("opengear/sdtconnector/gateways");
        try {
            for (String gwChildName : gatewayPreferences.childrenNames()) {
                Preferences gwNode = gatewayPreferences.node(gwChildName);
                String address = gwNode.get("address", "");
                String description = gwNode.get("description", "");
                String username = gwNode.get("username", "");
                String password = gwNode.get("password", "");
                Gateway gw = new Gateway(Integer.valueOf(gwChildName), address, username, password, description);
                gw.setPort(gwNode.getInt("sshport", 22));
                gatewayList.add(gw);
                Preferences hostPrefs = gwNode.node("hosts");
                for (String hostChildName : hostPrefs.childrenNames()) {
                    Preferences hostNode = hostPrefs.node(hostChildName);
                    String hostAddress = hostNode.get("address", "");
                    String hostDescription = hostNode.get("description", "");
                    Host host = new Host(Integer.valueOf(hostChildName), hostAddress, hostDescription);
                    gw.addHost(host);
                    // FIXME: load list
                    Preferences servicePrefs = hostNode.node("services");
                    for (String serviceChildName : servicePrefs.childrenNames()) {
                        Preferences serviceNode = servicePrefs.node(serviceChildName);
                        int serviceID = Integer.valueOf(serviceChildName);
                        host.addService(serviceID);
                    }
                }
            }
        } catch (BackingStoreException ex) {
            ex.printStackTrace();
        }
    }
    /**
     * Creates a new instance of SDTManager
     */
    private SDTManager() {
    }
    public static Gateway getGateway(int recordID) {
        for (Object o : gatewayList) {
            Gateway gw = (Gateway) o;
            if (gw.getRecordID() == recordID) {
                return gw;
            }
        }
        return null;
    }
    public static void addGateway(Gateway gw) {
        gatewayList.add(gw);
        saveGateway(gw);    
    }
    public static void removeGateway(Gateway gw) {
        /*
        for (ListIterator it = gatewayList.listIterator(); it.hasNext(); ) {
            if (((Gateway) it.next()).equals(gw)) {
                it.remove();
            }
        }*/
        gatewayList.remove(gw);
        try {
            gatewayPreferences.node(String.valueOf(gw.getRecordID())).removeNode();
        } catch (BackingStoreException ex) {
            ex.printStackTrace();
        }
    }
    public static void updateGateway(Gateway gw) {
        System.out.println("Updating gateway " + gw);
        saveGateway(gw);
    }
    private static void saveGateway(Gateway gw) {
        Preferences gwPrefs = gatewayPreferences.node(String.valueOf(gw.getRecordID()));
        gwPrefs.put("address", gw.getAddress());
        gwPrefs.put("description", gw.getDescription());
        gwPrefs.put("username", gw.getUsername());
        gwPrefs.put("password", gw.getPassword());
        gwPrefs.putInt("sshport", gw.getPort());
        try {
            gwPrefs.sync();
        } catch (BackingStoreException ex) {}
    }
    public static EventList getGatewayList() {
        return (EventList) gatewayList;
    }
    private static void saveHost(Preferences gwPrefs, Host host) {
        Preferences hostPrefs = gwPrefs.node("hosts/" + host.getRecordID());
        hostPrefs.put("address", host.getAddress());
        System.out.println("Saving description " + host.getDescription());
        hostPrefs.put("description", host.getDescription());
        // FIXME: save list
        for (Object o : host.getServiceList()) {
            Service s = (Service) o;
            Preferences servicePrefs = hostPrefs.node("services/" + s.getRecordID());
        }        
        try {
            gwPrefs.sync();
        } catch (BackingStoreException ex) {}
    }
    public static void addHost(Gateway gw, Host host) {
        gw.addHost(host);
        Preferences gwPrefs = gatewayPreferences.node(String.valueOf(gw.getRecordID()));
        saveHost(gwPrefs, host);
    }
    public static void removeHost(Gateway gw, Host host) {
        gw.removeHost(host);
        
        Preferences gwPrefs = gatewayPreferences.node(String.valueOf(gw.getRecordID()));
        try {
            gwPrefs.node("hosts/" + host.getRecordID()).removeNode();
        } catch (BackingStoreException ex) {}
    }
    public static void updateHost(Gateway gw, Host host) {
        System.out.println("Updating host " + host);
        Preferences gwPrefs = gatewayPreferences.node(String.valueOf(gw.getRecordID()));
        saveHost(gwPrefs, host);
    }
    private static void saveService(Service service) {
        Preferences serviceNode = servicePreferences.node(String.valueOf(service.getRecordID()));
        serviceNode.put("name", service.getName());
        // TODO: many launchers per service
        Preferences launcherPrefs = serviceNode.node("launchers/" + service.getLauncher().getRecordID());
        launcherPrefs.put("localAddress", service.getLauncher().getLocalHost());
        launcherPrefs.put("localPort", String.valueOf(service.getLauncher().getLocalPort()));
        launcherPrefs.put("remotePort", String.valueOf(service.getLauncher().getRemotePort()));
        launcherPrefs.put("clientID", String.valueOf(service.getLauncher().getClient().getRecordID()));
        try {
            serviceNode.sync();
            // FIXME: sync launcher?
        } catch (BackingStoreException ex) {}
    }
    public static void updateService(Service service) {
        System.out.println("Updating service " + service);
        saveService(service);
    }
    public static void addService(Service service) {
        serviceList.add(service);
        saveService(service);    
    }
    public static void removeService(Service service) {
        for (ListIterator it = serviceList.listIterator(); it.hasNext(); ) {
            if (((Service) it.next()).equals(service)) {
                it.remove();
            }
        }
        try {
            servicePreferences.node(String.valueOf(service.getRecordID())).removeNode();
        } catch (BackingStoreException ex) {
            ex.printStackTrace();
        }
    }
    public static EventList getServiceList() {
        return (EventList) serviceList;
    }
    
    private static void saveClient(Client client) {
        Preferences clientNode = clientPreferences.node(String.valueOf(client.getRecordID()));
        clientNode.put("name", client.getName());
        // FIXME
        clientNode.put("path", client.getPath());
        
        try {
            clientNode.sync();
        } catch (BackingStoreException ex) {}
    }
    public static void updateClient(Client client) {
        System.out.println("Updating client " + client);
        saveClient(client);
    }
    public static void addClient(Client client) {
        clientList.add(client);
        saveClient(client);    
    }
    public static void removeClient(Client client) {
        for (ListIterator it = clientList.listIterator(); it.hasNext(); ) {
            if (((Client) it.next()).equals(client)) {
                it.remove();
            }
        }
        try {
            clientPreferences.node(String.valueOf(client.getRecordID())).removeNode();
        } catch (BackingStoreException ex) {
            ex.printStackTrace();
        }
    }
    public static EventList getClientList() {
        return (EventList) clientList;
    }

    public static int getSystemRecordID() {
        String idSetting;
        if ((idSetting = Settings.getProperty("SystemRecordID")).equals("")) {
            systemRecordID = getInitialRecordID();
        } else {
            systemRecordID = Integer.parseInt(idSetting);
        }
        return systemRecordID;
    }
    public static void setSystemRecordID(int recordID) {
        Settings.setProperty("SystemRecordID", String.valueOf(recordID));
    }
    public static int nextRecordID() {
        setSystemRecordID(++systemRecordID);
        return systemRecordID;
    }
    public static int getInitialRecordID() {
        return 1000;
    }
    public static int getEditableRecordID() {
        return 500;
    }
    public static EventList getHostList(int recordID) {
        return getGateway(recordID).getHostList();
    }
    public static Host getHost(int gwRecordID, int hostRecordID) {
        return getGateway(gwRecordID).getHost(hostRecordID);
    }
    
    private static EventList gatewayList;  
    private static Preferences gatewayPreferences;
    private static EventList clientList;
    private static Preferences clientPreferences;
    private static EventList serviceList;
    private static Preferences servicePreferences;
    // FIXME: confusing variable names
    private static int systemRecordID;
}
