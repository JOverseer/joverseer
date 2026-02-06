package org.joverseer.ui.command;

import org.joverseer.preferences.PreferenceRegistry;
import org.joverseer.ui.support.Messages;
import org.joverseer.ui.support.dialogs.CustomTitledPageApplicationDialog;
import org.joverseer.ui.views.EditPreferencesForm;
import org.springframework.binding.form.FormModel;
import org.springframework.richclient.command.ActionCommand;
import org.springframework.richclient.dialog.FormBackedDialogPage;
import org.springframework.richclient.form.FormModelHelper;

/**
 * Opens the EditPreferencesForm to set the preferences
 * 
 * @author Marios Skounakis
 */

public class EditPreferencesCommand  extends ActionCommand {
    
	private String group = null;
	
	public String getGroup() {
		return this.group;
	}
	public void setGroup(String value) {
		this.group = value;
		String label = "editPreferencesCommand." + this.group.replace(' ','.');
		setId(label);
	}
	
    public EditPreferencesCommand() {
        super("editPreferencesCommand");
    }

    @Override
	protected void doExecuteCommand() {
        FormModel formModel = FormModelHelper.createFormModel(PreferenceRegistry.instance());
        final EditPreferencesForm form = new EditPreferencesForm(formModel);
        form.setStartingGroup(this.group);
        FormBackedDialogPage page = new FormBackedDialogPage(form);

        CustomTitledPageApplicationDialog dialog = new CustomTitledPageApplicationDialog(page) {
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
