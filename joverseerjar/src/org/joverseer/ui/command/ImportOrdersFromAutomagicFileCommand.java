package org.joverseer.ui.command;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.prefs.Preferences;

import javax.swing.JFileChooser;

import org.joverseer.joApplication;
import org.joverseer.support.GameHolder;
import org.joverseer.support.readers.orders.OrderFileReader;
import org.joverseer.ui.LifecycleEventsEnum;
import org.joverseer.ui.support.ActiveGameChecker;
import org.joverseer.ui.support.Messages;
import org.joverseer.ui.support.dialogs.ErrorDialog;
import org.springframework.core.io.Resource;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.command.ActionCommand;
import org.springframework.richclient.dialog.ConfirmationDialog;
import org.springframework.richclient.dialog.MessageDialog;

/**
 * Imports orders from a Joverseer (automagic or meow) order file
 * 
 * @author Marios Skounakis
 */
public class ImportOrdersFromAutomagicFileCommand extends ActionCommand {
    private boolean confirmed = false;
    
    public ImportOrdersFromAutomagicFileCommand() {
        super("importOrdersFromAutomagicFileCommand"); //$NON-NLS-1$
    }

    @Override
	protected void doExecuteCommand() {
        if (!ActiveGameChecker.checkActiveGameExists()) return;
    	loadOrders();
    }

    private void loadOrders() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
        fileChooser.setApproveButtonText("Load"); //$NON-NLS-1$
        Preferences prefs = Preferences.userNodeForPackage(ImportOrdersFromAutomagicFileCommand.class);
        String lastDir = prefs.get("importOrdersDir", null); //$NON-NLS-1$
        if (lastDir != null) {
            fileChooser.setCurrentDirectory(new File(lastDir));
        }
        if (fileChooser.showOpenDialog(Application.instance().getActiveWindow().getControl()) == JFileChooser.APPROVE_OPTION) {
            File f = fileChooser.getSelectedFile();
            
            prefs.put("importOrdersDir", f.getParentFile().getAbsolutePath()); //$NON-NLS-1$
            
            GameHolder gh = GameHolder.instance();
            try {
                String orderFile = "file:///" + f.getAbsolutePath();

                OrderFileReader orderFileReader = new OrderFileReader(gh.getGame());
                // check game ok
                Resource resource = Application.instance().getApplicationContext().getResource(orderFile);
                BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()));
                if (!orderFileReader.checkGame(reader)) {
                    ConfirmationDialog dlg = new ConfirmationDialog() {
                        @Override
						protected void onConfirm() {
                            ImportOrdersFromAutomagicFileCommand.this.confirmed = true;
                        }
                    };
                    dlg.setConfirmationMessage(Messages.getString("confirmInvalidAMFileImportDialog.message"));
                    dlg.setTitle(Messages.getString("confirmInvalidAMFileImportDialog.title"));
                    dlg.showDialog();
                    if (!this.confirmed) return;
                }
                reader = new BufferedReader(new InputStreamReader(resource.getInputStream()));
                orderFileReader.readOrders(reader);
                MessageDialog dlg = new MessageDialog(Messages.getString("importOrdersFromAutomagicFileCommand.importOrders"),
                		Messages.getString("importOrdersFromAutomagicFileCommand.OrdersImported", new Object[] {orderFileReader.getOrdersRead() }));
                dlg.showDialog();
                joApplication.publishEvent(LifecycleEventsEnum.GameChangedEvent, gh.getGame(), this);

            }
            catch (Exception exc) {
            	ErrorDialog.showErrorDialog(exc);
            }
        }
    }
}
