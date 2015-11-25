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
        this.orderNo = o.getOrderNo();
        this.parameters = o.getParameters();
        this.charId = o.getCharacter().getId();
        this.hexNo = o.getCharacter().getHexNo();
        this.orderIdx = o.getCharacter().getOrders()[0] == o ? 0 : 1;
    }
    
    public String getCharId() {
        return this.charId;
    }
    
    public void setCharId(String charId) {
        this.charId = charId;
    }
    
    public int getHexNo() {
        return this.hexNo;
    }
    
    public void setHexNo(int hexNo) {
        this.hexNo = hexNo;
    }
    
    public int getOrderIdx() {
        return this.orderIdx;
    }
    
    public void setOrderIdx(int orderIdx) {
        this.orderIdx = orderIdx;
    }
    
    public int getOrderNo() {
        return this.orderNo;
    }
    
    public void setOrderNo(int orderNo) {
        this.orderNo = orderNo;
    }
    
    public String getParameters() {
        return this.parameters;
    }
    
    public void setParameters(String parameters) {
        this.parameters = parameters;
    }
    
    
    
}
