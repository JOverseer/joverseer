package org.joverseer.ui.command;

import javax.swing.JOptionPane;

import org.joverseer.domain.Army;
import org.joverseer.support.GameHolder;
import org.joverseer.ui.support.ActiveGameChecker;
import org.joverseer.ui.support.Messages;
import org.joverseer.ui.support.dialogs.CustomTitledPageApplicationDialog;
import org.joverseer.ui.views.ExportOrdersForm;
import org.springframework.binding.form.FormModel;
import org.springframework.richclient.command.ActionCommand;
import org.springframework.richclient.dialog.FormBackedDialogPage;
import org.springframework.richclient.form.FormModelHelper;

/**
 * Opens the ExportOrdersForm
 * 
 * @author Marios Skounakis
 */
public class ExportOrdersCommand extends ActionCommand {
	final protected GameHolder gameHolder;
    public ExportOrdersCommand(GameHolder gameHolder) {
        super("ExportOrdersCommand");
        this.gameHolder = gameHolder;
    }

    @Override
	protected void doExecuteCommand() {
        if (!ActiveGameChecker.checkActiveGameExists()) return;
        if(this.gameHolder.getGame().getCurrentTurn() != this.gameHolder.getGame().getMaxTurn()) {
        	JOptionPane.showMessageDialog(
                null, 
                Messages.getString("turnWarning.content"), 
                Messages.getString("turnWarning.title"), 
                JOptionPane.INFORMATION_MESSAGE
            );
        	return;
        }
        FormModel formModel = FormModelHelper.createFormModel(new Army());
        final ExportOrdersForm form = new ExportOrdersForm(formModel,this.gameHolder);
        FormBackedDialogPage page = new FormBackedDialogPage(form);

        CustomTitledPageApplicationDialog dialog = new CustomTitledPageApplicationDialog(page) {
            @Override
			protected void onAboutToShow() {
            }

            @Override
			protected boolean onFinish() {
            	form.setSendAll(true);
            	form.commit();
                return form.getReadyToClose();
            }

			@Override
			protected String getFinishCommandId() {
        		return "submitAllNationsOrders";
			}
        };
        dialog.setTitle(Messages.getString("exportOrdersDialog.title"));
        dialog.showDialog();
    }

}