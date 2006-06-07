/*
 * Telnet.java
 *
*/

package sdtconnector;

import com.jgoodies.looks.LookUtils;


public class Telnet extends Client {
    
    /** Creates a new instance of Telnet */
    public Telnet() {
        super(3, "Default Telnet client");
    }
    public String getCommand(String host, int port) {
        if (LookUtils.IS_OS_WINDOWS) {
            return "cmd /c start telnet " + host + " " + port;
        } else {
            return "xterm -e telnet " + host + " " + port;
        }
    }
    public String getIconName() {
        return "telnet";
    }    
}
