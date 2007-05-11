package org.joverseer.ui.orders;

import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JCheckBox;

import org.joverseer.domain.Army;
import org.joverseer.domain.Order;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.support.GameHolder;
import org.joverseer.ui.support.dialogs.InputDialog;


public class OrderVisualizationData {
    ArrayList<Order> orders = new ArrayList<Order>();
    HashMap<Order, HashMap<String, Object>> orderInfo = new HashMap<Order, HashMap<String, Object>>();
    
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
                dlg.setTitle("Order - Provide Addition Information");
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
    
    public Object getAdditionalInfo(Order o, String key) {
        return orderInfo.get(o).get(key);
    }
}
