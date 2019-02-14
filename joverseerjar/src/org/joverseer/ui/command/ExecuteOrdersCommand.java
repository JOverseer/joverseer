package org.joverseer.ui.command;

//import org.joverseer.engine.Engine;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import org.joverseer.JOApplication;
import org.joverseer.game.Game;
import org.springframework.richclient.command.ActionCommand;

public class ExecuteOrdersCommand extends ActionCommand {

	public ExecuteOrdersCommand() {
		super("executeOrdersCommand");
	}

	@Override
	protected void doExecuteCommand() {
		Game g = JOApplication.getGame();
		if (g == null || !Game.isInitialized(g))
			return;
		try {
			Class<?> c = Class.forName("org.joverseer.engine.Engine");
			Constructor<?> constructor = c.getConstructor();
			Object engine = constructor.newInstance();
			Method m = c.getDeclaredMethod("executeOrders", Game.class);
			m.invoke(engine, g);
		} catch (ClassNotFoundException e) {

		} catch (Exception e) {

		}
		// Engine engine = new Engine();
		// engine.executeOrders(g);
	}

}
