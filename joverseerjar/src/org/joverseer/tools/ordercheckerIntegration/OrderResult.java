package org.joverseer.tools.ordercheckerIntegration;

import org.joverseer.domain.Order;

/**
 * Class that stores a result from Order Checker for a give order.
 * 
 * @author Marios Skounakis
 */
public class OrderResult {
    Order order;
    String message;
    OrderResultTypeEnum type;
    
    public String getMessage() {
        return this.message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public Order getOrder() {
        return this.order;
    }
    
    public void setOrder(Order order) {
        this.order = order;
    }
    
    public OrderResultTypeEnum getType() {
        return this.type;
    }
    
    public void setType(OrderResultTypeEnum type) {
        this.type = type;
    }

    public OrderResult(Order order, String message, OrderResultTypeEnum type) {
        super();
        this.order = order;
        this.message = message;
        this.type = type;
    }

    public OrderResult() {
        super();
    }
    
    
}
