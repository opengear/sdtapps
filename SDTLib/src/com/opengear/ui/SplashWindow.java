/*
 * SplashWindow.java
 *
 * Created on February 15, 2006, 3:20 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.opengear.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;
import java.net.URL;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;

/**
 *
 * @author wayne
 */
public class SplashWindow extends javax.swing.JWindow {
    
    /** Creates a new instance of SplashWindow */
    public SplashWindow(String fileName) {        
        super(new Frame());
        JPanel p = new JPanel();
        p.setLayout(new BorderLayout());
        
        JLabel l = new JLabel(getImage(fileName));
        p.add(l, BorderLayout.CENTER);
        //p.setBorder(new LineBorder(Color.BLACK));
        getContentPane().add(p, BorderLayout.CENTER);
        pack();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension labelSize = l.getPreferredSize();
        setLocation(screenSize.width / 2 - (labelSize.width / 2),
                    screenSize.height / 2 - (labelSize.height / 2));
        
    }
    public ImageIcon getImage(String path) {
        URL url = SplashWindow.class.getResource("/" + path);
        if (url != null) {
            return new ImageIcon(url);
        } else {
            return new ImageIcon(Toolkit.getDefaultToolkit().getImage(path));
        }
    }
}
