package org.joverseer.ui.command;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

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

/**
 * Runs the Orders (basically runs the game)
 * 
 * This is an unfinished command that is part of the execution module, whose aim is to
 * be able to execute the commands, thus turning JOverseer into a game module in addiotion
 * to a gui module. 
 * 
 * Needs implementation.
 * 
 * @author Marios Skounakis
 */
public class RunOrders extends ActionCommand {

    public RunOrders() {
        super("runOrdersCommand");
    }

    protected void doExecuteCommand() {
        if (!ActiveGameChecker.checkActiveGameExists()) return;
        Game g = GameHolder.instance().getGame();
        try {
        	Class c = Class.forName("org.joverseer.orders.BaseTurnProcessor");
        	Constructor constructor = c.getConstructor();
        	Object btp = constructor.newInstance();
        	Method m = c.getDeclaredMethod("processGame");
        	m.invoke(btp);
        }
        catch (ClassNotFoundException e) {
        	
        }
        catch (Exception e) {
        	
        }
    }

}
