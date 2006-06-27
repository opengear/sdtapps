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
import com.jgoodies.looks.LookUtils;


public class SDTManager {
    static {
        gatewayList = new BasicEventList();
        clientList = new BasicEventList();
        serviceList = new BasicEventList();
        loadDefaults();
        loadRecordID();
        load();
    }
    public static void load() {
        clientPreferences = Preferences.userRoot().node("opengear/sdtconnector/clients");
        try {
            for (String clientChildName : clientPreferences.childrenNames()) {
                Preferences clientNode = clientPreferences.node(clientChildName);
                String name = clientNode.get("name", "");
                String path = clientNode.get("path", "");
                String commandFormat = clientNode.get("commandFormat", "");
                Client client = new Client(Integer.parseInt(clientChildName), name, path, commandFormat);
                if (clientList.contains(client)) {
                    // Update a default setting
                    Client defaultClient = (Client) clientList.get(clientList.indexOf(client));
                    defaultClient.setName(name);
                    defaultClient.setPath(path);
                    defaultClient.setCommandFormat(commandFormat);
                } else {
                    clientList.add(client);
                }
            }
        } catch (BackingStoreException ex) {
            ex.printStackTrace();
        }
        
        servicePreferences = Preferences.userRoot().node("opengear/sdtconnector/services");
        try {
            for (String serviceChildName : servicePreferences.childrenNames()) {
                Preferences serviceNode = servicePreferences.node(serviceChildName);
                String name = serviceNode.get("name", "");
                Service service = new Service(Integer.parseInt(serviceChildName), name);
                Preferences launcherPrefs = serviceNode.node("launchers");
                // TODO: many configurable launchers per service
                for (String launcherChildName : launcherPrefs.childrenNames()) {
                    Preferences launcherNode = launcherPrefs.node(launcherChildName);
                    String localAddress = launcherNode.get("localAddress", "");
                    int localPort = Integer.valueOf(launcherNode.get("localPort", ""));
                    int remotePort = Integer.valueOf(launcherNode.get("remotePort", ""));
                    int clientID = Integer.valueOf(launcherNode.get("clientID", "0"));
                    Launcher launcher = new Launcher(Integer.valueOf(launcherChildName), localAddress, localPort, remotePort, clientID);
                    service.addLauncher(launcher);
                }
                if (serviceList.contains(service)) {
                    // Update a default setting
                    Service defaultService = (Service) serviceList.get(serviceList.indexOf(service));
                    defaultService.setName(name);
                    defaultService.setLaunchers(service.getLauncherList());
                } else {
                    serviceList.add(service);
                }
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
                Gateway gw = new Gateway(Integer.valueOf(gwChildName), name, address, username, password, description);
                gw.setPort(gwNode.getInt("sshport", 22));
                gatewayList.add(gw);
                Preferences hostPrefs = gwNode.node("hosts");
                for (String hostChildName : hostPrefs.childrenNames()) {
                    Preferences hostNode = hostPrefs.node(hostChildName);
                    String hostName = hostNode.get("name", "");
                    String hostAddress = hostNode.get("address", "");
                    String hostDescription = hostNode.get("description", "");
                    Host host = new Host(Integer.valueOf(hostChildName), hostName, hostAddress, hostDescription);
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

    private static void loadDefaults() {
        httpClient = new Client(nextRecordID(),
                "HTTP browser",
                LookUtils.IS_OS_WINDOWS ? "rundll32 url.dll,FileProtocolHandler" : "firefox",
                "%path% http://%host%:%port%/"); // This client is also used by AboutDialog
        Client httpsClient = new Client(nextRecordID(),
                "HTTPS browser",
                LookUtils.IS_OS_WINDOWS ? "rundll32 url.dll,FileProtocolHandler" : "firefox",
                "%path% https://%host%:%port%/");
        Client telnetClient = new Client(nextRecordID(),
                "Telnet client",
                "telnet",
                LookUtils.IS_OS_WINDOWS ? "cmd /c start %path% %host% %port%" : "xterm -e %path% %host% %port%");
        Client sshClient = new Client(nextRecordID(),
                LookUtils.IS_OS_WINDOWS ? "Putty SSH client" : "SSH client",
                LookUtils.IS_OS_WINDOWS ? "" : "ssh",
                LookUtils.IS_OS_WINDOWS ? "%path% -ssh -P %port% %host%" : "xterm -e %path% -o UserKnownHostsFile=/dev/null -p %port% %host%");
        Client vncClient = new Client(nextRecordID(),
                "VNC viewer",
                "",
                LookUtils.IS_OS_WINDOWS ? "%path% /nostatus %host%::%port" : "%path% %host%:%port%");
        Client rdpClient = new Client(nextRecordID(),
                "RDP viewer",
                "",
                LookUtils.IS_OS_WINDOWS ? "%path% /console /v:%host%:%port%" : "%path% %host%:%port%");
        Launcher httpLauncher = new Launcher(nextRecordID(), "localhost", 0, 80, httpClient);
        Launcher httpsLauncher = new Launcher(nextRecordID(), "localhost", 0, 443, httpsClient);
        Launcher telnetLauncher = new Launcher(nextRecordID(), "localhost", 0, 23, telnetClient);
        
        clientList.clear();
        serviceList.clear();
        clientList.add(httpClient);
        clientList.add(httpsClient);
        clientList.add(telnetClient);
        clientList.add(sshClient);
        clientList.add(vncClient);
        clientList.add(rdpClient);
        serviceList.add(new Service(nextRecordID(), "HTTP", httpLauncher, "www"));
        serviceList.add(new Service(nextRecordID(), "HTTPS", httpsLauncher, "www"));
        serviceList.add(new Service(nextRecordID(), "Telnet", telnetLauncher, "telnet"));
        serviceList.add(new Service(nextRecordID(), "SSH", new Launcher(nextRecordID(), "localhost", 0, 22, sshClient), "telnet"));
        serviceList.add(new Service(nextRecordID(), "VNC", new Launcher(nextRecordID(), "localhost", 0, 5900, vncClient), "vnc"));
        serviceList.add(new Service(nextRecordID(), "RDP", new Launcher(nextRecordID(), "localhost", 0, 3389, rdpClient), "tsclient"));
        Service ilo = new Service(nextRecordID(), "HP iLO", httpsLauncher);
        ilo.addLauncher(new Launcher(nextRecordID(), "localhost", 0, 23, null));
        serviceList.add(ilo);
        Service rsa = new Service(nextRecordID(), "IBM RSA-II", httpsLauncher);
        rsa.addLauncher(new Launcher(nextRecordID(), "localhost", 2000, 2000, null));
        serviceList.add(rsa);
        Service drac = new Service(nextRecordID(), "DRAC", httpsLauncher);
        drac.addLauncher(new Launcher(nextRecordID(), "localhost", 5900, 5900, null));
        serviceList.add(drac);
        serviceList.add(new Service(nextRecordID(), "Sun ALOM", telnetLauncher));
        //serviceList.add(new Service(nextRecordID(), "DRAC", new Launcher(2005, "localhost", 0, 1311, httpsClient)));
        serviceList.add(new Service(nextRecordID(), "ZENworks", new Launcher(nextRecordID(), "localhost", 0, 8080, httpClient)));
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
        for (Object o : service.getLauncherList()) {
            Launcher l = (Launcher) o;
            Preferences launcherNode = serviceNode.node("launchers/" + String.valueOf(l.getRecordID()));
           
            launcherNode.put("localAddress", l.getLocalHost());
            launcherNode.put("localPort", String.valueOf(l.getLocalPort()));
            launcherNode.put("remotePort", String.valueOf(l.getRemotePort()));
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
        /*
        for (ListIterator it = serviceList.listIterator(); it.hasNext(); ) {
            if (((Service) it.next()).equals(service)) {
                it.remove();
            }
        }*/
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
        /*
        for (ListIterator it = clientList.listIterator(); it.hasNext(); ) {
            if (((Client) it.next()).equals(client)) {
                it.remove();
            }
        }*/
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
    public static int loadRecordID() {
        String idSetting = Settings.getProperty("RecordID");
        
        if (idSetting.equals("") == false) {
            recordID = Integer.parseInt(idSetting);
        } else {
            recordID = initialRecordID();
        }
        return recordID;
    }
    public static void setRecordID(int recordID) {
        if (recordID >= initialRecordID()) {
            Settings.setProperty("RecordID", String.valueOf(recordID));
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
        return httpClient;
    }
    public static void setHttpClient(Client client) {
        httpClient = client;
    }
    
    private static EventList gatewayList;  
    private static Preferences gatewayPreferences;
    private static EventList clientList;
    private static Preferences clientPreferences;
    private static EventList serviceList;
    private static Preferences servicePreferences;
    private static int recordID;
    private static Client httpClient;
}
