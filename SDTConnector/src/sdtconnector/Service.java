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
        this.recordID = SDTManager.nextRecordID();
        // FIXME: don't create dummy launcher
        this.launcher = new Launcher();
    }
    public Service(int recordID, String name) {
        this.recordID = recordID;
        this.name = name;
    }
    public Service(int recordID, String name, Launcher launcher) {
        this.recordID = recordID;
        this.name = name;
        this.launcher = launcher;
    }
    public void addLauncher(Launcher launcher) {
        this.launcher = launcher;
        //launcherList.add(launcher);
    }
    public Launcher getLauncher() {
        return launcher;
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
    public String toString() {
        return name;
    }
    
    private int recordID;
    private String name;
    // TODO: many launchers per service
    // private List<Launcher> launcherList = new ArrayList<Launcher>(2);
    private Launcher launcher;
}
    