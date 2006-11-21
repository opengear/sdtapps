/*
 * SDTManager.java
 *
 * Created on January 18, 2006, 4:19 PM
 */

package sdtconnector;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.prefs.InvalidPreferencesFormatException;
import sdtconnector.Gateway;
import sdtconnector.Client;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import com.jgoodies.looks.LookUtils;


public class SDTManager {
    static {
        gatewayList = new BasicEventList();
        clientList = new BasicEventList();
        serviceList = new BasicEventList();
        load();
    }
    
    public static void load() {
        boolean loadDefaults = false;
        boolean migrate = false;

        try {
            if (Preferences.userRoot().nodeExists("opengear/sdtconnector")) {
                if (Preferences.userRoot().nodeExists("opengear/sdtconnector/settings")) {
                    try {
                        recordID = Integer.parseInt(Settings.getProperty("recordID"));
                    } catch (NumberFormatException nfex) {
                        recordID = initialRecordID();
                        loadDefaults = true;
                        migrate = true;
                    }
                }
            } else {
                loadDefaults = true;
            }
        } catch (BackingStoreException ex) {
            ex.printStackTrace();
        }
        
        if (loadDefaults) {
            try {
                File cwd = new File(System.getProperty("user.dir"));
                File defaults = new File(cwd, "defaults.xml");
                Preferences.importPreferences(new FileInputStream(defaults));
                recordID = Integer.parseInt(Settings.getProperty("recordID"));
            } catch (FileNotFoundException ex) {
            } catch (InvalidPreferencesFormatException ex) {
            } catch (IOException ex) {
            }            
        }

        clientPreferences = Preferences.userRoot().node("opengear/sdtconnector/clients");
        clientList.clear();
        try {
            for (String clientChildName : clientPreferences.childrenNames()) {
                Preferences clientNode = clientPreferences.node(clientChildName);
                String name = clientNode.get("name", "");
                String path = clientNode.get("path", "");
                if (migrate) {
                    if (name.equals("VNC viewer")) {
                        path = Settings.getProperty("vnc.path");
                        Settings.removeProperty("vnc.path");
                    } else if (name.equals("RDP viewer")) {
                        path = Settings.getProperty("rdp.path");
                        Settings.removeProperty("rdp.path");
                    }
                }
                String commandFormat = clientNode.get("commandFormat", "");
                Client client = new Client(Integer.parseInt(clientChildName), name, path, commandFormat);
                clientList.add(client);
                if (migrate) {
                    saveClient(client);
                }
            }
        } catch (BackingStoreException ex) {
            ex.printStackTrace();
        }
        
        servicePreferences = Preferences.userRoot().node("opengear/sdtconnector/services");
        serviceList.clear();
        try {
            for (String serviceChildName : servicePreferences.childrenNames()) {
                Preferences serviceNode = servicePreferences.node(serviceChildName);
                String name = serviceNode.get("name", "");
                String icon = serviceNode.get("icon", "service");
                Service service = new Service(Integer.parseInt(serviceChildName), name, icon);
                Preferences launcherPrefs = serviceNode.node("launchers");
                for (String launcherChildName : launcherPrefs.childrenNames()) {
                    Preferences launcherNode = launcherPrefs.node(launcherChildName);
                    String localAddress = launcherNode.get("localAddress", "");
                    int localPort = Integer.parseInt(launcherNode.get("localPort", "0"));
                    int remotePort = Integer.parseInt(launcherNode.get("remotePort", "0"));
                    int udpPort = Integer.parseInt(launcherNode.get("udpPort", "0"));
                    int clientID = Integer.parseInt(launcherNode.get("clientID", "0"));
                    Launcher launcher = new Launcher(Integer.parseInt(launcherChildName), localAddress, localPort, remotePort, udpPort, clientID);
                    service.addLauncher(launcher);
                }
                serviceList.add(service);
            }
        } catch (BackingStoreException ex) {
            ex.printStackTrace();
        }

        gatewayPreferences = Preferences.userRoot().node("opengear/sdtconnector/gateways");
        gatewayList.clear();
        try {
            for (String gwChildName : gatewayPreferences.childrenNames()) {
                Preferences gwNode = gatewayPreferences.node(gwChildName);
                String name = gwNode.get("name", "");
                String address = gwNode.get("address", "");
                String description = gwNode.get("description", "");
                String username = gwNode.get("username", "");
                String password = gwNode.get("password", "");
                Gateway gw;
                if (migrate) {
                    gw = new Gateway(nextRecordID(), name, address, username, password, description);
                } else {
                    gw = new Gateway(Integer.parseInt(gwChildName), name, address, username, password, description);
                }
                gw.setPort(gwNode.getInt("sshport", 22));
                gatewayList.add(gw);
                Preferences hostPrefs = gwNode.node("hosts");
                for (String hostChildName : hostPrefs.childrenNames()) {
                    Preferences hostNode = hostPrefs.node(hostChildName);
                    String hostName = hostNode.get("name", "");
                    String hostAddress = hostNode.get("address", "");
                    String hostDescription = hostNode.get("description", "");
                    Host host;
                    if (migrate) {
                        host = new Host(nextRecordID(), hostName, hostAddress, hostDescription);
                    } else {
                        host = new Host(Integer.parseInt(hostChildName), hostName, hostAddress, hostDescription);
                    }
                    gw.addHost(host);
                    if (migrate) {
                        Preferences protocolPrefs = hostNode.node("protocols");
                        if (!(protocolPrefs.get("www", "")).equals("")) {
                            host.addService("HTTP");
                        }
                        if (!(protocolPrefs.get("telnet", "")).equals("")) {
                            host.addService("Telnet");
                        }
                        if (!(protocolPrefs.get("vnc", "")).equals("")) {
                            host.addService("VNC");
                        }
                        if (!(protocolPrefs.get("rdp", "")).equals("")) {
                            host.addService("RDP");
                        }
                        saveHost(gatewayPreferences.node(String.valueOf(gw.getRecordID())), host);
                    } else {
                        for (String s : Settings.getPropertyList(hostNode.node("services"))) {
                            host.addService(Integer.parseInt(s));
                        }
                    }
                }
                if (migrate) {
                    gwNode.removeNode();
                    saveGateway(gw);
                }
            }
        } catch (BackingStoreException ex) {
            ex.printStackTrace();
        }
  
        Settings.setProperty("version", SDTConnector.VERSION);
        try {
            Preferences.userRoot().node("opengear/sdtconnector").sync();
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
        gwPrefs.put("name", gw.getName());
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
        hostPrefs.put("name", host.getName());
        hostPrefs.put("address", host.getAddress());
        System.out.println("Saving description " + host.getDescription());
        hostPrefs.put("description", host.getDescription());
        Settings.setPropertyList(hostPrefs.node("services"), host.getServiceList());
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
        for (Object o : service.getLauncherList()) {
            Launcher l = (Launcher) o;
            Preferences launcherNode = serviceNode.node("launchers/" + String.valueOf(l.getRecordID()));
           
            launcherNode.put("localAddress", l.getLocalHost());
            launcherNode.put("localPort", String.valueOf(l.getLocalPort()));
            launcherNode.put("remotePort", String.valueOf(l.getRemotePort()));
            launcherNode.put("udpPort", String.valueOf(l.getUdpPort()));
            if (l.getClient() != null) {
                launcherNode.put("clientID", String.valueOf(l.getClient().getRecordID()));
            }
        }
        try {
            serviceNode.sync();
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
        serviceList.remove(service);
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
        clientNode.put("path", client.getPath());
        clientNode.put("commandFormat", client.getCommandFormat());
        
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
        clientList.remove(client);
        try {
            clientPreferences.node(String.valueOf(client.getRecordID())).removeNode();
        } catch (BackingStoreException ex) {
            ex.printStackTrace();
        }
    }
    public static EventList getClientList() {
        return (EventList) clientList;
    }
    public static void setRecordID(int recordID) {
        if (recordID >= initialRecordID()) {
            Settings.setProperty("recordID", String.valueOf(recordID));
        }
    }
    public static int nextRecordID() {
        setRecordID(++recordID);
        return recordID;
    }
    public static int initialRecordID() {
        return 5000;
    }
    public static EventList getHostList(int recordID) {
        return getGateway(recordID).getHostList();
    }
    public static Host getHost(int gwRecordID, int hostRecordID) {
        return getGateway(gwRecordID).getHost(hostRecordID);
    }
    public static Client getHttpClient() {
        return (Client) clientList.get(0); // By default the HTTP client is first
    }
    
    private static EventList gatewayList;  
    private static Preferences gatewayPreferences;
    private static EventList clientList;
    private static Preferences clientPreferences;
    private static EventList serviceList;
    private static Preferences servicePreferences;
    private static int recordID;
}
