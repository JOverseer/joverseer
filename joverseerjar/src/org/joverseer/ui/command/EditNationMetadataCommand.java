package org.joverseer.ui.command;

import org.joverseer.JOApplication;
import org.joverseer.game.Game;
import org.joverseer.ui.support.ActiveGameChecker;
import org.joverseer.ui.support.Messages;
import org.joverseer.ui.support.dialogs.CustomTitledPageApplicationDialog;
import org.joverseer.ui.views.EditNationMetadataForm;
import org.springframework.binding.form.FormModel;
import org.springframework.richclient.command.ActionCommand;
import org.springframework.richclient.dialog.FormBackedDialogPage;
import org.springframework.richclient.form.FormModelHelper;

/**
 * Opens the EditNationMetadataForm to edit the Nation Metadata
 * @author Marios Skounakis
 */
public class EditNationMetadataCommand extends ActionCommand {
    
    public EditNationMetadataCommand() {
        super("editNationMetadataCommand");
    }

    @Override
	protected void doExecuteCommand() {
    	if (!ActiveGameChecker.checkActiveGameExists()) return;
        final Game g = JOApplication.getGame();
        FormModel formModel = FormModelHelper.createFormModel(g.getMetadata());
        final EditNationMetadataForm form = new EditNationMetadataForm(formModel);
        FormBackedDialogPage page = new FormBackedDialogPage(form);

        CustomTitledPageApplicationDialog dialog = new CustomTitledPageApplicationDialog(page) {
            @Override
			protected void onAboutToShow() {
                form.setFormObject(g.getMetadata());
            }

            @Override
			protected boolean onFinish() {
                form.commit();
                return true;
            }
        };
        dialog.setTitle(Messages.getString("editNationMetadataDialog.title"));
        dialog.showDialog();
    }

}
