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
import org.apache.commons.lang.StringUtils;
import org.jdesktop.swingx.util.OS;

public class Gateway {
    
    /**
     * Creates a new instance of Gateway
     */
    public Gateway() {
        recordID = SDTManager.nextRecordID();
    }
    public Gateway(int recordID, String name, String address, String username,
            String password, String description, String oobAddress,
            String oobStart, String oobStop, String udpgwStartFormat,
            String udpgwStopFormat, String udpgwPidRegex) {
        this.recordID = recordID;
        this.name = name;
        this.address = address;
        this.username = username;
        this.password = password;
        this.description = description;
        if (oobAddress != null) {
            this.oobAddress = oobAddress;
        }
        if (oobStart != null) {
            this.oobStart = oobStart;
        } else {
            if (OS.isWindows()) {
                this.oobStart = "cmd /c start \"Starting Out of Band Connection\" /wait /min rasdial OOB login password";
            } else {
                this.oobStart = "pon OOB";
            }
        }
        if (oobStop != null) {
            this.oobStop = oobStop;
        } else {
            if (OS.isWindows()) {
                this.oobStop = "cmd /c start \"Starting Out of Band Connection\" /wait /min rasdial network_connection login password";
            } else {
                this.oobStop = "poff OOB";
            }
        }
        if (udpgwStartFormat != null) {
            this.udpgwStartFormat = udpgwStartFormat;
        }
        if (udpgwStopFormat != null) {
            this.udpgwStopFormat = udpgwStopFormat;
        }
        if (udpgwPidRegex != null) {
            this.udpgwPidRegex = udpgwPidRegex;
        }
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
	void setHostList(EventList hostList) {
		this.hostList = hostList;
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
    public void setUdpgwStartFormat(String udpgwStartFormat) {
        this.udpgwStartFormat = udpgwStartFormat;
    }
    public String getUdpgwStartFormat() {
        return udpgwStartFormat;
    }
    public String getUdpgwStartCommand(String host, int port, int uport) {
        String cmd = udpgwStartFormat;

        cmd = StringUtils.replace(cmd, "%udphost%", host);
        cmd = StringUtils.replace(cmd, "%port%", String.valueOf(port));
        cmd = StringUtils.replace(cmd, "%udpport%", String.valueOf(uport));
        return cmd;
    }
    public void setUdpgwStopFormat(String udpgwStopFormat) {
        this.udpgwStopFormat = udpgwStopFormat;
    }
    public String getUdpgwStopFormat() {
        return udpgwStopFormat;
    }
    public String getUdpgwStopCommand(String host, int port, int uport, int pid) {
        String cmd = udpgwStopFormat;
        
        cmd = StringUtils.replace(cmd, "%udphost%", host);
        cmd = StringUtils.replace(cmd, "%port%", String.valueOf(port));
        cmd = StringUtils.replace(cmd, "%udpport%", String.valueOf(uport));
        cmd = StringUtils.replace(cmd, "%pid%", String.valueOf(pid));
        return cmd;        
    }
    public void setUdpgwPidRegex(String udpgwPidRegex) {
        this.udpgwPidRegex = udpgwPidRegex;
    }
    public String getUdpgwPidRegex() {
        return udpgwPidRegex;
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
    
    private String udpgwStartFormat = "{ udpgw %port% %udphost% %udpport% & } &> /dev/null ; echo $!";
    private String udpgwStopFormat = "kill %pid%";
    private String udpgwPidRegex = "[0-9]+";

    private int _hashCode = 0;
}
