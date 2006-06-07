/*
 * HTTPSBrowser.java
 *
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sdtconnector;


public class HTTPSBrowser extends Browser {
    
    public HTTPSBrowser() {
        super(2, "Default HTTPS browser");
        this.protocol = "https";
    }
}


