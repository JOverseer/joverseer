package org.joverseer.ui.orders;

import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JCheckBox;
import javax.swing.JTextField;

import org.joverseer.domain.Army;
import org.joverseer.domain.Order;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.support.GameHolder;
import org.joverseer.ui.support.Messages;
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
            Army a = (Army)GameHolder.instance().getGame().getTurn().getContainer(TurnElementsEnum.Army).findFirstByProperty("commanderName", o.getCharacter().getName()); //$NON-NLS-1$
            if (a == null) {
                // get info
                InputDialog dlg = new InputDialog();
                JCheckBox fed;
                JCheckBox cavalry;
                dlg.setTitle(Messages.getString("OrderVisualizationData.title")); //$NON-NLS-1$
                dlg.addComponent(Messages.getString("OrderVisualizationData.fed"), fed = new JCheckBox()); //$NON-NLS-1$
                dlg.addComponent(Messages.getString("OrderVisualizationData.cavalry"), cavalry = new JCheckBox()); //$NON-NLS-1$
                String txt = Messages.getString("OrderVisualizationData.EnterInforForMove", 
                		new Object[] {o.getCharacter().getName(), o.getCharacter().getHexNo()});  //$NON-NLS-1$ //$NON-NLS-2$
                dlg.init(txt);
                dlg.showDialog();
                if (dlg.getResult()) {
                    this.orders.add(order);
                    setAdditionalInfo(order, "cavalry", cavalry.isSelected()); //$NON-NLS-1$
                    setAdditionalInfo(order, "fed", fed.isSelected()); //$NON-NLS-1$
                }
            } else {
                this.orders.add(o);
            }
        } else if (o.getOrderNo() == 925 || o.getOrderNo() == 910) {
        	InputDialog dlg = new InputDialog();
        	JTextField hexNo = new JTextField();
        	Order oo = Order.getOtherOrder(o);
        	if (oo.getOrderNo() == 810 || oo.getOrderNo() == 820 || oo.getOrderNo() == 870) {
        		if (oo.getParameter(0) != null && !oo.getParameter(0).equals("") && !oo.getParameter(0).equals("-")) { //$NON-NLS-1$ //$NON-NLS-2$
        			hexNo.setText(oo.getParameter(0));
        		}
        	}
        	dlg.setTitle(Messages.getString("OrderVisualizationData.title")); //$NON-NLS-1$
            dlg.addComponent(Messages.getString("OrderVisualizationData.LocationColon"), hexNo); //$NON-NLS-1$
            String txt = Messages.getString("OrderVisualizationData.EnterInfoForRecon", new Object[] {o.getCharacter().getName()}); //$NON-NLS-1$
            dlg.init(txt);
            dlg.showDialog();
            if (dlg.getResult()) {
                this.orders.add(o);
                setAdditionalInfo(o, "hexNo", hexNo.getText()); //$NON-NLS-1$
            }
        } else {
            this.orders.add(o);
        }
    }

    public void clear() {
        this.orders.clear();
    }

    public boolean contains(Order o) {
        return this.orders.contains(o);
    }
    
    public void removeOrder(Order o) {
        this.orders.remove(o);
        this.orderInfo.remove(o);
    }
    
    public void setAdditionalInfo(Order o, String key, Object value) {
        if (this.orderInfo.get(o) == null) {
            this.orderInfo.put(o, new HashMap<String, Object>());
        }
        this.orderInfo.get(o).put(key, value);
    }
    
    public void removeAdditionalInfo(Order o, String key) {
        if (this.orderInfo.get(o) == null) return;
        this.orderInfo.get(o).remove(key);
    }
    
    public Object getAdditionalInfo(Order o, String key) {
    	if (!this.orderInfo.containsKey(o)) return null;
        return this.orderInfo.get(o).get(key);
    }
    
    public Order getOrderEditorOrder() {
    	return this.orderEditorOrder;
    }
    
    public void setOrderEditorOrder(Order order) {
    	this.orderEditorOrder = order;
    }
}
