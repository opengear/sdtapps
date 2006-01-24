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
import java.net.URL;

/**
 *
 */
public class Browser {
    
    /** Creates a new instance of Browser */
    public Browser() {
    }
    public static void displayURL(URL url) throws IOException {
        String browser = "firefox";
        if (LookUtils.IS_OS_WINDOWS) {
            browser = "rundll32 url.dll,FileProtocolHandler";
        }
        Runtime.getRuntime().exec(browser + " " + url.toString());
    }
}
