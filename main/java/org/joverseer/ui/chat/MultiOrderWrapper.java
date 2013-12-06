package org.joverseer.ui.chat;

import java.io.Serializable;
import java.util.ArrayList;


public class MultiOrderWrapper implements Serializable {
    private static final long serialVersionUID = 3729303190377429243L;

    ArrayList<OrderWrapper> orderWrappers = new ArrayList<OrderWrapper>();
    
    public ArrayList<OrderWrapper> getOrderWrappers() {
        return this.orderWrappers;
    }

}
