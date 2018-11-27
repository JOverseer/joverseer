package org.joverseer.orders;

import java.util.ArrayList;


public class RandomOrderScheduler extends BaseOrderScheduler {

    @Override
	public void scheduleOrders(ArrayList<OrderExecutionWrapper> orders) {
        getScheduledOrders().clear();
        while (orders.size() > 0) {
            int i = Double.valueOf(Math.random() * orders.size()).intValue();
            getScheduledOrders().add(orders.remove(i));
        }
    }
    

}
