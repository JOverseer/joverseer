package org.joverseer.ui.command;

import java.util.ArrayList;

import org.joverseer.domain.Character;
import org.joverseer.domain.NationRelations;
import org.joverseer.domain.PopulationCenter;
import org.joverseer.game.Game;
import org.joverseer.game.Turn;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.support.GameHolder;
import org.springframework.richclient.command.ActionCommand;

public class GetStealGoldSuccessRateCommand  extends ActionCommand {
    
    
    public GetStealGoldSuccessRateCommand() {
        super("getStealGoldSuccessRateCommand");
    }
    
    @Override
	protected void doExecuteCommand() {
    	Game game = GameHolder.instance().getGame();

    	System.out.println("turn;name;agent;agent total;pop size; pop fort; pop nation;relations;success;skill increase;gold");
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
    			
    			if (orderResults.contains("was ordered to steal the Gold.")) {
    				Turn prev = game.getTurn(i-1);
    				if (prev == null) continue;
    				Character co = (Character)prev.getContainer(TurnElementsEnum.Character).findFirstByProperty("name", c.getName());
    				if (co == null) continue;
    				PopulationCenter pop = (PopulationCenter)prev.getContainer(TurnElementsEnum.PopulationCenter).findFirstByProperty("hexNo", co.getHexNo());
    				if (pop == null) continue;
    				int si = c.getAgent() - co.getAgent();
	    			boolean success = orderResults.contains("Gold was stolen") || orderResults.contains("No Gold was found");
	    			int j = orderResults.indexOf("Gold was stolen");
	    			int goldStolen = 0;
	    			if (j > -1) {
	    				int k = j - 5;
	    				if (orderResults.charAt(k) == ' ' ||orderResults.charAt(k) == '.') k++;
	    				String goldStr = orderResults.substring(k, j-1).trim();
	    				goldStolen = Integer.parseInt(goldStr);
	    			}
	    			NationRelations nr = (NationRelations)t.getContainer(TurnElementsEnum.NationRelation).findFirstByProperty("nationNo", c.getNationNo());
	    			String ret = i + ";" + c.getName() + ";" + co.getAgent() + ";" + co.getAgentTotal() + ";" + pop.getSize().getCode() + ";" + pop.getFortification().getSize() + ";" + pop.getNationNo() + ";" + nr.getRelationsFor(pop.getNationNo()) + ";" + (success ? "1" :"0") + ";" + si + ";" + goldStolen;
	    			System.out.println(ret);
    			}
    		}
    	}
    }



}
