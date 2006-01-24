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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

/**
 *
 * @author wayne
 */
public class SDTManager {
    static {
        gatewayList = new BasicEventList<Gateway>();
        Gateway gw = new Gateway("cm4008.opengear.com", "test user", "password",
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
    }
    public static void removeGateway(String address) {
        
        for (ListIterator i = gatewayList.listIterator(); i.hasNext(); ) {
            Gateway gw = (Gateway) i.next();
            if (gw.getAddress().equals(address)) {
                i.remove();
            }
        }
    }
    public static EventList<Gateway> getGatewayList() {
        return gatewayList;
    }
    public static EventList<Host> getHostList(String gwAddress) {
        return getGateway(gwAddress).getHostList();
    }
    public static Host getHost(String gateway, String address) {
        return getGateway(gateway).getHost(address);
    }
    private static EventList<Gateway> gatewayList;
}
