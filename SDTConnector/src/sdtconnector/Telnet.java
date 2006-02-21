/*
 * Telnet.java
 *
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sdtconnector;

import com.jgoodies.looks.LookUtils;
import java.io.IOException;
import org.omg.SendingContext.RunTime;

/**
 *
 */
public class Telnet extends Launcher {
    
    /** Creates a new instance of Telnet */
    public Telnet() {        
    }
    public Telnet(String host, int port) {
        super(host, port);
    }
    public String getCommand() {
        if (LookUtils.IS_OS_WINDOWS) {
            return "cmd /c start telnet " + host + " " + port;            
        } else {
            return "xterm -e telnet " + host + " " + port;
        }
    }
}
