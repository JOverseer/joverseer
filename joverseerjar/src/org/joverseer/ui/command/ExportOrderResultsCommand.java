package org.joverseer.ui.command;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;

import org.joverseer.domain.Character;
import org.joverseer.game.Turn;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.support.GameHolder;
import org.joverseer.ui.support.dialogs.ErrorDialog;
import org.springframework.richclient.command.ActionCommand;

/**
 * Admin command
 *
 * Exports all Order Results for all characters and all turns for this game
 *
 * Output file is: c:\orders.out.txt
 *
 * @author Marios Skounakis
 */
public class ExportOrderResultsCommand extends ActionCommand {
	//dependencies
	GameHolder gameHolder;
    public ExportOrderResultsCommand(GameHolder gameHolder) {
        super("exportOrderResultsCommand");
        this.gameHolder = gameHolder;
    }

    @Override
	protected void doExecuteCommand() {
    	try {
    		File f = new File("c:\\orders.out.txt");
    		FileWriter w = new FileWriter(f);
    		for (int i=0; i<this.gameHolder.getGame().getMaxTurn(); i++) {
    			Turn t = this.gameHolder.getGame().getTurn(i);
    			if (t == null) continue;
    			w.write("\n\n\nTurn " + i + "\n");
    			for (Character c : (ArrayList<Character>)t.getContainer(TurnElementsEnum.Character).getItems()) {
    				if (c.getOrderResults() != null && !c.getOrderResults().equals("")) {
    					w.write("\n\n");
    					w.write(c.getName() + " " + c.getHexNo() + " t" + i + "\n");
    					w.write(c.getOrderResults());
    				}
    			}

    		}
    		w.close();
    	}
    	catch (Exception exc) {
    		ErrorDialog.showErrorDialog(exc);
    	}
    }

}
