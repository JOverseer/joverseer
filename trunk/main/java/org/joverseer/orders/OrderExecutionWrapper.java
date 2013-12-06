package org.joverseer.orders;

import org.joverseer.domain.Character;

public class OrderExecutionWrapper {
    Character character;
    int orderNo;
    
    public OrderExecutionWrapper() {}
    
    public OrderExecutionWrapper(Character character, int orderNo) {
        super();
        this.character = character;
        this.orderNo = orderNo;
    }

    public Character getCharacter() {
        return this.character;
    }
    
    public void setCharacter(Character character) {
        this.character = character;
    }
    
    public int getOrderNo() {
        return this.orderNo;
    }
    
    public void setOrderNo(int orderNo) {
        this.orderNo = orderNo;
    }
    
    
}
