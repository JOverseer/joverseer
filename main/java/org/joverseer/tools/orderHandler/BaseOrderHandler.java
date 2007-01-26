package org.joverseer.tools.orderHandler;

import org.joverseer.domain.Order;
import org.joverseer.domain.Character;
import org.joverseer.game.Game;
import org.joverseer.support.GameHolder;

public abstract class BaseOrderHandler {
    int[] orders;
    
    public BaseOrderHandler(int[] orders) {
        super();
        this.orders = orders;
    }

    public int[] getOrders() {
        return orders;
    }
    
    public void setOrders(int[] orders) {
        this.orders = orders;
    }

    public boolean appliesTo(Order o) {
        for (int on : orders) {
            if (o.getOrderNo() == on) return true;
        }
        return false;
    }
    
    public abstract OrderResult getOrderResult(Character c, int orderNo);
    
    public Game getGame() {
        Game g = GameHolder.instance().getGame();
        if (Game.isInitialized(g)) return g;
        return null;
    }
}
