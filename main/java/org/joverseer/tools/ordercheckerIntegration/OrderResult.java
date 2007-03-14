package org.joverseer.tools.ordercheckerIntegration;

import org.joverseer.domain.Order;


public class OrderResult {
    Order order;
    String message;
    OrderResultTypeEnum type;
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public Order getOrder() {
        return order;
    }
    
    public void setOrder(Order order) {
        this.order = order;
    }
    
    public OrderResultTypeEnum getType() {
        return type;
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
