package org.joverseer.ui.command;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.Locale;
import java.util.prefs.Preferences;

import javax.swing.JFileChooser;

import org.joverseer.game.Game;
import org.joverseer.support.GameHolder;
import org.joverseer.ui.LifecycleEventsEnum;
import org.joverseer.ui.JOverseerClient;
import org.joverseer.ui.support.JOverseerEvent;
import org.springframework.context.MessageSource;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.command.ActionCommand;
import org.springframework.richclient.dialog.ConfirmationDialog;

/**
 * Created by IntelliJ IDEA.
 * User: mskounak
 * Date: 13 Οκτ 2006
 * Time: 6:54:07 μμ
 * To change this template use File | Settings | File Templates.
 */
public class LoadGame extends ActionCommand {
    public LoadGame() {
        super("LoadGameCommand");
    }

    protected void doExecuteCommand() {
        if (GameHolder.hasInitializedGame()) {
            // show error, cannot import when game not initialized
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
        if (fileChooser.showOpenDialog(Application.instance().getActiveWindow().getControl()) == JFileChooser.APPROVE_OPTION) {
            File f = fileChooser.getSelectedFile();
            GameHolder gh = (GameHolder) Application.instance().getApplicationContext().getBean("gameHolder");
            try {
                ObjectInputStream in = new ObjectInputStream(new FileInputStream(f));
                gh.setGame((Game)in.readObject());
                Application.instance().getApplicationContext().publishEvent(
                                    new JOverseerEvent(LifecycleEventsEnum.GameChangedEvent.toString(), gh.getGame(), this));

            }
            catch (Exception exc) {
                int a = 1;
                // do nothing
                // todo fix
            }
        }
    }
}
