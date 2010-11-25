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
    public String getQuery() {
        return query;
    }
    public void setQuery(String query) {
        this.query = query;
    }
    public String toString() {
        return name;
    }
    public boolean equals(Object obj) {
        return (obj != null && recordID == ((Client) obj).getRecordID());
    }
    public String[] getCommand(String host, int port) {
            // Tokenise by space delimiter, allow backslash escaping
            String[] cmd = commandFormat.split("(?<!\\\\) ");
            for (int i = 0; i < cmd.length; i++) {
                cmd[i] = StringUtils.replace(cmd[i], "%path%", path);
                cmd[i] = StringUtils.replace(cmd[i], "%query%", query);
                cmd[i] = StringUtils.replace(cmd[i], "%host%", host);
                cmd[i] = StringUtils.replace(cmd[i], "%port%", String.valueOf(port));
            }
            return cmd;
    }
   
    private int recordID;
    private String name = "";
    private String path = "";
    private String commandFormat = "";
    
    // Ephemeral query string for command format
    private String query = "";
}
