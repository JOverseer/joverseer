package org.joverseer.ui.command;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.prefs.Preferences;
import java.util.zip.GZIPOutputStream;

import javax.swing.JFileChooser;
import javax.swing.JScrollPane;

import org.joverseer.JOApplication;
import org.joverseer.domain.PlayerInfo;
import org.joverseer.game.Game;
import org.joverseer.game.Turn;
import org.joverseer.preferences.PreferenceRegistry;
import org.joverseer.support.GameHolder;
import org.joverseer.support.RecentGames;
import org.joverseer.ui.JOverseerJIDEClient;
import org.joverseer.ui.LifecycleEventsEnum;
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
	private boolean doExecuteCompletedSave;
	//dependencies
	GameHolder gameHolder;
    /***
     * Indicate if the last save actually completed. no errors or cancel.
     * @return
     */
	public boolean isDoExecuteCompletedSave() {
		return this.doExecuteCompletedSave;
	}
	public SaveGame(GameHolder gameHolder) {
        super("SaveGameCommand"); //$NON-NLS-1$
        this.gameHolder = gameHolder;
    }

    // use this version when you want a different name for the command.
    public SaveGame(String commandId,GameHolder gameHolder) {
    	super(commandId);
        this.gameHolder = gameHolder;
    }
    @Override
	protected void doExecuteCommand() {
    	this.doExecuteCompletedSave = false;
        if (!this.gameHolder.isGameInitialized()) {
            // show error, cannot import when game not initialized
            ErrorDialog.showErrorDialog("errorSavingGame"); //$NON-NLS-1$
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
        String fname = String.format("game%s.jov", this.gameHolder.getGame().getMetadata().getGameNo()); //$NON-NLS-1$
        fileChooser.setSelectedFile(new File(fname));
        if (fileChooser.showSaveDialog(Application.instance().getActiveWindow().getControl()) == JFileChooser.APPROVE_OPTION) {
            BusyIndicator.showAt(Application.instance().getActiveWindow().getControl());
            File f = fileChooser.getSelectedFile();
            GZIPOutputStream zos;
            try {
                Game g = this.gameHolder.getGame();

                MapPanel mp = MapPanel.instance();
                JScrollPane scp = (JScrollPane)mp.getParent().getParent();
                g.setParameter("horizontalMapScroll", String.valueOf(scp.getHorizontalScrollBar().getValue())); //$NON-NLS-1$
                g.setParameter("verticalMapScroll", String.valueOf(scp.getVerticalScrollBar().getValue())); //$NON-NLS-1$
                if (mp.getSelectedHex() != null) {
                    g.setParameter("selHexX", String.valueOf((int)mp.getSelectedHex().getX())); //$NON-NLS-1$
                    g.setParameter("selHexY", String.valueOf((int)mp.getSelectedHex().getY())); //$NON-NLS-1$
                }
                ObjectOutputStream out = new ObjectOutputStream(zos = new GZIPOutputStream(new FileOutputStream(f)));
                try {
                	out.writeObject(g);
                	prefs.put("saveDir", f.getParent()); //$NON-NLS-1$

                	RecentGames rgs = new RecentGames();
                	Turn turn = g.getTurn(g.getMaxTurn());
            		String maybeUnknownDate = PlayerInfo.getDueDateDefaulted(turn.getPlayerInfo(g.getMetadata().getNationNo()), "unknown");
            		
            		Date ordersSentOn;
            		boolean ordersSent;
            		if(turn.getPlayerInfo(g.getMetadata().getNationNo()) != null) {
            			ordersSent = turn.getPlayerInfo(g.getMetadata().getNationNo()).getOrdersSentOn() != null;
            			ordersSentOn = turn.getPlayerInfo(g.getMetadata().getNationNo()).getOrdersSentOn();
            		} else {
            			ordersSent = false;
            			ordersSentOn = null;
            		}
            		String ordersSentStr;
            		if(ordersSentOn == null) ordersSentStr = null;
            		else {
            	        SimpleDateFormat formatter = new SimpleDateFormat("MMMM dd yyyy");
            	        String formattedDate = formatter.format(ordersSentOn);
            	        ordersSentStr = formattedDate.toUpperCase();
            		}
                	rgs.updateRecentGameInfoPreferenceWithGame(g.getMetadata().getGameNo(), f.getAbsolutePath(), maybeUnknownDate, ordersSent, ordersSentStr);
                } finally {
                	// make sure if we've opened it, and even if we get an error...
                	zos.finish();
                	out.close();
                }
                String pval = PreferenceRegistry.instance().getPreferenceValue("general.informationAfterSaveGame"); //$NON-NLS-1$
                if (pval.equals("yes")) { //$NON-NLS-1$
	                MessageDialog dlg = new MessageDialog(Messages.getString("SaveGame.title"), Messages.getString("SaveGame.text") + fileChooser.getSelectedFile().getCanonicalPath()); //$NON-NLS-1$ //$NON-NLS-2$
	        		dlg.showDialog();
                }
                this.doExecuteCompletedSave = true;
                JOApplication.publishEvent(LifecycleEventsEnum.SaveGameEvent, g, g);
                BusyIndicator.clearAt(Application.instance().getActiveWindow().getControl());
            }
            catch (Exception exc) {
                BusyIndicator.clearAt(Application.instance().getActiveWindow().getControl());
                ErrorDialog.showErrorDialog(exc);
                // do nothing
                // todo fix
            }
            finally {
            }
        }
    }
}
