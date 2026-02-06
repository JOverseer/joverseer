package org.joverseer.ui.command;

import org.joverseer.JOApplication;
import org.joverseer.game.Game;
import org.joverseer.ui.support.ActiveGameChecker;
import org.joverseer.ui.support.Messages;
import org.joverseer.ui.support.dialogs.CustomTitledPageApplicationDialog;
import org.joverseer.ui.views.EditNationAllegiancesForm;
import org.springframework.binding.form.FormModel;
import org.springframework.richclient.command.ActionCommand;
import org.springframework.richclient.dialog.FormBackedDialogPage;
import org.springframework.richclient.form.FormModelHelper;

/**
 * Open the EditNationAllegiancesForm
 * 
 * @author Marios Skounakis
 */
public class ChangeNationAllegiances extends ActionCommand {

	public ChangeNationAllegiances() {
		super("changeNationAllegiancesCommand");
	}

	@Override
	protected void doExecuteCommand() {
		if (!ActiveGameChecker.checkActiveGameExists())
			return;
		final Game g = JOApplication.getGame();
		FormModel formModel = FormModelHelper.createFormModel(g.getMetadata());
		final EditNationAllegiancesForm form = new EditNationAllegiancesForm(formModel);
		FormBackedDialogPage page = new FormBackedDialogPage(form);

		CustomTitledPageApplicationDialog dialog = new CustomTitledPageApplicationDialog(page) {
			@Override
			protected void onAboutToShow() {
				form.setFormObject(g);
			}

			@Override
			protected boolean onFinish() {
				form.commit();
				return true;
			}
		};
		dialog.setTitle(Messages.getString("changeNationAllegiancesDialog.title"));
		dialog.showDialog();
	}

}
