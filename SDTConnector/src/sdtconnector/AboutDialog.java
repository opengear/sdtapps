/*
 * AboutDialog.java
 *
 * Created on January 28, 2006, 6:58 PM
 */

package sdtconnector;

import com.opengear.util.IconLoader;
import com.opengear.util.ImageLoader;
import java.awt.Image;
import java.net.URL;
import javax.swing.ImageIcon;
import javax.swing.event.HyperlinkEvent;
import javax.swing.text.html.HTMLEditorKit;


public class AboutDialog extends javax.swing.JDialog {
    
    /**
     * Creates new form AboutDialog
     */
    public AboutDialog(java.awt.Frame parent, boolean modal, String version) {
        super(parent, modal);
        initComponents();
        imagePanel.setImage(ImageLoader.getImage("opengear.gif"));
        if (SDTConnector.DEBUG == true) {
            versionField.setText(version + " DEBUG");
        } else {
            versionField.setText(version);
        }
        textPane.setEditorKit(new HTMLEditorKit());
        textPane.setText("<html><body><center>"
                + "<font face=\"Verdana,Helvetica,Arial\">"
                + "Copyright (c) 2007 <a href=\"http://www.opengear.com\">Opengear</a>"
                + "</font></center>" 
                + "</body></html>");
        closeButton.setIcon(IconLoader.getButtonIcon("ok"));
        pack();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        javax.swing.JScrollPane jScrollPane1;
        org.jdesktop.swingx.JXPanel jXPanel1;
        org.jdesktop.swingx.JXPanel jXPanel5;

        jScrollPane1 = new javax.swing.JScrollPane();
        textArea = new javax.swing.JTextArea();
        jXPanel1 = new org.jdesktop.swingx.JXPanel();
        jXPanel5 = new org.jdesktop.swingx.JXPanel();
        closeButton = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        textPane = new javax.swing.JTextPane();
        versionLabel = new javax.swing.JLabel();
        versionField = new javax.swing.JLabel();
        imagePanel = new org.jdesktop.swingx.JXImagePanel();

        jScrollPane1.setBorder(null);
        textArea.setColumns(20);
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setRows(5);
        textArea.setText("\nCopyright 2007 Opengear");
        textArea.setWrapStyleWord(true);
        textArea.setAutoscrolls(false);
        textArea.setFocusable(false);
        textArea.setOpaque(false);
        textArea.setRequestFocusEnabled(false);
        jScrollPane1.setViewportView(textArea);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);
        jXPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jXPanel5.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        closeButton.setText("Close");
        closeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeButtonAction(evt);
            }
        });

        jScrollPane2.setBorder(null);
        jScrollPane2.setOpaque(false);
        jScrollPane2.setRequestFocusEnabled(false);
        textPane.setBorder(null);
        textPane.setEditable(false);
        textPane.setOpaque(false);
        textPane.addHyperlinkListener(new javax.swing.event.HyperlinkListener() {
            public void hyperlinkUpdate(javax.swing.event.HyperlinkEvent evt) {
                hyperlinkEvent(evt);
            }
        });

        jScrollPane2.setViewportView(textPane);

        versionLabel.setText("Version:");

        versionField.setText("0.0");

        org.jdesktop.layout.GroupLayout jXPanel5Layout = new org.jdesktop.layout.GroupLayout(jXPanel5);
        jXPanel5.setLayout(jXPanel5Layout);
        jXPanel5Layout.setHorizontalGroup(
            jXPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jXPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .add(jXPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jXPanel5Layout.createSequentialGroup()
                        .add(versionLabel)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(versionField)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 171, Short.MAX_VALUE)
                        .add(closeButton))
                    .add(jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 315, Short.MAX_VALUE))
                .addContainerGap())
        );
        jXPanel5Layout.setVerticalGroup(
            jXPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jXPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .add(jScrollPane2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 89, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 14, Short.MAX_VALUE)
                .add(jXPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(versionLabel)
                    .add(versionField)
                    .add(closeButton))
                .addContainerGap())
        );

        imagePanel.setBackground(new java.awt.Color(255, 255, 255));
        imagePanel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                imagePanelMouseClicked(evt);
            }
        });

        org.jdesktop.layout.GroupLayout imagePanelLayout = new org.jdesktop.layout.GroupLayout(imagePanel);
        imagePanel.setLayout(imagePanelLayout);
        imagePanelLayout.setHorizontalGroup(
            imagePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 343, Short.MAX_VALUE)
        );
        imagePanelLayout.setVerticalGroup(
            imagePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 67, Short.MAX_VALUE)
        );

        org.jdesktop.layout.GroupLayout jXPanel1Layout = new org.jdesktop.layout.GroupLayout(jXPanel1);
        jXPanel1.setLayout(jXPanel1Layout);
        jXPanel1Layout.setHorizontalGroup(
            jXPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(imagePanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jXPanel5, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jXPanel1Layout.setVerticalGroup(
            jXPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jXPanel1Layout.createSequentialGroup()
                .add(imagePanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jXPanel5, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jXPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jXPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
        );
        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void imagePanelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_imagePanelMouseClicked
        Launcher launcher = new Launcher(0, "www.opengear.com", 80, 0, 0, SDTManager.getHttpClient());
        launcher.launch();
    }//GEN-LAST:event_imagePanelMouseClicked
    
    private void hyperlinkEvent(javax.swing.event.HyperlinkEvent evt) {//GEN-FIRST:event_hyperlinkEvent
        if (evt.getEventType() != HyperlinkEvent.EventType.ACTIVATED) {
            return;
        }
        URL url = evt.getURL();  
        Launcher launcher = new Launcher(0, "www.opengear.com", 80, 0, 0, SDTManager.getHttpClient());
        launcher.launch();
    }//GEN-LAST:event_hyperlinkEvent
    
    private void closeButtonAction(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeButtonAction
        setVisible(false);
        dispose();
    }//GEN-LAST:event_closeButtonAction
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new AboutDialog(new javax.swing.JFrame(), true, SDTConnector.VERSION).setVisible(true);
            }
        });
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton closeButton;
    private org.jdesktop.swingx.JXImagePanel imagePanel;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextArea textArea;
    private javax.swing.JTextPane textPane;
    private javax.swing.JLabel versionField;
    private javax.swing.JLabel versionLabel;
    // End of variables declaration//GEN-END:variables
    
}
