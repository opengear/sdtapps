/*
 * UserDefinedClient.java
 *
 */

package sdtconnector;


public class UserDefinedClient extends Client {
    
    /**
     * Creates a new instance of UserDefinedClient
     */
    public UserDefinedClient () {
    }
    public UserDefinedClient (int recordID, String name, String path) {
        super(recordID, name, path);
    }
    public String getCommand(String host, int port) {
        String command = getPath().replaceAll("%host%", host);
        return command.replaceAll("%port%", String.valueOf(port));
    }
    public String getIconName() {
        return "service";
    }    
}
