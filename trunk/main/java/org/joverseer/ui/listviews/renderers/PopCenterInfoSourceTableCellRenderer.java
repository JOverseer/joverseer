package org.joverseer.ui.listviews.renderers;

import java.awt.Component;

import javax.swing.JTable;

import org.joverseer.support.infoSources.InfoSource;
import org.joverseer.support.infoSources.MetadataSource;
import org.joverseer.support.infoSources.PopCenterXmlInfoSource;
import org.springframework.richclient.table.BeanTableModel;

public class PopCenterInfoSourceTableCellRenderer extends AllegianceColorCellRenderer {

    public PopCenterInfoSourceTableCellRenderer(BeanTableModel tableModel) {
        super(tableModel);
    }

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        String strValue = "";
        if (InfoSource.class.isInstance(value)) {
            if (PopCenterXmlInfoSource.class.isInstance(value)) {
            	PopCenterXmlInfoSource sis = (PopCenterXmlInfoSource)value;
            	if (sis.getTurnNo() != sis.getPreviousTurnNo()) {
            		strValue = "t" + sis.getTurnNo() + " (t" + Math.max(sis.getPreviousTurnNo(), 0) + ")";
            	} else {
            		strValue = "t" + sis.getTurnNo();
            	}
            } else if (MetadataSource.class.isInstance(value)) {
                strValue = "t0";
            } 
        }
        return super.getTableCellRendererComponent(table, strValue, isSelected, hasFocus, row, column);
    }
}
