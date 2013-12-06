package org.joverseer.orders;

import java.util.ArrayList;


public class RandomOrderScheduler extends BaseOrderScheduler {

    @Override
	public void scheduleOrders(ArrayList<OrderExecutionWrapper> orders) {
        getScheduledOrders().clear();
        while (orders.size() > 0) {
            int i = new Double(Math.random() * orders.size()).intValue();
            getScheduledOrders().add(orders.remove(i));
        }
    }
    

}
