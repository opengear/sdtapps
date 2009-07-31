/*
 * SDTManager.java
 *
 * Created on January 18, 2006, 4:19 PM
 */

package sdtconnector;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.SortedList;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Comparator;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.InvalidPreferencesFormatException;
import javax.swing.JOptionPane;
import org.jdesktop.swingx.util.OS;
import sdtconnector.Gateway;
import sdtconnector.Client;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import com.jgoodies.looks.LookUtils;
import java.net.URL;


public class SDTManager {
    
    public static final String prefsPath = "opengear/sdtconnector";

    static {
        gatewayList = new BasicEventList();
        clientList = new BasicEventList();
        serviceList = new BasicEventList();
        
        sortedClientList = new SortedList(SDTManager.clientList, new Comparator() {
            public int compare(Object o1, Object o2) {
                Client c1 = (Client) o1;
                Client c2 = (Client) o2;
                return c1.getRecordID() - c2.getRecordID();
            }
        });
        sortedServiceList = new SortedList(SDTManager.serviceList, new Comparator() {
            public int compare(Object o1, Object o2) {
                Service s1 = (Service) o1;
                Service s2 = (Service) o2;
                return s1.getRecordID() - s2.getRecordID();
            }
        });
        
        loadDefaults();
    }
    
    private static int compareVersions(String str1, String str2) {
        String[] v1 = { "0", "0", "0" };
        String[] v2 = { "0", "0", "0" };
        int i;
        
        v1 = str1.split("\\.");
        v2 = str2.split("\\.");
        
        for (i = 0; i < 3; i++) {
            if (v1[i].equals(v2[i]) == false)
                return Integer.parseInt(v1[i]) - Integer.parseInt(v2[i]);
        }
        return 0;
    }
    
    private static void loadDefaults() {
        boolean loadDefaults = true;
        
        try {
            if (Preferences.userRoot().nodeExists("opengear/sdtconnector/settings")) {
                String version = Settings.getProperty("version");
                if (compareVersions(version, SDTConnector.VERSION) < 0) {
                    int retVal = JOptionPane.showConfirmDialog(Main.getMainWindow(),
                            "SDTConnector has found preferences created by an older version (" + version + ").\n" +
                            "The version you are running (" +  SDTConnector.VERSION + ") may contain updated service and client\n" +
                            "settings.\n\n" +
                            "Do you want to delete the old preferences and load the new default\n" +
                            "preferences now?  You will lose any existing gateway and host settings.\n\n",
                            "Load new default configuration?",
                            JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                    if (retVal != JOptionPane.YES_OPTION) {
                        JOptionPane.showMessageDialog(Main.getMainWindow(),
                                "To load the new default configuration later, click File -> Import\n" +
                                "Preferences and select: defaults.xml\n\n",
                                "Hint",
                                JOptionPane.INFORMATION_MESSAGE);
                        loadDefaults = false;
                    }
                } else {
                    loadDefaults = false;
                }
            }
        } catch (BackingStoreException ex) {
            ex.printStackTrace();
        }
        
        if (loadDefaults) {           
            InputStream is = null;
            try {
                if ((is = SDTManager.class.getResourceAsStream("/config/defaults.xml")) == null) {
                    File f = new File(System.getProperty("user.dir"), "/config/defaults.xml");
                    is = new FileInputStream(f);
                }
                
                if (is == null) {
                    throw new Exception();
                } else {
                    Preferences.userRoot().node(SDTManager.prefsPath).removeNode();
                    Preferences.importPreferences(is);
                    Preferences.userRoot().node(SDTManager.prefsPath).sync();
                    recordID = Integer.parseInt(Settings.getProperty("recordID"));
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(Main.getMainWindow(),
                            "To load the configuration manually, click File -> Import\n" +
                            "Preferences and select the XML configuration file.\n\n",
                            "Failed to load default preferences",
                            JOptionPane.ERROR_MESSAGE); 
            } finally {
                try {
                    if (is != null) {
                        is.close();
                    }
                } catch (IOException ex) {
                    Logger.getLogger(SDTManager.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        load();               
    }
    
    public static void load() {
        //
        // Old config format pre user definable services (pre SDTConnector 1.2)
        // may need to be migrated
        //
        boolean migrateFixedServices = false;
        
        try {
            recordID = Integer.parseInt(Settings.getProperty("recordID"));
        } catch (NumberFormatException nfex) {
            recordID = initialRecordID();
            migrateFixedServices = true;
        }
        
        clientPreferences = Preferences.userRoot().node("opengear/sdtconnector/clients");
        clientList.clear();
        try {
            for (String clientChildName : clientPreferences.childrenNames()) {
                String path = null;
                String commandFormat = null;
                Preferences clientNode = clientPreferences.node(clientChildName);
                String name = clientNode.get("name", "");
                if (OS.isWindows()) {
                    path = clientNode.get("path-win", null);
                    commandFormat = clientNode.get("commandFormat-win", null);
                }
                if (path == null) {
                    path = clientNode.get("path", "");
                }
                if (commandFormat == null) {
                    commandFormat = clientNode.get("commandFormat", "");
                }
                if (migrateFixedServices) {
                    if (name.equals("VNC viewer")) {
                        path = Settings.getProperty("vnc.path");
                        Settings.removeProperty("vnc.path");
                    } else if (name.equals("RDP viewer")) {
                        path = Settings.getProperty("rdp.path");
                        Settings.removeProperty("rdp.path");
                    }
                }
                Client client = new Client(Integer.parseInt(clientChildName), name, path, commandFormat);
                clientList.add(client);
                if (migrateFixedServices) {
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
                String oobAddress = gwNode.get("oobaddress", null);
                String oobStart = gwNode.get("oobstart", null);
                String oobStop = gwNode.get("oobstop", null);
                String udpgwStart = gwNode.get("udpgwstart", null);
                String udpgwPid = gwNode.get("udpgwpid", null);
                String udpgwStop = gwNode.get("udpgwstop", null);
                Gateway gw;
                int gwRecordID;
                
                if (migrateFixedServices) {
                    gwRecordID = nextRecordID();
                } else {
                    gwRecordID = Integer.parseInt(gwChildName);
                }
                gw = new Gateway(gwRecordID, name, address, username, password, description, oobAddress, oobStart, oobStop, udpgwStart, udpgwStop, udpgwPid);
                gw.setPort(gwNode.getInt("sshport", 22));
                gw.setOobPort(gwNode.getInt("oobport", 22));
                gatewayList.add(gw);
                Preferences hostPrefs = gwNode.node("hosts");
                for (String hostChildName : hostPrefs.childrenNames()) {
                    Preferences hostNode = hostPrefs.node(hostChildName);
                    String hostName = hostNode.get("name", "");
                    String hostAddress = hostNode.get("address", "");
                    String hostDescription = hostNode.get("description", "");
                    Host host;
                    if (migrateFixedServices) {
                        host = new Host(nextRecordID(), hostName, hostAddress, hostDescription);
                    } else {
                        host = new Host(Integer.parseInt(hostChildName), hostName, hostAddress, hostDescription);
                    }
                    gw.addHost(host);
                    if (migrateFixedServices) {
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
                if (migrateFixedServices) {
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

        //
        // Load gateway configuration passed through at Web Start launch
        //
        String address = System.getProperty("sdt.gateway.address");
        if (address != null && !address.isEmpty()) {
            Gateway gw = new Gateway();
            
            gw.setAddress(address);
            gw.setPort(new Integer(System.getProperty("sdt.gateway.sshport")));
            gw.setUsername(System.getProperty("sdt.gateway.username"));
            gw.setName(System.getProperty("sdt.gateway.name"));
            gw.setDescription(System.getProperty("sdt.gateway.description"));
            gw.retrieveHostsAtStartup(true);
            
            addGateway(gw);
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
        gwPrefs.put("oobaddress", gw.getOobAddress());
        gwPrefs.put("oobstart", gw.getOobStart());
        gwPrefs.put("oobstop", gw.getOobStop());
        gwPrefs.putInt("oobport", gw.getOobPort());
        gwPrefs.put("udpgwstart", gw.getUdpgwStartFormat());
        gwPrefs.put("udpgwpid", gw.getUdpgwPidRegex());
        gwPrefs.put("udpgwstop", gw.getUdpgwStopFormat());
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
        return (EventList) sortedServiceList;
    }
    
    public static Service getServiceByName(String name) {
        Service service;
        
        for (Object s : getServiceList()) {
            service  = (Service) s;
            if (service.getName().equalsIgnoreCase(name)) {
                return service;
            }
        }
        return null;
    }
    
    public static Service getServiceByPort(int remotePort, int udpPort) {
        Service service;
        Launcher launcher;
		
        for (Object s : getServiceList()) {
            service = (Service) s;
            for (Object l : service.getLauncherList()) {
                launcher = (Launcher) l;
                if ((remotePort != 0 && launcher.getRemotePort() == remotePort) ||
                    (udpPort != 0 && launcher.getUdpPort() == udpPort))
                {
                    return service;
                }
            }
        }
        return null;
    }	
    
    public static Gateway getGatewayByName(String name, String username) {
        Gateway gateway;
        
        for (Object g : getGatewayList()) {
            gateway = (Gateway) g;
            
            if (!gateway.getName().equalsIgnoreCase(name)) {
                continue;
            }
            if (username != null && !gateway.getUsername().equals(username)) {
                continue;
            }
            return gateway;
        }
        return null;
    }
    
    public static Gateway getGatewayByAddress(String address, String username) {
        Gateway gateway;
        
        for (Object g : getGatewayList()) {
            gateway = (Gateway) g;
            
            if (!gateway.getAddress().equalsIgnoreCase(address)) {
                continue;
            }
            if (username != null && !gateway.getUsername().equals(username)) {
                continue;
            }
            return gateway;
        }
        try {
            InetAddress inaddr = InetAddress.getByName(address);
            for (Object g : getGatewayList()) {
                gateway = (Gateway) g;
                try {
                    InetAddress gInaddr = InetAddress.getByName(gateway.getAddress());
                    if (!inaddr.equals(gInaddr)) {
                        continue;
                    }
                    if (username != null && !gateway.getUsername().equals(username)) {
                        continue;
                    }
                    return gateway;
                } catch (UnknownHostException ex) {}
            }
        } catch (UnknownHostException ex) {}
        
        return null;
    }
    
    private static void saveClient(Client client) {
        Preferences clientNode = clientPreferences.node(String.valueOf(client.getRecordID()));
        clientNode.put("name", client.getName());
		if (OS.isWindows()) {
			clientNode.put("path-win", client.getPath());
			clientNode.put("commandFormat-win", client.getCommandFormat());
		} else {
			clientNode.put("path", client.getPath());
			clientNode.put("commandFormat", client.getCommandFormat());
		}
        
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
    /*
    public static EventList getHostList(int gwRecordID) {
        return getGateway(gwRecordID).getHostList();
    }
    public static Host getHost(int gwRecordID, int hostRecordID) {
        return getGateway(gwRecordID).getHost(hostRecordID);
    }
     */
    public static Client getHttpClient() {
        return (Client) clientList.get(0); // By default the HTTP client is first
    }
    
    private static EventList gatewayList;
    private static Preferences gatewayPreferences;
    
    private static EventList clientList;
    private static SortedList sortedClientList;
    private static Preferences clientPreferences;
    
    private static EventList serviceList;
    private static SortedList sortedServiceList;
    private static Preferences servicePreferences;
    
    private static int recordID;
}
