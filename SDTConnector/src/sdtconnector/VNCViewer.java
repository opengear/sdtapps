/*
 * VNCViewer.java
 *
 */

package sdtconnector;

import java.io.IOException;
import java.util.prefs.Preferences;
import javax.swing.JOptionPane;
import org.jdesktop.swingx.util.OS;

public class VNCViewer extends Launcher {
    
    /** Creates a new instance of VNCViewer */
    public VNCViewer() {
    }
    public VNCViewer(String host, int port) {
        super(host, port);
    }
    public String getCommand() {
        String vncpath = Settings.getProperty("vnc.path");
        if (OS.isWindows()) {
            return vncpath + " /nostatus " + host + "::" + port;
        } else {
            return vncpath + " " + host + ":" + port;
        }
    }   
}
