package org.joverseer.ui.command;

import org.joverseer.JOApplication;
import org.joverseer.game.Game;
import org.joverseer.support.GameHolder;
import org.joverseer.ui.LifecycleEventsEnum;
import org.joverseer.ui.map.MapPanel;
import org.joverseer.ui.support.ActiveGameChecker;
import org.joverseer.ui.support.Messages;
import org.joverseer.ui.support.dialogs.CustomTitledPageApplicationDialog;
import org.joverseer.ui.views.EditPlayedNationsForm;
import org.springframework.binding.form.FormModel;
import org.springframework.richclient.command.ActionCommand;
import org.springframework.richclient.dialog.FormBackedDialogPage;
import org.springframework.richclient.dialog.MessageDialog;
import org.springframework.richclient.form.FormModelHelper;

/**
 * Opens the EditPlayedNations Form
 * 
 * @author Sam Terrett
 */
public class SelectPlayedNations extends ActionCommand {
	final protected GameHolder gameHolder;

	public SelectPlayedNations(GameHolder gameHolder) {
		super("selectPlayedNationsCommand");
		this.gameHolder = gameHolder;
	}

	@Override
	protected void doExecuteCommand() {
		if (!ActiveGameChecker.checkActiveGameExists())
			return;
		//Prevents running if PlayerInfo is null (no where to save info to)
		if(this.gameHolder.getGame().getTurn().getPlayerInfo(this.gameHolder.getGame().getMetadata().getNationNo()) == null){
			MessageDialog dialog = new MessageDialog(Messages.getString("selectPlayedNationsCommand.MessageNoPI.Title"), Messages.getString("selectPlayedNationsCommand.MessageNoPI.Message"));
	        dialog.showDialog();
			return;
		}
		final Game g = JOApplication.getGame();
		FormModel formModel = FormModelHelper.createFormModel(g.getMetadata());
		final EditPlayedNationsForm form = new EditPlayedNationsForm(formModel, this.gameHolder);
		FormBackedDialogPage page = new FormBackedDialogPage(form);
		page.setTitle(Messages.getString("editPlayedNationsForm.title"));	
		
		CustomTitledPageApplicationDialog dialog = new CustomTitledPageApplicationDialog(page) {
			@Override
			protected void onAboutToShow() {
				form.setFormObject(g);
			}

			@Override
			protected boolean onFinish() {
				form.commit();
				JOApplication.publishEvent(LifecycleEventsEnum.RefreshHexItems, MapPanel.instance().getSelectedHex(), this);
				return true;
			}
		};
		dialog.setTitle(Messages.getString("selectPlayedNationsDialog.title"));
		dialog.showDialog();
	}
}
