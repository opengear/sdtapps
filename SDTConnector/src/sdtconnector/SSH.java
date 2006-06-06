/*
 * SSH.java
 *
 */

package sdtconnector;

import com.jgoodies.looks.LookUtils;


public class SSH extends Client {
    
    /** Creates a new instance of SSH */
    public SSH() {
        super(502, "SSH client");
    }
    public String getCommand(String host, int port) {
        if (LookUtils.IS_OS_WINDOWS) {
            return "FIXME " + host + " " + port;
        } else {
            return "FIXME " + host + " " + port;
        }
    }
}
