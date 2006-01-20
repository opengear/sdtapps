/*
 * Gateway.java
 *
 * Created on January 18, 2006, 3:41 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sdtconnector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author wayne
 */
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
    public List<Host> getHostList() {
        return new ArrayList<Host>(hosts.values());
    }
    public void addHost(Host host) {
        hosts.put(host.getAddress(), host);
    }
    public void removeHost(String address) {
        hosts.remove(address);
    }
    public Host getHost(String address) {
        return hosts.get(address);
    }   
    public String toString() {
        return address;
    }
    
    // Variables
    private String address = "";

    private String username = "";
    private String password = "";
    private String description = "";
    private Map<String, Host> hosts = new HashMap<String, Host>();
}
