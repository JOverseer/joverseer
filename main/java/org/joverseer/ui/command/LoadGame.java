package org.joverseer.ui.command;

import java.awt.Point;
import java.io.EOFException;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Locale;
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
import org.springframework.context.MessageSource;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.command.ActionCommand;
import org.springframework.richclient.dialog.ConfirmationDialog;
import org.springframework.richclient.dialog.MessageDialog;
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
        super("LoadGameCommand");
    }
    
    public LoadGame(String fname) {
        super("LoadGameCommand");
        this.fname = fname;
    }

    @Override
	protected void doExecuteCommand() {
        if (GameHolder.hasInitializedGame()) {
            // show warning
            MessageSource ms = (MessageSource)Application.services().getService(MessageSource.class);
            ConfirmationDialog md = new ConfirmationDialog(
                    ms.getMessage("confirmLoadGameDialog.title", new String[]{}, Locale.getDefault()),
                    ms.getMessage("confirmLoadGameDialog.message", new String[]{}, Locale.getDefault()))
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
            fileChooser.setApproveButtonText("Load");
            Preferences prefs = Preferences.userNodeForPackage(JOverseerJIDEClient.class);
            String saveDir = prefs.get("saveDir", null);
            if (saveDir != null) {
                fileChooser.setCurrentDirectory(new File(saveDir));
            }
            fileChooser.setFileFilter(new DefaultFileFilter("*.jov", "JOverseer game file"));
            if (fileChooser.showOpenDialog(Application.instance().getActiveWindow().getControl()) == JFileChooser.APPROVE_OPTION) {
                this.fname = fileChooser.getSelectedFile().getAbsolutePath(); 
            }
        }

        if (this.fname != null) {
            BusyIndicator.showAt(Application.instance().getActiveWindow().getControl());
            File f = new File(this.fname);
            try {
                GameHolder gh = (GameHolder) Application.instance().getApplicationContext().getBean("gameHolder");
                Game g = Game.loadGame(f);
                g.getMetadata().setGame(g);
                gh.setGame(g);
                gh.setFile(this.fname);

                MapMetadataUtils mmu = new MapMetadataUtils();
                MapMetadata mm = (MapMetadata)Application.instance().getApplicationContext().getBean("mapMetadata");
                mmu.setMapSize(mm, g.getMetadata().getGameType());

                Application.instance().getApplicationContext().publishEvent(
                        new JOverseerEvent(LifecycleEventsEnum.GameChangedEvent.toString(), g, g));
                if (g.getParameter("horizontalMapScroll") != null) {
                    MapPanel mp = MapPanel.instance();
                    JScrollPane scp = (JScrollPane)mp.getParent().getParent();
                    try {
                        int hv = Integer.parseInt(g.getParameter("horizontalMapScroll"));
                        int vv = Integer.parseInt(g.getParameter("verticalMapScroll"));
                        scp.getHorizontalScrollBar().setValue(hv);
                        scp.getVerticalScrollBar().setValue(vv);
                    }
                    catch (Exception exc) {
                        
                    }
                }
                if (g.getParameter("selHexX") != null) {
                    try {
                        int hx = Integer.parseInt(g.getParameter("selHexX"));
                        int hy = Integer.parseInt(g.getParameter("selHexY"));
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
            	MessageDialog d = new MessageDialog("Error", "The game file is corrupt. This can be caused if you interrupt the program when saving the file, or if you didn't finish downloading the file from an online source.");
                d.showDialog();
            }
            catch (FileNotFoundException exc) {
            	MessageDialog d = new MessageDialog("Error", "JOverseer could not find the file '" + f.getAbsolutePath() + "'.");
                d.showDialog();
            }
            catch (Exception exc) {
            	String msg;
            	exc.printStackTrace();
            	if (exc.getMessage() == null) {
            		msg = "Unexpected Error";
            	} else {
            		msg = exc.getMessage();
            	}
                MessageDialog d = new MessageDialog("Error", msg);
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
