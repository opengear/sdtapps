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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import org.jdesktop.swingx.decorator.AlternateRowHighlighter;
import org.jdesktop.swingx.decorator.Highlighter;
import org.jdesktop.swingx.decorator.HighlighterPipeline;
import org.jdesktop.swingx.decorator.RolloverHighlighter;
import org.jdesktop.swingx.util.OS;
import sdtconnector.AddClientDialog;

public class PreferencesDialog extends javax.swing.JDialog {
   /** Creates new form PreferencesDialog */
    public PreferencesDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        closeButton.setIcon(IconLoader.getButtonIcon("ok"));
        addPrivateKeyButton.setIcon(IconLoader.getButtonIcon("add"));
        removePrivateKeyButton.setIcon(IconLoader.getButtonIcon("remove"));
        removeClientButton.setIcon(IconLoader.getButtonIcon("remove"));
        addClientButton.setIcon(IconLoader.getButtonIcon("add"));
        editClientButton.setIcon(IconLoader.getButtonIcon("edit"));
        removeServiceButton.setIcon(IconLoader.getButtonIcon("remove"));
        addServiceButton.setIcon(IconLoader.getButtonIcon("add"));
        editServiceButton.setIcon(IconLoader.getButtonIcon("edit"));
        privateKeyList.addAll(Settings.getPropertyList(Settings.root().node("PrivateKeyPaths")));
        privateKeyJList.setModel(new ca.odell.glazedlists.swing.EventListModel(privateKeyList));
        privateKeyJList.setRolloverEnabled(true);
        clientJList.setModel(new ca.odell.glazedlists.swing.EventListModel(SDTManager.getClientList()));
        clientJList.setRolloverEnabled(true);
        serviceJList.setModel(new ca.odell.glazedlists.swing.EventListModel(SDTManager.getServiceList()));
        clientJList.setHighlighters(new HighlighterPipeline(new Highlighter[] {
            Highlighter.notePadBackground
        }));
        serviceJList.setHighlighters(new HighlighterPipeline(new Highlighter[] {
            Highlighter.notePadBackground
        }));
        privateKeyJList.setHighlighters(new HighlighterPipeline(new Highlighter[] {
            Highlighter.notePadBackground
        }));
        if (OS.isWindows()) {
            registerSDTLabel.setText("<html>Use SDTConnector to open sdt:// links</html>");
        } else {
            registerSDTLabel.setText("<html>Use SDTConnector to open sdt:// links in Mozilla Firefox</html>");
        }
        pack();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        clientPreferencesPanel = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        clientJList = new org.jdesktop.swingx.JXList();
        addClientButton = new javax.swing.JButton();
        removeClientButton = new javax.swing.JButton();
        editClientButton = new javax.swing.JButton();
        servicePreferencesPanel = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        serviceJList = new org.jdesktop.swingx.JXList();
        addServiceButton = new javax.swing.JButton();
        removeServiceButton = new javax.swing.JButton();
        editServiceButton = new javax.swing.JButton();
        privateKeysPreferencesPanel = new javax.swing.JPanel();
        addPrivateKeyButton = new javax.swing.JButton();
        removePrivateKeyButton = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        privateKeyJList = new org.jdesktop.swingx.JXList();
        systemDefaultsPreferencesPanel = new javax.swing.JPanel();
        registerSDTButton = new javax.swing.JButton();
        registerSDTLabel = new javax.swing.JLabel();
        closeButton = new javax.swing.JButton();

        setTitle("SDTConnector Preferences");
        setModal(true);
        addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                formKeyPressed(evt);
            }
        });

        clientJList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        clientJList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                clientJListValueChanged(evt);
            }
        });
        jScrollPane2.setViewportView(clientJList);

        addClientButton.setText("Add");
        addClientButton.setHorizontalAlignment(javax.swing.SwingConstants.LEADING);
        addClientButton.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        addClientButton.setMargin(new java.awt.Insets(2, 5, 2, 5));
        addClientButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addClientActionPerformed(evt);
            }
        });

        removeClientButton.setText("Remove");
        removeClientButton.setEnabled(false);
        removeClientButton.setHorizontalAlignment(javax.swing.SwingConstants.LEADING);
        removeClientButton.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        removeClientButton.setMargin(new java.awt.Insets(2, 5, 2, 5));
        removeClientButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeClientActionPerformed(evt);
            }
        });

        editClientButton.setText("Edit");
        editClientButton.setHorizontalAlignment(javax.swing.SwingConstants.LEADING);
        editClientButton.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        editClientButton.setMargin(new java.awt.Insets(2, 5, 2, 5));
        editClientButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editClientActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout clientPreferencesPanelLayout = new org.jdesktop.layout.GroupLayout(clientPreferencesPanel);
        clientPreferencesPanel.setLayout(clientPreferencesPanelLayout);
        clientPreferencesPanelLayout.setHorizontalGroup(
            clientPreferencesPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, clientPreferencesPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 321, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(clientPreferencesPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(removeClientButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 67, Short.MAX_VALUE)
                    .add(editClientButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 67, Short.MAX_VALUE)
                    .add(addClientButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 67, Short.MAX_VALUE))
                .addContainerGap())
        );
        clientPreferencesPanelLayout.setVerticalGroup(
            clientPreferencesPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(clientPreferencesPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(clientPreferencesPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 188, Short.MAX_VALUE)
                    .add(clientPreferencesPanelLayout.createSequentialGroup()
                        .add(addClientButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(editClientButton)
                        .add(5, 5, 5)
                        .add(removeClientButton)))
                .addContainerGap())
        );

        jTabbedPane1.addTab("Clients", clientPreferencesPanel);

        serviceJList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        serviceJList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                serviceJListValueChanged(evt);
            }
        });
        jScrollPane4.setViewportView(serviceJList);

        addServiceButton.setText("Add");
        addServiceButton.setHorizontalAlignment(javax.swing.SwingConstants.LEADING);
        addServiceButton.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        addServiceButton.setMargin(new java.awt.Insets(2, 5, 2, 5));
        addServiceButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addServiceActionPerformed(evt);
            }
        });

        removeServiceButton.setText("Remove");
        removeServiceButton.setEnabled(false);
        removeServiceButton.setHorizontalAlignment(javax.swing.SwingConstants.LEADING);
        removeServiceButton.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        removeServiceButton.setMargin(new java.awt.Insets(2, 5, 2, 5));
        removeServiceButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeServiceActionPerformed(evt);
            }
        });

        editServiceButton.setText("Edit");
        editServiceButton.setHorizontalAlignment(javax.swing.SwingConstants.LEADING);
        editServiceButton.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        editServiceButton.setMargin(new java.awt.Insets(2, 5, 2, 5));
        editServiceButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editServiceActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout servicePreferencesPanelLayout = new org.jdesktop.layout.GroupLayout(servicePreferencesPanel);
        servicePreferencesPanel.setLayout(servicePreferencesPanelLayout);
        servicePreferencesPanelLayout.setHorizontalGroup(
            servicePreferencesPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, servicePreferencesPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(jScrollPane4, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 321, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(servicePreferencesPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(removeServiceButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 67, Short.MAX_VALUE)
                    .add(editServiceButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 67, Short.MAX_VALUE)
                    .add(addServiceButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 67, Short.MAX_VALUE))
                .addContainerGap())
        );
        servicePreferencesPanelLayout.setVerticalGroup(
            servicePreferencesPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(servicePreferencesPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(servicePreferencesPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jScrollPane4, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 188, Short.MAX_VALUE)
                    .add(servicePreferencesPanelLayout.createSequentialGroup()
                        .add(addServiceButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(editServiceButton)
                        .add(5, 5, 5)
                        .add(removeServiceButton)))
                .addContainerGap())
        );

        jTabbedPane1.addTab("Services", servicePreferencesPanel);

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
        removePrivateKeyButton.setEnabled(false);
        removePrivateKeyButton.setHorizontalAlignment(javax.swing.SwingConstants.LEADING);
        removePrivateKeyButton.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        removePrivateKeyButton.setMargin(new java.awt.Insets(2, 5, 2, 5));
        removePrivateKeyButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removePrivateKeyActionPerformed(evt);
            }
        });

        privateKeyJList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                privateKeyJListValueChanged(evt);
            }
        });
        jScrollPane1.setViewportView(privateKeyJList);

        org.jdesktop.layout.GroupLayout privateKeysPreferencesPanelLayout = new org.jdesktop.layout.GroupLayout(privateKeysPreferencesPanel);
        privateKeysPreferencesPanel.setLayout(privateKeysPreferencesPanelLayout);
        privateKeysPreferencesPanelLayout.setHorizontalGroup(
            privateKeysPreferencesPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, privateKeysPreferencesPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 333, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(privateKeysPreferencesPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(addPrivateKeyButton)
                    .add(removePrivateKeyButton))
                .addContainerGap())
        );

        privateKeysPreferencesPanelLayout.linkSize(new java.awt.Component[] {addPrivateKeyButton, removePrivateKeyButton}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        privateKeysPreferencesPanelLayout.setVerticalGroup(
            privateKeysPreferencesPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(privateKeysPreferencesPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(privateKeysPreferencesPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(privateKeysPreferencesPanelLayout.createSequentialGroup()
                        .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 188, Short.MAX_VALUE)
                        .addContainerGap())
                    .add(privateKeysPreferencesPanelLayout.createSequentialGroup()
                        .add(addPrivateKeyButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(removePrivateKeyButton)
                        .add(122, 122, 122))))
        );

        jTabbedPane1.addTab("Private Keys", privateKeysPreferencesPanel);

        registerSDTButton.setText("Register sdt://");
        registerSDTButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                registerSDTButtonActionPerformed(evt);
            }
        });

        registerSDTLabel.setText("registerSDTLabel");

        org.jdesktop.layout.GroupLayout systemDefaultsPreferencesPanelLayout = new org.jdesktop.layout.GroupLayout(systemDefaultsPreferencesPanel);
        systemDefaultsPreferencesPanel.setLayout(systemDefaultsPreferencesPanelLayout);
        systemDefaultsPreferencesPanelLayout.setHorizontalGroup(
            systemDefaultsPreferencesPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(systemDefaultsPreferencesPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(registerSDTButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(registerSDTLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 279, Short.MAX_VALUE)
                .addContainerGap())
        );
        systemDefaultsPreferencesPanelLayout.setVerticalGroup(
            systemDefaultsPreferencesPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(systemDefaultsPreferencesPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(systemDefaultsPreferencesPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, registerSDTLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 33, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, registerSDTButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 33, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(166, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("System Defaults", systemDefaultsPreferencesPanel);

        closeButton.setText("Close");
        closeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeButtonAction(evt);
            }
        });

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jTabbedPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 419, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, closeButton))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(jTabbedPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 235, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(closeButton)
                .add(12, 12, 12))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void registerSDTButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_registerSDTButtonActionPerformed
        if (SDTURLHelper.isRegistered()) {
            JOptionPane.showMessageDialog(this,
                    "SDT protocol is already registered.",
                    "Already registered",
                    JOptionPane.PLAIN_MESSAGE);
        } else {
            if (SDTURLHelper.register()) {
                JOptionPane.showMessageDialog(this,
                    "Successfully registered SDT protocol.",
                    "Success",
                    JOptionPane.PLAIN_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                    "Unable to register SDT protocol.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);               
            }
        }
    }//GEN-LAST:event_registerSDTButtonActionPerformed

    private void serviceJListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_serviceJListValueChanged
        editServiceButton.setEnabled(serviceJList.isSelectionEmpty() == false);
        removeServiceButton.setEnabled(serviceJList.isSelectionEmpty() == false &&
                ((Service) serviceJList.getSelectedValue()).getRecordID() > SDTManager.initialRecordID());
    }//GEN-LAST:event_serviceJListValueChanged

    private void clientJListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_clientJListValueChanged
        editClientButton.setEnabled(clientJList.isSelectionEmpty() == false);
        removeClientButton.setEnabled(clientJList.isSelectionEmpty() == false &&
                ((Client) clientJList.getSelectedValue()).getRecordID() > SDTManager.initialRecordID());
    }//GEN-LAST:event_clientJListValueChanged

    private void privateKeyJListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_privateKeyJListValueChanged
        removePrivateKeyButton.setEnabled(privateKeyJList.isSelectionEmpty() == false);
    }//GEN-LAST:event_privateKeyJListValueChanged

    private void editServiceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editServiceActionPerformed
        if (serviceJList.isSelectionEmpty()) {
            return;
        }
        Service service = (Service) serviceJList.getSelectedValue();
        AddServiceDialog dlg = new AddServiceDialog((java.awt.Frame) this.getParent(), true, service);
        
        dlg.setTitle("Edit Service");
        dlg.setLocationRelativeTo(this);
        dlg.setVisible(true);
        if (!service.getName().equals("")) {
            SDTManager.updateService(service);
        }
    }//GEN-LAST:event_editServiceActionPerformed

    private void removeServiceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeServiceActionPerformed
        if (serviceJList.isSelectionEmpty()) {
            return;
        }
        SDTManager.removeService((Service) serviceJList.getSelectedValue());
    }//GEN-LAST:event_removeServiceActionPerformed

    private void addServiceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addServiceActionPerformed
        Service service = new Service();
        AddServiceDialog dlg = new AddServiceDialog((java.awt.Frame) this.getParent(), true, service);
        
        dlg.setTitle("Add Service");
        dlg.setLocationRelativeTo(this);
        dlg.setVisible(true);
        if (!service.getName().equals("")) {
            SDTManager.addService(service);
        }
    }//GEN-LAST:event_addServiceActionPerformed

    private void closeButtonAction(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeButtonAction
        setVisible(false);
        dispose();
    }//GEN-LAST:event_closeButtonAction

    private void editClientActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editClientActionPerformed
        if (clientJList.isSelectionEmpty()) {
            return;
        }
        Client client = (Client) clientJList.getSelectedValue();
        AddClientDialog dlg = new AddClientDialog((java.awt.Frame) this.getParent(), true, client);
        
        dlg.setTitle("Edit Client");
        dlg.setLocationRelativeTo(this);
        dlg.setVisible(true);
        if (dlg.getReturnStatus() == dlg.RET_OK) {
            SDTManager.updateClient(client);
        }
    }//GEN-LAST:event_editClientActionPerformed

    private void removeClientActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeClientActionPerformed
        if (clientJList.isSelectionEmpty()) {
            return;
        }
        SDTManager.removeClient((Client) clientJList.getSelectedValue());
    }//GEN-LAST:event_removeClientActionPerformed

    private void addClientActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addClientActionPerformed
        Client client = new Client();
        AddClientDialog dlg = new AddClientDialog((java.awt.Frame) this.getParent(), true, client);
        
        dlg.setTitle("Add Client");
        dlg.setLocationRelativeTo(this);
        dlg.setVisible(true);
        if (dlg.getReturnStatus() == dlg.RET_OK) {
            SDTManager.addClient(client);
        }
    }//GEN-LAST:event_addClientActionPerformed

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
        Settings.setPropertyList(Settings.root().node("PrivateKeyPaths"), privateKeyList);
    }//GEN-LAST:event_removePrivateKeyActionPerformed

    private void addPrivateKeyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addPrivateKeyActionPerformed
        JFileChooser jc = new JFileChooser();
        jc.setDialogTitle("Select private SSH key");
        jc.setFileSelectionMode(jc.FILES_ONLY);        
 
        if (jc.showDialog(this, "OK") == JFileChooser.APPROVE_OPTION) {
            privateKeyList.add(jc.getSelectedFile().getAbsolutePath());
        }
        Settings.setPropertyList(Settings.root().node("PrivateKeyPaths"), privateKeyList);
    }//GEN-LAST:event_addPrivateKeyActionPerformed
    
    private void formKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_formKeyPressed
        System.out.println("Key " + evt.getKeyCode() + " pressed");
    }//GEN-LAST:event_formKeyPressed

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
    private javax.swing.JButton addClientButton;
    private javax.swing.JButton addPrivateKeyButton;
    private javax.swing.JButton addServiceButton;
    private org.jdesktop.swingx.JXList clientJList;
    private javax.swing.JPanel clientPreferencesPanel;
    private javax.swing.JButton closeButton;
    private javax.swing.JButton editClientButton;
    private javax.swing.JButton editServiceButton;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTabbedPane jTabbedPane1;
    private org.jdesktop.swingx.JXList privateKeyJList;
    private javax.swing.JPanel privateKeysPreferencesPanel;
    private javax.swing.JButton registerSDTButton;
    private javax.swing.JLabel registerSDTLabel;
    private javax.swing.JButton removeClientButton;
    private javax.swing.JButton removePrivateKeyButton;
    private javax.swing.JButton removeServiceButton;
    private org.jdesktop.swingx.JXList serviceJList;
    private javax.swing.JPanel servicePreferencesPanel;
    private javax.swing.JPanel systemDefaultsPreferencesPanel;
    // End of variables declaration//GEN-END:variables
    
    private EventList privateKeyList = new BasicEventList();
}