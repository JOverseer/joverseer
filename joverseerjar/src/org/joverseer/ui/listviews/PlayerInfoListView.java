package org.joverseer.ui.listviews;

import java.awt.Component;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import org.joverseer.game.TurnElementsEnum;

/**
 * List view for PlayerInfo objects
 * 
 * @author Marios Skounakis
 */
public class PlayerInfoListView extends ItemListView {

    public PlayerInfoListView() {
        super(TurnElementsEnum.PlayerInfo, PlayerInfoTableModel.class);
    }

    @Override
	protected int[] columnWidths() {
        return new int[]{64, 160, 64, 80, 200, 120};
    }

    @Override
	protected JComponent createControlImpl() {
        JComponent comp = super.createControlImpl();
        this.table.setDefaultRenderer(Date.class, new DefaultTableCellRenderer() {
            @Override
			public Component getTableCellRendererComponent(JTable table1, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                SimpleDateFormat sdf = new SimpleDateFormat(); 
                Date d = (Date)value;
                if (d == null) {
                    value = "";
                } else {
                    value = sdf.format(d);
                }
                return super.getTableCellRendererComponent(table1, value, isSelected, hasFocus, row, column);
            }
            
        });
        return comp;
    }
    
    
    
    

}
