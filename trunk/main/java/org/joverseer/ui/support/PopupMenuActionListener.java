package org.joverseer.ui.support;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * Created by IntelliJ IDEA.
 * User: mskounak
 * Date: 14 Δεκ 2006
 * Time: 10:28:31 μμ
 * To change this template use File | Settings | File Templates.
 */
public abstract class PopupMenuActionListener implements ActionListener {
    public abstract JPopupMenu getPopupMenu();

    public void actionPerformed(ActionEvent e) {
        JPopupMenu pm = getPopupMenu();
        JComponent cmp = (JComponent)e.getSource();
        pm.show(cmp, 0, cmp.getHeight());
    }
}
