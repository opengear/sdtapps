/*
 * IconLoader.java
 *
 * Created on February 7, 2006, 3:41 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sdtconnector;

import java.awt.Toolkit;
import java.net.URL;
import javax.swing.ImageIcon;

/**
 *
 * @author wayne
 */
public class IconLoader {
    
    /** Creates a new instance of IconLoader */
    private IconLoader() {
    }
    public static ImageIcon getIcon(String path) {
        URL url = IconLoader.class.getResource("/images/" + path);
        if (url != null) {
            return new ImageIcon(url);
        } else {
            return new ImageIcon(Toolkit.getDefaultToolkit().getImage("images/" + path));
        }
    }
    public static ImageIcon getMenuIcon(String name) {
        return getIcon("16x16/" + name + ".png");
    }
    public static ImageIcon getButtonIcon(String name) {
        return getIcon("16x16/" + name + ".png");
    }
    public static ImageIcon getToolbarIcon(String name) {
        return getIcon("22x22/" + name + ".png");
    }
}
