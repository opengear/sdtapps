/*
 * MainWindow.java
 *
 * Created on January 15, 2006, 9:24 AM
 */

package sdtconnector;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import com.jcraft.jsch.UserInfo;
import com.jgoodies.looks.LookUtils;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.prefs.InvalidPreferencesFormatException;
import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileFilter;
import sdtconnector.Gateway;
import sdtconnector.GatewayConnection;
import sdtconnector.LoginDialog;
import com.opengear.util.SwingExecutorService;
import com.opengear.util.SwingInvocationProxy;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.ListIterator;
import edu.emory.mathcs.backport.java.util.concurrent.ExecutorService;
import edu.emory.mathcs.backport.java.util.concurrent.Executors;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.border.LineBorder;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellEditor;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import net.roydesign.mac.MRJAdapter;
import org.jdesktop.swingx.JXLoginDialog;
import org.jdesktop.swingx.JXLoginPanel;
import org.jdesktop.swingx.event.ProgressEvent;
import org.jdesktop.swingx.util.OS;
import org.jdesktop.swingx.util.WindowUtils;
import static com.opengear.util.IconLoader.getIcon;
import static com.opengear.util.IconLoader.getMenuIcon;
import static com.opengear.util.IconLoader.getButtonIcon;
import static com.opengear.util.IconLoader.getToolbarIcon;

public class MainWindow extends javax.swing.JFrame {
    
    /** Creates new form MainWindow */
    public MainWindow() {
        initComponents();
        setIconImage(getIcon("16x16", "gateway").getImage());
        statusColor = statusBar.getBackground();
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
                } else if (last instanceof Host) {
                    descriptionArea.setText(((Host) last).getDescription());
                } else if (last instanceof Service) {
                    descriptionArea.setText(((Service) last).getName());
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
        
        listMenuAddGatewayItem.setAction(newGatewayAction);
        addGatewayMenuItem.setAction(newGatewayAction);
        addGatewayButton.setAction(newGatewayAction);
        
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
        exportPreferencesMenuItem.setIcon(getMenuIcon("save-as"));
        importPreferencesMenuItem.setIcon(getMenuIcon("open"));
        aboutMenuItem.setIcon(getMenuIcon("info"));
        fileMenuExitItem.setIcon(getMenuIcon("exit"));
        addGatewayButton.setIcon(getToolbarIcon("gateway"));
        addGatewayButton.setText("");
        addGatewayButton.setToolTipText("Create a secure tunnel to a gateway");
        addHostButton.setIcon(getToolbarIcon("host"));
        addHostButton.setText("");
        addHostButton.setToolTipText("Add a host to access via the secure tunnel");
        editButton.setIcon(getToolbarIcon("edit"));
        editButton.setText("");
        editButton.setToolTipText("Edit");
        deleteButton.setIcon(getToolbarIcon("delete"));
        deleteButton.setText("");
        deleteButton.setToolTipText("Delete");
        
        // Disable all but new gateway action on empty list
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
        // Fixup things to look slightly better on MaxOSX
        //
        if (OS.isMacOSX()) {
            fileMenu.remove(fileMenuExitItem);
            MRJAdapter.addAboutListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    aboutMenuItemActionPerformed(e);
                }
            });
            MRJAdapter.addPreferencesListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    prefsMenuItemActionPerformed(e);
                }
            });
            MRJAdapter.addQuitApplicationListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    fileMenuExitItemActionPerformed(e);
                }
            });
        }
        
        //
        // Do not put an etched border on GTK, it does not look right.
        //
        String lafName = UIManager.getLookAndFeel().getClass().getName();
        if (lafName.equals("com.sun.java.swing.plaf.gtk.GTKLookAndFeel")) {
            descriptionScrollPane.setBorder(null);
            gatewayList.setRowHeight(24); // GTK needs a larger row height
        }
        pack();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        javax.swing.JPanel jPanel1;
        javax.swing.JPanel jPanel2;
        javax.swing.JPanel jPanel3;
        javax.swing.JScrollPane jScrollPane1;
        javax.swing.JSeparator jSeparator1;
        javax.swing.JToolBar jToolBar1;
        javax.swing.JMenuBar menuBar;

        gatewayListPopup = new javax.swing.JPopupMenu();
        listMenuAddGatewayItem = new javax.swing.JMenuItem();
        listMenuAddHostItem = new javax.swing.JMenuItem();
        listEditMenuItem = new javax.swing.JMenuItem();
        listRemoveMenuItem = new javax.swing.JMenuItem();
        connectButtonPanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        gatewayList = new javax.swing.JTree();
        jToolBar1 = new javax.swing.JToolBar();
        addGatewayButton = new javax.swing.JButton();
        addHostButton = new javax.swing.JButton();
        editButton = new javax.swing.JButton();
        deleteButton = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        statusBar = new org.jdesktop.swingx.JXStatusBar();
        descriptionScrollPane = new javax.swing.JScrollPane();
        descriptionArea = new javax.swing.JTextArea();
        jPanel2 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        menuBar = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        addGatewayMenuItem = new javax.swing.JMenuItem();
        addHostMenu = new javax.swing.JMenuItem();
        importPreferencesMenuItem = new javax.swing.JMenuItem();
        exportPreferencesMenuItem = new javax.swing.JMenuItem();
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
        setTitle("Opengear SDTConnector");
        connectButtonPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 2, 2));

        connectButtonPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        connectButtonPanel.setMinimumSize(new java.awt.Dimension(220, 186));
        connectButtonPanel.setPreferredSize(new java.awt.Dimension(220, 186));

        gatewayList.setRootVisible(false);
        gatewayList.setRowHeight(20);
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
        jToolBar1.setMargin(new java.awt.Insets(0, 2, 0, 0));
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
        statusBar.setFocusable(false);

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(statusBar, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 537, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(statusBar, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
        );

        descriptionScrollPane.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        descriptionScrollPane.setOpaque(false);
        descriptionArea.setBackground(javax.swing.UIManager.getDefaults().getColor("Panel.background"));
        descriptionArea.setColumns(20);
        descriptionArea.setEditable(false);
        descriptionArea.setLineWrap(true);
        descriptionArea.setRows(3);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setAutoscrolls(false);
        descriptionArea.setBorder(null);
        descriptionArea.setFocusable(false);
        descriptionArea.setMargin(new java.awt.Insets(3, 3, 3, 3));
        descriptionArea.setOpaque(false);
        descriptionArea.setRequestFocusEnabled(false);
        descriptionScrollPane.setViewportView(descriptionArea);

        org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 11, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 247, Short.MAX_VALUE)
        );

        org.jdesktop.layout.GroupLayout jPanel3Layout = new org.jdesktop.layout.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 12, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 247, Short.MAX_VALUE)
        );

        fileMenu.setText("File");
        addGatewayMenuItem.setText("New Gateway");
        addGatewayMenuItem.setRequestFocusEnabled(false);
        fileMenu.add(addGatewayMenuItem);

        addHostMenu.setText("New Host");
        addHostMenu.setRequestFocusEnabled(false);
        fileMenu.add(addHostMenu);

        importPreferencesMenuItem.setText("Import Preferences");
        importPreferencesMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                importPreferencesMenuItemActionPerformed(evt);
            }
        });

        fileMenu.add(importPreferencesMenuItem);

        exportPreferencesMenuItem.setText("Export Preferences");
        exportPreferencesMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exportPreferencesMenuItemActionPerformed(evt);
            }
        });

        fileMenu.add(exportPreferencesMenuItem);

        fileMenuExitItem.setText("Exit");
        fileMenuExitItem.setRequestFocusEnabled(false);
        fileMenuExitItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fileMenuExitItemActionPerformed(evt);
            }
        });

        fileMenu.add(fileMenuExitItem);

        menuBar.add(fileMenu);

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

        menuBar.add(editMenu);

        helpMenu.setText("Help");
        aboutMenuItem.setText("About");
        aboutMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aboutMenuItemActionPerformed(evt);
            }
        });

        helpMenu.add(aboutMenuItem);

        menuBar.add(helpMenu);

        setJMenuBar(menuBar);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jToolBar1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 541, Short.MAX_VALUE)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 219, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(connectButtonPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 269, Short.MAX_VALUE)
                    .add(descriptionScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 269, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
            .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(jToolBar1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 247, Short.MAX_VALUE)
                    .add(jPanel3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                        .add(connectButtonPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(descriptionScrollPane, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 55, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 26, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );
        pack();
    }// </editor-fold>//GEN-END:initComponents
static FileFilter xmlFileFilter = new FileFilter() {
        public boolean accept(File f) {
            return f.isDirectory() || f.getName().toLowerCase().endsWith(".xml");
        }
        public String getDescription() {
            return "XML files";
        }
    };
    private void exportPreferencesMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exportPreferencesMenuItemActionPerformed
        JFileChooser jc = new JFileChooser();
        jc.setDialogTitle("Export SDTConnector preferences");
        jc.setFileSelectionMode(jc.FILES_ONLY);
        jc.setFileFilter(xmlFileFilter);
        jc.setMultiSelectionEnabled(false);
        if (jc.showDialog(this, "OK") == JFileChooser.APPROVE_OPTION) {
            
            Preferences node = Preferences.userRoot().node("opengear/sdtconnector");
            try {
                node.exportSubtree(new FileOutputStream(jc.getSelectedFile().getAbsolutePath()));
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
            } catch (BackingStoreException ex) {
                ex.printStackTrace();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            
        }
    }//GEN-LAST:event_exportPreferencesMenuItemActionPerformed
    
    private void importPreferencesMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_importPreferencesMenuItemActionPerformed
        
        JFileChooser jc = new JFileChooser();
        jc.setDialogTitle("Import SDTConnector preferences");
        jc.setFileSelectionMode(jc.FILES_ONLY);
        jc.setFileFilter(xmlFileFilter);
        jc.setMultiSelectionEnabled(false);
        
        
        if (jc.showDialog(this, "OK") == JFileChooser.APPROVE_OPTION) {
            try {
                String path = "opengear/sdtconnector";
                
                Preferences.userRoot().node(path).removeNode();
                Preferences.importPreferences(new FileInputStream(jc.getSelectedFile().getAbsolutePath()));
                Preferences.userRoot().node(path).sync();
                SDTManager.load();
            } catch (FileNotFoundException ex) {
            } catch (java.util.prefs.BackingStoreException ex) {
            } catch (IOException ex) {
            } catch (InvalidPreferencesFormatException ex) {
            }
        }
    }//GEN-LAST:event_importPreferencesMenuItemActionPerformed
        
    private void aboutMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aboutMenuItemActionPerformed
        JDialog dlg = new AboutDialog(this, true, SDTConnector.VERSION);
        dlg.setLocationRelativeTo(this);
        dlg.setVisible(true);
    }//GEN-LAST:event_aboutMenuItemActionPerformed
    
    private void fileMenuExitItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fileMenuExitItemActionPerformed
        setVisible(false);
        dispose();
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                System.exit(0);
            }
        });
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
            // Services can only be added at or below a host node
            gatewayListPopup.show(evt.getComponent(), evt.getX(), evt.getY());
        }
    }
    
    private void prefsMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_prefsMenuItemActionPerformed
        
        PreferencesDialog dlg = new PreferencesDialog(this, true);
        dlg.setLocationRelativeTo(this);
        dlg.setVisible(true);
        updateButtonState();
    }//GEN-LAST:event_prefsMenuItemActionPerformed
        
    private void removeSelectedNode(TreePath path) {
        Object last = path.getLastPathComponent();
        if (last instanceof Gateway) {
            SDTManager.removeGateway((Gateway)last);
            removeGatewayConnection(last.toString());
        } else if (last instanceof Host) {
            SDTManager.removeHost((Gateway) path.getPathComponent(1), (Host) last);
        }
    }
        
    private void gatewayListValueChanged(javax.swing.event.TreeSelectionEvent evt) {//GEN-FIRST:event_gatewayListValueChanged
        TreePath path = gatewayList.getSelectionPath();
        boolean isGateway = false;
        boolean isHost = false;
        String desc = "";
        String hint = "";
        if (path != null) {
             Gateway gw = (Gateway) path.getPathComponent(1);
            if (gw.getOob()) {
                statusBar.setBackground(Color.pink);
                statusBar.setLeadingMessage("Out of band enabled");
            } else {
                 if (statusBar.getLeadingMessage().equals("Out of band enabled")) {
                     statusBar.setLeadingMessage("");
                 }
                 statusBar.setBackground(statusColor);
            }
            Object last = path.getLastPathComponent();
            if (last instanceof Gateway) {
                desc = ((Gateway) last).getDescription();
                isGateway = true;
            } else if (last instanceof Host) {
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
        TreePath path = gatewayList.getSelectionPath();
        if (path == null) {
            return;
        }
        Object last = path.getLastPathComponent();
        String msg = "";
        if (last instanceof Gateway) {
            msg = "Are you sure you want to delete gateway "
                    + last.toString() + "\n"
                    + "and all the hosts connected to it?";
        } else {
            msg = "Are you sure you want to delete host " + last.toString() + "?";
        }
        int retVal = JOptionPane.showConfirmDialog(this, msg, "Confirm Deletion",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (retVal == JOptionPane.OK_OPTION) {
            removeSelectedNode(gatewayList.getSelectionPath());
            connectButtonPanel.removeAll();
            descriptionArea.setText("");
            this.repaint();
        }
    }
    
    private void addGatewayActionPerformed(ActionEvent evt) {
        Gateway gw = new Gateway();
        GatewayDialog dlg = new GatewayDialog(this, true, gw);
        dlg.setLocationRelativeTo(this);
        dlg.setTitle("New SDT Gateway");
        dlg.setVisible(true);
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

    private void oobActionPerformed(ActionEvent evt) {
        TreePath path = gatewayList.getSelectionPath();
        if (path == null) {
            return;
        }
        Gateway gw = (Gateway) path.getPathComponent(1);
        boolean oob = !gw.getOob();
        if (oob) {
            statusBar.setBackground(Color.pink);
            statusBar.setLeadingMessage("Out of band enabled");
        } else {
            GatewayConnection conn = connections.get(gw.getActiveAddress());
            if (conn != null) {
                statusBar.setLeadingMessage("Stopping out of band connection to " +
                        gw);
                ProgressEvent progress = new ProgressEvent(this) {
                public boolean isIndeterminate() {
                    return true;
                }
                };
                statusBar.progressStarted(progress);
                conn.stopOob();
                connections.remove(gw.getActiveAddress());
                statusBar.progressEnded(progress);
            }
            statusBar.setLeadingMessage("Out of band disabled");
            statusBar.setBackground(statusColor);
        }
        gw.setOob(oob);
    }

    private void autohostsActionPerformed(ActionEvent evt) {
        TreePath path = gatewayList.getSelectionPath();
        if (path == null) {
            return;
        }
        Gateway gw = (Gateway) path.getPathComponent(1);
        
        if (gw.getHostList().isEmpty() == false) {
            int retVal = JOptionPane.showConfirmDialog(null, 
                    "This will delete all existing hosts for " + gw,
                    "Warning",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.WARNING_MESSAGE);
            if (retVal == JOptionPane.CANCEL_OPTION) {
                return;
            }
        }
        
        GatewayConnection conn = getGatewayConnection(gw);
		EventList hosts = conn.getHosts();
		int i = 0;
    }

    private void editSelectedNode(final TreePath path) {
        Object last = path.getLastPathComponent();
        TreeModel model = (TreeModel) gatewayList.getModel();
        Gateway gw = (Gateway) path.getPathComponent(1);
        JDialog dlg;
        
        if (last instanceof Gateway) {
            dlg = new GatewayDialog(this, true, gw);
            dlg.setTitle("Edit SDT Gateway");
            dlg.setLocationRelativeTo(this);
            dlg.pack();
            dlg.setVisible(true);
            SDTManager.updateGateway(gw);
        } else if (last instanceof Host) {
            Host host = (Host) last;
            dlg = new AddHostDialog(this, true, host);
            dlg.setTitle("Edit SDT Host");
            dlg.setLocationRelativeTo(this);
            dlg.pack();
            dlg.setVisible(true);
            SDTManager.updateHost(gw, host);
        }
        model.valueForPathChanged(path, last);
        updateButtonState();
    }
    private void serviceButtonActionPerformed(java.awt.event.ActionEvent evt) {
            Service service = null;
        for (Object o : SDTManager.getServiceList()) {
            service = (Service) o;
            if (service.getRecordID() == Integer.parseInt(evt.getActionCommand())) {
                break;
            }
        }
        if (service != null) {
            TreePath path = gatewayList.getSelectionPath();
            if (path == null) {
                return;
            }
            Gateway gw = (Gateway) path.getPathComponent(1);
            Host host = (Host) path.getLastPathComponent();
            for (ListIterator it = service.getLauncherList().listIterator(); it.hasNext(); ) {
                Launcher l = (Launcher) it.next();
                sshLaunch(gw, host, l);
            }
        }
    }        
    private void updateButtonState() {
        TreePath path = gatewayList.getSelectionPath();
        if (path == null) {
            return;
        }
        Object last = path.getLastPathComponent();
        Host host = null;
        boolean isHost = last instanceof Host;
        boolean isGateway = last instanceof Gateway;
        if (isHost) {
            host = (Host) last;
            connectButtonPanel.removeAll();
            for (Object o : host.getServiceList()) {
                Service s = (Service) o;
                javax.swing.JButton serviceButton = new javax.swing.JButton();
                Insets panelInsets = connectButtonPanel.getInsets();
                int buttonWidth = ((connectButtonPanel.getWidth()
                    - panelInsets.left - panelInsets.right) / 2) - 2; // FlowLayout hgap is 2
                
                serviceButton.setPreferredSize(new Dimension(buttonWidth, 32));
                serviceButton.setActionCommand(String.valueOf(s.getRecordID()));
                serviceButton.setText(s.getName());
                // For some pre-canned clients we can't guess the executable path, make the user set it
                if (s.getFirstLauncher() != null
                        && s.getFirstLauncher().getClient() != null
                        && s.getFirstLauncher().getRecordID() < SDTManager.initialRecordID()
                        && s.getFirstLauncher().getClient().getPath().equals(""))
                {
                    serviceButton.setToolTipText("Set client executable for " + s.getFirstLauncher().getClient()
                            + "\r\n in Edit -> Preferences -> Clients");
                    serviceButton.setEnabled(false);
                } else {
                    serviceButton.setToolTipText("Launch " + s + " connection to " + host);
                }
                serviceButton.setIcon(getButtonIcon(s.getIcon()));
                serviceButton.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        serviceButtonActionPerformed(evt);
                    }
                });                
                connectButtonPanel.add(serviceButton);
            }
            ((TitledBorder) connectButtonPanel.getBorder()).setTitle("Services");
            pack();
            this.repaint();
        }  else if (isGateway) {
            Insets panelInsets = connectButtonPanel.getInsets();
            int buttonWidth = ((connectButtonPanel.getWidth()
                - panelInsets.left - panelInsets.right) / 2) - 2; // FlowLayout hgap is 2

            connectButtonPanel.removeAll();
            //
            // Display OOB button
            //
            javax.swing.JToggleButton oobButton = new javax.swing.JToggleButton();
            
            oobButton.setPreferredSize(new Dimension(buttonWidth, 32));
            oobButton.setAction(oobAction);
            oobButton.setText("Out Of Band");
            oobButton.setSelected(((Gateway)last).getOob());
            if (((Gateway)last).getOobAddress().equals("")) {
                oobButton.setEnabled(false);
                oobButton.setToolTipText("To setup out of band mode, double click "
                        + (Gateway)last + " and select the Out Of Band tab");
            }
            connectButtonPanel.add(oobButton);
            //
            // Display auto-configure hosts button
            //
            javax.swing.JButton autohostsButton = new javax.swing.JButton();
            
            autohostsButton.setPreferredSize(new Dimension(buttonWidth, 32));
            autohostsButton.setAction(autohostsAction);
            autohostsButton.setText("Retrieve Hosts");
            connectButtonPanel.add(autohostsButton);
            
            ((TitledBorder) connectButtonPanel.getBorder()).setTitle("Gateway Actions");
            pack();
            this.repaint();
        } else {
            connectButtonPanel.removeAll();
            this.repaint();
        }
    }
    GatewayConnection.Redirector getRedirectorForSelection(int port, String lhost, int lport, int uport) {
        TreePath path = gatewayList.getSelectionPath();
        Gateway gw = (Gateway) path.getPathComponent(1);
        Host host = (Host) path.getLastPathComponent();
        GatewayConnection conn = getGatewayConnection(gw);
        System.out.println("Adding redirection to " + host.getAddress() + ":" + port + " via "
                + gw.getActiveAddress());
            return conn.getRedirector(host.getAddress(), port, lhost, lport, uport);
    }
    private void sshLaunch(Gateway gw, final Host host, final Launcher launcher) {
        final int boundPort = getRedirectorForSelection(launcher.getRemotePort(), launcher.getLocalHost(), launcher.getLocalPort(), launcher.getUdpPort()).getLocalPort();
        final GatewayConnection conn = getGatewayConnection(gw);
        //getGlassPane().setVisible(true);
        bgExec.execute(new Runnable() {
            public void run() {
                if (conn.login()) {
                    if (launcher.getClient() != null) {
                        String cmd = launcher.getClient().getCommand(launcher.getLocalHost(), boundPort);
                        statusBar.setText("Launching " + cmd);
                        int localPort = launcher.getLocalPort();
                        launcher.setLocalPort(boundPort);
                        if (!launcher.launch()) {
                            statusBar.setText(cmd + " failed");
                        }
                        launcher.setLocalPort(localPort);
                    } else {
                        statusBar.setText("No client to launch");
                    }
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
        GatewayConnection conn = connections.get(gw.getActiveAddress());
        if (conn == null) {
            conn = new GatewayConnection(gw,
                    (GatewayConnection.Authentication) SwingInvocationProxy.create(
                    GatewayConnection.Authentication.class,
                    new GatewayAuth(gw)));
            conn.setListener((GatewayConnection.Listener) SwingInvocationProxy.create(
                    GatewayConnection.Listener.class, new SSHListener(gw)));
            connections.put(gw.getActiveAddress(), conn);
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
            username = gw.getUsername();
        }
        private LoginDialog showDialog(String title, int mode) {
            LoginDialog dlg = new LoginDialog(MainWindow.this, true);
            dlg.setTitle(title);
            dlg.setUsername(gateway.getUsername());
            dlg.setPassword(password);
            dlg.setMode(mode);
            dlg.setLocationRelativeTo(MainWindow.this);
            if (LookUtils.IS_JAVA_5_OR_LATER) {
                dlg.setLocation(WindowUtils.getPointForCentering(dlg));
            }
            dlg.pack();
            dlg.setVisible(true);
            return dlg;
        }
        public boolean promptAuthentication(String prompt) {
            LoginDialog dlg = showDialog(prompt, LoginDialog.PASSWORD);
            
            if (dlg.getReturnStatus() == LoginDialog.RET_CANCEL) {
                return false;
            }
            password = dlg.getPassword();
            username = dlg.getUsername();
            return true;
        }
        public boolean promptPassphrase(String prompt) {
            LoginDialog dlg = showDialog(prompt, LoginDialog.PASSPHRASE);
            if (dlg.getReturnStatus() == LoginDialog.RET_CANCEL) {
                return false;
            }
            passphrase = dlg.getPassword();
            username = dlg.getUsername();
            return true;
        }
        public String getUsername() {
            return username;
        }
        
        public String getPassword() {
            return password;
        }
        public String getPassphrase() {
            return passphrase;
        }
        Gateway gateway;
        String passphrase = "";
        String username = "";
        String password = "";
    }
    class SSHListener implements GatewayConnection.Listener {
        public SSHListener(Gateway gw) {
            gateway = gw;
        }
        
        
        public void sshLoginStarted() {
            statusBar.setLeadingMessage("Logging in to gateway " +
                    gateway);
            statusBar.progressStarted(progress);
        }
  
        public void sshLoginSucceeded() {
            statusBar.setLeadingMessage("Successfully logged in to " +
                    gateway);
            statusBar.progressEnded(progress);
        }
        public void sshLoginFailed() {
            statusBar.setLeadingMessage("Failed to authenticate to " +
                    gateway);
            statusBar.progressEnded(progress);
        }
        
        public void oobStarted() {
            statusBar.setLeadingMessage("Starting out of band connection to " +
                    gateway);
            statusBar.progressStarted(progress);
        }
        
        public void oobSucceeded() {
            statusBar.setLeadingMessage("Established out of band connection to " +
                    gateway);
            statusBar.progressEnded(progress);
        }
        public void oobFailed() {
            statusBar.setLeadingMessage("Out of band connection to " + 
                    gateway + " failed");
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
    private javax.swing.JScrollPane descriptionScrollPane;
    private javax.swing.JButton editButton;
    private javax.swing.JMenu editMenu;
    private javax.swing.JMenuItem editMenuDeleteItem;
    private javax.swing.JMenuItem editMenuEditItem;
    private javax.swing.JMenuItem exportPreferencesMenuItem;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JMenuItem fileMenuExitItem;
    private javax.swing.JTree gatewayList;
    private javax.swing.JPopupMenu gatewayListPopup;
    private javax.swing.JMenu helpMenu;
    private javax.swing.JMenuItem importPreferencesMenuItem;
    private javax.swing.JMenuItem listEditMenuItem;
    private javax.swing.JMenuItem listMenuAddGatewayItem;
    private javax.swing.JMenuItem listMenuAddHostItem;
    private javax.swing.JMenuItem listRemoveMenuItem;
    private javax.swing.JMenuItem prefsMenuItem;
    private org.jdesktop.swingx.JXStatusBar statusBar;
    // End of variables declaration//GEN-END:variables
    
    private SDTTreeModel treeModel;
    private Map<String, GatewayConnection> connections;
    ExecutorService swingExec = new SwingExecutorService();
    ExecutorService bgExec = Executors.newSingleThreadExecutor();
    private Color statusColor;
    
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
    private Action oobAction = new AbstractAction() {
        public void actionPerformed(ActionEvent evt) {
            oobActionPerformed(evt);
        }
    };
    private Action autohostsAction = new AbstractAction() {
        public void actionPerformed(ActionEvent evt) {
            autohostsActionPerformed(evt);
        }
    };
}
