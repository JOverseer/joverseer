package org.joverseer.ui.command;

import java.util.Locale;

import org.joverseer.preferences.PreferenceRegistry;
import org.joverseer.ui.views.EditNationMetadataForm;
import org.joverseer.ui.views.EditPreferencesForm;
import org.springframework.binding.form.FormModel;
import org.springframework.context.MessageSource;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.command.ActionCommand;
import org.springframework.richclient.dialog.FormBackedDialogPage;
import org.springframework.richclient.dialog.TitledPageApplicationDialog;
import org.springframework.richclient.form.FormModelHelper;


public class EditPreferencesCommand  extends ActionCommand {
    
    public EditPreferencesCommand() {
        super("editPreferencesCommand");
    }

    protected void doExecuteCommand() {
        FormModel formModel = FormModelHelper.createFormModel(PreferenceRegistry.instance());
        final EditPreferencesForm form = new EditPreferencesForm(formModel);
        FormBackedDialogPage page = new FormBackedDialogPage(form);

        TitledPageApplicationDialog dialog = new TitledPageApplicationDialog(page) {
            protected void onAboutToShow() {
            }

            protected boolean onFinish() {
                form.commit();
                return true;
            }
        };
        MessageSource ms = (MessageSource)Application.services().getService(MessageSource.class);
        dialog.setTitle(ms.getMessage("editPreferencesDialog.title", new Object[]{}, Locale.getDefault()));
        dialog.showDialog();
    }

}
