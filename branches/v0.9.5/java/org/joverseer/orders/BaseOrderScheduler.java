package org.joverseer.orders;

import java.util.ArrayList;


public class BaseOrderScheduler {
    ArrayList<OrderExecutionWrapper> scheduledOrders = new ArrayList<OrderExecutionWrapper>();
    
    public void scheduleOrders(ArrayList<OrderExecutionWrapper> orders) {
        scheduledOrders.clear();
        scheduledOrders.addAll(orders);
    }
    
    public ArrayList<OrderExecutionWrapper> getScheduledOrders() {
        return scheduledOrders;
    }
}
