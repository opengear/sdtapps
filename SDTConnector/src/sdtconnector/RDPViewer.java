/*
 * RDPViewer.java
 *
 */

package sdtconnector;

import org.jdesktop.swingx.util.OS;


public class RDPViewer extends Client {
    
    /**
     * Creates a new instance of RDPViewer
     */
    public RDPViewer() {
        super(SDTManager.nextSystemRecordID(), "RDP viewer");
    }
    public String getCommand(String host, int port) {
        if (OS.isWindows()) {
            return getPath() + " /console /v:" + host + ":" + port;
        } else {
            return getPath() + " " + host + ":" + port;
        }
    }
    public String getIconName() {
        return "tsclient";
    }
}