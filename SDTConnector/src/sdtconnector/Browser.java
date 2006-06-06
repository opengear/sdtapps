/*
 * Browser.java
 *
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sdtconnector;

import com.jgoodies.looks.LookUtils;
import java.net.MalformedURLException;
import java.net.URL;


public class Browser extends Client {
    protected String protocol;

    public Browser() {
        super(2, "Default HTTP browser");
        this.protocol = "http";
    }
    public Browser(int recordID, String name) {
        super(recordID, name);
        this.protocol = "http";        
    }
    public String getCommand(String host, int port) {
        try {
            URL url = new URL(protocol, host, port, "/");
            String browser = "firefox";
            if (LookUtils.IS_OS_WINDOWS) {
                browser = "rundll32 url.dll,FileProtocolHandler";
            }
            return browser + " " + url.toString();
        } catch (MalformedURLException ex) {
            return "";
        }
    }
    // Built in clients can be uniquely identified by name
    public boolean equals(Object obj) {
        return (obj != null && getRecordID() == ((Client) obj).getRecordID());
    }
}

