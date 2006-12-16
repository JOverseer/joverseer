package org.joverseer.ui.command;

import org.springframework.richclient.command.ActionCommand;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.dialog.MessageDialog;
import org.springframework.context.MessageSource;
import org.joverseer.support.GameHolder;
import org.joverseer.ui.JOverseerClient;

import javax.swing.*;
import java.io.ObjectOutputStream;
import java.io.FileOutputStream;
import java.io.File;
import java.util.Locale;
import java.util.prefs.Preferences;

/**
 * Created by IntelliJ IDEA.
 * User: mskounak
 * Date: 24 ”ер 2006
 * Time: 11:03:59 рм
 * To change this template use File | Settings | File Templates.
 */
public class SaveGame extends ActionCommand {
    public SaveGame() {
        super("SaveGameCommand");
    }

    protected void doExecuteCommand() {
        if (!GameHolder.hasInitializedGame()) {
            // show error, cannot import when game not initialized
            MessageSource ms = (MessageSource)Application.services().getService(MessageSource.class);
            MessageDialog md = new MessageDialog(
                    ms.getMessage("errorDialog.title", new String[]{}, Locale.getDefault()),
                    ms.getMessage("errorSavingGame", new String[]{}, Locale.getDefault()));
            md.showDialog();
            return;
        }
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogType(JFileChooser.SAVE_DIALOG);
        fileChooser.setApproveButtonText("Save");

        GameHolder gh = (GameHolder) Application.instance().getApplicationContext().getBean("gameHolder");
        String fname = String.format("game%s.jov", gh.getGame().getMetadata().getGameNo());
        fileChooser.setSelectedFile(new File(fname));
        if (fileChooser.showSaveDialog(Application.instance().getActiveWindow().getControl()) == JFileChooser.APPROVE_OPTION) {
            File f = fileChooser.getSelectedFile();
            try {
                ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(f));
                out.writeObject(gh.getGame());
                Preferences prefs = Preferences.userNodeForPackage(JOverseerClient.class);
                prefs.put("saveDir", f.getParent());
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
