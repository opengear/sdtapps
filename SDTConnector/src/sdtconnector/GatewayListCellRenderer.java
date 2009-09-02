package sdtconnector;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import static com.opengear.util.IconLoader.getMenuIcon;

class GatewayListCellRenderer extends DefaultTreeCellRenderer {

    public GatewayListCellRenderer() {
    }

    public Component getTreeCellRendererComponent(
                        JTree tree,
                        Object value,
                        boolean sel,
                        boolean expanded,
                        boolean leaf,
                        int row,
                        boolean hasFocus) {

        super.getTreeCellRendererComponent(
                tree, value, sel,
                expanded, leaf, row,
                hasFocus);

        if (value instanceof Gateway) {
            int style = getFont().getStyle();
            if (((Gateway)value).isVolatile()) {
                style |= Font.ITALIC;
            } else {
                style &= ~Font.ITALIC;
            }
            setFont(new Font(getFont().getName(), style, getFont().getSize()));
        }

        return this;
    }
}
