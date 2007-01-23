package org.joverseer.ui.orders;

import org.joverseer.domain.Order;

import java.util.ArrayList;


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
    
    public void removeOrder(Order o) {
        orders.remove(o);
    }
}
