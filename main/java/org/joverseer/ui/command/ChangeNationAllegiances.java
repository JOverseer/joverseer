package org.joverseer.ui.command;

import java.util.Locale;

import org.joverseer.game.Game;
import org.joverseer.support.GameHolder;
import org.joverseer.ui.support.ActiveGameChecker;
import org.joverseer.ui.views.EditNationAllegiancesForm;
import org.springframework.binding.form.FormModel;
import org.springframework.context.MessageSource;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.command.ActionCommand;
import org.springframework.richclient.dialog.FormBackedDialogPage;
import org.springframework.richclient.dialog.TitledPageApplicationDialog;
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
		final Game g = ((GameHolder) Application.instance().getApplicationContext().getBean("gameHolder")).getGame();
		FormModel formModel = FormModelHelper.createFormModel(g.getMetadata());
		final EditNationAllegiancesForm form = new EditNationAllegiancesForm(formModel);
		FormBackedDialogPage page = new FormBackedDialogPage(form);

		TitledPageApplicationDialog dialog = new TitledPageApplicationDialog(page) {
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
		MessageSource ms = (MessageSource) Application.services().getService(MessageSource.class);
		dialog.setTitle(ms.getMessage("changeNationAllegiancesDialog.title", new Object[] {}, Locale.getDefault()));
		dialog.showDialog();
	}

}
