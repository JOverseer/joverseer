package org.joverseer.ui.support;

import java.awt.Component;
import java.util.HashMap;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;


public class ColumnBasedTableCellRenderer extends DefaultTableCellRenderer {
    HashMap<Integer, DefaultTableCellRenderer> renderers = new HashMap<Integer, DefaultTableCellRenderer>();
    
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        DefaultTableCellRenderer renderer = renderers.get(column);
        if (renderer == null) {
            return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        }
        return renderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
    }
    
    public void setColumnRenderer(int column, DefaultTableCellRenderer renderer) {
        renderers.put(column, renderer);
    }
    

}
