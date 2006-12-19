package org.joverseer.ui.command;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Locale;
import java.util.prefs.Preferences;

import javax.swing.JFileChooser;

import org.joverseer.game.Game;
import org.joverseer.support.GameHolder;
import org.joverseer.support.readers.pdf.TurnPdfReader;
import org.joverseer.support.readers.xml.TurnXmlReader;
import org.joverseer.ui.JOverseerClientProgressMonitor;
import org.joverseer.ui.LifecycleEventsEnum;
import org.joverseer.ui.support.JOverseerEvent;
import org.springframework.binding.form.FormModel;
import org.springframework.context.MessageSource;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.command.ActionCommand;
import org.springframework.richclient.dialog.FormBackedDialogPage;
import org.springframework.richclient.dialog.MessageDialog;
import org.springframework.richclient.dialog.TitledPageApplicationDialog;
import org.springframework.richclient.form.FormModelHelper;


public class OpenPdfDir extends ActionCommand implements Runnable {
    File[] files;
    JOverseerClientProgressMonitor monitor;
    GameHolder gh;

    public OpenPdfDir() {
        super("openPdfDirCommand");
        gh = (GameHolder)Application.instance().getApplicationContext().getBean("gameHolder");
    }

    public void run() {
        Game game = gh.getGame();
        if (game == null) {
            return;
        }
        for (File f : files) {
            if (f.getAbsolutePath().endsWith(".pdf")) {
                try {
                    monitor.subTaskStarted(String.format("Imporing file '%s'.", new Object[]{f.getAbsolutePath()}));

                    final TurnPdfReader r = new TurnPdfReader(game, f.getCanonicalPath());
                    r.setMonitor(monitor);
                    r.run();
                }
                catch (Exception exc) {
                    int a = 1;
                    // do nothing
                    // todo fix
                }
            }

        }
        Application.instance().getApplicationContext().publishEvent(
                                        new JOverseerEvent(LifecycleEventsEnum.GameChangedEvent.toString(), gh.getGame(), this));
        monitor.done();

    }

    protected void doExecuteCommand() {
        if (!GameHolder.hasInitializedGame()) {
            // show error, cannot import when game not initialized
            MessageSource ms = (MessageSource)Application.services().getService(MessageSource.class);
            MessageDialog md = new MessageDialog(
                    ms.getMessage("errorDialog.title", new String[]{}, Locale.getDefault()),
                    ms.getMessage("errorImportingTurns", new String[]{}, Locale.getDefault()));
            md.showDialog();
            return;
        }

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
            class XmlFileFilter implements FilenameFilter {
                public boolean accept(File dir, String name) {
                    return name.endsWith(".pdf");
                }
            }
            files = file.listFiles(new XmlFileFilter());
            FormModel formModel = FormModelHelper.createFormModel(this);
            monitor = new JOverseerClientProgressMonitor(formModel);
            FormBackedDialogPage page = new FormBackedDialogPage(monitor);
            TitledPageApplicationDialog dialog = new TitledPageApplicationDialog(page) {
                protected void onAboutToShow() {
                    monitor.taskStarted(String.format("Importing Directory '%s'.", new Object[]{file.getAbsolutePath()}), 100 * files.length);
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
            MessageSource ms = (MessageSource)Application.services().getService(MessageSource.class);
            dialog.setTitle(ms.getMessage("importFilesDialog.title", new Object[]{}, Locale.getDefault()));
            dialog.showDialog();
        }
    }
}

