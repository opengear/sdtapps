/*
 * SDTManager.java
 *
 * Created on January 18, 2006, 4:19 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sdtconnector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author wayne
 */
public class SDTManager {
    static {
        gateways = new HashMap<String, Gateway>();
        Gateway gw = new Gateway("cm4008.opengear.com", "test user", "password",
                "Some gateway or other");
        gw.addHost(new Host("www.example.com", "Some description"));
        addGateway(gw);
        gw = new Gateway("cm4116.example.com", "root", "default",
                "CM4116 at i.lab somewhere or other");
        addGateway(gw);
    }
    /**
     * Creates a new instance of SDTManager
     */
    private SDTManager() {
    }
    public static Gateway getGateway(String address) {
        return gateways.get(address);
    }
    public static void addGateway(Gateway gw) {
        gateways.put(gw.getAddress(), gw);
    }
    public static void removeGateway(String address) {
        gateways.remove(address);
    }
    public static List<Gateway> getGatewayList() {
        return new ArrayList<Gateway>(gateways.values());
    }
    public static List<Host> getHostList(String gwAddress) {
        return getGateway(gwAddress).getHostList();
    }
    public static Host getHost(String gateway, String address) {
        return getGateway(gateway).getHost(address);
    }
    private static Map<String, Gateway> gateways;
}
