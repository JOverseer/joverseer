package org.joverseer.ui.listviews;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import org.joverseer.game.TurnElementsEnum;


public class RelationsListView extends ItemListView {
    public RelationsListView() {
        super(TurnElementsEnum.NationRelation, RelationsTableModel.class);
    }
    
    
    protected JComponent createControlImpl() {
        JComponent c = super.createControlImpl();
        table.setDefaultRenderer(String.class, new RelationsTableCellRenderer());
        return c;
    }



    protected int[] columnWidths() {
        return new int[]{64, 96, 
                        32, 32, 32, 32, 32, 
                        32, 32, 32, 32, 32,
                        32, 32, 32, 32, 32,
                        32, 32, 32, 32, 32,
                        32, 32, 32, 32, 32};
    }

    protected void setItems() {
        super.setItems();
        try {
            for (int i=1; i<26; i++) {
                table.getColumnModel().getColumn(i+1).setHeaderValue(tableModel.getColumnName(i+1));
            }
        } catch (Exception exc) {};
    }
    
    
    public class RelationsTableCellRenderer extends DefaultTableCellRenderer {

        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            String relation = value.toString();
            Color bgColor = Color.WHITE;
            if (relation.equals("F")) {
                bgColor = Color.GREEN;
            } else if (relation.equals("T")) {
                bgColor = Color.CYAN;
            } else if (relation.equals("D")) {
                bgColor = Color.GRAY;
            } else if (relation.equals("H")) {
                bgColor = Color.RED;
            }
            JLabel lbl = ((JLabel)c);
            c.setBackground(bgColor);
            return lbl;
        }
        
    }
}
