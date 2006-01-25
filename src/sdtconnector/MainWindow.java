/*
 * MainWindow.java
 *
 * Created on January 15, 2006, 9:24 AM
 */

package sdtconnector;

import com.jcraft.jsch.UserInfo;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;


/**
 *
 * @author  wayne
 */
public class MainWindow extends javax.swing.JFrame {
    
    /** Creates new form MainWindow */
    public MainWindow() {
        initComponents();
        connections = new HashMap<String, GatewayConnection>();
        gatewayList.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        gatewayList.setShowsRootHandles(true);
        DefaultMutableTreeNode top = new DefaultMutableTreeNode("SDT Gateways", true);
        
        gatewayList.setModel(treeModel = new SDTTreeModel());
        gatewayList.setSelectionRow(0);
        treeModel.addTreeModelListener(new TreeModelListener() {
            public void treeNodesChanged(TreeModelEvent e) {
                // Update the description when a node in the tree changes
                if (!e.getTreePath().equals(gatewayList.getSelectionPath())) {
                    return;
                }
                Object last = gatewayList.getSelectionPath().getLastPathComponent();
                if (last instanceof Gateway) {
                    descriptionArea.setText(((Gateway) last).getDescription());
                } else {
                    descriptionArea.setText(((Host) last).getDescription());
                }
            }
            public void treeNodesInserted(TreeModelEvent e) { }
            public void treeNodesRemoved(TreeModelEvent e) { }
            public void treeStructureChanged(TreeModelEvent e) { }
        });
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        connectButtonPanel = new javax.swing.JPanel();
        telnetButton = new javax.swing.JButton();
        webButton = new javax.swing.JButton();
        vncButton = new javax.swing.JButton();
        rdpButton = new javax.swing.JButton();
        addHostButton = new javax.swing.JButton();
        editButton = new javax.swing.JButton();
        removeButton = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        descriptionArea = new javax.swing.JTextArea();
        jScrollPane1 = new javax.swing.JScrollPane();
        gatewayList = new javax.swing.JTree();
        addGatewayButton = new javax.swing.JButton();
        jXStatusBar1 = new org.jdesktop.swingx.JXStatusBar();
        jMenuBar1 = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        addGatewayMenuItem = new javax.swing.JMenuItem();
        addHostMenu = new javax.swing.JMenuItem();
        exitMenuItem = new javax.swing.JMenuItem();
        editMenu = new javax.swing.JMenu();
        editGatewayMenuItem = new javax.swing.JMenuItem();
        editHostMenuItem = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JSeparator();
        prefsMenuItem = new javax.swing.JMenuItem();
        helpMenu = new javax.swing.JMenu();
        aboutMenuItem = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Opengear SDT Connector");
        setLocationByPlatform(true);
        connectButtonPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Connect using ..."));
        telnetButton.setText("Telnet");
        telnetButton.setEnabled(false);
        telnetButton.setNextFocusableComponent(webButton);
        telnetButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                telnetButtonActionPerformed(evt);
            }
        });

        webButton.setText("Web");
        webButton.setEnabled(false);
        webButton.setNextFocusableComponent(rdpButton);
        webButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                webButtonActionPerformed(evt);
            }
        });

        vncButton.setText("VNC");
        vncButton.setEnabled(false);
        vncButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                vncButtonActionPerformed(evt);
            }
        });

        rdpButton.setText("RDP");
        rdpButton.setEnabled(false);
        rdpButton.setNextFocusableComponent(vncButton);

        org.jdesktop.layout.GroupLayout connectButtonPanelLayout = new org.jdesktop.layout.GroupLayout(connectButtonPanel);
        connectButtonPanel.setLayout(connectButtonPanelLayout);
        connectButtonPanelLayout.setHorizontalGroup(
            connectButtonPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(connectButtonPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(connectButtonPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(webButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(telnetButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(connectButtonPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(vncButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 92, Short.MAX_VALUE)
                    .add(rdpButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 92, Short.MAX_VALUE))
                .addContainerGap())
        );

        connectButtonPanelLayout.linkSize(new java.awt.Component[] {rdpButton, telnetButton, vncButton, webButton}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        connectButtonPanelLayout.setVerticalGroup(
            connectButtonPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(connectButtonPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(connectButtonPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(telnetButton)
                    .add(rdpButton))
                .add(17, 17, 17)
                .add(connectButtonPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(webButton)
                    .add(vncButton))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        addHostButton.setText("Add Host");
        addHostButton.setEnabled(false);
        addHostButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addHostButtonActionPerformed(evt);
            }
        });

        editButton.setText("Edit");
        editButton.setEnabled(false);
        editButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editButtonActionPerformed(evt);
            }
        });

        removeButton.setText("Remove");
        removeButton.setEnabled(false);
        removeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeButtonActionPerformed(evt);
            }
        });

        jScrollPane2.setBorder(javax.swing.BorderFactory.createTitledBorder("Description"));
        descriptionArea.setColumns(20);
        descriptionArea.setEditable(false);
        descriptionArea.setLineWrap(true);
        descriptionArea.setRows(5);
        descriptionArea.setText("A Description of the host would go here");
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setAutoscrolls(false);
        descriptionArea.setBorder(null);
        descriptionArea.setFocusable(false);
        descriptionArea.setMargin(new java.awt.Insets(3, 3, 3, 3));
        descriptionArea.setOpaque(false);
        descriptionArea.setRequestFocusEnabled(false);
        jScrollPane2.setViewportView(descriptionArea);

        gatewayList.setRootVisible(false);
        gatewayList.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
            public void valueChanged(javax.swing.event.TreeSelectionEvent evt) {
                gatewayListValueChanged(evt);
            }
        });

        jScrollPane1.setViewportView(gatewayList);

        addGatewayButton.setText("Add Gateway");
        addGatewayButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addGatewayButtonActionPerformed(evt);
            }
        });

        jXStatusBar1.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        jXStatusBar1.setFocusable(false);

        fileMenu.setText("File");
        addGatewayMenuItem.setText("New Gateway");
        addGatewayMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addGatewayButtonActionPerformed(evt);
            }
        });

        fileMenu.add(addGatewayMenuItem);

        addHostMenu.setText("New Host");
        addHostMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addHostButtonActionPerformed(evt);
            }
        });

        fileMenu.add(addHostMenu);

        exitMenuItem.setText("Exit");
        fileMenu.add(exitMenuItem);

        jMenuBar1.add(fileMenu);

        editMenu.setText("Edit");
        editGatewayMenuItem.setText("Edit Gateway");
        editGatewayMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editButtonActionPerformed(evt);
            }
        });

        editMenu.add(editGatewayMenuItem);

        editHostMenuItem.setText("Edit Host");
        editHostMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editButtonActionPerformed(evt);
            }
        });

        editMenu.add(editHostMenuItem);

        editMenu.add(jSeparator1);

        prefsMenuItem.setText("Preferences");
        prefsMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                prefsMenuItemActionPerformed(evt);
            }
        });

        editMenu.add(prefsMenuItem);

        jMenuBar1.add(editMenu);

        helpMenu.setText("Help");
        aboutMenuItem.setText("About");
        helpMenu.add(aboutMenuItem);

        jMenuBar1.add(helpMenu);

        setJMenuBar(jMenuBar1);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 201, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(layout.createSequentialGroup()
                                .add(28, 28, 28)
                                .add(connectButtonPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .add(layout.createSequentialGroup()
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jScrollPane2, 0, 0, Short.MAX_VALUE)))
                        .addContainerGap(65, Short.MAX_VALUE))
                    .add(layout.createSequentialGroup()
                        .add(addGatewayButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(addHostButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(editButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(removeButton)
                        .addContainerGap(36, Short.MAX_VALUE))))
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jXStatusBar1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 530, Short.MAX_VALUE)
        );

        layout.linkSize(new java.awt.Component[] {connectButtonPanel, jScrollPane2}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        layout.linkSize(new java.awt.Component[] {addGatewayButton, addHostButton, editButton, removeButton}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(connectButtonPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jScrollPane2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 124, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 258, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(addGatewayButton)
                    .add(editButton)
                    .add(addHostButton)
                    .add(removeButton))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jXStatusBar1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 21, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );
        pack();
    }// </editor-fold>//GEN-END:initComponents
    
    private void prefsMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_prefsMenuItemActionPerformed
        
        PreferencesDialog dlg = new PreferencesDialog(this, true);
        dlg.setVisible(true);
        updateButtonState();
    }//GEN-LAST:event_prefsMenuItemActionPerformed
    
    private void webButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_webButtonActionPerformed
        
        GatewayConnection.Redirector r = getRedirectorForSelection(80);
        try {
            URL url = new URL("http", "localhost", r.getLocalPort(), "/");
            Browser.displayURL(url);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error",
                    JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_webButtonActionPerformed
    
    private void removeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeButtonActionPerformed
        if (gatewayList.isSelectionEmpty()) {
            return;
        }
        
        Object last = gatewayList.getLastSelectedPathComponent();
        if (last instanceof Gateway) {
            SDTManager.removeGateway(last.toString());
        } else {
            TreePath path = gatewayList.getSelectionPath();
            SDTManager.removeHost((Gateway) path.getPathComponent(1), (Host) last);
        }
        
    }//GEN-LAST:event_removeButtonActionPerformed
    
    private void addGatewayButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addGatewayButtonActionPerformed
        
        Gateway gw = new Gateway();
        GatewayDialog dlg = new GatewayDialog(this, true, gw);
        //dlg.setLocationRelativeTo(this);
        dlg.setVisible(true);
        dlg.setTitle("New SDT Gateway");
        if (dlg.getReturnStatus() == dlg.RET_OK) {
            SDTManager.addGateway(gw);
            TreePath path = new TreePath(new Object[] {
                gatewayList.getModel().getRoot(), gw });
            
            gatewayList.scrollPathToVisible(path);
            gatewayList.setSelectionPath(path);
        }
    }//GEN-LAST:event_addGatewayButtonActionPerformed
    
    private void vncButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_vncButtonActionPerformed
        VNCViewer.launch("localhost", getRedirectorForSelection(5900).getLocalPort());
    }//GEN-LAST:event_vncButtonActionPerformed
    
    private void gatewayListValueChanged(javax.swing.event.TreeSelectionEvent evt) {//GEN-FIRST:event_gatewayListValueChanged
        TreePath path = gatewayList.getSelectionPath();
        boolean isGateway = false;
        boolean isHost = false;
        String desc = "";
        if (path != null) {
            Object last = path.getLastPathComponent();
            if (last instanceof Gateway) {
                desc = ((Gateway) last).getDescription();
                isGateway = true;
            } else {
                desc = ((Host) last).getDescription();
                isHost = true;
            }
        }
        
        descriptionArea.setText(desc);
        addHostMenu.setEnabled(isHost || isGateway);
        addGatewayMenuItem.setEnabled(true);
        editGatewayMenuItem.setEnabled(isGateway);
        editHostMenuItem.setEnabled(isHost);
        addHostButton.setEnabled(isHost || isGateway);
        removeButton.setEnabled(isHost || isGateway);
        editButton.setEnabled(isHost || isGateway);
        
        updateButtonState();
    }//GEN-LAST:event_gatewayListValueChanged
    
    private void addHostButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addHostButtonActionPerformed
        
        TreePath path = gatewayList.getSelectionPath();
        if (path == null) {
            return;
        }
        
        Host host = new Host();
        AddHostDialog dlg = new AddHostDialog(this, true, host);
        //dlg.setLocationRelativeTo(this);
        dlg.setTitle("New SDT Host");
        dlg.setVisible(true);
        if (dlg.getReturnStatus() == dlg.RET_OK) {
            
            // Add it to the DB
            SDTManager.addHost((Gateway) path.getPathComponent(1), host);
            
            // Make the new host selected by default
            path = path.pathByAddingChild(host);
            gatewayList.scrollPathToVisible(path);
            gatewayList.setSelectionPath(path);
        }
    }//GEN-LAST:event_addHostButtonActionPerformed
    
    private void editButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editButtonActionPerformed
        
        TreePath path = gatewayList.getSelectionPath();
        boolean isGateway = path.getPathCount() == 2;
        TreeModel model = (TreeModel) gatewayList.getModel();
        Gateway gw = (Gateway) path.getPathComponent(1);
        JDialog dlg;
        
        if (isGateway) {
            String oldAddress = gw.getAddress();
            dlg = new GatewayDialog(this, true, gw);
            dlg.setTitle("Edit SDT Gateway");
            dlg.pack();
            dlg.setVisible(true);
            if (!oldAddress.equals(gw.getAddress())) {
                removeGatewayConnection(oldAddress);
            }
            SDTManager.updateGateway(gw, oldAddress);
        } else {
            Host host = (Host) path.getPathComponent(2);
            
            String oldAddress = host.getAddress();
            System.out.println("Editing host " + oldAddress);
            dlg = new AddHostDialog(this, true, host);
            dlg.setTitle("Edit SDT Host");
            dlg.pack();
            dlg.setVisible(true);
            SDTManager.updateHost(gw, host, oldAddress);
        }
        model.valueForPathChanged(path, path.getLastPathComponent());
        
    }//GEN-LAST:event_editButtonActionPerformed
    
    private void telnetButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_telnetButtonActionPerformed
        Telnet.launch("localhost", getRedirectorForSelection(23).getLocalPort());
    }//GEN-LAST:event_telnetButtonActionPerformed
    
    private void updateButtonState() {
        TreePath path = gatewayList.getSelectionPath();
        if (path == null) {
            return;
        }
        boolean isHost = path.getLastPathComponent() instanceof Host;
        boolean rdpIsSet = Settings.getProperty("rdp.path").length() > 0;
        boolean vncIsSet = Settings.getProperty("vnc.path").length() > 0;
        rdpButton.setEnabled(isHost && rdpIsSet);
        if (!rdpIsSet) {
            rdpButton.setToolTipText("Set the RDP client in Edit -> Preferences");
        } else {
            rdpButton.setToolTipText("Connect to this host using a RDP client");
        }
        vncButton.setEnabled(isHost && vncIsSet);
        if (!vncIsSet) {
            vncButton.setToolTipText("Set the VNC client in Edit -> Preferences");
        } else {
            vncButton.setToolTipText("Connect to this host using a VNC client");
        }
        telnetButton.setEnabled(isHost);
        webButton.setEnabled(isHost);
    }
    GatewayConnection.Redirector getRedirectorForSelection(int port) {
        TreePath path = gatewayList.getSelectionPath();
        Gateway gw = (Gateway) path.getPathComponent(1);
        Host host = (Host) path.getLastPathComponent();
        GatewayConnection conn = getGatewayConnection(gw);
        System.out.println("Adding redirection to " + host + ":" + port + " via "
                + gw);
        return conn.getRedirector(host.toString(), port);
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MainWindow().setVisible(true);
            }
        });
    }
    
    private GatewayConnection getGatewayConnection(Gateway gw) {
        GatewayConnection conn = connections.get(gw.getAddress());
        if (conn == null) {
            connections.put(gw.getAddress(), conn = new GatewayConnection(gw, new UserInfo() {
                public String getPassphrase() {
                    return "";
                }
                public String getPassword() {
                    return "";
                }
                public boolean promptPassphrase(String string) {
                    return false;
                }
                public boolean promptPassword(String string) {
                    return false;
                }
                public boolean promptYesNo(String string) {
                    return false;
                }
                public void showMessage(String string) {
                }
            }, Executors.newSingleThreadExecutor()));
        }
        return conn;
    }
    private void removeGatewayConnection(String oldAddress) {
        GatewayConnection conn = connections.get(oldAddress);
        conn.shutdown();
        connections.remove(oldAddress);
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem aboutMenuItem;
    private javax.swing.JButton addGatewayButton;
    private javax.swing.JMenuItem addGatewayMenuItem;
    private javax.swing.JButton addHostButton;
    private javax.swing.JMenuItem addHostMenu;
    private javax.swing.JPanel connectButtonPanel;
    private javax.swing.JTextArea descriptionArea;
    private javax.swing.JButton editButton;
    private javax.swing.JMenuItem editGatewayMenuItem;
    private javax.swing.JMenuItem editHostMenuItem;
    private javax.swing.JMenu editMenu;
    private javax.swing.JMenuItem exitMenuItem;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JTree gatewayList;
    private javax.swing.JMenu helpMenu;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private org.jdesktop.swingx.JXStatusBar jXStatusBar1;
    private javax.swing.JMenuItem prefsMenuItem;
    private javax.swing.JButton rdpButton;
    private javax.swing.JButton removeButton;
    private javax.swing.JButton telnetButton;
    private javax.swing.JButton vncButton;
    private javax.swing.JButton webButton;
    // End of variables declaration//GEN-END:variables
    
    private SDTTreeModel treeModel;
    private Map<String, GatewayConnection> connections;
    
}
