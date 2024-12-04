package org.joverseer.ui.command;

import java.io.IOException;

import org.joverseer.JOApplication;
import org.joverseer.game.Game;
import org.joverseer.metadata.MetadataReaderException;
import org.joverseer.support.GameHolder;
import org.joverseer.ui.LifecycleEventsEnum;
import org.joverseer.ui.support.ActiveGameChecker;
import org.joverseer.ui.support.Messages;
import org.springframework.richclient.command.ActionCommand;
import org.springframework.richclient.dialog.ConfirmationDialog;

public class RefreshGameCSV extends ActionCommand {
	GameHolder gameHolder;
	public RefreshGameCSV(GameHolder gameHolder) {
		super("refreshGameCSV");
		this.gameHolder = gameHolder;
	}

	@Override
	protected void doExecuteCommand() {
		// TODO Auto-generated method stub
		if (!ActiveGameChecker.checkActiveGameExists()) return;
		final Game g = this.gameHolder.getGame();
		ConfirmationDialog dlg = new ConfirmationDialog(Messages.getString("refreshGameCSV.ConfirmationTitle"),
				Messages.getString("refreshGameCSV.ConfirmationMessage")) {
			@Override
			protected void onConfirm() {
				try {
					g.getMetadata().loadCombatModifiers(true);
				} catch (IOException | MetadataReaderException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				JOApplication.publishEvent(LifecycleEventsEnum.GameChangedEvent, g, this);
			}
		};
		dlg.showDialog();


	}

}
