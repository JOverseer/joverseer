package org.joverseer.ui.command;

import java.io.FileWriter;
import java.util.ArrayList;

import org.joverseer.domain.Army;
import org.joverseer.domain.ArmyElement;
import org.joverseer.domain.ArmyElementType;
import org.joverseer.game.Game;
import org.joverseer.game.Turn;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.support.GameHolder;
import org.joverseer.ui.support.dialogs.ErrorDialog;
import org.springframework.richclient.command.ActionCommand;

/**
 * Admin command
 * 
 * Exports all t0 armies of this game to a text file (c:\file.out) 
 * 
 * @author Marios Skounakis
 */
public class ExportStartingArmiesCommand extends ActionCommand {
    public ExportStartingArmiesCommand() {
        super("exportStartingArmiesCommand");
    }

    @Override
	protected void doExecuteCommand() {
    	try {
	    	String fname = "c:\\file.out";
	    	FileWriter fw = new FileWriter(fname);
	    	
	    	Game g = GameHolder.instance().getGame();
	    	Turn t = g.getTurn(0);
	    	for (Army a : (ArrayList<Army>)t.getContainer(TurnElementsEnum.Army).getItems()) {
	    		if (a.computeNumberOfMen() == 0) continue;
	    		String s = "";
	    		s += a.getHexNo() + "," +
	    			a.getCommanderName() + "," +
	    			(a.getNationAllegiance() != null ? a.getNationAllegiance().getAllegiance() : "0") + "," +
	    			a.getNationNo() + "," +
	    			(a.isNavy() ? "1" : "0") + "," +
	    			a.getSize().getSize() + "," +
	    			a.computeNumberOfMen() + "," + 
	    			a.getMorale() + ",";
	    		
	    		for (ArmyElementType aet : ArmyElementType.values()) {
	    			ArmyElement ae = a.getElement(aet);
	    			if (ae == null) {
	    				s += aet.getType() + ",,,,,";
	    			} else {
	    				s += ae.getArmyElementType().getType() + "," +
	    					ae.getNumber() + "," +
	    					ae.getTraining() + "," +
	    					ae.getWeapons() + "," +
	    					ae.getArmor() + ",";
	    			}
	    		}
	            fw.write(s + "\n");
	    	}
	    	fw.close();
    	}
    	catch (Exception exc) {
    		ErrorDialog ed = new ErrorDialog(exc);
    		ed.showDialog();
    	}
    }
}
