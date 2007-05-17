package org.joverseer.ui.listviews;

import java.awt.Component;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import org.joverseer.game.TurnElementsEnum;


public class PlayerInfoListView extends ItemListView {

    public PlayerInfoListView() {
        super(TurnElementsEnum.PlayerInfo, PlayerInfoTableModel.class);
    }

    protected int[] columnWidths() {
        return new int[]{64, 160, 64, 80, 200, 120};
    }

    protected JComponent createControlImpl() {
        JComponent comp = super.createControlImpl();
        table.setDefaultRenderer(Date.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                SimpleDateFormat sdf = new SimpleDateFormat(); 
                Date d = (Date)value;
                if (d == null) {
                    value = "";
                } else {
                    value = sdf.format(d);
                }
                return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            }
            
        });
        return comp;
    }
    
    
    
    

}
