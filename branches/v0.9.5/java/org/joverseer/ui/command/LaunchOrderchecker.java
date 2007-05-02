package org.joverseer.ui.command;

import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.UIManager;

import org.springframework.richclient.command.ActionCommand;

public class LaunchOrderchecker extends ActionCommand {
    public LaunchOrderchecker() {
        super("launchOrderchecker");
    }
    
    protected void doExecuteCommand() {
        try {
        } 
        catch (Exception exc) {
            int a = 1;
        }
    }
}
