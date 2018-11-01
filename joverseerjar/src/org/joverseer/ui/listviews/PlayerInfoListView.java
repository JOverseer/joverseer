package org.joverseer.ui.listviews;

import java.awt.Component;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import org.joverseer.game.TurnElementsEnum;
import org.joverseer.support.JOverseerEventListener;
import org.joverseer.ui.LifecycleEventsEnum;
import org.joverseer.ui.support.JOverseerEvent;

/**
 * List view for PlayerInfo objects
 * 
 * @author Marios Skounakis
 */
public class PlayerInfoListView extends ItemListView implements JOverseerEventListener {

    public PlayerInfoListView() {
        super(TurnElementsEnum.PlayerInfo, PlayerInfoTableModel.class);
    }

    @Override
	protected int[] columnWidths() {
        return new int[]{64, 160, 80, 200, 120};
    }

    @Override
	protected JComponent createControlImpl() {
        JComponent comp = super.createControlImpl();
        this.table.setDefaultRenderer(Date.class, new DefaultTableCellRenderer() {
			private static final long serialVersionUID = 1L;

			@Override
			public Component getTableCellRendererComponent(JTable table1, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                SimpleDateFormat sdf = new SimpleDateFormat();
                if (value == null) {
                	value = "";
                } else if (value instanceof Date) {
                	Date d = (Date)value;
               		value = sdf.format(d);
                }
                return super.getTableCellRendererComponent(table1, value, isSelected, hasFocus, row, column);
            }
            
        });
        return comp;
    }

	@Override
	public void onApplicationEvent(JOverseerEvent e) {
		if (e.isLifecycleEvent(LifecycleEventsEnum.OrderSaveToFileEvent))  {
			this.setItems();
		}
	}
	
}
