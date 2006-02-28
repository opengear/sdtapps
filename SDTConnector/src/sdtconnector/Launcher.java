/*
 * Launcher.java
 *
 *
 */

package sdtconnector;

import java.io.IOException;


public abstract class Launcher implements Runnable {
    
    /** Creates a new instance of Launcher */
    public Launcher() {
        host = "";
        port = 0;
    }
    public Launcher(String host, int port) {
        this.host = host;
        this.port = port;
    }
    public void setHost(String host) {
        this.host = host;
    }
    
    public String getHost() {
        return host;
    }
    
    public void setPort(int port) {
        this.port = port;
    }
    
    public int getPort() {
        return port;
    }
    public void run() {
        launch();
    }
    public boolean launch() {
        try {
            Runtime.getRuntime().exec(getCommand());            
            return true;
        } catch (IOException ex) {
            return false;
        }        
    }
    public abstract String getCommand();
    
    protected String host;
    protected int port;
    
}
