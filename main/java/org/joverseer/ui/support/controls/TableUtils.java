package org.joverseer.ui.support.controls;

import javax.swing.JTable;

/**
 * Variou static table utilities
 * 
 * @author Marios Skounakis
 */
public class TableUtils {
	public static void setTableColumnWidths(JTable table, int[] widths) {
		for (int i = 0; i < widths.length; i++) {
			if (i >= table.getColumnModel().getColumnCount())
				return;
			table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
		}
	}
}
