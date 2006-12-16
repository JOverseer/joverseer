package org.joverseer.ui.support;

import org.springframework.context.ApplicationListener;
import org.springframework.context.ApplicationEvent;
import org.springframework.richclient.application.Application;
import org.joverseer.ui.LifecycleEventsEnum;
import org.joverseer.game.Game;
import org.joverseer.support.GameHolder;
import org.joverseer.metadata.GameMetadata;

/**
 * Created by IntelliJ IDEA.
 * User: mskounak
 * Date: 16 Δεκ 2006
 * Time: 10:04:03 μμ
 * To change this template use File | Settings | File Templates.
 */
public class StatusBarUpdater implements ApplicationListener {
    public void onApplicationEvent(ApplicationEvent applicationEvent) {
        if (applicationEvent instanceof JOverseerEvent) {
            JOverseerEvent e = (JOverseerEvent)applicationEvent;
            if (e.getEventType().equals(LifecycleEventsEnum.GameChangedEvent.toString())) {
                refreshGameInfo();
            } else if (e.getEventType().equals(LifecycleEventsEnum.SelectedTurnChangedEvent.toString())) {
                refreshGameInfo();
            }
        }
    }

    private void refreshGameInfo() {
        String msg = null;
        Game game = ((GameHolder)Application.instance().getApplicationContext().getBean("gameHolder")).getGame();
        if (game == null) {
            msg = "No active game.";
        } else {
            GameMetadata gm = game.getMetadata();
            msg = "Game %s (%s), %s, Turn %s";
            msg = String.format(msg, gm.getGameNo(), gm.getGameType().toString(), gm.getNationByNum(gm.getNationNo()).getName(), game.getCurrentTurn());
        }
        //Application.instance().getActiveWindow().getStatusBar().setMessage(msg);
        Application.instance().getActiveWindow().getControl().setTitle("JOverseer - " + msg);
    }
}
