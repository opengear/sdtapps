/*
 * Host.java
 *
 * Created on January 18, 2006, 4:02 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sdtconnector;

/**
 *
 * @author wayne
 */
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
    private String address = "";
    private String description = "";
}
