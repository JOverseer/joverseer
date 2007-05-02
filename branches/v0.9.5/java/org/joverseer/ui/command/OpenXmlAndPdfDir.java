package org.joverseer.ui.command;

import java.awt.Dimension;
import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Locale;
import java.util.prefs.Preferences;
import java.util.regex.Pattern;

import javax.swing.JFileChooser;

import org.joverseer.game.Game;
import org.joverseer.support.GameHolder;
import org.joverseer.support.readers.pdf.TurnPdfReader;
import org.joverseer.support.readers.xml.TurnXmlReader;
import org.joverseer.ui.JOverseerClientProgressMonitor;
import org.joverseer.ui.LifecycleEventsEnum;
import org.joverseer.ui.support.ActiveGameChecker;
import org.joverseer.ui.support.JOverseerEvent;
import org.springframework.binding.form.FormModel;
import org.springframework.context.MessageSource;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.command.AbstractCommand;
import org.springframework.richclient.command.ActionCommand;
import org.springframework.richclient.dialog.ConfirmationDialog;
import org.springframework.richclient.dialog.FormBackedDialogPage;
import org.springframework.richclient.dialog.TitledPageApplicationDialog;
import org.springframework.richclient.form.FormModelHelper;


public class OpenXmlAndPdfDir extends ActionCommand implements Runnable {
    File[] files;
    JOverseerClientProgressMonitor monitor;
    GameHolder gh;
    TitledPageApplicationDialog dialog;
    
    public OpenXmlAndPdfDir() {
        super("openXmlAndPdfDirCommand");
        gh = (GameHolder)Application.instance().getApplicationContext().getBean("gameHolder");
    }

    public void run() {
        Game game = gh.getGame();
        if (game == null) {
            return;
        }
        boolean errorOccurred = false;
        for (File f : files) {
            if (f.getAbsolutePath().endsWith(".xml")) {
                try {
                    monitor.subTaskStarted(String.format("Importing file '%s'.", new Object[]{f.getAbsolutePath()}));

                    final TurnXmlReader r = new TurnXmlReader(game, "file:///" + f.getCanonicalPath());
                    r.setMonitor(monitor);
                    r.run();
                    if (r.getErrorOccured()) {
                    	errorOccurred = true;
                    }
                }
                catch (Exception exc) {
                    int a = 1;
                    monitor.subTaskStarted(exc.getMessage());
                    // do nothing
                    // todo fix
                }
            }

        }
        boolean warningOccurred = false;
        for (File f : files) {
            if (f.getAbsolutePath().endsWith(".pdf")) {
                try {
                    monitor.subTaskStarted(String.format("Importing file '%s'.", new Object[]{f.getAbsolutePath()}));

                    final TurnPdfReader r = new TurnPdfReader(game, f.getCanonicalPath());
                    r.setMonitor(monitor);
                    r.run();
                    if (r.getErrorOccurred()) {
                    	warningOccurred = true;
                    }
                }
                catch (Exception exc) {
                    int a = 1;
                    monitor.subTaskStarted(exc.getMessage());
                    // do nothing
                    // todo fix
                }
            }

        }
        String globalMsg = "";
        if (errorOccurred) {
        	globalMsg = "Serious errors occurred during the import. The game information may not be reliable.";
        } else if (warningOccurred) {
        	globalMsg = "Some small errors occurred during the import. All vital information was imported but some secondary information from the pdf files was not parsed successfully.";
        } else {
        	globalMsg = "Import was successful.";
        }
        monitor.setGlobalMessage(globalMsg);
        Application.instance().getApplicationContext().publishEvent(
                                        new JOverseerEvent(LifecycleEventsEnum.GameChangedEvent.toString(), gh.getGame(), this));
        
        monitor.done();
        dialog.setDescription("Processing finished.");

    }

    protected void doExecuteCommand() {
        if (!ActiveGameChecker.checkActiveGameExists()) return;

        MessageSource ms = (MessageSource)Application.services().getService(MessageSource.class);
        ConfirmationDialog dlg = new ConfirmationDialog(ms.getMessage("changeAllegiancesConfirmationDialog.title", new Object[]{}, Locale.getDefault()),
                ms.getMessage("changeAllegiancesConfirmationDialog.message", new Object[]{}, Locale.getDefault())) {
            protected void onConfirm() {
                ChangeNationAllegiances cmd = new ChangeNationAllegiances();
                cmd.doExecuteCommand();
            }
        };
        dlg.setPreferredSize(new Dimension(500, 70));
        dlg.showDialog();
        

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        Preferences prefs = Preferences.userNodeForPackage(OpenXmlDir.class);
        String lastDir = prefs.get("importDir", null);
        if (lastDir != null) {
            fileChooser.setCurrentDirectory(new File(lastDir));
        }
        if (fileChooser.showOpenDialog(Application.instance().getActiveWindow().getControl()) == JFileChooser.APPROVE_OPTION) {
            final File file = fileChooser.getSelectedFile();
            prefs.put("importDir", file.getAbsolutePath());
            final Runnable thisObj = this;
            class XmlAndPdfFileFilter implements FileFilter {
                public boolean accept(File file) {
                    return !file.isDirectory() && (file.getName().endsWith(".pdf") || Pattern.matches("g\\d{3}n\\d{2}t\\d{3}.xml", file.getName()));
                }
            }
            //files = file.listFiles(new XmlAndPdfFileFilter());
            files = getFilesRecursive(file, new XmlAndPdfFileFilter());
            FormModel formModel = FormModelHelper.createFormModel(this);
            monitor = new JOverseerClientProgressMonitor(formModel);
            FormBackedDialogPage page = new FormBackedDialogPage(monitor);
            dialog = new TitledPageApplicationDialog(page) {
                protected void onAboutToShow() {
                    monitor.taskStarted(String.format("Importing Directory '%s'.", new Object[]{file.getAbsolutePath()}), 100 * files.length);
                    Thread t = new Thread(thisObj);
                    t.start();
                }

                protected boolean onFinish() {
                    return true;
                }

                protected Object[] getCommandGroupMembers() {
                    return new AbstractCommand[] {
                            getFinishCommand()
                    };
                }
            };
            dialog.setTitle(ms.getMessage("importFilesDialog.title", new Object[]{}, Locale.getDefault()));
            dialog.showDialog();
        }
    }

    private File[] getFilesRecursive(File folder, FileFilter filter) {
        ArrayList<File> ret = new ArrayList<File>();
        File[] files = folder.listFiles(filter);
        ret.addAll(Arrays.asList(files));
        FileFilter folderFilter = new FileFilter() {
            public boolean accept(File pathname) {
                return pathname.isDirectory();
            }
        };
        for (File subfolder : folder.listFiles(folderFilter)) {
            ret.addAll(Arrays.asList(getFilesRecursive(subfolder, filter)));
        }
        return ret.toArray(new File[]{});
    }
    
}
