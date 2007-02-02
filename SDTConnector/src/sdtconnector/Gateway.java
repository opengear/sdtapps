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
import java.io.IOException;


public class Gateway {
    
    /**
     * Creates a new instance of Gateway
     */
    public Gateway() {
        recordID = SDTManager.nextRecordID();
    }
    public Gateway(int recordID, String name, String address, String username,
            String password, String description, String oobAddress,
            String oobStart, String oobStop) {
        this.recordID = recordID;
        this.name = name;
        this.address = address;
        this.username = username;
        this.password = password;
        this.description = description;
        this.oobAddress = oobAddress;
        this.oobStart = oobStart;
        this.oobStop = oobStop;
    }
        
    public int getRecordID() {
        return recordID;
    }
    public void setRecordID(int recordID) {
        this.recordID = recordID;
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
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
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
    public void removeHost(Host host) {
        hostList.remove(host);
        /*
        for (ListIterator it = hostList.listIterator(); it.hasNext(); ) {
            if (((Host) it.next()).equals(host)) {
                it.remove();
                break;
            }
        } 
         */      
    }
    public Host getHost(int recordID) {
        for (Object o : hostList) {
            Host host = (Host) o;
            if (host.getRecordID() == recordID) {
                return host;
            }
        }
        return null;
    }
    public String toString() {
        if (name.equals("")) {
            return address;
        }
        return name;
    }
    public boolean equals(Object obj) {
        return (obj != null && recordID == ((Gateway) obj).getRecordID());
    }
    public String getActiveAddress() {
        if (oob) {
            return getOobAddress();
        }
        return address;
    }
    public int getActivePort() {
        if (oob) {
            return getOobPort();
        }
        return port;
    }
    public String getOobAddress() {
        return oobAddress;
    }
    public void setOobAddress(String oobAddress) {
        this.oobAddress = oobAddress;
    }
    public int getOobPort() {
        return oobPort;
    }
    public void setOobPort(int oobPort) {
        this.oobPort = oobPort;
    }
    public String getOobStart() {
        return oobStart;
    }
    public void setOobStart(String oobStart) {
        this.oobStart = oobStart;
    }
    public String getOobStop() {
        return oobStop;
    }
    public void setOobStop(String oobStop) {
        this.oobStop = oobStop;
    }
    public boolean getOob() {
        return oob;
    }
    public void setOob(boolean oob) {
        this.oob = oob;
    }
    
    // Variables
    private int recordID;
    private String name = "";
    private String address = "";
    private int port = 22;
    private String username = "";
    private String password = "";
    private String description = "";
    private EventList hostList = new BasicEventList();
        
    private String oobAddress = "";
    private int oobPort = 22;
    private String oobStart = "";
    private String oobStop = "";
    private boolean oob = false;
    
    private int _hashCode = 0;
}
