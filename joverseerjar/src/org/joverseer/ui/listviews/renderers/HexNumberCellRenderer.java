package org.joverseer.ui.listviews.renderers;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;

import org.springframework.richclient.table.BeanTableModel;

public class HexNumberCellRenderer extends AllegianceColorCellRenderer {

	private static final long serialVersionUID = 1L;

	public HexNumberCellRenderer(BeanTableModel tableModel) {
		super(tableModel);
        setHorizontalAlignment(SwingConstants.CENTER);
    }

	/*
	 * (non-Javadoc)
	 * @see org.joverseer.ui.listviews.renderers.AllegianceColorCellRenderer#getTableCellRendererComponent(javax.swing.JTable, java.lang.Object, boolean, boolean, int, int)
	 * render hexes with 4 digits, adding leading 0 if needed, or just blank if it's 0 or null (eg character held hostage)
	 */
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		Component c1 = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		JLabel lbl = (JLabel) c1;
		if (value == null) {
			lbl.setText("");
		} else if (value instanceof Number) {
			Integer v = (Integer) value;
			if (v == 0) {
				lbl.setText("");
			} else {
				lbl.setText(String.format("%04d",value));
			}
		}	
           return c1;
     }
}
