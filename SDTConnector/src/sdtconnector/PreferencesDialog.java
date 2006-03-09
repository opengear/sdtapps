/*
 * PreferencesDialog.java
 *
 * Created on January 25, 2006, 12:43 PM
 */

package sdtconnector;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import com.opengear.util.IconLoader;
import java.awt.Color;
import java.awt.Graphics;
import java.io.File;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import org.jdesktop.swingx.decorator.AlternateRowHighlighter;
import org.jdesktop.swingx.decorator.Highlighter;
import org.jdesktop.swingx.decorator.HighlighterPipeline;
import org.jdesktop.swingx.decorator.RolloverHighlighter;
import org.jdesktop.swingx.util.OS;

public class PreferencesDialog extends javax.swing.JDialog {
    /** A return status code - returned if Cancel button has been pressed */
    public static final int RET_CANCEL = 0;
    /** A return status code - returned if OK button has been pressed */
    public static final int RET_OK = 1;
    
    /** Creates new form PreferencesDialog */
    public PreferencesDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        rdpField.setText(Settings.getProperty("rdp.path"));
        vncField.setText(Settings.getProperty("vnc.path"));
        okButton.setIcon(IconLoader.getButtonIcon("ok"));
        cancelButton.setIcon(IconLoader.getButtonIcon("cancel"));
        addPrivateKeyButton.setIcon(IconLoader.getButtonIcon("add"));
        removePrivateKeyButton.setIcon(IconLoader.getButtonIcon("remove"));
        privateKeyList.addAll(Settings.getPropertyList("PrivateKeyPaths"));
        privateKeyJList.setModel(new ca.odell.glazedlists.swing.EventListModel(privateKeyList));
        privateKeyJList.setRolloverEnabled(true);
                
        privateKeyJList.setHighlighters(new HighlighterPipeline(new Highlighter[] {
            Highlighter.notePadBackground
        }));
        pack();
    }
    
    /** @return the return status of this dialog - one of RET_OK or RET_CANCEL */
    public int getReturnStatus() {
        return returnStatus;
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        okButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel3 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        rdpField = new javax.swing.JTextField();
        rdpBrowseButton = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        vncField = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        vncBrowseButton = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        addPrivateKeyButton = new javax.swing.JButton();
        removePrivateKeyButton = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        privateKeyJList = new org.jdesktop.swingx.JXList();

        setTitle("Preferences");
        setModal(true);
        addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                formKeyPressed(evt);
            }
        });
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                closeDialog(evt);
            }
        });

        okButton.setText("OK");
        okButton.setIconTextGap(6);
        okButton.setMargin(new java.awt.Insets(2, 7, 2, 14));
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });

        cancelButton.setText("Cancel");
        cancelButton.setIconTextGap(6);
        cancelButton.setMargin(new java.awt.Insets(2, 7, 2, 14));
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("RDP Client"));
        rdpField.setMinimumSize(new java.awt.Dimension(4, 30));

        rdpBrowseButton.setIcon(IconLoader.getButtonIcon("fileopen"));
        rdpBrowseButton.setText("Browse ...");
        rdpBrowseButton.setMargin(new java.awt.Insets(2, 5, 2, 5));
        rdpBrowseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdpBrowseButtonActionPerformed(evt);
            }
        });

        jLabel2.setText("Enter the location of your RDP client");

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, rdpField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 356, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel1Layout.createSequentialGroup()
                        .add(jLabel2)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 55, Short.MAX_VALUE)
                        .add(rdpBrowseButton)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .add(rdpField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 7, Short.MAX_VALUE)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(rdpBrowseButton)
                    .add(jLabel2))
                .addContainerGap())
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("VNC Client"));

        jLabel1.setText("Enter the location of your VNC client");

        vncBrowseButton.setIcon(IconLoader.getButtonIcon("fileopen"));
        vncBrowseButton.setText("Browse ...");
        vncBrowseButton.setMargin(new java.awt.Insets(2, 5, 2, 5));
        vncBrowseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                vncBrowseButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(vncField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 356, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel2Layout.createSequentialGroup()
                        .add(jLabel1)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 54, Short.MAX_VALUE)
                        .add(vncBrowseButton)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel2Layout.createSequentialGroup()
                .add(vncField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 7, Short.MAX_VALUE)
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(vncBrowseButton)
                    .add(jLabel1))
                .addContainerGap())
        );

        org.jdesktop.layout.GroupLayout jPanel3Layout = new org.jdesktop.layout.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(jPanel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );
        jTabbedPane1.addTab("Remote Desktop Clients", jPanel3);

        addPrivateKeyButton.setText("Add");
        addPrivateKeyButton.setHorizontalAlignment(javax.swing.SwingConstants.LEADING);
        addPrivateKeyButton.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        addPrivateKeyButton.setMargin(new java.awt.Insets(2, 5, 2, 5));
        addPrivateKeyButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addPrivateKeyActionPerformed(evt);
            }
        });

        removePrivateKeyButton.setText("Remove");
        removePrivateKeyButton.setHorizontalAlignment(javax.swing.SwingConstants.LEADING);
        removePrivateKeyButton.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        removePrivateKeyButton.setMargin(new java.awt.Insets(2, 5, 2, 5));
        removePrivateKeyButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removePrivateKeyActionPerformed(evt);
            }
        });

        jScrollPane1.setViewportView(privateKeyJList);

        org.jdesktop.layout.GroupLayout jPanel4Layout = new org.jdesktop.layout.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 295, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(addPrivateKeyButton)
                    .add(removePrivateKeyButton))
                .addContainerGap())
        );

        jPanel4Layout.linkSize(new java.awt.Component[] {addPrivateKeyButton, removePrivateKeyButton}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel4Layout.createSequentialGroup()
                        .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 166, Short.MAX_VALUE)
                        .addContainerGap())
                    .add(jPanel4Layout.createSequentialGroup()
                        .add(addPrivateKeyButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(removePrivateKeyButton)
                        .add(122, 122, 122))))
        );
        jTabbedPane1.addTab("Private Keys", jPanel4);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jTabbedPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 395, Short.MAX_VALUE)
                    .add(layout.createSequentialGroup()
                        .add(okButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 67, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(cancelButton)))
                .addContainerGap())
        );

        layout.linkSize(new java.awt.Component[] {cancelButton, okButton}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(jTabbedPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 217, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(cancelButton)
                    .add(okButton))
                .addContainerGap())
        );
        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void removePrivateKeyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removePrivateKeyActionPerformed
        if (privateKeyJList.isSelectionEmpty()) {
            return;
        }
        int[] selected = privateKeyJList.getSelectedIndices();
        // Remove from the end first, since removing earlier ones brings later 
        // ones forward by one
        for (int i = selected.length - 1; i >= 0; --i) {
            privateKeyList.remove(selected[i]);
        }        
    }//GEN-LAST:event_removePrivateKeyActionPerformed

    private void addPrivateKeyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addPrivateKeyActionPerformed
        JFileChooser jc = new JFileChooser();
        jc.setDialogTitle("Select private SSH key");
        jc.setFileSelectionMode(jc.FILES_ONLY);        
       
        if (jc.showDialog(this, "OK") == JFileChooser.APPROVE_OPTION) {
            privateKeyList.add(jc.getSelectedFile().getAbsolutePath());
        }
    }//GEN-LAST:event_addPrivateKeyActionPerformed
    
    private void formKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_formKeyPressed
        System.out.println("Key " + evt.getKeyCode() + " pressed");
    }//GEN-LAST:event_formKeyPressed
    
    private void rdpBrowseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdpBrowseButtonActionPerformed
        showChooser("RDP", rdpField);
    }//GEN-LAST:event_rdpBrowseButtonActionPerformed
    
    private void vncBrowseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_vncBrowseButtonActionPerformed
        showChooser("VNC", vncField);
    }//GEN-LAST:event_vncBrowseButtonActionPerformed
    private void showChooser(String type, JTextField field) {
        JFileChooser jc = new JFileChooser();
        jc.setDialogTitle("Select " + type + " client");
        jc.setFileSelectionMode(jc.FILES_ONLY);
        
        // On windows, only look for .exe files
        if (OS.isWindows()) {
            jc.setFileFilter(new FileFilter() {
                public boolean accept(File f) {
                    return f.isDirectory() || f.getName().toLowerCase().endsWith(".exe");
                }
                public String getDescription() {
                    return "Executable files";
                }
            });
        }
        if (jc.showDialog(this, "OK") == JFileChooser.APPROVE_OPTION) {
            field.setText(jc.getSelectedFile().getAbsolutePath());
        }
    }
    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
        Settings.setProperty("vnc.path", vncField.getText());
        Settings.setProperty("rdp.path", rdpField.getText());
        Settings.setPropertyList("PrivateKeyPaths", privateKeyList);
        doClose(RET_OK);
    }//GEN-LAST:event_okButtonActionPerformed
    
    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        doClose(RET_CANCEL);
    }//GEN-LAST:event_cancelButtonActionPerformed
    
    /** Closes the dialog */
    private void closeDialog(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_closeDialog
        doClose(RET_CANCEL);
    }//GEN-LAST:event_closeDialog
    
    private void doClose(int retStatus) {
        returnStatus = retStatus;
        setVisible(false);
        dispose();
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new PreferencesDialog(new javax.swing.JFrame(), true).setVisible(true);
            }
        });
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addPrivateKeyButton;
    private javax.swing.JButton cancelButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JButton okButton;
    private org.jdesktop.swingx.JXList privateKeyJList;
    private javax.swing.JButton rdpBrowseButton;
    private javax.swing.JTextField rdpField;
    private javax.swing.JButton removePrivateKeyButton;
    private javax.swing.JButton vncBrowseButton;
    private javax.swing.JTextField vncField;
    // End of variables declaration//GEN-END:variables
    
    private int returnStatus = RET_CANCEL;
    private EventList privateKeyList = new BasicEventList();
}
