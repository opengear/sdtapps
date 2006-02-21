/*
 * Browser.java
 *
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sdtconnector;

import com.jgoodies.looks.LookUtils;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 *
 */
public class Browser extends Launcher {
    
    /** Creates a new instance of Browser */
    public Browser() {}
    public Browser(String host, int port) {
        super(host, port);
    }
    public String getCommand() {
        try {
            
            URL url = new URL("http", host, port, "/");
            String browser = "firefox";
            if (LookUtils.IS_OS_WINDOWS) {
                browser = "rundll32 url.dll,FileProtocolHandler";
            }
            return browser + " " + url.toString();
        } catch (MalformedURLException ex) {
            return "";
        }
    }   
}
