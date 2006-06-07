/*
 * VNCViewer.java
 *
 */

package sdtconnector;

import org.jdesktop.swingx.util.OS;


public class VNCViewer extends Client {
    
    /** Creates a new instance of VNCViewer */
    public VNCViewer() {
        super(501, "VNC viewer");
    }
    public String getCommand(String host, int port) {
        if (OS.isWindows()) {
            return getPath() + " /nostatus " + host + "::" + port;
        } else {
            return getPath() + " " + host + ":" + port;
        }
    }
    public String getIconName() {
        return "vnc";
    }    
}
