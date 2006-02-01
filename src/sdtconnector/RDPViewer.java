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
    public void launch(String host, int port) {
        String rdppath = Settings.getProperty("rdp.path");
        String cmd = rdppath + " " + host + ":" + port;
                
        try {
            Runtime.getRuntime().exec(cmd);
        } catch (IOException ex) { }        
    }
}
