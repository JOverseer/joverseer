package org.joverseer.ui.command;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.Locale;
import java.util.prefs.Preferences;

import javax.swing.JFileChooser;

import org.joverseer.game.Game;
import org.joverseer.support.GameHolder;
import org.joverseer.support.readers.orders.OrderFileReader;
import org.joverseer.ui.LifecycleEventsEnum;
import org.joverseer.ui.JOverseerClient;
import org.joverseer.ui.support.JOverseerEvent;
import org.springframework.context.MessageSource;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.command.ActionCommand;
import org.springframework.richclient.dialog.ConfirmationDialog;
import org.springframework.richclient.dialog.MessageDialog;

/**
 * Created by IntelliJ IDEA.
 * User: mskounak
 * Date: 13 Οκτ 2006
 * Time: 6:54:07 μμ
 * To change this template use File | Settings | File Templates.
 */
public class ImportOrders extends ActionCommand {
    public ImportOrders() {
        super("ImportOrdersCommand");
    }

    protected void doExecuteCommand() {
    	if (!GameHolder.hasInitializedGame()) {
            // show error, cannot import when game not initialized
            MessageSource ms = (MessageSource)Application.services().getService(MessageSource.class);
            MessageDialog md = new MessageDialog(
                    ms.getMessage("errorDialog.title", new String[]{}, Locale.getDefault()),
                    ms.getMessage("errorImportingTurns", new String[]{}, Locale.getDefault()));
            md.showDialog();
            return;
        }
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
                orderFileReader.readOrders();
                Application.instance().getApplicationContext().publishEvent(
                                    new JOverseerEvent(LifecycleEventsEnum.GameChangedEvent.toString(), gh.getGame(), this));

            }
            catch (Exception exc) {
                MessageDialog d = new MessageDialog("Error", exc.getMessage());
                d.showDialog();
                // do nothing
                // todo fix
            }
        }
    }
}
