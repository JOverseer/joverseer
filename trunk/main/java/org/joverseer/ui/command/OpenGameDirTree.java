package org.joverseer.ui.command;

import java.awt.Dimension;
import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import java.util.regex.Pattern;

import javax.swing.JFileChooser;
import javax.swing.JTextField;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joverseer.game.Game;
import org.joverseer.support.GameHolder;
import org.joverseer.support.readers.pdf.TurnPdfReader;
import org.joverseer.support.readers.xml.TurnXmlReader;
import org.joverseer.ui.JOverseerClient;
import org.joverseer.ui.JOverseerClientProgressMonitor;
import org.joverseer.ui.LifecycleEventsEnum;
import org.joverseer.ui.support.ActiveGameChecker;
import org.joverseer.ui.support.JOverseerEvent;
import org.springframework.binding.form.FormModel;
import org.springframework.context.MessageSource;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.command.ActionCommand;
import org.springframework.richclient.dialog.ConfirmationDialog;
import org.springframework.richclient.dialog.FormBackedDialogPage;
import org.springframework.richclient.dialog.InputApplicationDialog;
import org.springframework.richclient.dialog.MessageDialog;
import org.springframework.richclient.dialog.TitledPageApplicationDialog;
import org.springframework.richclient.form.FormModelHelper;


public class OpenGameDirTree extends ActionCommand implements Runnable {
    File[] files;
    ArrayList<File> turnFolders = new ArrayList<File>();
    JOverseerClientProgressMonitor monitor;
    GameHolder gh;
    TitledPageApplicationDialog dialog;
    
    static Log log = LogFactory.getLog(OpenGameDirTree.class);

    public OpenGameDirTree() {
        super("openGameDirTreeCommand");
        gh = (GameHolder)Application.instance().getApplicationContext().getBean("gameHolder");
    }

    protected void doExecuteCommand() {
        if (!ActiveGameChecker.checkActiveGameExists()) return;
        
        turnFolders.clear();
        
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
            
            String[] turnFolderPatterns = new String[]{
                    "t%s",
                    "t%02d",
                    "t%03d",
                    "t %s",
                    "t %02d",
                    "t %03d",
                    "turn%s",
                    "turn%02d",
                    "turn%03d",
                    "turn %s",
                    "turn %02d",
                    "turn %03d",
            };
            
//            InputApplicationDialog dlg = new InputApplicationDialog();
//            dlg.setInputField(new JTextField());
//            dlg.setTitle("Enter turn folder pattern.");
//            dlg.setInputLabelMessage("Pattern :");
//            dlg.showDialog();
//            turnFolderPattern = ((JTextField)dlg.getInputField()).getText();

            // find turn folders
            ArrayList<File> allFiles = new ArrayList<File>();
            int turnFolderCount = 0;
            int fileCount = 0;
            for (int i=0; i<100; i++) {
                for (String turnFolderPattern : turnFolderPatterns) {
                    String tfp = file.getAbsolutePath() + "/" + String.format(turnFolderPattern, i);
                    File tf = new File(tfp);
                    if (tf.exists()) {
                        turnFolders.add(tf);
                        files = tf.listFiles(new XmlAndPdfFileFilter());
                        try {
                            log.info("Adding turn folder " + tf.getCanonicalPath() + " with " + files.length + " files.");
                        }
                        catch (Exception exc) {
                            
                        }
                        fileCount += files.length;
                        break;
                    }
                }
            }
            final int fileCountFinal = fileCount;
            FormModel formModel = FormModelHelper.createFormModel(this);
            monitor = new JOverseerClientProgressMonitor(formModel);
            FormBackedDialogPage page = new FormBackedDialogPage(monitor);
            dialog = new TitledPageApplicationDialog(page) {
                protected void onAboutToShow() {
                    monitor.taskStarted(String.format("Importing Game Tree '%s'.", new Object[]{file.getAbsolutePath()}), 100 * fileCountFinal);
                    Thread t = new Thread(thisObj);
                    t.start();
                }

                protected boolean onFinish() {
                    return true;
                }

                protected ActionCommand getCancelCommand() {
                    return null;
                }
            };
            dialog.setTitle(ms.getMessage("importFilesDialog.title", new Object[]{}, Locale.getDefault()));
            dialog.showDialog();
        }
    }

    public void run() {
        Game game = gh.getGame();
        if (game == null) {
            return;
        }
        boolean errorOccurred = false;
        boolean warningOccurred = false;
        for (File tf : turnFolders) {
            files = getFilesRecursive(tf, new XmlAndPdfFileFilter());
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
    
    
    class XmlAndPdfFileFilter implements FileFilter {
        public boolean accept(File file) {
            return !file.isDirectory() && (file.getName().endsWith(".pdf") || Pattern.matches("g\\d{3}n\\d{2}t\\d{3}.xml", file.getName()));
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
        //Collections.sort(ret, new FileComparator());
        return ret.toArray(new File[]{});
    }
    
    class FileComparator implements Comparator {

        public int compare(Object arg0, Object arg1) {
            if (!File.class.isInstance(arg0) || !File.class.isInstance(arg1)) return 0;
            String fn1 = ((File)arg0).getName();
            String fn2 = ((File)arg1).getName();
            int i = fn1.indexOf(".");
            int j = fn2.indexOf(".");
            if (i == -1 || j == -1) return 0;
            fn1 = fn1.substring(i - 4, 4);
            fn2 = fn2.substring(i - 4, 4);
            return fn1.compareTo(fn2);
        }
        
    }

}
