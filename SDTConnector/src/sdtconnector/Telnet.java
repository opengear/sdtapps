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
    public void launch(String host, int port) {
        String cmd = "";
        if (LookUtils.IS_OS_WINDOWS) {
            cmd = "cmd /c start telnet " + host + " " + port;            
        } else {
            cmd = "xterm -e telnet " + host + " " + port;
        }
        try {
            Runtime.getRuntime().exec(cmd);
        } catch (IOException ex) {}
    }
}
