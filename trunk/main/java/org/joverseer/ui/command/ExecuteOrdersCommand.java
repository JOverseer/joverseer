package org.joverseer.ui.command;

import org.joverseer.engine.Engine;
import org.joverseer.game.Game;
import org.joverseer.support.GameHolder;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.command.ActionCommand;

public class ExecuteOrdersCommand extends ActionCommand {
    
    public ExecuteOrdersCommand() {
        super("executeOrdersCommand");
    }

    protected void doExecuteCommand() {
        Game g = ((GameHolder)Application.instance().getApplicationContext().getBean("gameHolder")).getGame();
        if (g == null || !Game.isInitialized(g)) return;
        Engine engine = new Engine();
        engine.executeOrders(g);
    }

}
