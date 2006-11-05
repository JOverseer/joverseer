package org.joverseer.ui.command;

import org.springframework.richclient.command.ActionCommand;
import org.springframework.richclient.application.Application;
import org.joverseer.game.Game;
import org.joverseer.metadata.GameMetadata;
import org.joverseer.metadata.GameTypeEnum;
import org.joverseer.support.GameHolder;

/**
 * Created by IntelliJ IDEA.
 * User: mskounak
 * Date: 22 Σεπ 2006
 * Time: 10:46:41 μμ
 * To change this template use File | Settings | File Templates.
 */
public class CreateGame extends ActionCommand {
    public CreateGame() {
        super("createGameCommand");
    }

    protected void doExecuteCommand() {
        Game game = new Game();
        game.setMaxTurn(0);
        GameMetadata gm = (GameMetadata)Application.instance().getApplicationContext().getBean("gameMetadata");
        game.setMetadata(gm);
        GameHolder gh = (GameHolder)Application.instance().getApplicationContext().getBean("gameHolder");
        gh.setGame(game);
    }
}
