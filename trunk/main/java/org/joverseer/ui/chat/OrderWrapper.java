package org.joverseer.ui.chat;

import org.joverseer.domain.Order;
import java.io.Serializable;


public class OrderWrapper implements Serializable {
    private static final long serialVersionUID = 3769006563129294447L;
    int orderNo;
    String parameters;
    String charId;
    int hexNo;
    int orderIdx;
    
    public OrderWrapper() {};
    
    public OrderWrapper(Order o) {
        orderNo = o.getOrderNo();
        parameters = o.getParameters();
        charId = o.getCharacter().getId();
        hexNo = o.getCharacter().getHexNo();
        orderIdx = o.getCharacter().getOrders()[0] == o ? 0 : 1;
    }
    
    public String getCharId() {
        return charId;
    }
    
    public void setCharId(String charId) {
        this.charId = charId;
    }
    
    public int getHexNo() {
        return hexNo;
    }
    
    public void setHexNo(int hexNo) {
        this.hexNo = hexNo;
    }
    
    public int getOrderIdx() {
        return orderIdx;
    }
    
    public void setOrderIdx(int orderIdx) {
        this.orderIdx = orderIdx;
    }
    
    public int getOrderNo() {
        return orderNo;
    }
    
    public void setOrderNo(int orderNo) {
        this.orderNo = orderNo;
    }
    
    public String getParameters() {
        return parameters;
    }
    
    public void setParameters(String parameters) {
        this.parameters = parameters;
    }
    
    
    
}
