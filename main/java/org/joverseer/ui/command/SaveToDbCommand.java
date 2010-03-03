package org.joverseer.ui.command;

import org.joverseer.db.JOverseerDAO;
import org.joverseer.support.GameHolder;
import org.springframework.richclient.command.ActionCommand;

public class SaveToDbCommand extends ActionCommand {
    
    public SaveToDbCommand() {
        super("saveToDbCommand");
    }
    
    
    
    protected void doExecuteCommand() {
    	JOverseerDAO dao = new JOverseerDAO();
    	dao.SerializeGame("c:\\jov.mdb", GameHolder.instance().getGame());
    }
}
