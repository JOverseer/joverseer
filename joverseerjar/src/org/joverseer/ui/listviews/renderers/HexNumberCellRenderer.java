package org.joverseer.ui.listviews.renderers;

import java.awt.Component;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import org.springframework.richclient.table.BeanTableModel;

public class HexNumberCellRenderer extends AllegianceColorCellRenderer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public HexNumberCellRenderer(BeanTableModel tableModel) {
		super(tableModel);
        setHorizontalAlignment(SwingConstants.CENTER);
    }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            if (value instanceof Number) {
                value = String.format("%04d",value);
            }
            return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        }
}

