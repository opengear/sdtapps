/*
 * Client.java
 *
 */

package sdtconnector;


public class Client {
    
    /**
     * Creates a new instance of Client
     */
    public Client() {
        recordID = SDTManager.nextRecordID();
    }
    public Client(int recordID, String name) {
        this.recordID = recordID;
        this.name = name;
    }
    public Client(int recordID, String name, String path, String commandFormat) {
        this.recordID = recordID;
        this.name = name;
        this.path = path;
        this.commandFormat = commandFormat;
    }    
    public void setRecordID(int recordID) {
        this.recordID = recordID;
    }
    public int getRecordID() {
        return recordID;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }
    public void setPath(String path) {
        this.path = path;
    }
    public String getPath() {
        return path;
    }
    public void setCommandFormat(String commandFormat) {
        this.commandFormat = commandFormat;
    }
    public String getCommandFormat() {
        return commandFormat;
    }
    public String toString() {
        return name;
    }
    public boolean equals(Object obj) {
        return (obj != null && recordID == ((Client) obj).getRecordID());
    }
    public String getCommand(String host, int port) {
        String cmd = commandFormat.replaceAll("%path%", path);
        cmd = cmd.replaceAll("%host%", host);
        return cmd.replaceAll("%port%", String.valueOf(port));
    }
    
    private int recordID;
    private String name = "";
    private String path = "";
    private String commandFormat = "";
}