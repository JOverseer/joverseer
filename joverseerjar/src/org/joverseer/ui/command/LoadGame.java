package org.joverseer.ui.command;

import java.awt.Point;
import java.io.EOFException;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.prefs.Preferences;

import javax.swing.JFileChooser;
import javax.swing.JScrollPane;

import org.joverseer.game.Game;
import org.joverseer.support.GameHolder;
import org.joverseer.support.RecentGames;
import org.joverseer.ui.JOverseerJIDEClient;
import org.joverseer.ui.LifecycleEventsEnum;
import org.joverseer.ui.map.MapMetadata;
import org.joverseer.ui.map.MapMetadataUtils;
import org.joverseer.ui.map.MapPanel;
import org.joverseer.ui.support.JOverseerEvent;
import org.joverseer.ui.support.Messages;
import org.joverseer.ui.support.dialogs.ErrorDialog;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.command.ActionCommand;
import org.springframework.richclient.dialog.ConfirmationDialog;
import org.springframework.richclient.filechooser.DefaultFileFilter;
import org.springframework.richclient.progress.BusyIndicator;

/**
 * Loads a game from a saved game file
 * 
 * @author Marios Skounakis
 */
public class LoadGame extends ActionCommand {
    String fname = null;
    
    public LoadGame() {
        super("LoadGameCommand"); //$NON-NLS-1$
    }
    
    public LoadGame(String fname) {
        super("LoadGameCommand"); //$NON-NLS-1$
        this.fname = fname;
    }

    @Override
	protected void doExecuteCommand() {
        if (GameHolder.hasInitializedGame()) {
            // show warning
            ConfirmationDialog md = new ConfirmationDialog(
            		Messages.getString("confirmLoadGameDialog.title"), //$NON-NLS-1$
            		Messages.getString("confirmLoadGameDialog.message")) //$NON-NLS-1$
            {
                @Override
				protected void onConfirm() {
                    loadGame();
                }
            };
            md.showDialog();
        } else {
            loadGame();
        }
    }
    
    
    public void loadGame() {
        if (this.fname == null) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
            fileChooser.setApproveButtonText("Load"); //$NON-NLS-1$
            Preferences prefs = Preferences.userNodeForPackage(JOverseerJIDEClient.class);
            String saveDir = prefs.get("saveDir", null); //$NON-NLS-1$
            if (saveDir != null) {
                fileChooser.setCurrentDirectory(new File(saveDir));
            }
            fileChooser.setFileFilter(new DefaultFileFilter("*.jov", "JOverseer game file")); //$NON-NLS-1$ //$NON-NLS-2$
            if (fileChooser.showOpenDialog(Application.instance().getActiveWindow().getControl()) == JFileChooser.APPROVE_OPTION) {
                this.fname = fileChooser.getSelectedFile().getAbsolutePath(); 
            }
        }

        if (this.fname != null) {
            BusyIndicator.showAt(Application.instance().getActiveWindow().getControl());
            File f = new File(this.fname);
            try {
                GameHolder gh = (GameHolder) Application.instance().getApplicationContext().getBean("gameHolder"); //$NON-NLS-1$
                Game g = Game.loadGame(f);
                g.getMetadata().setGame(g);
                gh.setGame(g);
                gh.setFile(this.fname);

                MapMetadataUtils mmu = new MapMetadataUtils();
                MapMetadata mm = (MapMetadata)Application.instance().getApplicationContext().getBean("mapMetadata"); //$NON-NLS-1$
                mmu.setMapSize(mm, g.getMetadata().getGameType());

                Application.instance().getApplicationContext().publishEvent(
                        new JOverseerEvent(LifecycleEventsEnum.GameChangedEvent.toString(), g, g));
                Application.instance().getApplicationContext().publishEvent(
                        new JOverseerEvent(LifecycleEventsEnum.GameLoadedEvent.toString(), g, g));
                if (g.getParameter("horizontalMapScroll") != null) { //$NON-NLS-1$
                    MapPanel mp = MapPanel.instance();
                    JScrollPane scp = (JScrollPane)mp.getParent().getParent();
                    try {
                        int hv = Integer.parseInt(g.getParameter("horizontalMapScroll")); //$NON-NLS-1$
                        int vv = Integer.parseInt(g.getParameter("verticalMapScroll")); //$NON-NLS-1$
                        scp.getHorizontalScrollBar().setValue(hv);
                        scp.getVerticalScrollBar().setValue(vv);
                    }
                    catch (Exception exc) {
                        
                    }
                }
                if (g.getParameter("selHexX") != null) { //$NON-NLS-1$
                    try {
                        int hx = Integer.parseInt(g.getParameter("selHexX")); //$NON-NLS-1$
                        int hy = Integer.parseInt(g.getParameter("selHexY")); //$NON-NLS-1$
                        Application.instance().getApplicationContext().publishEvent(
                                new JOverseerEvent(LifecycleEventsEnum.SelectedHexChangedEvent.toString(), new Point(hx, hy), g));
                    }
                    catch (Exception exc) {
                        
                    }
                }
                RecentGames rgs = new RecentGames();
                rgs.updateRecentGameInfoPreferenceWithGame(g.getMetadata().getGameNo(), f.getAbsolutePath());
                
            }
            catch (EOFException exc) {
            	ErrorDialog d = new ErrorDialog(Messages.getString("LoadGame.CorruptFile")); //$NON-NLS-1$ //$NON-NLS-2$
                d.showDialog();
            }
            catch (FileNotFoundException exc) {
            	ErrorDialog d = new ErrorDialog(Messages.getString("LoadGame.CantFind", new String[] { f.getAbsolutePath() })); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                d.showDialog();
            }
            catch (Exception exc) {
                ErrorDialog d = new ErrorDialog(exc);
                d.showDialog();
                // do nothing
                // todo fix
            }
            finally {
                BusyIndicator.clearAt(Application.instance().getActiveWindow().getControl());
                this.fname = null;
            }
        }
    }
}
