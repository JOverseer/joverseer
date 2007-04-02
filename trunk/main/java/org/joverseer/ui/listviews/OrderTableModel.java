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
import org.joverseer.ui.orderEditor.OrderParameterValidator;
import org.joverseer.ui.orders.OrderVisualizationData;
import org.joverseer.ui.support.JOverseerEvent;
import org.joverseer.domain.IBelongsToNation;
import org.joverseer.domain.Order;


public class OrderTableModel extends ItemTableModel {
    int drawColumnIndex = 6;
    int resultsColumnIndex = 7;
    int nationIndex = 0;
    int costColumnIndex = 8;
    int errorsColumnIndex = 5;
    int noAndCodeIndex = 3;
    int paramIndex = 4;
    
    OrderParameterValidator validator = new OrderParameterValidator();
    
    public OrderTableModel(MessageSource messageSource) {
        super(Order.class, messageSource);
    }

    protected String[] createColumnPropertyNames() {
        return new String[] {"character.nationNo", "character.name", "character.hexNo", "noAndCode", "parameters", "errors", "draw", "results", "cost"};
    }

    protected Class[] createColumnClasses() {
        return new Class[]{String.class, String.class, Integer.class, String.class, String.class, String.class, Boolean.class, ImageIcon.class, String.class};  
    }

	
    protected boolean isCellEditableInternal(Object object, int i) {
        return i>2 && i != resultsColumnIndex;
    }

    public void fireTableCellUpdated(int row, int column) {
        //super.fireTableCellUpdated(row, column);
        Point selectedHex = MapPanel.instance().getSelectedHex();
        if (selectedHex != null) {
            Application.instance().getApplicationContext().publishEvent(
                    new JOverseerEvent(LifecycleEventsEnum.RefreshHexItems.toString(), selectedHex, this));
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
        } else if (i == errorsColumnIndex) {
        	String txt = "";
        	Order order = (Order)object;
        	for (int j=0; j<9; j++) {
        		String msg = validator.checkParam(order, j);
        		if (msg != null) {
        			msg = "Param " + j + " " + msg;
        			txt += (txt.equals("") ? "" : ", ") + msg;
        		}
        	}
        	return txt;
        } else if (i == paramIndex) {
            Order order = (Order)object;
            return Order.getParametersAsString(order.getParameters());
        }
        return super.getValueAtInternal(object, i);
    }

    protected void setValueAtInternal(Object v, Object obj, int col) {
        if (col == drawColumnIndex) {
            OrderVisualizationData ovd = (OrderVisualizationData)Application.instance().getApplicationContext().getBean("orderVisualizationData");
            if (ovd.contains((Order)obj)) {
                ovd.removeOrder((Order)obj);
            } else {
                ovd.addOrder((Order)obj);
            }
            Application.instance().getApplicationContext().publishEvent(
                    new JOverseerEvent(LifecycleEventsEnum.RefreshMapItems.toString(), this, this));

            return;
        } else if (col == noAndCodeIndex) {
        	Order o = (Order)obj;
        	String oldNoAndCode = o.getNoAndCode();
        	o.setNoAndCode(v.toString());
        	if (!oldNoAndCode.equals(o.getNoAndCode())) {
        		o.setParameters("");
        	}
        	return;
        } else if (col == paramIndex) {
            if (v != null) {
                String txt = (String)v;
                txt = txt.replace(" ", "#");
                v = txt;
            }
            super.setValueAtInternal(v, obj, col);
        }
        super.setValueAtInternal(v, obj, col);
    }
    
    
    
    
    
}
