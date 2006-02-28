/*
 * Host.java
 *
 * Created on January 18, 2006, 4:02 PM
 */

package sdtconnector;


public class Host {
    
    /** Creates a new instance of Host */
    public Host() {
    }
    public Host(String address, String description) {
        this.address = address;
        this.description = description;
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
        return address.equals(((Host) obj).getAddress());
    }
    public int hashCode() {
        if (_hashCode == 0) {
            _hashCode = address.hashCode();
        }
        return _hashCode;
    }
    private String address = "";
    private String description = "";
    public boolean telnet = false;
    public boolean www = false;
    public boolean vnc = false;
    public boolean rdp = false;
    private int _hashCode = 0;
}
