package org.joverseer.ui.support.controls;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import org.joverseer.ui.UISizes;

/**
 * Variou static table utilities
 * 
 * @author Marios Skounakis
 */
public class TableUtils {
	public static void setTableColumnWidths(JTable table, int[] widths, UISizes scaler) {
		for (int i = 0; i < widths.length; i++) {
			if (i >= table.getColumnModel().getColumnCount())
				return;
			table.getColumnModel().getColumn(i).setPreferredWidth(scaler.calculateTableColumnWidth(widths[i]));
		}
	}
	
	public static void setTableColumnWidths(JTable table, int[] widths) {
		for (int i = 0; i < widths.length; i++) {
			if (i >= table.getColumnModel().getColumnCount())
				return;
			table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
		}
	}
	
	/**
	 * Set a cell render for a given table column indexed by column index
	 */
	public static void setTableColumnRenderer(JTable table, int iColumn, TableCellRenderer renderer) {
		table.getColumnModel().getColumn(iColumn).setCellRenderer(renderer);
	}
}
