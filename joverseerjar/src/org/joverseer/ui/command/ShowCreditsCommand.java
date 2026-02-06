package org.joverseer.ui.command;

import org.joverseer.ui.support.Messages;
import org.joverseer.ui.support.dialogs.CustomTitledPageApplicationDialog;
import org.joverseer.ui.views.CreditsForm;
import org.springframework.binding.form.FormModel;
import org.springframework.core.io.Resource;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.command.AbstractCommand;
import org.springframework.richclient.command.ActionCommand;
import org.springframework.richclient.dialog.FormBackedDialogPage;
import org.springframework.richclient.form.FormModelHelper;

/**
 * Shows the program credits using the CreditsForm
 * 
 * @author Marios Skounakis
 */
public class ShowCreditsCommand  extends ActionCommand {
    public ShowCreditsCommand() {
        super("ShowCreditsCommand");
    }

    @Override
	protected void doExecuteCommand() {
        Resource res = Application.instance().getApplicationContext().getResource("classpath:ui/credits.htm");
        FormModel formModel = FormModelHelper.createFormModel(res);
        final CreditsForm form = new CreditsForm(formModel);
        FormBackedDialogPage page = new FormBackedDialogPage(form);

        CustomTitledPageApplicationDialog dialog = new CustomTitledPageApplicationDialog(page) {
            @Override
			protected void onAboutToShow() {
            }

            @Override
			protected boolean onFinish() {
                return true;
            }

            @Override
			protected Object[] getCommandGroupMembers() {
                return new AbstractCommand[] {
                        getFinishCommand()
                };
            }
        };
        dialog.setTitle(Messages.getString("creditsDialog.title"));
        dialog.showDialog();
    }
}
