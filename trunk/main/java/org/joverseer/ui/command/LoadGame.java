package org.joverseer.ui.command;

import org.springframework.richclient.command.ActionCommand;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.application.event.LifecycleApplicationEvent;
import org.springframework.context.ApplicationEvent;
import org.joverseer.support.GameHolder;
import org.joverseer.game.Game;
import org.joverseer.ui.LifecycleEventsEnum;
import org.joverseer.ui.support.JOverseerEvent;

import javax.swing.*;
import java.io.*;

/**
 * Created by IntelliJ IDEA.
 * User: mskounak
 * Date: 13 Οκτ 2006
 * Time: 6:54:07 μμ
 * To change this template use File | Settings | File Templates.
 */
public class LoadGame extends ActionCommand {
    public LoadGame() {
        super("LoadGameCommand");
    }

    protected void doExecuteCommand() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
        fileChooser.setApproveButtonText("Load");
        if (fileChooser.showOpenDialog(Application.instance().getActiveWindow().getControl()) == JFileChooser.APPROVE_OPTION) {
            File f = fileChooser.getSelectedFile();
            GameHolder gh = (GameHolder) Application.instance().getApplicationContext().getBean("gameHolder");
            try {
                ObjectInputStream in = new ObjectInputStream(new FileInputStream(f));
                gh.setGame((Game)in.readObject());
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
