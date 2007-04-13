/*
 * Copyright 2005 the original author or authors. Licensed under the Apache License, Version 2.0 (the "License"); you
 * may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language governing permissions and limitations under the
 * License.
 */
package com.jidesoft.spring.richclient.docking;

import java.awt.Color;
import java.util.Locale;
import java.util.prefs.Preferences;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.RepaintManager;

import org.joverseer.game.Game;
import org.joverseer.preferences.PreferenceRegistry;
import org.joverseer.support.GameHolder;
import org.joverseer.ui.JOverseerJIDEClient;
import org.joverseer.ui.map.MapPanel;
import org.joverseer.ui.orderEditor.test.TestOrderParameters;
import org.joverseer.ui.support.GraphicUtils;
import org.springframework.context.MessageSource;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.application.ApplicationWindow;
import org.springframework.richclient.application.config.DefaultApplicationLifecycleAdvisor;
import org.springframework.richclient.command.CommandGroup;
import org.springframework.richclient.dialog.ConfirmationDialog;
import org.springframework.richclient.progress.StatusBarCommandGroup;

import com.jidesoft.action.CommandBarFactory;
import com.jidesoft.docking.DefaultDockableHolder;
import com.sun.org.apache.bcel.internal.generic.LNEG;

/**
 * Extends the default application lifecycle advisor to allow the injection of any status bar command group
 * implementation. It also changes the repaint manager to use the technique of Scott Deplap to detetect illegal UI
 * updates outside of the EDT
 * 
 * @author Jonny Wray
 */
public class JideApplicationLifecycleAdvisor extends DefaultApplicationLifecycleAdvisor {

    private StatusBarCommandGroup statusBar = null;
    private RepaintManager repaintManager;
    boolean canCloseWindow = true;

    public CommandGroup getSpecificCommandGroup(String name) {
        return getCommandGroup(name);
    }

    public void onPostStartup() {
        initializeRepaintManager();

        if (JOverseerJIDEClient.cmdLineArgs == null || JOverseerJIDEClient.cmdLineArgs.length == 0
                || !JOverseerJIDEClient.cmdLineArgs[0].equals("d")) {
            JMenuBar menuBar = Application.instance().getActiveWindow().getControl().getJMenuBar();
            for (int i = 0; i < menuBar.getMenuCount(); i++) {
                if (menuBar.getMenu(i).getText().equals("Admin")) {
                    menuBar.getMenu(i).setVisible(false);
                }
            }
        }

    }


    public void onWindowOpened(ApplicationWindow arg0) {
        super.onWindowOpened(arg0);
        if (PreferenceRegistry.instance().getPreferenceValue("general.tipOfTheDay").equals("yes")) {
            GraphicUtils.showTipOfTheDay();
        }
    }

    public void setRepaintManager(RepaintManager repaintManager) {
        this.repaintManager = repaintManager;
    }

    public void setStatusBar(StatusBarCommandGroup statusBar) {
        this.statusBar = statusBar;
    }

    public StatusBarCommandGroup getStatusBarCommandGroup() {
        if (statusBar == null) {
            statusBar = new StatusBarCommandGroup();
        }
        return statusBar;
    }

    private void initializeRepaintManager() {
        if (repaintManager != null) {
            RepaintManager.setCurrentManager(repaintManager);
        }
    }

    public boolean onPreWindowClose(ApplicationWindow arg0) {
        canCloseWindow = true;
        if (GameHolder.hasInitializedGame()) {
            canCloseWindow = false;
            // show warning
            MessageSource ms = (MessageSource) Application.services().getService(MessageSource.class);
            ConfirmationDialog md = new ConfirmationDialog(ms.getMessage("confirmCloseAppDialog.title",
                    new String[] {}, Locale.getDefault()), ms.getMessage("confirmCloseAppDialog.message",
                    new String[] {}, Locale.getDefault())) {

                protected void onConfirm() {
                    canCloseWindow = true;
                }
            };
            md.showDialog();
        }

        return canCloseWindow;
    }

    public void onPreInitialize(Application arg0) {
        super.onPreInitialize(arg0);
    }


}
