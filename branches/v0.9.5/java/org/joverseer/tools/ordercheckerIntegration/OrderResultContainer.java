package org.joverseer.tools.ordercheckerIntegration;

import java.util.ArrayList;
import java.util.List;

import org.joverseer.domain.Order;
import org.joverseer.support.Container;
import org.joverseer.ui.LifecycleEventsEnum;
import org.joverseer.ui.support.JOverseerEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;



public class OrderResultContainer implements ApplicationListener {
    Container results = new Container(new String[]{"order"});
    
    public void addResult(OrderResult res) {
        results.addItem(res);
    }
    
    public void addAll(List list) {
        for (Object o : list) {
            addResult((OrderResult)o);
        }
    }
    
    public ArrayList<OrderResult> getResultsForOrder(Order o) {
        return (ArrayList<OrderResult>)results.findAllByProperty("order", o);
    }
    
    public OrderResultTypeEnum getResultTypeForOrder(Order o) {
        OrderResultTypeEnum resType = null;
        for (OrderResult r : getResultsForOrder(o)) {
            if (resType == null || r.getType().getValue() > resType.getValue()) {
                resType = r.getType();
            }
        }
        return resType;
    }
    
    public void removeResultsForOrder(Order o) {
        ArrayList<OrderResult> ors = getResultsForOrder(o);
        for (OrderResult r : ors) {
            results.removeItem(r);
        }
    }

    public void onApplicationEvent(ApplicationEvent applicationEvent) {
        if (applicationEvent instanceof JOverseerEvent) {
            JOverseerEvent e = (JOverseerEvent) applicationEvent;
            if (e.getEventType().equals(LifecycleEventsEnum.OrderChangedEvent.toString())) {
                Order o = (Order)e.getData();
                removeResultsForOrder(o);
            }
        }
    }
}
