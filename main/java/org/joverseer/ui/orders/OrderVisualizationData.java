package org.joverseer.ui.orders;

import org.joverseer.domain.Army;
import org.joverseer.domain.Order;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.support.GameHolder;
import org.joverseer.ui.support.dialogs.InputDialog;
import org.springframework.richclient.layout.TableLayoutBuilder;

import com.jidesoft.dialog.BannerPanel;
import com.jidesoft.dialog.ButtonPanel;
import com.jidesoft.dialog.StandardDialog;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.border.BevelBorder;


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
                dlg.addComponent("Fed :", fed = new JCheckBox());
                dlg.addComponent("Cavalry :", cavalry = new JCheckBox());
                dlg.init("Enter the required information for the army movement.");
                dlg.showDialog();
                if (dlg.getResult()) {
                    orders.add(order);
                    setAdditionalInfo(order, "cav", cavalry.isSelected());
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
