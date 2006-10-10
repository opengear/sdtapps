/*
 * Service.java
 *
 */

package sdtconnector;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import java.util.ArrayList;
import java.util.List;


public class Service {
    
    /** Creates a new instance of Service */
    public Service() {
        launcherList = new BasicEventList();
        launcherList.clear();  
        recordID = SDTManager.nextRecordID();
    }
    public Service(int recordID, String name, String icon) {
        this.recordID = recordID;
        this.name = name;
        launcherList = new BasicEventList();
        launcherList.clear();
        this.icon = icon;
    }
    public Service(int recordID, String name, Launcher launcher) {
        this.recordID = recordID;
        this.name = name;
        launcherList = new BasicEventList();
        launcherList.clear();        
        launcherList.add(launcher);
    }
    public Service(int recordID, String name, Launcher launcher, String icon) {
        this.recordID = recordID;
        this.name = name;
        launcherList = new BasicEventList();
        launcherList.clear();
        launcherList.add(launcher);
        this.icon = icon;
    }
    public void addLauncher(Launcher launcher) {
        launcherList.add(launcher);
    }
    public void removeLauncher(Launcher launcher) {
        launcherList.remove(launcher);
    }
    public Launcher getFirstLauncher() {
        if (launcherList.isEmpty()) {
            return null;
        }
        return (Launcher) launcherList.get(0);
    }
    public EventList getLauncherList() {
        return launcherList;
    }
    public void setLaunchers(EventList launcherList) {
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
    public String getIcon() {
        return icon;
    }
    public void setIcon(String icon) {
        this.icon = icon;
    }
    public boolean equals(Object obj) {
        return (obj != null && recordID == ((Service) obj).getRecordID());
    }
    public String toString() {
        return name;
    }
    
    private int recordID;
    private String name = "";
    private EventList launcherList;
    private String icon = "service";
}
    