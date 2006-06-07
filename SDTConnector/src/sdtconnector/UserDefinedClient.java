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
    public UserDefinedClient(int recordID, String name, String command) {
        super(recordID, name);
        this.command = command;
    }
    public void setCommand(String command) {
        this.command = command;
    }
    public String getCommand(String host, int port) {
        // FIXME: put host and port into command
        return command;
    }
    public String getIconName() {
        return "service";
    }    

    private String command = "";
}
