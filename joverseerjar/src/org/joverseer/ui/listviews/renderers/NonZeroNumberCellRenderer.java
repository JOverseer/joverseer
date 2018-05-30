package org.joverseer.ui.listviews.renderers;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;

import org.springframework.richclient.table.BeanTableModel;

@SuppressWarnings("serial")
public class NonZeroNumberCellRenderer extends AllegianceColorCellRenderer {

	public NonZeroNumberCellRenderer(BeanTableModel tableModel) {
		super(tableModel);
		// TODO Auto-generated constructor stub
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {
		Component c1 = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		JLabel lbl = (JLabel) c1;
		if (value == null) {
			lbl.setText("");
		} else if (value instanceof Number) {
			Integer v = (Integer) value;
			if (v == 0) {
				lbl.setText("");
			}
		}
        return c1;
	}

}
