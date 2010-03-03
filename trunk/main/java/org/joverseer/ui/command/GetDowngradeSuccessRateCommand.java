package org.joverseer.ui.command;

import java.util.ArrayList;

import org.joverseer.domain.Character;
import org.joverseer.game.Game;
import org.joverseer.game.Turn;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.support.GameHolder;
import org.springframework.richclient.command.ActionCommand;

public class GetDowngradeSuccessRateCommand extends ActionCommand {
    
    
    public GetDowngradeSuccessRateCommand() {
        super("getDowngradeSuccessRateCommand");
    }
    
    protected void doExecuteCommand() {
    	Game game = GameHolder.instance().getGame();
    	
    	for (int i=0; i<=game.getMaxTurn(); i++) {
    		Turn t = game.getTurn(i);
    		if (t == null) continue;
    		for (Character c : (ArrayList<Character>)t.getContainer(TurnElementsEnum.Character).getItems()) {
    			String orderResults = c.getOrderResults();
    			if (orderResults == null || orderResults.equals("")) continue;
    			orderResults = orderResults.replace("\n", " ");
    			orderResults = orderResults.replace("\r", " ");
    			orderResults = orderResults.replace("  ", " ");
    			orderResults = orderResults.replace("  ", " ");
    			orderResults = orderResults.replace("  ", " ");
    			if (orderResults.contains("was ordered to downgrade our relations.")) {
	    			boolean success = orderResults.contains("were downgraded.");
	    			String ret = i + ";" + c.getName() + ";" + c.getCommand() + ";" + c.getCommandTotal() + ";" + (success ? "1" :"0");
	    			System.out.println(ret);
    			}
    		}
    	}
    }

}
