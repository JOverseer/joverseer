package org.joverseer.orders.me.orderProcessors.spells;

import java.util.Arrays;

import org.joverseer.domain.Order;
import org.joverseer.orders.AbstractOrderProcessor;


public abstract class AbstractSpellOrderProcessor extends AbstractOrderProcessor {
    
    
    public AbstractSpellOrderProcessor() {
        super();
    }

    public int getSpellNo(Order o) {
        return Integer.parseInt(o.getParameter(0));
    }

    public String getSpellTarget(Order o) {
        return o.getParameter(1);
    }
}
