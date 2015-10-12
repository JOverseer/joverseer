package org.joverseer.ui.command;

import org.joverseer.preferences.PreferenceRegistry;
import org.joverseer.ui.support.Messages;
import org.joverseer.ui.views.EditPreferencesForm;
import org.springframework.binding.form.FormModel;
import org.springframework.richclient.command.ActionCommand;
import org.springframework.richclient.dialog.FormBackedDialogPage;
import org.springframework.richclient.dialog.TitledPageApplicationDialog;
import org.springframework.richclient.form.FormModelHelper;

/**
 * Opens the EditPreferencesForm to set the preferences
 * 
 * @author Marios Skounakis
 */

public class EditPreferencesCommand  extends ActionCommand {
    
    public EditPreferencesCommand() {
        super("editPreferencesCommand");
    }

    @Override
	protected void doExecuteCommand() {
        FormModel formModel = FormModelHelper.createFormModel(PreferenceRegistry.instance());
        final EditPreferencesForm form = new EditPreferencesForm(formModel);
        FormBackedDialogPage page = new FormBackedDialogPage(form);

        TitledPageApplicationDialog dialog = new TitledPageApplicationDialog(page) {
            @Override
			protected void onAboutToShow() {
            }

            @Override
			protected boolean onFinish() {
                form.commit();
                return true;
            }
        };
        dialog.setTitle(Messages.getString("editPreferencesDialog.title"));
        dialog.showDialog();
    }

}
