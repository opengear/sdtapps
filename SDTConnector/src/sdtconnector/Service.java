/*
 * Service.java
 *
 */

package sdtconnector;

import java.util.ArrayList;
import java.util.List;


public class Service {
    
    /** Creates a new instance of Service */
    public Service() {
        recordID = SDTManager.nextUserRecordID();
    }
    public Service(int recordID, String name) {
        this.recordID = recordID;
        this.name = name;
    }
    public Service(int recordID, String name, Launcher launcher) {
        this.recordID = recordID;
        this.name = name;
        launcherList.add(launcher);
    }
    public Service(int recordID, String name, Launcher launcher, boolean editable) {
        this.recordID = recordID;
        this.name = name;
        launcherList.add(launcher);
        this.editable = editable;
    }
    public void addLauncher(Launcher launcher) {
        launcherList.add(launcher);
    }
    public Launcher getLauncher() {
        if (launcherList.isEmpty()) {
            return null;
        }
        return launcherList.get(0);
    }
    public List<Launcher> getLaunchers() {
        return launcherList;
    }
    public int getRecordID() {
        return recordID;
    }
    public void setRecordID(int recordID) {
        this.recordID = recordID;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setEditable(boolean editable) {
        this.editable = editable;
    }
    public boolean isEditable() {
        return editable;
    }
    public String toString() {
        return name;
    }
    
    private int recordID;
    private String name = "";
    private List<Launcher> launcherList = new ArrayList<Launcher>(2);
    private boolean editable = true;
}
    