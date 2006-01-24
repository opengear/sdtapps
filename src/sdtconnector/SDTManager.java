/*
 * SDTManager.java
 *
 * Created on January 18, 2006, 4:19 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sdtconnector;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import java.util.ListIterator;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 *
 * @author wayne
 */
public class SDTManager {
    static {
        init();
    }
    private static void init() {
        Gateway gw;
        gatewayList = new BasicEventList<Gateway>();
        preferences = Preferences.userRoot().node("Opengear/SDTConnector/gateways");
        try {
            for (String id : preferences.childrenNames()) {
                Preferences child = preferences.node(id);
                String address = child.get("address", "");
                String description = child.get("description", "");
                String username = child.get("username", "");
                String password = child.get("password", "");
                gw = new Gateway(address, username, password, description);
                gatewayList.add(gw);
                Preferences hosts = child.node("hosts");
                for (String hostID : hosts.childrenNames()) {
                    Preferences host = hosts.node(hostID);
                    String hostAddress = host.get("address", "");
                    String hostDescription = host.get("description", "");
                    gw.addHost(new Host(hostAddress, hostDescription));
                }
            }
        } catch (BackingStoreException ex) {
            ex.printStackTrace();
        }
        if (false) {
            gw = new Gateway("cm4008.opengear.com", "test user", "password",
                    "Some gateway or other");
            gw.addHost(new Host("www.example.com", "Some description"));
            addGateway(gw);
            gw = new Gateway("cm4116.example.com", "root", "default",
                    "CM4116 at i.lab somewhere or other");
            addGateway(gw);
            gw = new Gateway("192.168.99.11", "root", "default",
                    "CM4148 prototype");
            addGateway(gw);
            gw.addHost(new Host("192.168.99.2", "Web server"));
        }
        
    }
    /**
     * Creates a new instance of SDTManager
     */
    private SDTManager() {
    }
    public static Gateway getGateway(String address) {
        for (Gateway gw : gatewayList) {
            if (gw.getAddress().equals(address)) {
                return gw;
            }
        }
        return null;
    }
    public static void addGateway(Gateway gw) {
        gatewayList.add(gw);
        saveGateway(gw);
    }
    public static void removeGateway(String address) {
        
        for (ListIterator i = gatewayList.listIterator(); i.hasNext(); ) {
            Gateway gw = (Gateway) i.next();
            if (gw.getAddress().equals(address)) {
                i.remove();
            }
        }
        try {
            preferences.node(address).removeNode();
        } catch (BackingStoreException ex) {
            ex.printStackTrace();
        }
    }
    public static void updateGateway(Gateway gw, String oldAddress) {
        if (!oldAddress.equals(gw.getAddress())) {
            try {
                preferences.node(oldAddress).removeNode();
            } catch (BackingStoreException ex) {}
        }
        saveGateway(gw);
    }
    private static void saveGateway(Gateway gw) {
        Preferences gwPrefs = preferences.node(gw.getAddress());
        gwPrefs.put("address", gw.getAddress());
        gwPrefs.put("description", gw.getDescription());
        gwPrefs.put("username", gw.getUsername());
        gwPrefs.put("password", gw.getPassword());
    }
    public static EventList<Gateway> getGatewayList() {
        return gatewayList;
    }
    private static void saveHost(Preferences gwPrefs, Host host) {
        Preferences hostPrefs = gwPrefs.node("hosts/" + host.getAddress());
        hostPrefs.put("address", host.getAddress());
        System.out.println("Saving description " + host.getDescription());
        hostPrefs.put("description", host.getDescription());
    }
    public static void addHost(Gateway gw, Host host) {
        gw.addHost(host);
        Preferences gwPrefs = preferences.node(gw.getAddress());
        saveHost(gwPrefs, host);
    }
    public static void removeHost(Gateway gw, Host host) {
        gw.removeHost(host.getAddress());
        
        Preferences gwPrefs = preferences.node(gw.getAddress());
        try {
            gwPrefs.node("hosts/" + host.getAddress()).removeNode();
        } catch (BackingStoreException ex) {}
    }
    public static void updateHost(Gateway gw, Host host, String oldAddress) {
        System.out.println("Updating host " + host);
        Preferences gwPrefs = preferences.node(gw.getAddress());
        
        if (!oldAddress.equals(host.getAddress())) {
            try {
                gwPrefs.node("hosts/" + oldAddress).removeNode();
            } catch (BackingStoreException ex) { }
        }
        
        saveHost(gwPrefs, host);
    }
    
    public static EventList<Host> getHostList(String gwAddress) {
        return getGateway(gwAddress).getHostList();
    }
    public static Host getHost(String gateway, String address) {
        return getGateway(gateway).getHost(address);
    }
    private static EventList<Gateway> gatewayList;
    private static Preferences preferences;
}
