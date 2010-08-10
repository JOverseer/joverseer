package org.joverseer.ui.orders;

import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JCheckBox;
import javax.swing.JTextField;

import org.joverseer.domain.Army;
import org.joverseer.domain.Order;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.support.GameHolder;
import org.joverseer.ui.support.dialogs.InputDialog;

/**
 * Holds info for visualizing orders on the map (used by the Order renderers)
 * 
 * Basically when an order is added, if it is missing some data (as in the case of
 * move army orders when the army cannot be found), it popups up a screen asking for the
 * additional info
 * 
 * @author Marios Skounakis
 */
public class OrderVisualizationData {
    ArrayList<Order> orders = new ArrayList<Order>();
    HashMap<Order, HashMap<String, Object>> orderInfo = new HashMap<Order, HashMap<String, Object>>();
    Order orderEditorOrder;
    
    public void addOrder(Order o) {

        if (o.getOrderNo() == 850 || o.getOrderNo() == 860) {
            final Order order = o;
            // movement order
            // check if character has army
            Army a = (Army)GameHolder.instance().getGame().getTurn().getContainer(TurnElementsEnum.Army).findFirstByProperty("commanderName", o.getCharacter().getName());
            if (a == null) {
                // get info
                InputDialog dlg = new InputDialog();
                JCheckBox fed;
                JCheckBox cavalry;
                dlg.setTitle("Order - Provide Additional Information");
                dlg.addComponent("Fed :", fed = new JCheckBox());
                dlg.addComponent("Cavalry :", cavalry = new JCheckBox());
                String txt = o.getCharacter().getName() + "'s army (" + o.getCharacter().getHexNo() + "): Enter the required information for drawing the army move."; 
                dlg.init(txt);
                dlg.showDialog();
                if (dlg.getResult()) {
                    orders.add(order);
                    setAdditionalInfo(order, "cavalry", cavalry.isSelected());
                    setAdditionalInfo(order, "fed", fed.isSelected());
                }
            } else {
                orders.add(o);
            }
        } else if (o.getOrderNo() == 925 || o.getOrderNo() == 910) {
        	InputDialog dlg = new InputDialog();
        	JTextField hexNo = new JTextField();
        	Order oo = Order.getOtherOrder(o);
        	if (oo.getOrderNo() == 810 || oo.getOrderNo() == 820 || oo.getOrderNo() == 870) {
        		if (oo.getParameter(0) != null && !oo.getParameter(0).equals("") && !oo.getParameter(0).equals("-")) {
        			hexNo.setText(oo.getParameter(0));
        		}
        	}
        	dlg.setTitle("Order - Provide Additional Information");
            dlg.addComponent("Location after movement phase :", hexNo);
            String txt = o.getCharacter().getName() + "'s Recon/ScoArea: Enter the required information for drawing the character's Recon/ScoArea.";
            dlg.init(txt);
            dlg.showDialog();
            if (dlg.getResult()) {
                orders.add(o);
                setAdditionalInfo(o, "hexNo", hexNo.getText());
            }
        } else {
            orders.add(o);
        }
    }

    public void clear() {
        orders.clear();
    }

    public boolean contains(Order o) {
        return orders.contains(o);
    }
    
    public void removeOrder(Order o) {
        orders.remove(o);
        orderInfo.remove(o);
    }
    
    public void setAdditionalInfo(Order o, String key, Object value) {
        if (orderInfo.get(o) == null) {
            orderInfo.put(o, new HashMap<String, Object>());
        }
        orderInfo.get(o).put(key, value);
    }
    
    public void removeAdditionalInfo(Order o, String key) {
        if (orderInfo.get(o) == null) return;
        orderInfo.get(o).remove(key);
    }
    
    public Object getAdditionalInfo(Order o, String key) {
    	if (!orderInfo.containsKey(o)) return null;
        return orderInfo.get(o).get(key);
    }
    
    public Order getOrderEditorOrder() {
    	return orderEditorOrder;
    }
    
    public void setOrderEditorOrder(Order order) {
    	orderEditorOrder = order;
    }
}
