package org.joverseer.tools.ordercheckerIntegration;

import java.io.Serializable;

import org.joverseer.domain.Order;

/**
 * Class that stores a result from Order Checker for a given order.
 * 
 * @author Marios Skounakis
 */
public class OrderResult implements Serializable{
    /**
	 * 
	 */
	private static final long serialVersionUID = -1245651465490169390L;
	Order order;
    String message;
    OrderResultTypeEnum type;
    int nationNo;
    
    public int getNationNo() {
		return this.nationNo;
	}

	public void setNationNo(int nationNo) {
		this.nationNo = nationNo;
	}

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
        this.nationNo = 0;
    }
    
    public OrderResult(Order order, String message, OrderResultTypeEnum type, int NNo) {
        super();
        this.order = order;
        this.message = message;
        this.type = type;
        this.nationNo = NNo;
    }

    public OrderResult() {
        super();
    }
    
    
}
