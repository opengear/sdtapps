/*
 * IconLoader.java
 *
 * Created on February 7, 2006, 3:41 PM
 */

package com.opengear.util;

import java.awt.Toolkit;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.UIManager;

public class IconLoader {
    
    /** Creates a new instance of IconLoader */
    private IconLoader() {
    }
    public static ImageIcon getIcon(String size, String name) {
        String path = "images/" + size + "/" + name + ".png";
        URL url = IconLoader.class.getResource("/" + path);
        if (url != null) {
            return new ImageIcon(url);
        } else {
            return new ImageIcon(Toolkit.getDefaultToolkit().getImage(path));
        }
    }
    private static Icon getIcon(GtkIcon.Size gtksize, String name) {
        if (isGTK()) {
            Icon ico = GtkIcon.getIcon(name, gtksize);
            if (ico != null) {
                return ico;
            }
        }
        switch (gtksize) {
            case MENU:
            case BUTTON:
            case SMALL_TOOLBAR:
                return getIcon("16x16", name);
            case LARGE_TOOLBAR:
                return getIcon("22x22", name);
            default:
                break;
        }
        return null;
    }
    public static Icon getMenuIcon(String name) {
        return getIcon(GtkIcon.Size.MENU, name);
    }
    public static Icon getButtonIcon(String name) {
        return getIcon(GtkIcon.Size.BUTTON, name);
        
    }
    public static Icon getToolbarIcon(String name) {
        return getIcon(GtkIcon.Size.LARGE_TOOLBAR, name);
    }
    public static Icon getLargeIcon(String name) {
        return getIcon("48x48", name);
    }
    private static boolean isGTK() {
        String lafName = UIManager.getLookAndFeel().getClass().getName();
        return lafName.equals("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
    }
    
}


class GtkIcon {
    /**
     * These are type-safe versions of the hard-coded numbers in GTKStyle, for
     * use with getGnomeStockIcon.
     */
    public enum Size {
        INVALID,
        MENU, // 16 x 16
        SMALL_TOOLBAR, // 18x18
        LARGE_TOOLBAR, // 24x24
        BUTTON, // 20x20
        DND, // 32x32
        DIALOG // 48x48
    }
    
    private GtkIcon() {
    }
    
    /**
     * Returns an Icon for one of the GNOME stock icons. If the icon is not
     * available for any reason, you'll get null. (Not using the GTK LAF is
     * one reason why.)
     * The GNOME header file listing the possible strings is here:
     * http://cvs.gnome.org/viewcvs/gtk%2B/gtk/gtkstock.h?view=markup
     */
    public static Icon getIcon(String name, Size size) {
        if (!gtkIcons.containsKey(name)) {
            return null;
        }
        name = "gtk-" + gtkIcons.get(name);
        try {
            Class gtkStockIconClass =
                    Class.forName("com.sun.java.swing.plaf.gtk.GTKStyle$GTKStockIcon");
            java.lang.reflect.Constructor constructor =
                    gtkStockIconClass.getDeclaredConstructor(String.class, int.class);
            constructor.setAccessible(true);
            return (Icon) constructor.newInstance(name, size.ordinal());
        } catch (Exception ex) {
            return null;
        }
    }
    static {
        Map<String, String> icons = new HashMap<String, String>();
        icons.put("ok", "ok");
        icons.put("cancel", "cancel");
        icons.put("info", "info");
        icons.put("edit", "edit");
        icons.put("delete", "delete");
        icons.put("fileopen", "open");
        icons.put("exit", "quit");
        icons.put("add", "add");
        icons.put("remove", "remove");
        gtkIcons = icons;
    }
    private static Map<String, String> gtkIcons;
}