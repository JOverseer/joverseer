package org.joverseer.orders;

import java.util.ArrayList;

import org.joverseer.domain.Character;
import org.joverseer.domain.Order;
import org.joverseer.game.Game;
import org.joverseer.game.Turn;
import org.joverseer.orders.checks.AbstractCheck;


public abstract class AbstractOrderProcessor {
    public static ArrayList<AbstractOrderProcessor> processorRegistry = new ArrayList<AbstractOrderProcessor>(); 
    
    ArrayList<AbstractCheck> checks = new ArrayList<AbstractCheck>();
    
    public AbstractOrderProcessor() {
        processorRegistry.add(this);
    }
    
    public Order getOrder(Character c, int orderNo) {
        return c.getOrders()[orderNo];
    }
    
    public abstract boolean appliesTo(Character c, int orderNo);
    public abstract void processOrderImpl(Turn t, Character c, int orderNo);
    
    public void processOrder(Turn t, Character c, int orderNo) {
        if (!checkOrder(c, orderNo)) {
            return;
        }
        processOrderImpl(t, c, orderNo);
    }

    public boolean checkOrder(Character c, int orderNo) {
        Order o = getOrder(c, orderNo);
        for (AbstractCheck check : getChecks()) {
            if (!check.check(o)) {
                OrderUtils.appendOrderResult(c, check.getErrorMessage(o));
                return false;
            }
        }
        return true;
    }
    
    public ArrayList<AbstractCheck> getChecks() {
        return checks;
    }

    
    public void setChecks(ArrayList<AbstractCheck> checks) {
        this.checks = checks;
    }
    
    public void addCheck(AbstractCheck check) {
        checks.add(check);
    }
    
}
