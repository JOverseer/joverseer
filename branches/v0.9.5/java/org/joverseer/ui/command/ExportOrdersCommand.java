package org.joverseer.ui.command;

import java.util.Locale;

import org.joverseer.domain.Army;
import org.joverseer.ui.support.ActiveGameChecker;
import org.joverseer.ui.views.EditNationAllegiancesForm;
import org.joverseer.ui.views.ExportOrdersForm;
import org.springframework.binding.form.FormModel;
import org.springframework.context.MessageSource;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.command.ActionCommand;
import org.springframework.richclient.dialog.FormBackedDialogPage;
import org.springframework.richclient.dialog.TitledPageApplicationDialog;
import org.springframework.richclient.form.FormModelHelper;


public class ExportOrdersCommand extends ActionCommand {
    public ExportOrdersCommand() {
        super("ExportOrdersCommand");
    }

    protected void doExecuteCommand() {
        if (!ActiveGameChecker.checkActiveGameExists()) return;
        FormModel formModel = FormModelHelper.createFormModel(new Army());
        final ExportOrdersForm form = new ExportOrdersForm(formModel);
        FormBackedDialogPage page = new FormBackedDialogPage(form);

        TitledPageApplicationDialog dialog = new TitledPageApplicationDialog(page) {
            protected void onAboutToShow() {
            }

            protected boolean onFinish() {
                return true;
            }
        };
        MessageSource ms = (MessageSource)Application.services().getService(MessageSource.class);
        dialog.setTitle(ms.getMessage("exportOrdersDialog.title", new Object[]{}, Locale.getDefault()));
        dialog.showDialog();
    }

}
