package org.joverseer.ui.support;

import org.springframework.context.ApplicationListener;
import org.springframework.context.ApplicationEvent;
import org.springframework.richclient.application.Application;
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
	final Application theOneApplication;
	final GameHolder gh;

	public StatusBarUpdater(Application anApplication,GameHolder gh) {
		this.theOneApplication = anApplication;
		this.gh = gh;
	}
	@Override
	public void onApplicationEvent(ApplicationEvent applicationEvent) {
        if (applicationEvent instanceof JOverseerEvent) {
        	this.onJOEvent((JOverseerEvent)applicationEvent);
        }
    }
    public void onJOEvent(JOverseerEvent e) {
        switch (e.type) {
        case GameChangedEvent:
        case SelectedTurnChangedEvent:
            refreshGameInfo();
        }
    }

    private void refreshGameInfo() {
        String msg = null;
        Game game = this.gh.getGame();
        if (game == null) {
            msg = Messages.getString("StatusBarUpdater.NoActiveGame"); //$NON-NLS-1$
        } else {
            GameMetadata gm = game.getMetadata();
            msg = Messages.getString("StatusBarUpdater.GameAndTurn"); //$NON-NLS-1$
            msg = String.format(msg, gm.getGameNo(), gm.getGameType().toString(), gm.getNationByNum(gm.getNationNo()).getName(), game.getCurrentTurn()); //$NON-NLS-1$ //$NON-NLS-2$
            if (game.getTurn() != null && game.getTurn().getSeason() != null) {
                msg = String.format(Messages.getString("StatusBarUpdater.5"), msg, game.getTurn().getSeason(), game.getTurn().getTurnDate()); //$NON-NLS-1$
            }
            if (this.gh.getFile() != null) msg += " - " + this.gh.getFile();  //$NON-NLS-1$
        }
        this.theOneApplication.getActiveWindow().getControl().setTitle("JOverseer - " + msg); //$NON-NLS-1$
    }
}
