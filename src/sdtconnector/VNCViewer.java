/*
 * VNCViewer.java
 *
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sdtconnector;

import java.io.IOException;
import java.util.prefs.Preferences;
import javax.swing.JOptionPane;

/**
 *
 */
public class VNCViewer {
    
    /** Creates a new instance of VNCViewer */
    public VNCViewer() {
    }
    public static void launch(String host, int port) {
        try {
            Runtime.getRuntime().exec("C:\\program files\\ultravnc\\vncviewer.exe" + 
                    " " + host + ":" + port);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
    }
}
