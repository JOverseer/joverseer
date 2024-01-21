/**
 * 
 */
package org.joverseer.ui.command;

import org.joverseer.support.GameHolder;
import org.joverseer.ui.support.ActiveGameChecker;
import org.joverseer.ui.support.Messages;
import org.joverseer.ui.support.dialogs.ErrorDialog;
import org.joverseer.ui.views.ExportDiploForm;
import org.joverseer.ui.views.ExportOrdersForm;
import org.springframework.binding.form.FormModel;
import org.springframework.richclient.command.ActionCommand;
import org.springframework.richclient.dialog.FormBackedDialogPage;
import org.springframework.richclient.dialog.TitledPageApplicationDialog;
import org.springframework.richclient.form.FormModelHelper;
import org.joverseer.domain.Diplo;

/**
 * Opens the ExportDiploForm
 * 
 * Mainly the same code as ExportOrderFormCommand with modifications
 * @author Sam Terrett
 */
public class ExportDiploCommand extends ActionCommand {
	final protected GameHolder gameHolder;
    public ExportDiploCommand(GameHolder gameHolder) {
        super("exportDiploCommand");
        this.gameHolder = gameHolder;
    }
	@Override
	protected void doExecuteCommand() {
        if (!ActiveGameChecker.checkActiveGameExists()) return;
        if(this.gameHolder.getGame().getTurn().getNationDiplo(this.gameHolder.getGame().getMetadata().getNationNo()) == null) {
        	ErrorDialog.showErrorDialog("No diplomatic message saved. \nTo save one, go to Tools -> Turn -> Diplomatic Message");
        	return;
        }
        FormModel formModel = FormModelHelper.createFormModel(new Diplo());
        
        final ExportDiploForm form = new ExportDiploForm(formModel,this.gameHolder);
        FormBackedDialogPage page = new FormBackedDialogPage(form);
        page.setTitle(Messages.getString("exportDiploForm.title", new Object[] {String.join(", ", this.gameHolder.getGame().getTurn().getNationDiplo(this.gameHolder.getGame().getMetadata().getNationNo()).getNations())}));

        TitledPageApplicationDialog dialog = new TitledPageApplicationDialog(page) {
            @Override
			protected void onAboutToShow() {
            }

            @Override
			protected boolean onFinish() {
            	form.commit();
                return form.getReadyToClose();
            }

			@Override
			protected String getFinishCommandId() {
        		return "ExportOrdersSubmit";
			}
        };
        dialog.setTitle(Messages.getString("exportDiploDialog.title"));
        dialog.showDialog();
	}

}
