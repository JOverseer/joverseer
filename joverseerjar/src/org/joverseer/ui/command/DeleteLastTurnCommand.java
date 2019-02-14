package org.joverseer.ui.command;

import org.joverseer.JOApplication;
import org.joverseer.game.Game;
import org.joverseer.support.GameHolder;
import org.joverseer.ui.LifecycleEventsEnum;
import org.joverseer.ui.support.ActiveGameChecker;
import org.joverseer.ui.support.Messages;
import org.joverseer.ui.support.dialogs.ErrorDialog;
import org.springframework.richclient.command.ActionCommand;
import org.springframework.richclient.dialog.ConfirmationDialog;

/**
 * Delete the last turn of the game
 *
 * @author Marios Skounakis
 */
public class DeleteLastTurnCommand extends ActionCommand {

	//dependencies
	GameHolder gameHolder;
	public DeleteLastTurnCommand(GameHolder gameHolder) {
		super("deleteLastTurnCommand");
		this.gameHolder = gameHolder;
	}

	@Override
	protected void doExecuteCommand() {
		if (!ActiveGameChecker.checkActiveGameExists())
			return;
		final Game g = this.gameHolder.getGame();
		if (g.getMaxTurn() == 0) {
    		ErrorDialog.showErrorDialog("standardErrors.NoTurnsInGame");
			return;
		}
		ConfirmationDialog dlg = new ConfirmationDialog(Messages.getString("deleteLastTurnCommand.ConfirmationTitle"),
				Messages.getString("deleteLastTurnCommand.ConfirmationMessage")) {
			@Override
			protected void onConfirm() {

				g.getTurns().removeItem(g.getTurn(g.getMaxTurn()));
				int newMaxTurn = 0;
				for (int i = 0; i < g.getMaxTurn(); i++) {
					if (g.getTurn(i) != null) {
						newMaxTurn = i;
					}
				}
				if (g.getCurrentTurn() == g.getMaxTurn()) {
					g.setCurrentTurn(newMaxTurn);
				}
				g.setMaxTurn(newMaxTurn);
				JOApplication.publishEvent(LifecycleEventsEnum.GameChangedEvent, g, this);
			}
		};
		dlg.showDialog();
	}
}
