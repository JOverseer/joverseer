package org.joverseer.ui.command;

import java.util.Locale;

import javax.swing.JComponent;

import org.joverseer.domain.Army;
import org.joverseer.ui.CreditsForm;
import org.springframework.binding.form.FormModel;
import org.springframework.context.MessageSource;
import org.springframework.core.io.Resource;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.command.AbstractCommand;
import org.springframework.richclient.command.ActionCommand;
import org.springframework.richclient.dialog.FormBackedDialogPage;
import org.springframework.richclient.dialog.TitledPageApplicationDialog;
import org.springframework.richclient.form.FormModelHelper;


public class ShowCreditsCommand  extends ActionCommand {
    public ShowCreditsCommand() {
        super("ShowCreditsCommand");
    }

    protected void doExecuteCommand() {
        Resource res = Application.instance().getApplicationContext().getResource("classpath:ui/credits.htm");
        FormModel formModel = FormModelHelper.createFormModel(res);
        final CreditsForm form = new CreditsForm(formModel);
        FormBackedDialogPage page = new FormBackedDialogPage(form);

        TitledPageApplicationDialog dialog = new TitledPageApplicationDialog(page) {
            protected void onAboutToShow() {
            }

            protected boolean onFinish() {
                return true;
            }

            protected Object[] getCommandGroupMembers() {
                return new AbstractCommand[] {
                        getFinishCommand()
                };
            }
        };
        MessageSource ms = (MessageSource)Application.services().getService(MessageSource.class);
        dialog.setTitle(ms.getMessage("creditsDialog.title", new Object[]{}, Locale.getDefault()));
        dialog.showDialog();
    }
}
