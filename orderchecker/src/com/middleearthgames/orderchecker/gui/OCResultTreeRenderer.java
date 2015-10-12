package com.middleearthgames.orderchecker.gui;

import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

public class OCResultTreeRenderer extends DefaultTreeCellRenderer {
    @SuppressWarnings("unused")
	private ImageIcon redIcon = new ImageIcon("images/red.gif");
    public ImageIcon getRedIcon() {
		return redIcon;
	}
	public void setRedIcon(ImageIcon redIcon) {
		this.redIcon = redIcon;
	}
	public ImageIcon getYellowIcon() {
		return yellowIcon;
	}
	public void setYellowIcon(ImageIcon yellowIcon) {
		this.yellowIcon = yellowIcon;
	}
	public ImageIcon getGreenIcon() {
		return greenIcon;
	}
	public void setGreenIcon(ImageIcon greenIcon) {
		this.greenIcon = greenIcon;
	}
	public ImageIcon getOrderIcon() {
		return orderIcon;
	}
	public void setOrderIcon(ImageIcon orderIcon) {
		this.orderIcon = orderIcon;
	}
	public ImageIcon getCharIcon() {
		return charIcon;
	}
	public void setCharIcon(ImageIcon charIcon) {
		this.charIcon = charIcon;
	}

	@SuppressWarnings("unused")
	private ImageIcon yellowIcon = new ImageIcon("images/yellow.gif");
    @SuppressWarnings("unused")
	private ImageIcon greenIcon = new ImageIcon("images/green.gif");
    @SuppressWarnings("unused")
	private ImageIcon orderIcon = new ImageIcon("images/order.gif");
    @SuppressWarnings("unused")
	private ImageIcon charIcon = new ImageIcon("images/character.gif");

    private OCDialog parent;
        public ImageIcon getIcon(OCTreeNode node)
        {
            switch(node.getNodeType())
            {
            case OCTreeNode.CHARACTER_NODE: // '\001'
                return getCharIcon();

            case OCTreeNode.ORDER_NODE: // '\0'
                return getOrderIcon();

            case OCTreeNode.RESULT_NODE: // '\002'
                int type = node.getResultType();
                switch(type)
                {
                case OCTreeNode.ERROR_RESULT: // '\004'
                    return getRedIcon();

                case OCTreeNode.WARNING_RESULT: // '\003'
                    return getYellowIcon();

                case OCTreeNode.INFO_RESULT: // '\001'
                case OCTreeNode.HELP_RESULT: // '\002'
                    return getGreenIcon();

                case OCTreeNode.NO_RESULT: // '\0'
                default:
                    return null;
                }
            }
            return null;
        }
        @Override
		public Component getTreeCellRendererComponent(JTree tree1, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus1)
        {
            try {
                super.getTreeCellRendererComponent(tree1, value, sel, expanded, leaf, row, hasFocus1);
                if(value != (Object)(parent.getRoot()))
                {
                	OCTreeNode node = (OCTreeNode)value;
                    setFont(node.getActiveFont());
                    if(node.getNodeType() == 2)
                    {
                        String currentText = getText();
                        if(currentText.length() > 3)
                        {
                            currentText = currentText.substring(4);
                            setText(currentText);
                        }
                    }
                    ImageIcon icon = getIcon(node);
                    if(icon != null)
                        setIcon(icon);
                }
                return this;
            }
            catch (Exception ex) {
                ex.printStackTrace();
                return this;
            }
        }

        public OCResultTreeRenderer(OCDialog parent)
        {
        	this.parent = parent;
        }

}
