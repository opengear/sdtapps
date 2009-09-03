/*
 * Settings.java
 *
 */

package sdtconnector;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public class Settings {
   
    /** Creates a new instance of Settings */
    public Settings() {
    }
    public static Preferences root() { 
        return Preferences.userRoot().node(path); 
    }
    public static String getProperty(String id) {
        return root().get(id, "");
    }
    public static void setProperty(String id, String value) {
        root().put(id, value);
    }
    public static void removeProperty(String id) {
        root().remove(id);
    }
    public static Collection<String> getPropertyList(Preferences list) {
        try {
            List<String> ret = new LinkedList<String>();
            for (String elemID : list.keys()) {
                String val = list.get(elemID, "");
                ret.add(val);
            }
            return ret;
        } catch (BackingStoreException ex) {
            return Collections.emptyList();
        }
    }
    public static void setPropertyList(Preferences list, Collection l) {
        int count = 0;
        try {
            list.clear();
            for (Iterator i = l.iterator(); i.hasNext(); ++count) {
                Object o = i.next();
                if (o instanceof Service) {
                    list.put(String.valueOf(count), String.valueOf(((Service) o).getRecordID()));
                } else {
                    list.put(String.valueOf(count), o.toString());
                }
            }
            list.flush();
        } catch (BackingStoreException ex) {
            ex.printStackTrace();
        }        
    }
    public static String[] keys() {
        try {
            return root().keys();
        } catch (BackingStoreException ex) {
            return new String[] { };
        }
    }
    static String path = SDTManager.prefsPath + "/settings";
}
