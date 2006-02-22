/*
 * ImageLoader.java
 *
 * Created on February 17, 2006, 9:18 PM
 *
 */

package com.opengear.util;

import java.awt.Image;
import java.awt.Toolkit;
import java.net.URL;
import javax.swing.ImageIcon;


public class ImageLoader {
    
    /** Creates a new instance of ImageLoader */
    private ImageLoader() {
    }
    public static Image getImage(String path) {
        URL url = ImageLoader.class.getResource("/images/" + path);
        if (url != null) {
            return new ImageIcon(url).getImage();
        } else {
            return Toolkit.getDefaultToolkit().getImage("images/" + path);
        }
    }
}
