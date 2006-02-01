/*
 * MainWindow.java
 *
 * Created on January 15, 2006, 9:24 AM
 */

package sdtconnector;

import com.jcraft.jsch.UserInfo;
import com.jgoodies.looks.LookUtils;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import edu.emory.mathcs.backport.java.util.concurrent.ExecutorService;
import edu.emory.mathcs.backport.java.util.concurrent.Executors;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellEditor;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import org.jdesktop.swingx.JXLoginDialog;
import org.jdesktop.swingx.JXLoginPanel;
import org.jdesktop.swingx.event.ProgressEvent;
import org.jdesktop.swingx.util.OS;
import org.jdesktop.swingx.util.WindowUtils;


/**
 *
 * @author  wayne
 */
public class MainWindow extends javax.swing.JFrame {
    
    /** Creates new form MainWindow */
    public MainWindow() {
        initComponents();
        setIconImage(getMenuIcon("gateway").getImage());
        
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
        
        newHostAction.putValue(Action.SMALL_ICON, getMenuIcon("host"));
        newHostAction.putValue(Action.NAME, "New Host");
        
        listMenuAddHostItem.setAction(newHostAction);
        addHostMenu.setAction(newHostAction);
        addHostButton.setAction(newHostAction);
        
        newGatewayAction.putValue(Action.SMALL_ICON, getMenuIcon("gateway"));
        newGatewayAction.putValue(Action.NAME, "New Gateway");
        addGatewayMenuItem.setAction(newGatewayAction);
        addGatewayButton.setAction(newGatewayAction);
        listMenuAddGatewayItem.setAction(newGatewayAction);
        listMenuAddHostItem.setAction(newHostAction);
        
        editAction.putValue(Action.SMALL_ICON, getMenuIcon("edit"));
        editAction.putValue(Action.NAME, "Edit");
        
        editButton.setAction(editAction);
        listEditMenuItem.setAction(editAction);
        editMenuEditItem.setAction(editAction);
        
        deleteAction.putValue(Action.SMALL_ICON, getMenuIcon("delete"));
        deleteAction.putValue(Action.NAME, "Delete");
        
        listRemoveMenuItem.setAction(deleteAction);
        editMenuDeleteItem.setAction(deleteAction);
        deleteButton.setAction(deleteAction);
        
        prefsMenuItem.setIcon(getMenuIcon("preferences"));
        
        
        aboutMenuItem.setIcon(getMenuIcon("about"));
        fileMenuExitItem.setIcon(getMenuIcon("exit"));
        addGatewayButton.setIcon(getButtonIcon("gateway"));
        addGatewayButton.setText("");
        addGatewayButton.setToolTipText("Create a new Secure Desktop Tunnel");
        addHostButton.setIcon(getButtonIcon("host"));
        addHostButton.setText("");
        addHostButton.setToolTipText("Add a Host via the Secure Desktop Tunnel");
        editButton.setIcon(getButtonIcon("edit"));
        editButton.setText("");
        deleteButton.setIcon(getButtonIcon("delete"));
        deleteButton.setText("");
        telnetButton.setIcon(getIcon("48x48/telnet.png"));
        webButton.setIcon(getIcon("48x48/www.png"));
        rdpButton.setIcon(getIcon("48x48/tsclient.png"));
        vncButton.setIcon(getIcon("48x48/vnc.png"));
        // Disable edit/delete actions on an empty list
        if (gatewayList.getSelectionPath() == null) {
            newHostAction.setEnabled(false);
            deleteAction.setEnabled(false);
            editAction.setEnabled(false);
        }
        DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
        gatewayList.setCellRenderer(renderer);
        renderer.setLeafIcon(getMenuIcon("host"));
        renderer.setClosedIcon(getMenuIcon("gateway"));
        renderer.setOpenIcon(getMenuIcon("gateway"));
        
        //
        // Remove menu items that don't look right on MaxOSX
        //
        if (OS.isMacOSX()) {
            fileMenu.remove(fileMenuExitItem);
        }
        pack();
    }
    private ImageIcon getIcon(String path) {
        URL url = getClass().getResource("/images/" + path);
        if (url != null) {
            return new ImageIcon(url);
        } else {
            return new ImageIcon(getToolkit().getImage("images/" + path));
        }
    }
    private ImageIcon getMenuIcon(String name) {
        return getIcon("16x16/" + name + ".png");
    }
    private ImageIcon getButtonIcon(String name) {
        return getIcon("22x22/" + name + ".png");
    }
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        gatewayListPopup = new javax.swing.JPopupMenu();
        listMenuAddGatewayItem = new javax.swing.JMenuItem();
        listMenuAddHostItem = new javax.swing.JMenuItem();
        listEditMenuItem = new javax.swing.JMenuItem();
        listRemoveMenuItem = new javax.swing.JMenuItem();
        connectButtonPanel = new javax.swing.JPanel();
        telnetButton = new javax.swing.JButton();
        webButton = new javax.swing.JButton();
        vncButton = new javax.swing.JButton();
        rdpButton = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        gatewayList = new javax.swing.JTree();
        jToolBar1 = new javax.swing.JToolBar();
        addGatewayButton = new javax.swing.JButton();
        addHostButton = new javax.swing.JButton();
        editButton = new javax.swing.JButton();
        deleteButton = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        statusBar = new org.jdesktop.swingx.JXStatusBar();
        jScrollPane2 = new javax.swing.JScrollPane();
        descriptionArea = new javax.swing.JTextArea();
        jMenuBar1 = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        addGatewayMenuItem = new javax.swing.JMenuItem();
        addHostMenu = new javax.swing.JMenuItem();
        fileMenuExitItem = new javax.swing.JMenuItem();
        editMenu = new javax.swing.JMenu();
        editMenuEditItem = new javax.swing.JMenuItem();
        editMenuDeleteItem = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JSeparator();
        prefsMenuItem = new javax.swing.JMenuItem();
        helpMenu = new javax.swing.JMenu();
        aboutMenuItem = new javax.swing.JMenuItem();

        listMenuAddGatewayItem.setText("New Gateway");
        gatewayListPopup.add(listMenuAddGatewayItem);

        listMenuAddHostItem.setText("New Host");
        gatewayListPopup.add(listMenuAddHostItem);

        listEditMenuItem.setText("Edit");
        gatewayListPopup.add(listEditMenuItem);

        listRemoveMenuItem.setText("Delete");
        gatewayListPopup.add(listRemoveMenuItem);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Opengear SDT Connector");
        connectButtonPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Connect"));
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
        rdpButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdpButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout connectButtonPanelLayout = new org.jdesktop.layout.GroupLayout(connectButtonPanel);
        connectButtonPanel.setLayout(connectButtonPanelLayout);
        connectButtonPanelLayout.setHorizontalGroup(
            connectButtonPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(connectButtonPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(connectButtonPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(webButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(telnetButton))
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
                .add(connectButtonPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(telnetButton)
                    .add(rdpButton))
                .add(17, 17, 17)
                .add(connectButtonPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(webButton)
                    .add(vncButton))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        gatewayList.setRootVisible(false);
        gatewayList.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
            public void valueChanged(javax.swing.event.TreeSelectionEvent evt) {
                gatewayListValueChanged(evt);
            }
        });
        gatewayList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                gatewayListMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                gatewayListMouseReleased(evt);
            }
        });

        jScrollPane1.setViewportView(gatewayList);

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);
        jToolBar1.setMargin(new java.awt.Insets(0, 5, 0, 0));
        addGatewayButton.setIcon(getButtonIcon("gateway"));
        addGatewayButton.setText("gw");
        addGatewayButton.setFocusable(false);
        addGatewayButton.setMargin(new java.awt.Insets(5, 5, 5, 5));
        addGatewayButton.setRequestFocusEnabled(false);
        jToolBar1.add(addGatewayButton);

        addHostButton.setText("host");
        addHostButton.setFocusable(false);
        addHostButton.setMargin(new java.awt.Insets(5, 5, 5, 5));
        jToolBar1.add(addHostButton);

        editButton.setText("edit");
        editButton.setToolTipText("Edit");
        editButton.setFocusable(false);
        editButton.setMargin(new java.awt.Insets(5, 5, 5, 5));
        jToolBar1.add(editButton);

        deleteButton.setText("delete");
        deleteButton.setToolTipText("Delete");
        deleteButton.setFocusable(false);
        deleteButton.setMargin(new java.awt.Insets(5, 5, 5, 5));
        jToolBar1.add(deleteButton);

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        statusBar.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 2, 1, 2));
        statusBar.setDoubleBuffered(false);
        statusBar.setFocusable(false);

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(statusBar, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 539, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(statusBar, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
        );

        jScrollPane2.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jScrollPane2.setOpaque(false);
        descriptionArea.setBackground(javax.swing.UIManager.getDefaults().getColor("Panel.background"));
        descriptionArea.setColumns(20);
        descriptionArea.setEditable(false);
        descriptionArea.setLineWrap(true);
        descriptionArea.setRows(5);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setAutoscrolls(false);
        descriptionArea.setBorder(null);
        descriptionArea.setFocusable(false);
        descriptionArea.setMargin(new java.awt.Insets(3, 3, 3, 3));
        descriptionArea.setOpaque(false);
        descriptionArea.setRequestFocusEnabled(false);
        jScrollPane2.setViewportView(descriptionArea);

        fileMenu.setText("File");
        addGatewayMenuItem.setText("New Gateway");
        addGatewayMenuItem.setRequestFocusEnabled(false);
        fileMenu.add(addGatewayMenuItem);

        addHostMenu.setText("New Host");
        addHostMenu.setRequestFocusEnabled(false);
        fileMenu.add(addHostMenu);

        fileMenuExitItem.setText("Exit");
        fileMenuExitItem.setRequestFocusEnabled(false);
        fileMenuExitItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fileMenuExitItemActionPerformed(evt);
            }
        });

        fileMenu.add(fileMenuExitItem);

        jMenuBar1.add(fileMenu);

        editMenu.setText("Edit");
        editMenuEditItem.setText("Edit Gateway");
        editMenuEditItem.setRequestFocusEnabled(false);
        editMenu.add(editMenuEditItem);

        editMenuDeleteItem.setText("Delete");
        editMenuDeleteItem.setRequestFocusEnabled(false);
        editMenu.add(editMenuDeleteItem);

        editMenu.add(jSeparator1);

        prefsMenuItem.setText("Preferences");
        prefsMenuItem.setRequestFocusEnabled(false);
        prefsMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                prefsMenuItemActionPerformed(evt);
            }
        });

        editMenu.add(prefsMenuItem);

        jMenuBar1.add(editMenu);

        helpMenu.setText("Help");
        aboutMenuItem.setText("About");
        aboutMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aboutMenuItemActionPerformed(evt);
            }
        });

        helpMenu.add(aboutMenuItem);

        jMenuBar1.add(helpMenu);

        setJMenuBar(jMenuBar1);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jToolBar1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 543, Short.MAX_VALUE)
            .add(layout.createSequentialGroup()
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 227, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(32, 32, 32)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(jScrollPane2)
                    .add(connectButtonPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
            .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        layout.linkSize(new java.awt.Component[] {connectButtonPanel, jScrollPane2}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(jToolBar1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 344, Short.MAX_VALUE)
                    .add(layout.createSequentialGroup()
                        .add(connectButtonPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(18, 18, 18)
                        .add(jScrollPane2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 151, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 26, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );
        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void rdpButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdpButtonActionPerformed
        sshLaunch(new RDPViewer(), 3389);
    }//GEN-LAST:event_rdpButtonActionPerformed

    private void aboutMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aboutMenuItemActionPerformed
        JDialog dlg = new AboutDialog(this, true);
        dlg.setLocationRelativeTo(this);
        dlg.setVisible(true);
    }//GEN-LAST:event_aboutMenuItemActionPerformed
    
    private void fileMenuExitItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fileMenuExitItemActionPerformed
        setVisible(false);
        dispose();
    }//GEN-LAST:event_fileMenuExitItemActionPerformed
    
    private void gatewayListMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gatewayListMouseReleased
        showListPopup(evt);
    }//GEN-LAST:event_gatewayListMouseReleased
    
    private void gatewayListMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gatewayListMousePressed
        TreePath path = gatewayList.getPathForLocation(evt.getX(), evt.getY());
        if (path != null) {
            gatewayList.setSelectionPath(path);
            if (evt.getClickCount() == 2) {
                editSelectedNode(path);
            }
        }
        
        showListPopup(evt);
    }//GEN-LAST:event_gatewayListMousePressed
    
    private void showListPopup(final java.awt.event.MouseEvent evt) {
        
        if (evt.isPopupTrigger()) {
            TreePath path = gatewayList.getPathForLocation(evt.getX(), evt.getY());
            // If no selected node, unable to edit or delete it
            listRemoveMenuItem.setEnabled(path != null);
            listEditMenuItem.setEnabled(path != null);
            gatewayListPopup.show(evt.getComponent(), evt.getX(), evt.getY());
        }
    }
    
    private void prefsMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_prefsMenuItemActionPerformed
        
        PreferencesDialog dlg = new PreferencesDialog(this, true);
        dlg.setLocationRelativeTo(this);
        dlg.setVisible(true);
        updateButtonState();
    }//GEN-LAST:event_prefsMenuItemActionPerformed
 
    private void webButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_webButtonActionPerformed
        sshLaunch(new Browser(), 80);
    }//GEN-LAST:event_webButtonActionPerformed
    
    private void removeSelectedNode(TreePath path) {
        Object last = path.getLastPathComponent();
        if (last instanceof Gateway) {
            SDTManager.removeGateway(last.toString());
            removeGatewayConnection(last.toString());
        } else {
            SDTManager.removeHost((Gateway) path.getPathComponent(1), (Host) last);
        }
    }
    
    private void vncButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_vncButtonActionPerformed
        sshLaunch(new VNCViewer(), 5900);
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
        
        newHostAction.setEnabled(isHost || isGateway);
        editAction.setEnabled(isHost || isGateway);
        deleteAction.setEnabled(isHost || isGateway);
        
        updateButtonState();
    }//GEN-LAST:event_gatewayListValueChanged
    
    private void editActionPerformed(ActionEvent evt) {
        if (gatewayList.isSelectionEmpty()) {
            return;
        }
        editSelectedNode(gatewayList.getSelectionPath());
    }
    
    private void deleteActionPerformed(ActionEvent evt) {
        if (gatewayList.isSelectionEmpty()) {
            return;
        }
        removeSelectedNode(gatewayList.getSelectionPath());
    }
    private void addGatewayActionPerformed(ActionEvent evt) {
        Gateway gw = new Gateway();
        GatewayDialog dlg = new GatewayDialog(this, true, gw);
        dlg.setLocationRelativeTo(this);
        dlg.setVisible(true);
        dlg.setTitle("New SDT Gateway");
        if (dlg.getReturnStatus() == dlg.RET_OK) {
            SDTManager.addGateway(gw);
            TreePath path = new TreePath(new Object[] {
                gatewayList.getModel().getRoot(), gw });
            
            gatewayList.scrollPathToVisible(path);
            gatewayList.setSelectionPath(path);
        }
    }
    
    private void addHostActionPerformed(ActionEvent evt) {
        
        TreePath path = gatewayList.getSelectionPath();
        if (path == null) {
            return;
        }
        
        Host host = new Host();
        AddHostDialog dlg = new AddHostDialog(this, true, host);
        dlg.setLocationRelativeTo(this);
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
    }
    
    private void editSelectedNode(final TreePath path) {
        boolean isGateway = path.getPathCount() == 2;
        TreeModel model = (TreeModel) gatewayList.getModel();
        Gateway gw = (Gateway) path.getPathComponent(1);
        JDialog dlg;
        
        if (isGateway) {
            String oldAddress = gw.getAddress();
            int oldPort = gw.getPort();
            dlg = new GatewayDialog(this, true, gw);
            dlg.setTitle("Edit SDT Gateway");
            dlg.setLocationRelativeTo(this);
            dlg.pack();
            dlg.setVisible(true);
            if (!oldAddress.equals(gw.getAddress()) || gw.getPort() != oldPort) {
                removeGatewayConnection(oldAddress);
            }
            SDTManager.updateGateway(gw, oldAddress);
        } else {
            Host host = (Host) path.getPathComponent(2);
            
            String oldAddress = host.getAddress();
            System.out.println("Editing host " + oldAddress);
            dlg = new AddHostDialog(this, true, host);
            dlg.setTitle("Edit SDT Host");
            dlg.setLocationRelativeTo(this);
            dlg.pack();
            dlg.setVisible(true);
            SDTManager.updateHost(gw, host, oldAddress);
        }
        model.valueForPathChanged(path, path.getLastPathComponent());
        updateButtonState();
    }
    
    private void telnetButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_telnetButtonActionPerformed
        sshLaunch(new Telnet(), 23);
    }//GEN-LAST:event_telnetButtonActionPerformed
    
    private void updateButtonState() {
        TreePath path = gatewayList.getSelectionPath();
        if (path == null) {
            return;
        }
        Object last = path.getLastPathComponent();
        Host host = null;
        boolean isHost = last instanceof Host;
        if (isHost) {
            host = (Host) last;
        }
        boolean rdpIsSet = Settings.getProperty("rdp.path").length() > 0;
        boolean vncIsSet = Settings.getProperty("vnc.path").length() > 0;
        rdpButton.setEnabled(isHost && rdpIsSet && host.rdp);
        if (!rdpIsSet) {
            rdpButton.setToolTipText("Set the RDP client in Edit -> Preferences");
        } else {
            rdpButton.setToolTipText(isHost ?
                "Connect to " + last + " using a RDP client" : "");
        }
        vncButton.setEnabled(isHost && vncIsSet && host.vnc);
        if (!vncIsSet) {
            vncButton.setToolTipText("Set the VNC client in Edit -> Preferences");
        } else {
            vncButton.setToolTipText(isHost ?
                "Connect to " + last + " using a VNC client" : "");
        }
        telnetButton.setEnabled(isHost && host.telnet);
        telnetButton.setToolTipText(isHost ? "Telnet to " + last : "");
        webButton.setEnabled(isHost && host.www);
        webButton.setToolTipText(isHost ? "Browse to " + last : "");
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
    private void sshLaunch(final Launcher launcher, int port) {
        
        TreePath path = gatewayList.getSelectionPath();
        if (path == null) {
            return;
        }
        launcher.setHost("localhost");
        launcher.setPort(getRedirectorForSelection(port).getLocalPort());
        
        Gateway gw = (Gateway) path.getPathComponent(1);
        Host host = (Host) path.getLastPathComponent();
        final GatewayConnection conn = getGatewayConnection(gw);
        //getGlassPane().setVisible(true);
        
        bgExec.execute(new Runnable() {
            public void run() {
                if (conn.login()) {
                    swingExec.execute(launcher);
                }
            }
        });
        
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
            conn = new GatewayConnection(gw, new GatewayAuth(gw), swingExec);
            conn.setListener(new SSHListener(gw));
            connections.put(gw.getAddress(), conn);
        }
        return conn;
    }
    private void removeGatewayConnection(String oldAddress) {
        GatewayConnection conn = connections.get(oldAddress);
        if (conn != null) {
            conn.shutdown();
            connections.remove(oldAddress);
        }
    }
    class GatewayAuth implements GatewayConnection.Authentication {
        public GatewayAuth(Gateway gw) {
            this.gateway = gw;
            password = gw.getPassword();
        }
        public boolean promptAuthentication() {
            LoginDialog dlg = new LoginDialog(MainWindow.this, true);
            dlg.setUsername(gateway.getUsername());
            dlg.setPassword(password);
            dlg.setLocationRelativeTo(MainWindow.this);
            if (LookUtils.IS_JAVA_5_OR_LATER) {
                dlg.setLocation(WindowUtils.getPointForCentering(dlg));
            }
            dlg.pack();
            dlg.setVisible(true);
            if (dlg.getReturnStatus() == LoginDialog.RET_CANCEL) {
                return false;
            }
            password = dlg.getPassword();
            return true;
        }
        
        public String getUsername() {
            return gateway.getUsername();
        }
        
        public String getPassword() {
            return password;
        }
        Gateway gateway;
        String passphrase;
        String password;
    }
    class SSHListener implements GatewayConnection.Listener {
        public SSHListener(Gateway gw) {
            gateway = gw;
        }
        
        
        public void sshLoginStarted() {
            statusBar.setLeadingMessage("Logging in to gateway " +
                    gateway.getAddress());
            statusBar.progressStarted(progress);
        }
        
        public void sshLoginSucceeded() {
            statusBar.setLeadingMessage("Successfully logged in to " +
                    gateway.getAddress());
            statusBar.progressEnded(progress);
        }
        public void sshLoginFailed() {
            statusBar.setLeadingMessage("Failed to authenticate to " +
                    gateway.getAddress());
            statusBar.progressEnded(progress);
        }
        public void sshTcpChannelStarted(String host, int port) {
            statusBar.setLeadingMessage("Connecting to " + host + ":" + port);
            statusBar.progressStarted(progress);
        }
        
        public void sshTcpChannelFailed(String host, int port) {
            statusBar.setLeadingMessage("Failed to connect to " + host + ":" + port);
            statusBar.progressEnded(progress);
        }
        
        public void sshTcpChannelEstablished(String host, int port) {
            statusBar.setLeadingMessage("Connected to " + host + ":" + port);
            statusBar.progressEnded(progress);
        }
        ProgressEvent progress = new ProgressEvent(this) {
            public boolean isIndeterminate() {
                return true;
            }
        };
        
        Gateway gateway;
        
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem aboutMenuItem;
    private javax.swing.JButton addGatewayButton;
    private javax.swing.JMenuItem addGatewayMenuItem;
    private javax.swing.JButton addHostButton;
    private javax.swing.JMenuItem addHostMenu;
    private javax.swing.JPanel connectButtonPanel;
    private javax.swing.JButton deleteButton;
    private javax.swing.JTextArea descriptionArea;
    private javax.swing.JButton editButton;
    private javax.swing.JMenu editMenu;
    private javax.swing.JMenuItem editMenuDeleteItem;
    private javax.swing.JMenuItem editMenuEditItem;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JMenuItem fileMenuExitItem;
    private javax.swing.JTree gatewayList;
    private javax.swing.JPopupMenu gatewayListPopup;
    private javax.swing.JMenu helpMenu;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JMenuItem listEditMenuItem;
    private javax.swing.JMenuItem listMenuAddGatewayItem;
    private javax.swing.JMenuItem listMenuAddHostItem;
    private javax.swing.JMenuItem listRemoveMenuItem;
    private javax.swing.JMenuItem prefsMenuItem;
    private javax.swing.JButton rdpButton;
    private org.jdesktop.swingx.JXStatusBar statusBar;
    private javax.swing.JButton telnetButton;
    private javax.swing.JButton vncButton;
    private javax.swing.JButton webButton;
    // End of variables declaration//GEN-END:variables
    
    private SDTTreeModel treeModel;
    private Map<String, GatewayConnection> connections;
    ExecutorService swingExec = new SwingExecutorService();
    ExecutorService bgExec = Executors.newSingleThreadExecutor();
    
    private Action deleteAction = new AbstractAction() {
        public void actionPerformed(ActionEvent evt) {
            deleteActionPerformed(evt);
        }
    };
    private Action editAction = new AbstractAction() {
        public void actionPerformed(ActionEvent evt) {
            editActionPerformed(evt);
        }
    };
    private Action newHostAction = new AbstractAction() {
        public void actionPerformed(ActionEvent evt) {
            addHostActionPerformed(evt);
        }
    };
    private Action newGatewayAction = new AbstractAction() {
        public void actionPerformed(ActionEvent evt) {
            addGatewayActionPerformed(evt);
        }
    };
    
}
