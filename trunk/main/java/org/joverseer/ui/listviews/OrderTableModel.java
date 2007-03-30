package org.joverseer.ui.listviews;

import java.awt.Point;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JPopupMenu;

import org.springframework.context.MessageSource;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.command.ActionCommand;
import org.springframework.richclient.command.CommandGroup;
import org.springframework.richclient.image.ImageSource;
import org.springframework.richclient.table.SortableTableModel;
import org.joverseer.game.Game;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.metadata.GameMetadata;
import org.joverseer.support.GameHolder;
import org.joverseer.tools.orderCostCalculator.OrderCostCalculator;
import org.joverseer.tools.ordercheckerIntegration.OrderResultContainer;
import org.joverseer.tools.ordercheckerIntegration.OrderResultTypeEnum;
import org.joverseer.ui.LifecycleEventsEnum;
import org.joverseer.ui.map.MapPanel;
import org.joverseer.ui.orders.OrderVisualizationData;
import org.joverseer.ui.support.JOverseerEvent;
import org.joverseer.domain.IBelongsToNation;
import org.joverseer.domain.Order;


public class OrderTableModel extends ItemTableModel {
    int drawColumnIndex = 5;
    int resultsColumnIndex = 6;
    int nationIndex = 0;
    int costColumnIndex = 7;
    
    public OrderTableModel(MessageSource messageSource) {
        super(Order.class, messageSource);
    }

    protected String[] createColumnPropertyNames() {
        return new String[] {"character.nationNo", "character.name", "character.hexNo", "noAndCode", "parameters", "draw", "results", "cost"};
    }

    protected Class[] createColumnClasses() {
        return new Class[]{String.class, String.class, String.class, String.class, String.class, Boolean.class, ImageIcon.class, String.class};  
    }

	
    protected boolean isCellEditableInternal(Object object, int i) {
        return i>2 && i != resultsColumnIndex;
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
        if (i == nationIndex) {
            Order order = (Order)object;
            Game game = ((GameHolder) Application.instance().getApplicationContext().getBean("gameHolder")).getGame();
            if (game == null) return "";
            if (IBelongsToNation.class.isInstance(order.getCharacter()) && getColumnPropertyNames()[i].endsWith("nationNo")) {
                GameMetadata gm = game.getMetadata();
                Integer nationNo = ((IBelongsToNation)order.getCharacter()).getNationNo();
                if (nationNo == null) return "";
                return gm.getNationByNum(nationNo).getShortName();
            }
            return super.getValueAtInternal(object, i);
        } else if (i == drawColumnIndex) {
            OrderVisualizationData ovd = (OrderVisualizationData)Application.instance().getApplicationContext().getBean("orderVisualizationData");
            return ovd.contains((Order)object);
        } else if (i == resultsColumnIndex) {
            OrderResultContainer container = (OrderResultContainer)Application.instance().getApplicationContext().getBean("orderResultContainer");
            OrderResultTypeEnum orderResultType = container.getResultTypeForOrder((Order)object);
            Icon ico = null;
            if (orderResultType != null) {
                ImageSource imgSource = (ImageSource) Application.instance().getApplicationContext().getBean("imageSource");
                if (orderResultType == null) {
                    ico = null;
                } else if (orderResultType == OrderResultTypeEnum.Info) {
                    ico = new ImageIcon(imgSource.getImage("orderresult.info.icon"));
                } else if (orderResultType == OrderResultTypeEnum.Help) {
                    ico = new ImageIcon(imgSource.getImage("orderresult.help.icon"));
                } else if (orderResultType == OrderResultTypeEnum.Warning) {
                    ico = new ImageIcon(imgSource.getImage("orderresult.warn.icon"));
                } else if (orderResultType == OrderResultTypeEnum.Error) {
                    ico = new ImageIcon(imgSource.getImage("orderresult.error.icon"));
                } else if (orderResultType == OrderResultTypeEnum.Okay) {
                    ico = new ImageIcon(imgSource.getImage("orderresult.okay.icon"));
                } 
            }
            return ico;
        } else if (i == costColumnIndex) {
            Order order = (Order)object;
            OrderCostCalculator calc = new OrderCostCalculator();
            int cost = calc.getOrderCost(order);
            if (cost != 0) {
                return cost;
            } else if (cost == -1) {
                return "Error";
            } else {
                return "";
            }
        }
        return super.getValueAtInternal(object, i);
    }

    protected void setValueAtInternal(Object arg0, Object arg1, int arg2) {
        if (arg2 == drawColumnIndex) {
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
