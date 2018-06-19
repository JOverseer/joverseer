package org.joverseer.ui.listviews.renderers;

import java.awt.Component;

import javax.swing.JTable;

import org.joverseer.domain.FortificationSizeEnum;
import org.joverseer.domain.PopulationCenterSizeEnum;
import org.joverseer.ui.support.UIUtils;
import org.springframework.richclient.table.BeanTableModel;

public class EnumTableCellRenderer extends AllegianceColorCellRenderer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public EnumTableCellRenderer(BeanTableModel tableModel) {
		super(tableModel);
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {
		if (PopulationCenterSizeEnum.class.isInstance(value) || (FortificationSizeEnum.class.isInstance(value))) {
			value =UIUtils.enumToString(value);
		}
		return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
	}

}
