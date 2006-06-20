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
        recordID = SDTManager.nextRecordID();
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
    public Service(int recordID, String name, Launcher launcher, String iconName) {
        this.recordID = recordID;
        this.name = name;
        launcherList.add(launcher);
        this.iconName = iconName;
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
    public void setLaunchers(List<Launcher> launcherList) {
        this.launcherList = launcherList;
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
    public String getIconName() {
        return iconName;
    }
    public void setIconName(String iconName) {
        this.iconName = iconName;
    }
    public boolean equals(Object obj) {
        return (obj != null && recordID == ((Service) obj).getRecordID());
    }
    public String toString() {
        return name;
    }
    
    private int recordID;
    private String name = "";
    private List<Launcher> launcherList = new ArrayList<Launcher>(2);
    private String iconName = "service";
}
    