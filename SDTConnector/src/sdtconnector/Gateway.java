/*
 * Gateway.java
 *
 * Created on January 18, 2006, 3:41 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sdtconnector;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;


public class Gateway {
    
    /**
     * Creates a new instance of Gateway
     */
    public Gateway() {
    }
    public Gateway(String address, String username, String password,
            String description) {
        this.address = address;
        this.username = username;
        this.password = password;
        this.description = description;
    }
    
    
    public String getAddress() {
        return address;
    }
    public int getPort() {
        return port;
    }
    public void setPort(int port) {
        this.port = port;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public EventList getHostList() {
        return hostList;
    }
    public void addHost(Host host) {
        hostList.add(host);
    }
    public int hashCode() {
        if (_hashCode == 0) {
            _hashCode = address.hashCode();
        }
        return _hashCode;
    }
    public void removeHost(String address) {
        ListIterator it;
        for (it = hostList.listIterator(); it.hasNext(); ) {
            Host host = (Host) it.next();
            if (host.getAddress().equals(address)) {
                it.remove();
                break;
            }
        }       
    }
    public Host getHost(String address) {
        for (Iterator i = hostList.iterator(); i.hasNext(); ) {
            Host host = (Host) i.next();
            if (host.getAddress().equals(address)) {
                return host;
            }
        }
        return null;
    }
    public String toString() {
        return address;
    }
    
    public boolean equals(Object obj) {
        return getAddress().equals(((Gateway) obj).getAddress());
    }
    
    // Variables
    private String address = "";
    private int port = 22;
    private String username = "";
    private String password = "";
    private String description = "";
    private EventList hostList = new BasicEventList();

    private int _hashCode = 0;
}
