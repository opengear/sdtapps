/*
 * SDTTreeModel.java
 *
 * Created on January 20, 2006, 6:43 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package sdtconnector;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.event.ListEvent;
import ca.odell.glazedlists.event.ListEventListener;
import sdtconnector.Gateway;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

/**
 *
 * @author wayne
 */
public class SDTTreeModel implements TreeModel {
    
    /** Creates a new instance of SDTTreeModel */
    public SDTTreeModel() {
        SDTManager.getGatewayList().addListEventListener(new ListEventListener() {
            public void listChanged(ListEvent e) {
                gatewayListChanged(e);
            }
        });
        
        // Add listeners for existing gateways
        for (Iterator i = SDTManager.getGatewayList().iterator(); i.hasNext(); ) {
            Gateway gw = (Gateway) i.next();
            GatewayNode node = new GatewayNode(gw);
            gw.getHostList().addListEventListener(node);
            gatewayNodes.add(node);
        }
        
    }
    
    public void gatewayListChanged(ListEvent e) {
        
        EventList l = e.getSourceList();
        while (e.next()) {
            GatewayNode node;
            switch (e.getType()) {
                
                case ListEvent.INSERT:
                    // replicate in the local list
                    Gateway gw = (Gateway) l.get(e.getIndex());
                    node = new GatewayNode(gw);
                    gw.getHostList().addListEventListener(node);
                    gatewayNodes.add(node);
                    fireTreeNodesInserted(new TreeModelEvent(this,
                            new Object[] { root }, new int[] { e.getIndex() },
                            new Object[] { gw }));
                    break;
                case ListEvent.DELETE:
                    node = gatewayNodes.get(e.getIndex());
                    node.gateway.getHostList().removeListEventListener(node);
                    gatewayNodes.remove(e.getIndex());
                    fireTreeNodesRemoved(new TreeModelEvent(this, new Object[] { root },
                            new int[] { e.getIndex() }, new Object[] { node.gateway }));
                    break;
            }
        }
        
    }
    class GatewayNode implements ListEventListener {
        public GatewayNode(Gateway gw) {
            this.gateway = gw;
        }
        public void listChanged(ListEvent e) {
            EventList l = (EventList) e.getSourceList();
            while (e.next()) {
                switch (e.getType()) {
                    case ListEvent.INSERT:
                        
                        Host host = (Host) l.get(e.getIndex());
                        fireTreeNodesInserted(new TreeModelEvent(this,
                                new Object[] { root, gateway }, new int[] { e.getIndex() },
                                new Object[] { host }));
                        break;
                    case ListEvent.DELETE:
                        
                        fireTreeNodesRemoved(new TreeModelEvent(this, new Object[] { root, gateway },
                                new int[] { e.getIndex() }, new Object[] {  }));
                        break;
                }
            }
        }
        protected Gateway gateway;
    }
    public Object getRoot() {
        return root;
    }
    private List list(Object parent) {
        if (getRoot() == parent) {
            return SDTManager.getGatewayList();
        } else if (parent instanceof Gateway) {
            return ((Gateway) parent).getHostList();
        }
        return Collections.emptyList();
    }
    
    public Object getChild(Object parent, int index) {
        return list(parent).get(index);        
    }
    
    public int getChildCount(Object parent) {
        return list(parent).size();        
    }
    public int getIndexOfChild(Object parent, Object child) {
        return list(parent).indexOf(child);        
    }
    public boolean isLeaf(Object node) {
        return node instanceof Host;
    }    
    public void valueForPathChanged(TreePath path, Object newValue) {
        fireTreeNodesChanged(new TreeModelEvent(this, path));
    }    
    
    public void addTreeModelListener(TreeModelListener l) {
        listeners.add(l);
    }
    public void removeTreeModelListener(TreeModelListener l) {
        listeners.remove(l);
    }
    public void fireTreeNodesInserted(TreeModelEvent e) {
        for (TreeModelListener l : listeners) {
            l.treeNodesInserted(e);
        }
    }
    public void fireTreeNodesRemoved(TreeModelEvent e) {
        for (TreeModelListener l : listeners) {
            l.treeNodesRemoved(e);
        }
    }
    
    public void fireTreeNodesChanged(TreeModelEvent e) {
        for (TreeModelListener l : listeners) {
            l.treeNodesChanged(e);
        }
    }
    public TreePath getPath(Gateway gw) {
        return new TreePath(new Object[] { root, gw });
    }
    public TreePath getPath(Gateway gw, Host host) {
        return new TreePath(new Object[] { root, gw, host });
    }
    private List<TreeModelListener> listeners = new ArrayList<TreeModelListener>();
    private List<GatewayNode> gatewayNodes = new ArrayList<GatewayNode>();
    private Object root = this;
}
