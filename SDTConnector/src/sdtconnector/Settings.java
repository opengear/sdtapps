/*
 * Settings.java
 *
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sdtconnector;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 *
 */
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
    public static String[] keys() {
        try {
            return settings.keys();
        } catch (BackingStoreException ex) {
            return new String[] { };
        }
    }
    static Preferences settings;
}
