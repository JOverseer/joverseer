package org.joverseer.tools.orderHandler;

import java.util.ArrayList;

import org.joverseer.support.ProductContainer;
import org.joverseer.support.messages.Message;


public class OrderResult {
    ProductContainer orderCost = new ProductContainer();
    ArrayList<Message> messages = new ArrayList<Message>();
    
    public ArrayList<Message> getMessages() {
        return messages;
    }
    
    public void setMessages(ArrayList<Message> messages) {
        this.messages = messages;
    }
    
    public ProductContainer getOrderCost() {
        return orderCost;
    }
    
    public void setOrderCost(ProductContainer orderCost) {
        this.orderCost = orderCost;
    }
    
    public void addMessage(Message msg) {
        messages.add(msg);
    }
}
