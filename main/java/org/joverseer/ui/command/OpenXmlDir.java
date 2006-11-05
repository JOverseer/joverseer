package org.joverseer.ui.command;

import org.springframework.richclient.command.ActionCommand;
import org.springframework.richclient.filechooser.FileChooserUtils;
import org.springframework.richclient.filechooser.DefaultFileFilter;
import org.springframework.richclient.application.Application;
import org.joverseer.support.readers.xml.TurnXmlReader;
import org.joverseer.support.GameHolder;
import org.joverseer.ui.support.JOverseerEvent;
import org.joverseer.ui.LifecycleEventsEnum;

import javax.swing.*;
import java.io.FileFilter;
import java.io.File;
import java.io.FilenameFilter;

/**
 * Created by IntelliJ IDEA.
 * User: mskounak
 * Date: 22 Σεπ 2006
 * Time: 11:10:47 μμ
 * To change this template use File | Settings | File Templates.
 */
public class OpenXmlDir extends ActionCommand {
    

    public OpenXmlDir() {
        super("openXmlDirCommand");
    }

    protected void doExecuteCommand() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (fileChooser.showOpenDialog(Application.instance().getActiveWindow().getControl()) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            File[] files = file.listFiles();
            GameHolder gh = (GameHolder)Application.instance().getApplicationContext().getBean("gameHolder");
            for (File f : files) {
                if (f.getAbsolutePath().endsWith(".xml")) {
                    try {
                        TurnXmlReader r = new TurnXmlReader();
                        r.readFile(f.getAbsolutePath());
                        r.updateGame(gh.getGame());
                        Application.instance().getApplicationContext().publishEvent(
                                            new JOverseerEvent(LifecycleEventsEnum.GameChangedEvent.toString(), gh.getGame(), this));

                    }
                    catch (Exception exc) {
                        int a = 1;
                        // do nothing
                        // todo fix
                    }
                }
            }
        }
    }
}
