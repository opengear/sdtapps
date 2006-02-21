/*
 * RDPViewer.java
 *
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sdtconnector;

import java.io.IOException;
import org.jdesktop.swingx.util.OS;

/**
 *
 */
public class RDPViewer extends Launcher {
    
    /**
     * Creates a new instance of RDPViewer
     */
    public RDPViewer() {
    }
    public String getCommand() {
        String rdppath = Settings.getProperty("rdp.path");
        if (OS.isWindows()) {
            return rdppath + " /console /v:" + host + ":" + port;
        } else {
            return rdppath + " " + host + ":" + port;
        }
    }
}
