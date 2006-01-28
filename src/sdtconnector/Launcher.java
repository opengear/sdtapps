/*
 * Launcher.java
 *
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sdtconnector;

/**
 *
 */
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
        launch(host, port);
    }
    protected abstract void launch(String host, int port);
    
    protected String host;
    protected int port;
    
    
    
}
