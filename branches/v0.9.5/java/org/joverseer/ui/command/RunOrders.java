package org.joverseer.ui.command;

import org.joverseer.game.Game;
import org.joverseer.game.Turn;
import org.joverseer.orders.BaseTurnProcessor;
import org.joverseer.support.GameHolder;
import org.joverseer.ui.LifecycleEventsEnum;
import org.joverseer.ui.support.ActiveGameChecker;
import org.joverseer.ui.support.JOverseerEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.command.ActionCommand;


public class RunOrders extends ActionCommand {

    public RunOrders() {
        super("runOrdersCommand");
    }

    protected void doExecuteCommand() {
        if (!ActiveGameChecker.checkActiveGameExists()) return;
        Game g = GameHolder.instance().getGame();
        BaseTurnProcessor btp = (BaseTurnProcessor)Application.instance().getApplicationContext().getBean("TurnProcessor");
        Turn newTurn = btp.copyTurn(g.getTurn());
        newTurn.setTurnNo(g.getTurn().getTurnNo() + 1);
        btp.processTurn(newTurn);
        try {
            g.addTurn(newTurn);
            Application.instance().getApplicationContext().publishEvent(
                    new JOverseerEvent(LifecycleEventsEnum.GameChangedEvent.toString(), g, this));

        }
        catch (Exception exc) {}
    }

}
