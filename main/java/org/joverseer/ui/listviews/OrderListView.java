package org.joverseer.ui.listviews;

import org.joverseer.game.TurnElementsEnum;
import org.joverseer.game.Game;
import org.joverseer.support.GameHolder;
import org.joverseer.support.Container;
import org.joverseer.domain.Order;
import org.springframework.richclient.application.Application;
import org.springframework.context.ApplicationEvent;
import org.joverseer.domain.Character;
import org.joverseer.ui.support.JOverseerEvent;
import org.joverseer.ui.LifecycleEventsEnum;

import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: mskounak
 * Date: 29 Íןו 2006
 * Time: 10:02:33 לל
 * To change this template use File | Settings | File Templates.
 */
public class OrderListView extends ItemListView {
    public OrderListView() {
        super(TurnElementsEnum.Character, OrderTableModel.class);
    }

    protected int[] columnWidths() {
        return new int[]{32, 64, 64, 64, 150};
    }

    protected void setItems() {
        Game g = ((GameHolder) Application.instance().getApplicationContext().getBean("gameHolder")).getGame();
        if (!Game.isInitialized(g)) return;
        Container items = g.getTurn().getContainer(turnElementType);
        ArrayList orders = new ArrayList();
        for (Character c : (ArrayList<Character>)items.getItems()) {
            for (Order o : c.getOrders()) {
                if (o.isBlank()) continue;
                orders.add(o);
            }
        }
        tableModel.setRows(orders);
    }

    public void onApplicationEvent(ApplicationEvent applicationEvent) {
        if (applicationEvent instanceof JOverseerEvent) {
            JOverseerEvent e = (JOverseerEvent)applicationEvent;
            if (e.getEventType().equals(LifecycleEventsEnum.SelectedTurnChangedEvent.toString())) {
                setItems();
            } else if (e.getEventType().equals(LifecycleEventsEnum.SelectedHexChangedEvent.toString())) {
                setItems();
            } else if (e.getEventType().equals(LifecycleEventsEnum.GameChangedEvent.toString())) {
                setItems();
            } else if (e.getEventType().equals(LifecycleEventsEnum.OrderChangedEvent.toString())) {
                setItems();
            }
        }
    }
}
