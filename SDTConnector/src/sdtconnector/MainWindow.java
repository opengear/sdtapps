/*
 * MainWindow.java
 *
 * Created on January 15, 2006, 9:24 AM
 */

package sdtconnector;

import ca.odell.glazedlists.EventList;
import com.jgoodies.looks.LookUtils;
import java.awt.Component;
import java.awt.Dimension;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.prefs.InvalidPreferencesFormatException;
import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileFilter;
import com.opengear.util.SwingInvocationProxy;
import java.awt.Color;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.ListIterator;
import edu.emory.mathcs.backport.java.util.concurrent.ExecutorService;
import edu.emory.mathcs.backport.java.util.concurrent.Executors;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import net.roydesign.mac.MRJAdapter;
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

        if (SDTURLHelper.hasURL()) {
            if (!cmdConnection()) {
                // Successfully submitted command to an already running
                // SDT Connector, exit
                System.exit(0);
            }
        }

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
        defaultPreferencesMenuItem.setIcon(getMenuIcon("preferences"));
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
        GatewayListCellRenderer renderer = new GatewayListCellRenderer();
        gatewayList.setCellRenderer(renderer);
        renderer.setLeafIcon(getMenuIcon("host"));
        renderer.setClosedIcon(getMenuIcon("gateway"));
        renderer.setOpenIcon(getMenuIcon("gateway"));
        
        //
        // Do not put an etched border on GTK, it does not look right.
        //
        String lafName = UIManager.getLookAndFeel().getClass().getName();
        if (lafName.equals("com.sun.java.swing.plaf.gtk.GTKLookAndFeel")) {
            descriptionScrollPane.setBorder(null);
            gatewayList.setRowHeight(24); // GTK needs a larger row height
        }
        pack();
        
        if (SDTConnector.DEBUG == true) {
            JOptionPane.showMessageDialog(this,
                        "Not for general distribution.",
                        "Debug build",
                        JOptionPane.WARNING_MESSAGE);
        }
        
        // Register browser handler for sdt:// URLs
        registerProtocolHandler();

        
        
        // Auto configure gateways
        for (Object o : SDTManager.getGatewayList()) {
            Gateway gw = (Gateway) o;
            if (!gw.isVolatile()) {
                continue;
            }
            // Runs on the GatewayConnection thread
            retrieveHosts(gw);
        }

        // Launch connection specified by command line arg/protocol handler
        launchSDTURL();
    }

    public void launchSDTURL() {
        if (SDTURLHelper.hasURL()) {
            Gateway gw = SDTURLHelper.getVolatileGateway();
            GatewayConnection conn = connections.get(gw.getActiveAddress());
            if (conn == null) {
                // Establish a new connection
                conn = getGatewayConnection(gw);
            }
            // Queue on the GatewayConnection thread
            conn.launchSDTURL();
        }
    }

    // Returns true if starting the server was successful, or sending a command
    // to an existing server was not successful
    private static final int CMD_PORT = 51599;
    private boolean cmdConnection() {

        InetAddress addr;
        try {
            addr = InetAddress.getByAddress("localhost", new byte[] { 127, 0, 0, 1 });
        } catch (UnknownHostException ex) {
            return true;
        }

        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(CMD_PORT, 999, addr);
        } catch (IOException ex) {}

        if (serverSocket != null) {
            // Kick off server thread
            new Thread(new CmdServer(serverSocket)).start();
            return true;
        }

        String address = System.getProperty("sdt.gateway.address");
        if (address == null || address.isEmpty()) {
            return true;
        }

        // Send the command to server and exit
        Socket clientSocket = new Socket();
        SocketAddress sockaddr = new InetSocketAddress(addr, CMD_PORT);
        PrintWriter out = null;
        try {
            clientSocket.connect(sockaddr, 1000);
            out = new PrintWriter(clientSocket.getOutputStream(), true);

            System.out.println("CmdClient: Connected to server");

            out.println(SDTURLHelper.getURL());
            out.print("sdt.gateway.address ");
            out.println(address);
            out.print("sdt.gateway.sshport ");
            out.println(System.getProperty("sdt.gateway.sshport"));
            out.print("sdt.gateway.username ");
            out.println(System.getProperty("sdt.gateway.username"));
            out.print("sdt.gateway.name ");
            out.println(System.getProperty("sdt.gateway.name"));
            out.print("sdt.gateway.description ");
            out.println(System.getProperty("sdt.gateway.description"));
            out.println(System.getProperty("sdt.privatekey"));

            out.close();
            clientSocket.close();
        } catch (IOException ex) {
            return true;
        }

        return false;
    }
    
    private void registerProtocolHandler() {
        String skip = Settings.getProperty("skipHandlerCheck");

        if (skip.equals("true")) {
            return;
        }

        if (SDTURLHelper.isRegistered() == false) {
            String registerSDTMessage;
            String yesText = "Yes";
            String noText = "No";
            String neverText = "No, don't ask me again";
            Object[] options = { yesText, noText, neverText };

            if (OS.isWindows()) {
                registerSDTMessage = "Use SDTConnector to open sdt:// links?";
            } else {
                registerSDTMessage = "Use SDTConnector to open sdt:// links in Mozilla Firefox?";
            }

            int n = JOptionPane.showOptionDialog(this,
                registerSDTMessage,
                "Enable sdt:// links",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]);
            
            if (n == -1) {
                return;
            }
            
            if (options[n].equals(yesText)) {
                SDTURLHelper.register();
            } else if (options[n].equals(neverText)) {
                Settings.setProperty("skipHandlerCheck", "true");
            }
        }
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        gatewayListPopup = new javax.swing.JPopupMenu();
        listMenuAddGatewayItem = new javax.swing.JMenuItem();
        listMenuAddHostItem = new javax.swing.JMenuItem();
        listEditMenuItem = new javax.swing.JMenuItem();
        listRemoveMenuItem = new javax.swing.JMenuItem();
        connectButtonPanel = new javax.swing.JPanel();
        javax.swing.JScrollPane jScrollPane1 = new javax.swing.JScrollPane();
        gatewayList = new javax.swing.JTree();
        javax.swing.JToolBar jToolBar1 = new javax.swing.JToolBar();
        addGatewayButton = new javax.swing.JButton();
        addHostButton = new javax.swing.JButton();
        editButton = new javax.swing.JButton();
        deleteButton = new javax.swing.JButton();
        javax.swing.JPanel jPanel1 = new javax.swing.JPanel();
        statusBar = new org.jdesktop.swingx.JXStatusBar();
        descriptionScrollPane = new javax.swing.JScrollPane();
        descriptionArea = new javax.swing.JTextArea();
        javax.swing.JPanel jPanel2 = new javax.swing.JPanel();
        javax.swing.JPanel jPanel3 = new javax.swing.JPanel();
        javax.swing.JMenuBar menuBar = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        addGatewayMenuItem = new javax.swing.JMenuItem();
        addHostMenu = new javax.swing.JMenuItem();
        importPreferencesMenuItem = new javax.swing.JMenuItem();
        exportPreferencesMenuItem = new javax.swing.JMenuItem();
        defaultPreferencesMenuItem = new javax.swing.JMenuItem();
        fileMenuExitItem = new javax.swing.JMenuItem();
        editMenu = new javax.swing.JMenu();
        editMenuEditItem = new javax.swing.JMenuItem();
        editMenuDeleteItem = new javax.swing.JMenuItem();
        javax.swing.JSeparator jSeparator1 = new javax.swing.JSeparator();
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
        setTitle("SDTConnector");

        connectButtonPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        connectButtonPanel.setMinimumSize(new java.awt.Dimension(220, 186));
        connectButtonPanel.setPreferredSize(new java.awt.Dimension(220, 186));
        connectButtonPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 2, 2));

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
        statusBar.setDoubleBuffered(false);
        statusBar.setFocusable(false);

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(statusBar, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 616, Short.MAX_VALUE)
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
            .add(0, 308, Short.MAX_VALUE)
        );

        org.jdesktop.layout.GroupLayout jPanel3Layout = new org.jdesktop.layout.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 12, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 308, Short.MAX_VALUE)
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

        defaultPreferencesMenuItem.setText("Restore Defaults");
        defaultPreferencesMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                defaultPreferencesMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(defaultPreferencesMenuItem);

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
            .add(jToolBar1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 620, Short.MAX_VALUE)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 219, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(connectButtonPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 328, Short.MAX_VALUE)
                    .add(descriptionScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 328, Short.MAX_VALUE))
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
                    .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 308, Short.MAX_VALUE)
                    .add(jPanel3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                        .add(connectButtonPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 245, Short.MAX_VALUE)
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
                Preferences.userRoot().node(SDTManager.prefsPath).removeNode();
                Preferences.importPreferences(new FileInputStream(jc.getSelectedFile().getAbsolutePath()));
                Preferences.userRoot().node(SDTManager.prefsPath).sync();
                SDTManager.load();
		updateButtonState();
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
            removeGatewayConnection((Gateway)last);
        } else if (last instanceof Host) {
            SDTManager.removeHost((Gateway) path.getPathComponent(1), (Host) last);
        }
    }
        
    private void gatewayListValueChanged(javax.swing.event.TreeSelectionEvent evt) {//GEN-FIRST:event_gatewayListValueChanged
        TreePath path = gatewayList.getSelectionPath();
        boolean enableActions = false;
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
            } else if (last instanceof Host) {
                desc = ((Host) last).getDescription();
            }
            enableActions = !gw.isVolatile();
        }
        
        descriptionArea.setText(desc);
        
        newHostAction.setEnabled(enableActions);
        editAction.setEnabled(enableActions);
        deleteAction.setEnabled(enableActions);
        
        updateButtonState();
    }//GEN-LAST:event_gatewayListValueChanged

    private void defaultPreferencesMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_defaultPreferencesMenuItemActionPerformed
        int retVal = JOptionPane.showConfirmDialog(Main.getMainWindow(),
                "Restoring default preferences will delete all gateways, hosts,\n" +
                "custom services and clients. Are you sure?",
                "Restore default preferences?",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (retVal == JOptionPane.YES_OPTION) {
            SDTManager.importDefaults();
            SDTManager.load();
        }
    }//GEN-LAST:event_defaultPreferencesMenuItemActionPerformed
    
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
            statusBar.setLeadingMessage("Out of band enabled for " + gw);
            gw.setOob(true);
        } else {
            GatewayConnection conn = connections.get(gw.getActiveAddress());
            if (conn != null) {
                conn.setStopOobListener((GatewayConnection.StopOobListener) SwingInvocationProxy.create(
                        GatewayConnection.StopOobListener.class, new StopOobListener(gw)));
                        conn.stopOob();
            }
            statusBar.setBackground(statusColor);
            statusBar.setLeadingMessage("Out of band disabled for " + gw);
            gw.setOob(false);
        }
    }

    private void autohostsActionPerformed(ActionEvent evt) {
        TreePath path = gatewayList.getSelectionPath();
        if (path == null) {
            return;
        }
        Gateway gw = (Gateway) path.getPathComponent(1);
        retrieveHosts(gw);
    }
    
    public void retrieveHosts(Gateway gw) {
        if (gw.getHostList().isEmpty() == false) {
            if (gw.isVolatile() == false) {
                int retVal = JOptionPane.showConfirmDialog(this,
                        "This will delete all existing hosts for " + gw,
                        "Warning",
                        JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.WARNING_MESSAGE);
                if (retVal == JOptionPane.CANCEL_OPTION) {
                    statusBar.setLeadingMessage(""); // FIXME
                    return;
                }
            }
            
            SDTManager.removeHosts(gw);
        }

        GatewayConnection conn = getGatewayConnection(gw);
        conn.setAutohostsListener((GatewayConnection.AutohostsListener) SwingInvocationProxy.create(
                GatewayConnection.AutohostsListener.class, new AutohostsListener(gw)));
        conn.getHosts();
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
            launchService(gw, host, service);
            /*
            for (ListIterator it = service.getLauncherList().listIterator(); it.hasNext(); ) {
                Launcher l = (Launcher) it.next();
                sshLaunch(gw, host, l);
            }
             */
        }
    }        
    private void updateButtonState() {
        TreePath path = gatewayList.getSelectionPath();
        if (path == null) {
            connectButtonPanel.removeAll();
            ((TitledBorder) connectButtonPanel.getBorder()).setTitle("");
            this.repaint();
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
                serviceButton.setText(s.toString());
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
            connectButtonPanel.revalidate();
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
            connectButtonPanel.revalidate();
            this.repaint();
        } else {
            connectButtonPanel.removeAll();
            ((TitledBorder) connectButtonPanel.getBorder()).setTitle("");
            connectButtonPanel.revalidate();
            this.repaint();
        }
    }
    GatewayConnection.Redirector getRedirectorForSelection(int remotePort, String localHost, int localPort, int udpOverTcpPort) {
        // Stop any redirectors using a requested local port
        for (GatewayConnection gwc : connections.values()) {
            gwc.shutdownConflictingRedirectors(localHost, localPort, udpOverTcpPort);
        }
        TreePath path = gatewayList.getSelectionPath();
        Gateway gw = (Gateway) path.getPathComponent(1);
        Host host = (Host) path.getLastPathComponent();
        
        GatewayConnection conn = getGatewayConnection(gw);
        
        System.out.println("Adding redirection to " + host.getAddress() + ":" + remotePort + " via "
                + gw.getActiveAddress());

        return conn.getRedirector(host.getAddress(), remotePort, localHost, localPort, udpOverTcpPort);
    }
    public void launchService(final Gateway gw, final Host host, final Service service) {
        Object last = gatewayList.getSelectionPath().getLastPathComponent();
        
        TreePath path = treeModel.getPath(gw, host);
        gatewayList.setSelectionPath(path);
        
        for (ListIterator it = service.getLauncherList().listIterator(); it.hasNext(); ) {
            Launcher l = (Launcher) it.next();
            sshLaunch(gw, host, l);
        }
    }
    private void sshLaunch(final Gateway gw, final Host host, final Launcher launcher) {
        GatewayConnection.Redirector r;
        final int boundPort;
        final GatewayConnection conn;

        r = getRedirectorForSelection(launcher.getRemotePort(),launcher.getLocalHost(), launcher.getLocalPort(), launcher.getUdpPort());
        if (r == null) {
            StringBuilder msg = new StringBuilder("Failed to redirect ");
            
            msg.append("local TCP port " + (launcher.getLocalPort() == 0 ? "(any)" : String.valueOf(launcher.getLocalPort())));
            if (launcher.getUdpPort() != 0) {
                msg.append(", local UDP port " + String.valueOf(launcher.getUdpPort()));
            }
            if (OS.isWindows() == false) {
                if ((launcher.getLocalPort() > 0 && launcher.getLocalPort() < 1024)
                    || (launcher.getUdpPort() > 0 && launcher.getUdpPort() < 1024))
                {
                    msg.append(", root privileges required");
                }
            }
            statusBar.setText(msg.toString());
            return;
        }
        boundPort = r.getLocalPort();
        conn = getGatewayConnection(gw);

        //getGlassPane().setVisible(true);
        bgExec.execute(new Runnable() {
            public void run() {
                if (conn.login()) {
                    if (launcher.getClient() != null) {
                        statusBar.setText("Launching " + launcher.getClient());
                        int localPort = launcher.getLocalPort();
                        launcher.setLocalPort(boundPort);
                        if (!launcher.launch()) {
                            statusBar.setText("Failed, check settings in Edit -> Preferences -> Clients -> "
                                    + launcher.getClient() + " -> Edit");
                            String[] cmd = launcher.getClient().getCommand(launcher.getLocalHost(), boundPort);
                            System.out.println(cmd[0] + " failed");
                        }
                        launcher.setLocalPort(localPort);
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
        GatewayConnection conn = connections.get(gw.toString());
        
        if (conn != null && conn.getUsername().equals(gw.getUsername()) && conn.getPassword().equals(gw.getPassword())) {
            return conn;
        } else if (conn != null) {
            // Re-create the connection if the username has changed between logins
            conn.shutdown();
        }
        // Create new gateway connection
        conn = new GatewayConnection(gw,
                (GatewayConnection.Authentication) SwingInvocationProxy.create(
                GatewayConnection.Authentication.class,
                new GatewayAuth(gw)));
        conn.setSSHListener((GatewayConnection.SSHListener) SwingInvocationProxy.create(
                GatewayConnection.SSHListener.class, new SSHListener(gw)));
        connections.put(gw.toString(), conn);
        
        return conn;
    }
    private void removeGatewayConnection(Gateway gw) {
        GatewayConnection conn = connections.get(gw.toString());
        if (conn != null) {
            conn.shutdown();
            connections.remove(gw.toString());
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

        private PromptDialog showPrompt(String title, String prompt) {
            PromptDialog dlg = new PromptDialog(MainWindow.this, true);
            dlg.setTitle(title);
            dlg.setPrompt(prompt);
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

        public String[] doPrompt(String instructions, String[] prompts, boolean[] echo) {
            StringBuffer prompt = new StringBuffer();
            String response[] = new String[prompts.length];
            prompt.append("<HTML>");
            for (String s : prompts) {
                prompt.append(s);
                prompt.append("<BR>");
            }
            prompt.append("</HTML>");

            PromptDialog dlg = showPrompt("Authentication", prompt.toString());
             if (dlg.getReturnStatus() == LoginDialog.RET_CANCEL) {
                return null;
            }

            response[0] = dlg.getResponse();
            return response;
        }

    }
    class SSHListener implements GatewayConnection.SSHListener {
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
	
    class StopOobListener implements GatewayConnection.StopOobListener {
        public StopOobListener(Gateway gw) {
            gateway = gw;
        }
        public void stopOobStarted() {
			for (Component c : connectButtonPanel.getComponents()) {
				c.setEnabled(false);
			}
			connectButtonPanel.repaint();
            statusBar.setLeadingMessage("Stopping out of band connection to " +
                    gateway);
            statusBar.progressStarted(progress);
        }
        public void stopOobSucceeded() {
            connections.remove(gateway.getActiveAddress());
            statusBar.progressEnded(progress);
			updateButtonState();
        }
        public void stopOobFailed() {
			JOptionPane.showMessageDialog(Main.getMainWindow(),
					"Unable to stop the out of band connection,\nyou must stop it manually.",
					"Error",
					JOptionPane.ERROR_MESSAGE);
            statusBar.setLeadingMessage("Out of band disabled for " + gateway);
            statusBar.setBackground(statusColor);
            connections.remove(gateway.getActiveAddress());
            statusBar.progressEnded(progress);
			updateButtonState();
        }
		
        ProgressEvent progress = new ProgressEvent(this) {
            public boolean isIndeterminate() {
                return true;
            }
        };
        
        Gateway gateway;
	}
    class AutohostsListener implements GatewayConnection.AutohostsListener {
        public AutohostsListener(Gateway gw) {
            gateway = gw;
        }
        public void autohostsStarted() {
			for (Component c : connectButtonPanel.getComponents()) {
				c.setEnabled(false);
			}
			connectButtonPanel.repaint();
            statusBar.setLeadingMessage("Retrieving hosts from " +
                    gateway);
            statusBar.progressStarted(progress);
        }
        public void autohostsSucceeded(EventList hosts) {
            if (hosts != null && hosts.isEmpty() == false) {
                    EventList hl = gateway.getHostList();
                    while (hl.isEmpty() == false) {
                            Host h = (Host) hl.get(0);
                            SDTManager.removeHost(gateway, h);
                    }
                    for (Object o : hosts) {
                            Host h = (Host) o;
                            SDTManager.addHost(gateway, h);
                    }
            }
		
            statusBar.setLeadingMessage("Successfully retrieved hosts from " +
                    gateway);
            statusBar.progressEnded(progress);
            updateButtonState();
        }
        public void autohostsFailed() {
            statusBar.setLeadingMessage("Failed to retrieve hosts from " +
                    gateway);
            statusBar.progressEnded(progress);
            updateButtonState();
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
    private javax.swing.JMenuItem defaultPreferencesMenuItem;
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
