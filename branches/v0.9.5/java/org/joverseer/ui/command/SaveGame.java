package org.joverseer.ui.command;

import org.springframework.richclient.command.ActionCommand;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.dialog.MessageDialog;
import org.springframework.richclient.progress.BusyIndicator;
import org.springframework.context.MessageSource;
import org.joverseer.game.Game;
import org.joverseer.support.GameHolder;
import org.joverseer.ui.JOverseerClient;
import org.joverseer.ui.map.MapPanel;

import javax.swing.*;

import java.io.ObjectOutputStream;
import java.io.FileOutputStream;
import java.io.File;
import java.util.Locale;
import java.util.prefs.Preferences;
import java.util.zip.*;

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
        Preferences prefs = Preferences.userNodeForPackage(JOverseerClient.class);
        String saveDir = prefs.get("saveDir", null);
        if (saveDir != null) {
            fileChooser.setCurrentDirectory(new File(saveDir));
        }
        GameHolder gh = (GameHolder) Application.instance().getApplicationContext().getBean("gameHolder");
        String fname = String.format("game%s.jov", gh.getGame().getMetadata().getGameNo());
        fileChooser.setSelectedFile(new File(fname));
        if (fileChooser.showSaveDialog(Application.instance().getActiveWindow().getControl()) == JFileChooser.APPROVE_OPTION) {
            BusyIndicator.showAt(Application.instance().getActiveWindow().getControl());
            File f = fileChooser.getSelectedFile();
            GZIPOutputStream zos;
            try {
                Game g = gh.getGame();
                MapPanel mp = MapPanel.instance();
                JScrollPane scp = (JScrollPane)mp.getParent().getParent();
                g.setParameter("horizontalMapScroll", String.valueOf(scp.getHorizontalScrollBar().getValue()));
                g.setParameter("verticalMapScroll", String.valueOf(scp.getVerticalScrollBar().getValue()));
                if (mp.getSelectedHex() != null) {
                    g.setParameter("selHexX", String.valueOf((int)mp.getSelectedHex().getX()));
                    g.setParameter("selHexY", String.valueOf((int)mp.getSelectedHex().getY()));
                }

                ObjectOutputStream out = new ObjectOutputStream(zos = new GZIPOutputStream(new FileOutputStream(f)));
                out.writeObject(g);
                prefs.put("saveDir", f.getParent());
                zos.finish();
                out.close();
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
