package org.joverseer.ui.support.controls;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * Abstract action listener that shows a popup menu
 * @author Marios Skounakis
 */
public abstract class PopupMenuActionListener implements ActionListener {
    /**
     * Return the popup menu to show
     */
    public abstract JPopupMenu getPopupMenu();

    @Override
	public void actionPerformed(ActionEvent e) {
        JPopupMenu pm = getPopupMenu();
        JComponent cmp = (JComponent)e.getSource();
        pm.show(cmp, 0, cmp.getHeight());
    }
}
