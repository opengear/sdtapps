/*
 * SSH.java
 *
 */

package sdtconnector;

import com.jgoodies.looks.LookUtils;


public class SSH extends Client {
    
    /** Creates a new instance of SSH */
    public SSH() {
        super(500, LookUtils.IS_OS_WINDOWS ? "Putty SSH client" : "SSH client");
        if (LookUtils.IS_OS_WINDOWS == false) {
            setPath("ssh");
        }
    }
    public String getCommand(String host, int port) {
        if (LookUtils.IS_OS_WINDOWS) {
            return getPath() + " -ssh -P " + port + " " + host;
        } else {
            return "xterm -e " + getPath() + " -p " + port + " " + host;
        }
    }
    public String getIconName() {
        return "telnet";
    }    
}
