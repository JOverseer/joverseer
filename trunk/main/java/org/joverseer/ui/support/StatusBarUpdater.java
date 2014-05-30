package org.joverseer.ui.support;

import org.springframework.context.ApplicationListener;
import org.springframework.context.ApplicationEvent;
import org.springframework.richclient.application.Application;
import org.joverseer.ui.LifecycleEventsEnum;
import org.joverseer.game.Game;
import org.joverseer.support.GameHolder;
import org.joverseer.metadata.GameMetadata;

/**
 * Updates the UI with info about the game
 * Currently, although called StatusBarUpdater, it updates the title bar
 * 
 * @author: Marios Skounakis
 */
public class StatusBarUpdater implements ApplicationListener {
    @Override
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
        GameHolder gh = (GameHolder)Application.instance().getApplicationContext().getBean("gameHolder");  //$NON-NLS-1$
        Game game = gh.getGame();
        if (game == null) {
            msg = Messages.getString("StatusBarUpdater.NoActiveGame"); //$NON-NLS-1$
        } else {
            GameMetadata gm = game.getMetadata();
            msg = Messages.getString("StatusBarUpdater.GameAndTurn"); //$NON-NLS-1$
            msg = String.format(msg, gm.getGameNo(), gm.getGameType().toString() + (gm.getNewXmlFormat() ? "n" : ""), gm.getNationByNum(gm.getNationNo()).getName(), game.getCurrentTurn()); //$NON-NLS-1$ //$NON-NLS-2$
            if (game.getTurn() != null && game.getTurn().getSeason() != null) {
                msg = String.format(Messages.getString("StatusBarUpdater.5"), msg, game.getTurn().getSeason(), game.getTurn().getTurnDate()); //$NON-NLS-1$
            }
            if (gh.getFile() != null) msg += " - " + gh.getFile();  //$NON-NLS-1$
        }
        //Application.instance().getActiveWindow().getStatusBar().setMessage(msg);
        Application.instance().getActiveWindow().getControl().setTitle("JOverseer - " + msg); //$NON-NLS-1$
    }
}
