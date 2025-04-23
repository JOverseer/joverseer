package org.joverseer.ui.command;

import org.joverseer.ui.support.Messages;
import org.joverseer.ui.support.dialogs.CustomTitledPageApplicationDialog;
import org.joverseer.ui.views.ChangelogForm;
import org.springframework.binding.form.FormModel;
import org.springframework.core.io.Resource;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.command.AbstractCommand;
import org.springframework.richclient.command.ActionCommand;
import org.springframework.richclient.dialog.FormBackedDialogPage;
import org.springframework.richclient.form.FormModelHelper;

/**
 * Shows the program changelog using the Changelog form
 * 
 * @author Marios Skounakis
 */
public class ShowChangelogCommand extends ActionCommand {
    public ShowChangelogCommand() {
        super("ShowChangelogCommand");
    }

    @Override
	protected void doExecuteCommand() {
        Resource res = Application.instance().getApplicationContext().getResource("classpath:changelog.txt");
        FormModel formModel = FormModelHelper.createFormModel(res);
        final ChangelogForm form = new ChangelogForm(formModel);
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
        dialog.setTitle(Messages.getString("changelogDialog.title"));
        dialog.showDialog();
    }
}