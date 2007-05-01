package org.joverseer.ui.command;

import java.io.File;
import java.util.Locale;

import javax.swing.JFileChooser;

import org.joverseer.support.GameHolder;
import org.joverseer.support.readers.orders.OrderFileReader;
import org.joverseer.ui.LifecycleEventsEnum;
import org.joverseer.ui.support.ActiveGameChecker;
import org.joverseer.ui.support.JOverseerEvent;
import org.springframework.context.MessageSource;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.command.ActionCommand;
import org.springframework.richclient.dialog.ConfirmationDialog;
import org.springframework.richclient.dialog.MessageDialog;


public class ImportOrdersFromAutomagicFileCommand extends ActionCommand {
    private boolean confirmed = false;
    
    public ImportOrdersFromAutomagicFileCommand() {
        super("importOrdersFromAutomagicFileCommand");
    }

    protected void doExecuteCommand() {
        if (!ActiveGameChecker.checkActiveGameExists()) return;
    	loadOrders();
    }

    private void loadOrders() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
        fileChooser.setApproveButtonText("Load");
        if (fileChooser.showOpenDialog(Application.instance().getActiveWindow().getControl()) == JFileChooser.APPROVE_OPTION) {
            File f = fileChooser.getSelectedFile();
            
            GameHolder gh = (GameHolder) Application.instance().getApplicationContext().getBean("gameHolder");
            try {
                OrderFileReader orderFileReader = new OrderFileReader();
                orderFileReader.setGame(gh.getGame());
                orderFileReader.setOrderFile("file:///" + f.getAbsolutePath());
                // check game ok
                if (!orderFileReader.checkGame()) {
                    ConfirmationDialog dlg = new ConfirmationDialog() {
                        protected void onConfirm() {
                            confirmed = true;
                        }
                    };
                    MessageSource ms = (MessageSource)Application.instance().getApplicationContext().getBean("messageSource");
                    dlg.setConfirmationMessage(ms.getMessage("confirmInvalidAMFileImportDialog.message", new Object[]{}, Locale.getDefault()));
                    dlg.setTitle(ms.getMessage("confirmInvalidAMFileImportDialog.title", new Object[]{}, Locale.getDefault()));
                    dlg.showDialog();
                    if (!confirmed) return;
                }
                orderFileReader.readOrders();
                MessageDialog dlg = new MessageDialog("Import Orders", orderFileReader.getOrdersRead() + " orders were imported.");
                dlg.showDialog();
                Application.instance().getApplicationContext().publishEvent(
                                    new JOverseerEvent(LifecycleEventsEnum.GameChangedEvent.toString(), gh.getGame(), this));

            }
            catch (Exception exc) {
                MessageDialog d = new MessageDialog("Error", exc.getMessage());
                d.showDialog();
            }
        }
    }
}