/*
 * Settings.java
 *
 */

package sdtconnector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public class Settings {
    static {
        settings = Preferences.userRoot().node("opengear/sdtconnector/settings");
    }
    /** Creates a new instance of Settings */
    public Settings() {
    }
    public static String getProperty(String id) {
        return settings.get(id, "");
    }
    public static void setProperty(String id, String value) {
        settings.put(id, value);
    }
    public static Collection getPropertyList(String id) {
        Preferences list = settings.node(id);
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
    public static void setPropertyList(String id, Collection l) {
        int count = 0;
        Preferences list = settings.node(id);
        try {
            list.clear();
            for (Iterator i = l.iterator(); i.hasNext(); ++count) {
                Object o = i.next();
                list.put(String.valueOf(count), o.toString());
            }
            list.flush();
        } catch (BackingStoreException ex) {
            ex.printStackTrace();
        }        
    }
    public static String[] keys() {
        try {
            return settings.keys();
        } catch (BackingStoreException ex) {
            return new String[] { };
        }
    }
    static Preferences settings;
}
