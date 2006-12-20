package org.joverseer.ui.command;

import java.io.File;

import org.joverseer.support.GameHolder;
import org.joverseer.support.readers.pdf.TurnPdfReader;
import org.joverseer.ui.JOverseerClientProgressMonitor;
import org.joverseer.ui.LifecycleEventsEnum;
import org.joverseer.ui.support.JOverseerEvent;
import org.springframework.binding.form.FormModel;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.command.ActionCommand;
import org.springframework.richclient.dialog.FormBackedDialogPage;
import org.springframework.richclient.dialog.MessageDialog;
import org.springframework.richclient.dialog.TitledPageApplicationDialog;
import org.springframework.richclient.filechooser.FileChooserUtils;
import org.springframework.richclient.form.FormModelHelper;


public class OpenPdfFile extends ActionCommand {

    public OpenPdfFile() {
        super("openPdfFileCommand");
    }

    protected void doExecuteCommand() {
        File file = FileChooserUtils.showFileChooser(Application.instance().getActiveWindow().getControl(), ".xml", "Select", "Xml Turn File");
        try {
            GameHolder gh = (GameHolder)Application.instance().getApplicationContext().getBean("gameHolder");
            final TurnPdfReader r = new TurnPdfReader(gh.getGame(), file.getAbsolutePath());
            FormModel formModel = FormModelHelper.createFormModel(r);
            final JOverseerClientProgressMonitor monitor = new JOverseerClientProgressMonitor(formModel);
            FormBackedDialogPage page = new FormBackedDialogPage(monitor);
            TitledPageApplicationDialog dialog = new TitledPageApplicationDialog(page) {
                protected void onAboutToShow() {
                    monitor.taskStarted("Import XML Turn.", 100);
                    r.setMonitor(monitor);
                    Thread t = new Thread(r);
                    t.start();
                }

                protected boolean onFinish() {
                    return true;
                }

                protected ActionCommand getCancelCommand() {
                    return null;
                }
            };

            //dialog.setTitle();
            dialog.showDialog();

            Application.instance().getApplicationContext().publishEvent(
                    new JOverseerEvent(LifecycleEventsEnum.GameChangedEvent.toString(), gh.getGame(), this));

        }
        catch (Exception exc) {
            //todo create message handler class
            logger.error(exc);
            MessageDialog dlg = new MessageDialog(String.format("Error importing Pdf Turn file %s.", file.getAbsolutePath()), exc.getMessage());
            dlg.showDialog();
            return;
        }
        try {
            //todo create message handler class
            String msg = String.format("Pdf Turn '%s' imported succesfully.", new Object[]{file.getAbsolutePath()});
            Application.instance().getActiveWindow().getStatusBar().setMessage(msg);
        }
        catch (Exception exc) {
            // do nothing
        }
    }

}
