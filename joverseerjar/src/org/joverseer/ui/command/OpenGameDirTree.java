package org.joverseer.ui.command;

import java.awt.Dimension;
import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import javax.swing.JFileChooser;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joverseer.JOApplication;
import org.joverseer.game.Game;
import org.joverseer.metadata.GameMetadata;
import org.joverseer.support.GameFileComparator;
import org.joverseer.support.GameHolder;
import org.joverseer.support.GamePreference;
import org.joverseer.support.TurnPostProcessor;
import org.joverseer.support.XmlAndPdfFileFilter;
import org.joverseer.support.readers.newXml.TurnNewXmlReader;
import org.joverseer.support.readers.pdf.TurnPdfReader;
import org.joverseer.support.readers.xml.TurnXmlReader;
import org.joverseer.ui.JOverseerClientProgressMonitor;
import org.joverseer.ui.LifecycleEventsEnum;
import org.joverseer.ui.support.ActiveGameChecker;
import org.joverseer.ui.support.Messages;
import org.joverseer.ui.support.dialogs.CustomTitledPageApplicationDialog;
import org.springframework.binding.form.FormModel;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.command.ActionCommand;
import org.springframework.richclient.dialog.ConfirmationDialog;
import org.springframework.richclient.dialog.FormBackedDialogPage;
import org.springframework.richclient.form.FormModelHelper;

/**
 * Reads the xml and pdf files for a whole game from a directory tree Structure
 * must be: - game folder - folder for turn 1 - folder for turn 2 - ...
 *
 * The game folder may have any arbitrary name The turn folders must follow a
 * specific pattern, basically a name that can be: - t0 - t00 - t000 - turn0 -
 * turn00 - turn000 - turn 0 - turn 00 - turn 000 and all capitalization
 * variations of the above
 *
 *
 * @author Marios Skounakis
 */
public class OpenGameDirTree extends ActionCommand implements Runnable {
	File[] files;
	ArrayList<File> turnFolders = new ArrayList<File>();
	JOverseerClientProgressMonitor monitor;
	CustomTitledPageApplicationDialog dialog;
	//dependencies
	GameHolder gh;

	static Log log = LogFactory.getLog(OpenGameDirTree.class);

	public OpenGameDirTree(GameHolder gameHolder) {
		super("openGameDirTreeCommand");
		this.gh = gameHolder;
	}

	@Override
	protected void doExecuteCommand() {
		if (!ActiveGameChecker.checkActiveGameExists())
			return;

		this.turnFolders.clear();

		ConfirmationDialog dlg = new ConfirmationDialog(Messages.getString("changeAllegiancesConfirmationDialog.title"),
				Messages.getString("changeAllegiancesConfirmationDialog.message")) {
			@Override
			protected void onConfirm() {
				ChangeNationAllegiances cmd = new ChangeNationAllegiances();
				cmd.doExecuteCommand();
			}
		};
		dlg.setPreferredSize(new Dimension(500, 70));
		dlg.showDialog();

		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

		String lastDir = GamePreference.getValueForPreference("importDir", OpenGameDirTree.class);
		if (lastDir != null) {
			fileChooser.setCurrentDirectory(new File(lastDir));
		}
		if (fileChooser.showOpenDialog(Application.instance().getActiveWindow().getControl()) == JFileChooser.APPROVE_OPTION) {
			final File file = fileChooser.getSelectedFile();
			GamePreference.setValueForPreference("importDir", file.getAbsolutePath(), OpenGameDirTree.class);
			GameMetadata gm = GameMetadata.instance();

			final Runnable thisObj = this;

			String[] turnFolderPatterns = new String[] { "t%s", "t%02d", "t%03d", "t %s", "t %02d", "t %03d", "turn%s", "turn%02d", "turn%03d", "turn %s", "turn %02d", "turn %03d", };

			// InputApplicationDialog dlg = new InputApplicationDialog();
			// dlg.setInputField(new JTextField());
			// dlg.setTitle("Enter turn folder pattern.");
			// dlg.setInputLabelMessage("Pattern :");
			// dlg.showDialog();
			// turnFolderPattern = ((JTextField)dlg.getInputField()).getText();

			int fileCount = 0;
			for (int i = 0; i < 100; i++) {
				for (String turnFolderPattern : turnFolderPatterns) {
					String tfp = file.getAbsolutePath() + "/" + String.format(turnFolderPattern, i);
					File tf = new File(tfp);
					if (tf.exists()) {
						this.turnFolders.add(tf);
						this.files = tf.listFiles(new XmlAndPdfFileFilter(gm.getGameNo()));
						try {
							log.info("Adding turn folder " + tf.getCanonicalPath() + " with " + this.files.length + " files.");
						} catch (Exception exc) {

						}
						fileCount += this.files.length;
						break;
					}
				}
			}
			if (fileCount == 0) {
				String tfp = file.getAbsolutePath() + "/";
				File tf = new File(tfp);
				if (tf.exists()) {
					this.turnFolders.add(tf);
					this.files = tf.listFiles(new XmlAndPdfFileFilter(gm.getGameNo()));
					try {
						log.info("Adding turn folder " + tf.getCanonicalPath() + " with " + this.files.length + " files.");
					} catch (Exception exc) {

					}
					fileCount += this.files.length;
				}
			}
			final int fileCountFinal = fileCount;
			FormModel formModel = FormModelHelper.createFormModel(this);
			this.monitor = new JOverseerClientProgressMonitor(formModel);
			FormBackedDialogPage page = new FormBackedDialogPage(this.monitor);
			this.dialog = new CustomTitledPageApplicationDialog(page) {
				@Override
				protected void onAboutToShow() {
					OpenGameDirTree.this.monitor.taskStarted(String.format("Importing Game Tree '%s'.", new Object[] { file.getAbsolutePath() }), 100 * fileCountFinal);
					Thread t = new Thread(thisObj);
					t.start();
				}

				@Override
				protected boolean onFinish() {
					return true;
				}

				@Override
				protected ActionCommand getCancelCommand() {
					return null;
				}
			};
			this.dialog.setTitle(Messages.getString("importFilesDialog.title"));
			this.dialog.showDialog();
		}
	}

	@Override
	public void run() {
		Game game = this.gh.getGame();
		if (game == null) {
			return;
		}
		boolean errorOccurred = false;
		boolean warningOccurred = false;
		for (File tf : this.turnFolders) {
			this.files = getFilesRecursive(tf, new XmlAndPdfFileFilter(game.getMetadata().getGameNo()));
			for (File f : this.files) {
				if (f.getAbsolutePath().endsWith(".xml")) {
					try {
						this.monitor.subTaskStarted(String.format("Importing file '%s'.", new Object[] { f.getAbsolutePath() }));

						final TurnXmlReader r = new TurnXmlReader(game, "file:///" + f.getCanonicalPath());
						r.setMonitor(this.monitor);
						//note that this may change game.NewXmlFormat!
						r.run();
						if (r.getErrorOccured()) {
							errorOccurred = true;
						}
						if (game.getMetadata().getNewXmlFormat()) {
							final TurnNewXmlReader xr = new TurnNewXmlReader(game, "file:///" + f.getCanonicalPath(), r.getTurnInfo().getNationNo());
							xr.setMonitor(this.monitor);
							xr.run();
							if (xr.getErrorOccured()) {
								errorOccurred = true;
							}
						}
					} catch (Exception exc) {
						this.monitor.subTaskStarted(exc.getMessage());
						// do nothing
						// todo fix
					}
				}

			}
			if (!game.getMetadata().getNewXmlFormat()) {
				for (File f : this.files) {
					if (f.getAbsolutePath().endsWith(".pdf")) {
						try {
							this.monitor.subTaskStarted(String.format("Importing file '%s'.", new Object[] { f.getAbsolutePath() }));

							final TurnPdfReader r = new TurnPdfReader(game, f.getCanonicalPath());
							r.setMonitor(this.monitor);
							r.run();
							if (r.getErrorOccurred()) {
								warningOccurred = true;
							}
						} catch (Exception exc) {
							this.monitor.subTaskStarted(exc.getMessage());
							// do nothing
							// todo fix
						}
					}
				}
			}
			TurnPostProcessor turnPostProcessor = new TurnPostProcessor();
			turnPostProcessor.postProcessTurn(game.getTurn(game.getMaxTurn()));

		}
		String globalMsg = "";
		if (errorOccurred) {
			globalMsg = "Serious errors occurred during the import. The game information may not be reliable.";
		} else if (warningOccurred) {
			globalMsg = "Some small errors occurred during the import. All vital information was imported but some secondary information from the pdf files was not parsed successfully.";
		} else {
			globalMsg = "Import was successful.";
		}
		this.monitor.setGlobalMessage(globalMsg);
		JOApplication.publishEvent(LifecycleEventsEnum.GameChangedEvent, this.gh.getGame(), this);
		this.monitor.done();
		this.dialog.setDescription("Processing finished.");
	}

	private File[] getFilesRecursive(File folder, FileFilter filter) {
		ArrayList<File> ret = new ArrayList<File>();
		File[] files1 = folder.listFiles(filter);
		ret.addAll(Arrays.asList(files1));
		FileFilter folderFilter = new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				return pathname.isDirectory();
			}
		};
		for (File subfolder : folder.listFiles(folderFilter)) {
			ret.addAll(Arrays.asList(getFilesRecursive(subfolder, filter)));
		}
		Collections.sort(ret, new GameFileComparator());
		return ret.toArray(new File[] {});
	}

}
