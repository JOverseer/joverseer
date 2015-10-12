package org.joverseer.ui.command;

import java.util.ArrayList;

import org.joverseer.domain.Character;
import org.joverseer.domain.Order;
import org.joverseer.game.Game;
import org.joverseer.game.Turn;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.support.GameHolder;
import org.joverseer.ui.LifecycleEventsEnum;
import org.joverseer.ui.orders.OrderVisualizationData;
import org.joverseer.ui.support.ActiveGameChecker;
import org.joverseer.ui.support.GraphicUtils;
import org.joverseer.ui.support.JOverseerEvent;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.command.ActionCommand;

public class ToggleDrawAllOrdersCommand extends ActionCommand {
    boolean value = false;
	
    public ToggleDrawAllOrdersCommand() {
    	super("toggleDrawAllOrdersCommand");
    }
    
    
    @Override
	protected void doExecuteCommand() {
    	if (!ActiveGameChecker.checkActiveGameExists()) return;
    	Game g = GameHolder.instance().getGame();
    	Turn t = g.getTurn();
    	ArrayList<Character> chars = (ArrayList<Character>)t.getContainer(TurnElementsEnum.Character).getItems();
		OrderVisualizationData ovd = (OrderVisualizationData)Application.instance().getApplicationContext().getBean("orderVisualizationData");
		if (this.value) {
			ovd.clear();
			this.value = false;
		} else {
	    	for (Character c : chars) {
	    		for (Order o : c.getOrders()) {
	    			if (GraphicUtils.canRenderOrder(o)) {
	    				ovd.addOrder(o);
	    			}
	    		}
	    	}
	    	this.value = true;
		}
		Application.instance().getApplicationContext().publishEvent(
                new JOverseerEvent(LifecycleEventsEnum.RefreshMapItems.toString(), this, this));
    }
}
