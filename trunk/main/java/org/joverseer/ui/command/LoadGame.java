package org.joverseer.ui.command;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.Locale;
import java.util.prefs.Preferences;

import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;

import org.joverseer.game.Game;
import org.joverseer.support.GameHolder;
import org.joverseer.ui.LifecycleEventsEnum;
import org.joverseer.ui.JOverseerClient;
import org.joverseer.ui.support.JOverseerEvent;
import org.springframework.context.MessageSource;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.command.ActionCommand;
import org.springframework.richclient.dialog.ConfirmationDialog;
import org.springframework.richclient.dialog.MessageDialog;
import org.springframework.richclient.filechooser.DefaultFileFilter;
import org.springframework.richclient.progress.BusyIndicator;
import org.springframework.richclient.util.SwingWorker;

import java.util.zip.*;

public class LoadGame extends ActionCommand {
    public LoadGame() {
        super("LoadGameCommand");
    }

    protected void doExecuteCommand() {
        if (GameHolder.hasInitializedGame()) {
            // show warning
            MessageSource ms = (MessageSource)Application.services().getService(MessageSource.class);
            ConfirmationDialog md = new ConfirmationDialog(
                    ms.getMessage("confirmLoadGameDialog.title", new String[]{}, Locale.getDefault()),
                    ms.getMessage("confirmLoadGameDialog.message", new String[]{}, Locale.getDefault()))
            {
                protected void onConfirm() {
                    loadGame();
                }
            };
            md.showDialog();
        } else {
            loadGame();
        }
    }
    
    private void loadGame() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
        fileChooser.setApproveButtonText("Load");
        Preferences prefs = Preferences.userNodeForPackage(JOverseerClient.class);
        String saveDir = prefs.get("saveDir", null);
        if (saveDir != null) {
            fileChooser.setCurrentDirectory(new File(saveDir));
        }
        fileChooser.setFileFilter(new DefaultFileFilter("*.jov", "JOverseer game file"));

        if (fileChooser.showOpenDialog(Application.instance().getActiveWindow().getControl()) == JFileChooser.APPROVE_OPTION) {
            BusyIndicator.showAt(Application.instance().getActiveWindow().getControl());
            File f = fileChooser.getSelectedFile();
            try {
                GameHolder gh = (GameHolder) Application.instance().getApplicationContext().getBean("gameHolder");
                Game g = Game.loadGame(f);
                gh.setGame(g);
                Application.instance().getApplicationContext().publishEvent(
                        new JOverseerEvent(LifecycleEventsEnum.GameChangedEvent.toString(), g, g));
            }
            catch (Exception exc) {
                MessageDialog d = new MessageDialog("Error", exc.getMessage());
                d.showDialog();
                // do nothing
                // todo fix
            }
            finally {
                BusyIndicator.clearAt(Application.instance().getActiveWindow().getControl());
            }
        }
    }
}
