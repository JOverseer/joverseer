package org.joverseer.ui.listviews.renderers;

import java.awt.Component;

import javax.swing.JTable;

import org.joverseer.domain.CharacterDeathReasonEnum;
import org.springframework.richclient.table.BeanTableModel;

/**
 * Cell renderer for CharacterDeathReasonEnum
 * 
 * @author Marios Skounakis
 */
public class DeathReasonEnumRenderer extends AllegianceColorCellRenderer {

    public DeathReasonEnumRenderer(BeanTableModel tableModel) {
        super(tableModel);
    }

    @Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        String strValue = "";
        if (value == null) {
        	strValue = "?";
        } else if (value == CharacterDeathReasonEnum.NotDead) {
        	strValue = "";
        } else {
        	strValue = value.toString();
        }
        return super.getTableCellRendererComponent(table, strValue, isSelected, hasFocus, row, column);
    }

}
