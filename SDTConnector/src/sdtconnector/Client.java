/*
 * Client.java
 *
 */

package sdtconnector;


public abstract class Client {
    
    /**
     * Creates a new instance of Client
     */
    public Client() {
        this.recordID = SDTManager.nextRecordID();
    }
    public Client(int recordID, String name) {
        this.recordID = recordID;
        this.name = name;
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
    public String toString() {
        return name;
    }
    public boolean equals(Object obj) {
        return (obj != null && recordID == ((Client) obj).getRecordID());
    }
    public abstract String getCommand(String host, int port);

    private int recordID;
    private String name;
    private String path;
}