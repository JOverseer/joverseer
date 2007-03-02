package org.joverseer.ui.listviews;

import java.awt.Point;

import javax.swing.JPopupMenu;

import org.springframework.context.MessageSource;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.command.ActionCommand;
import org.springframework.richclient.command.CommandGroup;
import org.springframework.richclient.table.SortableTableModel;
import org.joverseer.game.Game;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.support.GameHolder;
import org.joverseer.ui.LifecycleEventsEnum;
import org.joverseer.ui.map.MapPanel;
import org.joverseer.ui.orders.OrderVisualizationData;
import org.joverseer.ui.support.JOverseerEvent;
import org.joverseer.domain.Order;


public class OrderTableModel extends ItemTableModel {
    
    
    public OrderTableModel(MessageSource messageSource) {
        super(Order.class, messageSource);
    }

    protected String[] createColumnPropertyNames() {
        return new String[] {"character.name", "character.hexNo", "noAndCode", "parameters", "draw"};
    }

    protected Class[] createColumnClasses() {
        return new Class[]{String.class, String.class, String.class, String.class, Boolean.class};  //To change body of implemented methods use File | Settings | File Templates.
    }

	
    protected boolean isCellEditableInternal(Object object, int i) {
        return i>1;
    }

    public void fireTableCellUpdated(int row, int column) {
        super.fireTableCellUpdated(row, column);
        Point selectedHex = MapPanel.instance().getSelectedHex();
        if (selectedHex != null) {
            Application.instance().getApplicationContext().publishEvent(
                    new JOverseerEvent(LifecycleEventsEnum.SelectedHexChangedEvent.toString(), selectedHex, this));
        }
    }

    protected Object getValueAtInternal(Object object, int i) {
        if (i == 4) {
            OrderVisualizationData ovd = (OrderVisualizationData)Application.instance().getApplicationContext().getBean("orderVisualizationData");
            return ovd.contains((Order)object);
        }
        return super.getValueAtInternal(object, i);
    }

    protected void setValueAtInternal(Object arg0, Object arg1, int arg2) {
        if (arg2 == 4) {
            OrderVisualizationData ovd = (OrderVisualizationData)Application.instance().getApplicationContext().getBean("orderVisualizationData");
            if (ovd.contains((Order)arg1)) {
                ovd.removeOrder((Order)arg1);
            } else {
                ovd.addOrder((Order)arg1);
            }
            Application.instance().getApplicationContext().publishEvent(
                    new JOverseerEvent(LifecycleEventsEnum.RefreshMapItems.toString(), this, this));

            return;
        }
        super.setValueAtInternal(arg0, arg1, arg2);
    }
    
    
    
    
    
}