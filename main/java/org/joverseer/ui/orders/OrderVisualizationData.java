package org.joverseer.ui.orders;

import org.joverseer.domain.Order;

import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: mskounak
 * Date: 29 Íןו 2006
 * Time: 10:29:07 לל
 * To change this template use File | Settings | File Templates.
 */
public class OrderVisualizationData {
    ArrayList<Order> orders = new ArrayList<Order>();

    public void addOrder(Order o) {
        orders.add(o);
    }

    public void clear() {
        orders.clear();
    }

    public boolean contains(Order o) {
        return orders.contains(o);
    }
}
