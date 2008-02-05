/*
 * Host.java
 *
 * Created on January 18, 2006, 4:02 PM
 */

package sdtconnector;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import java.util.ListIterator;


public class Host {
    
    /** Creates a new instance of Host */
    public Host() {
        recordID = SDTManager.nextRecordID();
    }
    public Host(int recordID, String name, String address, String description) {
        this.name = name;
        this.recordID = recordID;
        this.address = address;
        this.description = description;
    }
    public void clearServiceList() {
        serviceList.clear();
    }
    public EventList getServiceList() {
        return serviceList;
    }
    public void addService(Service service) {
        serviceList.add(service);
    }
    public void addService(int recordID) {
        for (Object o : SDTManager.getServiceList()) {
            Service service = (Service) o;
            if (service.getRecordID() == recordID) {
                serviceList.add(service);
                break;
            }
        }        
    }
    public void addService(String name) {
        for (Object o : SDTManager.getServiceList()) {
            Service service = (Service) o;
            if (service.getName().equals(name)) {
                serviceList.add(service);
                break;
            }
        }        
    }
    public void removeService(Service service) {
        serviceList.remove(service);
    }
    public boolean hasService(Service service) {
        for (Object o : serviceList) {
            Service s = (Service) o;
            if (s.equals(service)) {
                return true;
            }
        }
        return false;
    }
    public int getRecordID() {
        return recordID;
    }
    public void setRecordID(int recordID) {
        this.recordID = recordID;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getDescription() {
        return description;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public Service getServiceByName(String name) {
        Service service;
        
        for (Object s : serviceList) {
            service = (Service) s;
            if (service.getName().equalsIgnoreCase(name)) {
                return service;
            }
        }
        return null;
    }
    public Service getServiceByPort(int remotePort, int udpPort) {
        Service service;
        Launcher launcher;
		
        for (Object s : serviceList) {
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
    public String toString() {
        if (name.equals("")) {
            return address;
        }
        return name;
    }
    public boolean equals(Object obj) {
        return (obj != null && recordID == ((Host) obj).recordID);
    }

    private EventList serviceList = new BasicEventList();
    private String name = "";
    private String address = "";
    private String description = "";
    private int _hashCode = 0;
    private int recordID;
}
