package org.joverseer.ui.command;

import org.springframework.richclient.command.ActionCommand;
import org.springframework.richclient.filechooser.FileChooserUtils;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.application.event.LifecycleApplicationEvent;
import org.springframework.richclient.dialog.ApplicationDialog;
import org.springframework.richclient.dialog.MessageDialog;
import org.springframework.richclient.image.ImageSource;
import org.joverseer.support.readers.xml.TurnXmlReader;
import org.joverseer.support.GameHolder;
import org.joverseer.ui.events.GameChangedListener;
import org.joverseer.ui.events.SelectedHexChangedListener;
import org.joverseer.ui.events.GameChangedEvent;
import org.joverseer.ui.SimpleLifecycleAdvisor;
import org.joverseer.ui.LifecycleEventsEnum;

import java.io.File;
import java.util.EventListener;

/**
 * Created by IntelliJ IDEA.
 * User: mskounak
 * Date: 22 Σεπ 2006
 * Time: 10:41:02 μμ
 * To change this template use File | Settings | File Templates.
 */
public class OpenXmlFile extends ActionCommand {

    public OpenXmlFile() {
        super("openXmlFileCommand");
    }

    protected void doExecuteCommand() {
        File file = FileChooserUtils.showFileChooser(Application.instance().getActiveWindow().getControl(), ".xml", "Select", "Xml Turn File");
        try {
            TurnXmlReader r = new TurnXmlReader();
            r.readFile(file.getAbsolutePath());

            GameHolder gh = (GameHolder)Application.instance().getApplicationContext().getBean("gameHolder");
            r.updateGame(gh.getGame());

            Application.instance().getApplicationContext().publishEvent(
                    new LifecycleApplicationEvent(LifecycleEventsEnum.GameChangedEvent.toString(), gh.getGame()));

        }
        catch (Exception exc) {
            //todo create message handler class
            logger.error(exc);
            MessageDialog dlg = new MessageDialog(String.format("Error importing Xml Turn file %s.", file.getAbsolutePath()), exc.getMessage());
            dlg.showDialog();
            return;
        }
        try {
            //todo create message handler class
            String msg = String.format("Xml Turn '%s' imported succesfully.", new String[]{file.getAbsolutePath()});
            Application.instance().getActiveWindow().getStatusBar().setMessage(msg);
        }
        catch (Exception exc) {
            // do nothing
        }
    }

}
