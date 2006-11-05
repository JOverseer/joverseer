package org.joverseer.ui.support;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * User: mskounak
 * Date: 13 Οκτ 2006
 * Time: 10:45:44 μμ
 * To change this template use File | Settings | File Templates.
 */
public class TableUtils {
    public static void setTableColumnWidths(JTable table, int[] widths) {
        for (int i=0; i<widths.length; i++) {
            if (i >= table.getColumnModel().getColumnCount()) return;
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }
    }
}
