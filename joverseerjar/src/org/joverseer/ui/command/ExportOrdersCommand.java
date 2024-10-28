package org.joverseer.ui.command;

import java.awt.Component;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;

import org.joverseer.domain.Army;
import org.joverseer.support.GameHolder;
import org.joverseer.ui.support.ActiveGameChecker;
import org.joverseer.ui.support.Messages;
import org.joverseer.ui.views.ExportOrdersForm;
import org.springframework.binding.form.FormModel;
import org.springframework.richclient.command.ActionCommand;
import org.springframework.richclient.dialog.FormBackedDialogPage;
import org.springframework.richclient.dialog.TitledPageApplicationDialog;
import org.springframework.richclient.form.FormModelHelper;

/**
 * Opens the ExportOrdersForm
 * 
 * @author Marios Skounakis
 */
public class ExportOrdersCommand extends ActionCommand {
	final protected GameHolder gameHolder;
	ExportOrdersForm form;
	TitledPageApplicationDialog dialog;
	
    public ExportOrdersCommand(GameHolder gameHolder) {
        super("ExportOrdersCommand");
        this.gameHolder = gameHolder;
    }

    @Override
	protected void doExecuteCommand() {
        if (!ActiveGameChecker.checkActiveGameExists()) return;
        FormModel formModel = FormModelHelper.createFormModel(new Army());
        this.form = new ExportOrdersForm(formModel,this.gameHolder);
        FormBackedDialogPage page = new FormBackedDialogPage(this.form);

        this.dialog = new TitledPageApplicationDialog(page) {
            @Override
			protected void onAboutToShow() {
            }

            @Override
			protected boolean onFinish() {
            	ExportOrdersCommand.this.form.setSendAll(false);
            	ExportOrdersCommand.this.form.commit();
                return ExportOrdersCommand.this.form.getReadyToClose();
            }

			@Override
			protected String getFinishCommandId() {
        		return "ExportOrdersSubmit";
			}
			
			@Override
			protected Object[] getCommandGroupMembers() {	//getFinishCommand(), 
				return new Object[] { new ActionCommand("submitAllNationsOrders") { //$NON-NLS-1$
					@Override
					protected void doExecuteCommand() {
						submitAllOrders();
					}
				}, getCancelCommand() };
			}
        };

        this.dialog.setTitle(Messages.getString("exportOrdersDialog.title"));
        this.dialog.showDialog();
    }
    
    public boolean submitAllOrders() {
    	this.form.setSendAll(true);
        this.form.commit();
        return this.form.getReadyToClose();
    }

}
