package org.joverseer.orders;

import java.util.ArrayList;


public abstract class AbstractOrderScheduler {
    ArrayList<OrderExecutionWrapper> scheduledOrders = new ArrayList<OrderExecutionWrapper>();
    
    public abstract void scheduleOrders(ArrayList<OrderExecutionWrapper> orders);
    
    public ArrayList<OrderExecutionWrapper> getScheduledOrders() {
        return scheduledOrders;
    }
}
