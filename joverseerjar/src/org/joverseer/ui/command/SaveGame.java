package org.joverseer.ui.command;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.prefs.Preferences;
import java.util.zip.GZIPOutputStream;

import javax.swing.JFileChooser;
import javax.swing.JScrollPane;

import org.joverseer.game.Game;
import org.joverseer.preferences.PreferenceRegistry;
import org.joverseer.support.GameHolder;
import org.joverseer.support.RecentGames;
import org.joverseer.ui.JOverseerJIDEClient;
import org.joverseer.ui.map.MapPanel;
import org.joverseer.ui.support.Messages;
import org.joverseer.ui.support.dialogs.ErrorDialog;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.command.ActionCommand;
import org.springframework.richclient.dialog.MessageDialog;
import org.springframework.richclient.progress.BusyIndicator;

/**
 * Saves the current game to a file
 * 
 * @author Marios Skounakis
 */
public class SaveGame extends ActionCommand {
    public SaveGame() {
        super("SaveGameCommand"); //$NON-NLS-1$
    }

    @Override
	protected void doExecuteCommand() {
        if (!GameHolder.hasInitializedGame()) {
            // show error, cannot import when game not initialized
            ErrorDialog md = new ErrorDialog(Messages.getString("errorSavingGame")); //$NON-NLS-1$
            md.showDialog();
            return;
        }
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogType(JFileChooser.SAVE_DIALOG);
        fileChooser.setApproveButtonText("Save"); //$NON-NLS-1$
        
        Preferences prefs = Preferences.userNodeForPackage(JOverseerJIDEClient.class);
        String saveDir = prefs.get("saveDir", null); //$NON-NLS-1$
        if (saveDir != null) {
            fileChooser.setCurrentDirectory(new File(saveDir));
        }
        GameHolder gh = (GameHolder) Application.instance().getApplicationContext().getBean("gameHolder"); //$NON-NLS-1$
        String fname = String.format("game%s.jov", gh.getGame().getMetadata().getGameNo()); //$NON-NLS-1$
        fileChooser.setSelectedFile(new File(fname));
        if (fileChooser.showSaveDialog(Application.instance().getActiveWindow().getControl()) == JFileChooser.APPROVE_OPTION) {
            BusyIndicator.showAt(Application.instance().getActiveWindow().getControl());

            File f = fileChooser.getSelectedFile();
            GZIPOutputStream zos;
            try {
                Game g = gh.getGame();

                MapPanel mp = MapPanel.instance();
                JScrollPane scp = (JScrollPane)mp.getParent().getParent();
                g.setParameter("horizontalMapScroll", String.valueOf(scp.getHorizontalScrollBar().getValue())); //$NON-NLS-1$
                g.setParameter("verticalMapScroll", String.valueOf(scp.getVerticalScrollBar().getValue())); //$NON-NLS-1$
                if (mp.getSelectedHex() != null) {
                    g.setParameter("selHexX", String.valueOf((int)mp.getSelectedHex().getX())); //$NON-NLS-1$
                    g.setParameter("selHexY", String.valueOf((int)mp.getSelectedHex().getY())); //$NON-NLS-1$
                }

                ObjectOutputStream out = new ObjectOutputStream(zos = new GZIPOutputStream(new FileOutputStream(f)));
                out.writeObject(g);
                prefs.put("saveDir", f.getParent()); //$NON-NLS-1$

                RecentGames rgs = new RecentGames();
                rgs.updateRecentGameInfoPreferenceWithGame(g.getMetadata().getGameNo(), f.getAbsolutePath());

                zos.finish();
                out.close();
                BusyIndicator.clearAt(Application.instance().getActiveWindow().getControl());
                String pval = PreferenceRegistry.instance().getPreferenceValue("general.informationAfterSaveGame"); //$NON-NLS-1$
                if (pval.equals("yes")) { //$NON-NLS-1$
	                MessageDialog dlg = new MessageDialog(Messages.getString("SaveGame.title"), Messages.getString("SaveGame.text") + fileChooser.getSelectedFile().getCanonicalPath()); //$NON-NLS-1$ //$NON-NLS-2$
	        		dlg.showDialog();
                }
            }
            catch (Exception exc) {
                BusyIndicator.clearAt(Application.instance().getActiveWindow().getControl());
                MessageDialog d = new MessageDialog("Error", exc.getMessage());
                d.showDialog();
                // do nothing
                // todo fix
            }
            finally {
            }
        }
    }
}
