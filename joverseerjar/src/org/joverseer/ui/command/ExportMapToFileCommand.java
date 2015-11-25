package org.joverseer.ui.command;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.prefs.Preferences;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import org.joverseer.game.Game;
import org.joverseer.support.GameHolder;
import org.joverseer.ui.JOverseerJIDEClient;
import org.joverseer.ui.map.MapPanel;
import org.joverseer.ui.support.ActiveGameChecker;
import org.joverseer.ui.support.Messages;
import org.joverseer.ui.support.dialogs.ErrorDialog;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.command.ActionCommand;
import org.springframework.richclient.dialog.MessageDialog;

/**
 * Exports the current map image to a file as a jpeg image
 * 
 * @author Marios Skounakis
 *
 */
public class ExportMapToFileCommand  extends ActionCommand {
    
    public ExportMapToFileCommand() {
        super("exportMapToFileCommand"); //$NON-NLS-1$
    }

    @Override
	protected void doExecuteCommand() {
    	if (!ActiveGameChecker.checkActiveGameExists()) return;
    	BufferedImage map = MapPanel.instance().getMap();
    	if (map == null) return;
    	JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogType(JFileChooser.SAVE_DIALOG);
        fileChooser.setApproveButtonText(Messages.getString("ExportMapToFileCommand.Save")); //$NON-NLS-1$
        Preferences prefs = Preferences.userNodeForPackage(JOverseerJIDEClient.class);
        String saveDir = prefs.get("saveDir", null); //$NON-NLS-1$
        if (saveDir != null) {
            fileChooser.setCurrentDirectory(new File(saveDir));
        }
        Game game = GameHolder.instance().getGame();
        fileChooser.setSelectedFile(new File(Messages.getString("ExportMapToFileCommand.gamefileprefix") + game.getMetadata().getGameNo() + Messages.getString("ExportMapToFileCommand.TurnAbb") + game.getCurrentTurn() + ".jpeg")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        fileChooser.setFileFilter(new FileFilter() {
			@Override
			public boolean accept(File f) {
				return f.isDirectory() || f.getAbsolutePath().endsWith(".jpeg"); //$NON-NLS-1$
			}

			@Override
			public String getDescription() {
				return Messages.getString("ExportMapToFileCommand.7"); //$NON-NLS-1$
			}
        	
        });
        if (fileChooser.showSaveDialog(Application.instance().getActiveWindow().getControl()) == JFileChooser.APPROVE_OPTION) {
        	try {
        		BufferedImage img = new BufferedImage(map.getWidth(), map.getHeight(), BufferedImage.TYPE_INT_RGB);
        		Graphics2D g = img.createGraphics();
        		g.drawImage(map, 0, 0, null);
        		//new NeuQuantQuantizerOP().filter(img, img);
        		ImageIO.write(img, "jpeg", fileChooser.getSelectedFile()); //$NON-NLS-1$
        		MessageDialog dlg = new MessageDialog(Messages.getString("ExportMapToFileCommand.SavedConfirmation.title"), Messages.getString("ExportMapToFileCommand.MapSavedToFile", new Object[] { fileChooser.getSelectedFile().getCanonicalPath()})); //$NON-NLS-1$ //$NON-NLS-2$
        		dlg.showDialog();
        	}
        	catch (Exception exc) {
        		ErrorDialog dlg = new ErrorDialog(exc);
        		dlg.showDialog();
        	}
        }
    }

}
