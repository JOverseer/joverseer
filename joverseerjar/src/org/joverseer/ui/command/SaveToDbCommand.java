package org.joverseer.ui.command;

import org.joverseer.db.JOverseerDAO;
import org.joverseer.support.GameHolder;
import org.springframework.richclient.command.ActionCommand;

public class SaveToDbCommand extends ActionCommand {

	//dependencies
	GameHolder gameHolder;
    public SaveToDbCommand(GameHolder gameHolder) {
        super("saveToDbCommand");
        this.gameHolder = gameHolder;
    }

    @Override
	protected void doExecuteCommand() {
    	JOverseerDAO dao = new JOverseerDAO();
    	dao.SerializeGame("c:\\jov.mdb", this.gameHolder.getGame());
    }
}
