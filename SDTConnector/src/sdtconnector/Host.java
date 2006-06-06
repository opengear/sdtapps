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
        this.recordID = SDTManager.nextRecordID();
    }
    public Host(int recordID, String address, String description) {
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
    public void removeService(Service service) {
        serviceList.remove(service);
        /*
        for (ListIterator it = serviceList.listIterator(); it.hasNext(); ) {
            if (((Service) it.next()).equals(service)) {
                it.remove();
                break;
            }
        } 
         */      
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
    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public String toString() {
        return address;
    }
    public boolean equals(Object obj) {
        return (obj != null && recordID == ((Host) obj).recordID);
    }

    private EventList serviceList = new BasicEventList();
    private int recordID;
    private String address = "";
    private String description = "";
    private int _hashCode = 0;
}
