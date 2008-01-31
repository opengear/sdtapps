/*
 * Client.java
 *
 */

package sdtconnector;

import org.apache.commons.lang.StringUtils;


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
    public String[] getCommand(String host, int port) {
        int i;
        String[] command;
        
        command = commandFormat.trim().split("\\s+");
        for (i = 0; i < command.length; i++) {
            command[i] = command[i].replaceAll("%path%", path);
            command[i] = command[i].replaceAll("%host%", host);
            command[i] = command[i].replaceAll("%port%", String.valueOf(port));
            if (SDTConnector.DEBUG == true) {
                System.out.println("command[" + i + "] " + command[i]);
            }
        }
        
        return command;
    }
    
    private int recordID;
    private String name = "";
    private String path = "";
    private String commandFormat = "";
}