/*
 * Copyright 2002-2004 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.joverseer.ui;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.apache.log4j.Level;
import org.springframework.richclient.application.ApplicationWindow;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.application.flexdock.FlexDockApplicationPage;
import org.springframework.richclient.application.config.ApplicationWindowConfigurer;
import org.springframework.richclient.application.config.DefaultApplicationLifecycleAdvisor;
import org.springframework.richclient.dialog.ConfirmationDialog;
import org.springframework.richclient.dialog.FormBackedDialogPage;
import org.springframework.richclient.dialog.TitledPageApplicationDialog;
import org.springframework.richclient.form.FormModelHelper;
import org.springframework.binding.form.FormModel;
import org.springframework.context.MessageSource;
import org.flexdock.docking.Dockable;
import org.flexdock.docking.DockingManager;
import org.flexdock.docking.state.DockingState;
import org.flexdock.perspective.Perspective;
import org.flexdock.perspective.PerspectiveManager;
import org.flexdock.perspective.persist.PerspectiveModel;
import org.flexdock.view.View;
import org.joverseer.metadata.GameMetadata;
import org.joverseer.metadata.GameTypeEnum;
import org.joverseer.game.Game;
import org.joverseer.support.GameHolder;
import org.joverseer.support.readers.xml.TurnXmlReader;
import org.joverseer.domain.Order;
import org.joverseer.ui.flexdock.JOverseerViewDescriptor;
import org.joverseer.ui.orders.OrderEditorForm;

import java.awt.*;
import java.util.Locale;
import java.util.prefs.Preferences;

import javax.print.attribute.standard.JobHoldUntil;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSeparator;
import javax.swing.JToolBar;

/**
 * Custom application lifecycle implementation that configures the sample app at
 * well defined points within its lifecycle.
 * 
 * @author Keith Donald
 */
public class SimpleLifecycleAdvisor extends DefaultApplicationLifecycleAdvisor {

    private final Log _logger = LogFactory.getLog(getClass());
    boolean canCloseWindow = true;

    /**
     * This method is called prior to the opening of an application window. Note
     * at this point the window control has not been created. This hook allows
     * programmatic control over the configuration of the window (by setting
     * properties on the configurer) and it provides a hook where code that
     * needs to be executed prior to the window opening can be plugged in (like
     * a startup wizard, for example).
     * 
     * @param configurer The application window configurer
     */
    public void onPreWindowOpen( ApplicationWindowConfigurer configurer ) {

        BasicConfigurator.configure();
        // If you override this method, it is critical to allow the superclass
        // implementation to run as well.
        super.onPreWindowOpen(configurer);
        configurer.setInitialSize(new Dimension(900,680));

//        GameMetadata gm = (GameMetadata) Application.instance().getApplicationContext().getBean("gameMetadata");
//        gm.setGameType(GameTypeEnum.game2950);
//        gm.load();
//        Game game = new Game();
//        game.setMetadata(gm);
//        ((GameHolder)Application.instance().getApplicationContext().getBean("gameHolder")).setGame(game);
//
//        try {
//            TurnXmlReader r = new TurnXmlReader(game, "c:/middleearth/g26/t0/g026n07t000.xml");
//            r.readFile("c:/middleearth/g26/t0/g026n07t000.xml");
//            r.updateGame(game);
////            r = new TurnXmlReader();
////            r.readFile("c:/middleearth/g26/t1/g026n07t001.xml");
////            r.updateGame(game);
//        }
//        catch (Exception exc) {
//            // do nothing
//            int a = 1;
//        }
        Logger.getRootLogger().setLevel(Level.WARN);

        // Uncomment to hide the menubar, toolbar, or alter window size...
        // configurer.setShowMenuBar(false);
        // configurer.setShowToolBar(false);
        // configurer.setInitialSize(new Dimension(640, 480));
    }

    /**
     * Called just after the command context has been internalized. At this
     * point, all the commands for the window have been created and are
     * available for use. If you need to force the execution of a command prior
     * to the display of an application window (like a login command), this is
     * where you'd do it.
     * 
     * @param window The window who's commands have just been created
     */
    public void onCommandsCreated( ApplicationWindow window ) {
        if( _logger.isInfoEnabled() ) {
            _logger.info("onCommandsCreated( windowNumber=" + window.getNumber() + " )");
        }
    }

    /**
     * Called after the actual window control has been created.
     * 
     * @param window The window being processed
     */
    public void onWindowCreated( ApplicationWindow window ) {
        if( _logger.isInfoEnabled() ) {
            _logger.info("onWindowCreated( windowNumber=" + window.getNumber() + " )");
        }
        JFrame frame = Application.instance().getActiveWindow().getControl();
        JToolBar toolbar = null;
        for (Component c : frame.getContentPane().getComponents()) {
            if (JToolBar.class.isInstance(c)) {
                toolbar = (JToolBar)c;
            }
        }
        
        
        
//        if (toolbar != null) {
//            toolbar.addSeparator();
//            
//            MapOptionsView view = (MapOptionsView)Application.instance().getApplicationContext().getBean("mapOptsView");
//            toolbar.add(view.getControl());            
//        }
    }

    /**
     * Called immediately after making the window visible.
     * 
     * @param window The window being processed
     */
    public void onWindowOpened( ApplicationWindow window ) {
        if( _logger.isInfoEnabled() ) {
            _logger.info("onWindowOpened( windowNumber=" + window.getNumber() + " )");
        }
        
    }

    /**
     * Called when the window is being closed. This hook allows control over
     * whether the window is allowed to close. By returning false from this
     * method, the window will not be closed.
     * 
     * @return boolean indicator if window should be closed. <code>true</code>
     *         to allow the close, <code>false</code> to prevent the close.
     */
    public boolean onPreWindowClose( ApplicationWindow window ) {
        if( _logger.isInfoEnabled() ) {
            _logger.info("onPreWindowClose( windowNumber=" + window.getNumber() + " )");
        }
        canCloseWindow = true;
        if (GameHolder.hasInitializedGame()) {
            canCloseWindow = false;
            // show warning
            MessageSource ms = (MessageSource)Application.services().getService(MessageSource.class);
            ConfirmationDialog md = new ConfirmationDialog(
                    ms.getMessage("confirmCloseAppDialog.title", new String[]{}, Locale.getDefault()),
                    ms.getMessage("confirmCloseAppDialog.message", new String[]{}, Locale.getDefault()))
            {
                protected void onConfirm() {
                    canCloseWindow = true;
                }
            };
            md.showDialog();
        }
        if (canCloseWindow) {
            Preferences prefs = Preferences.userNodeForPackage(JOverseerClient.class);
            prefs.put("windowSize", String.valueOf(
                                        window.getControl().getState() + "," +
                                        window.getControl().getX() + "," +
                                        window.getControl().getY() + "," +
                                        window.getControl().getWidth() + "," +
                                        window.getControl().getHeight()));
        }
        return canCloseWindow;
    }

    /**
     * Called when the application has fully started. This is after the initial
     * application window has been made visible.
     */
    public void onPostStartup() {
        if( _logger.isInfoEnabled() ) {
            _logger.info("onPostStartup()");
        }
        DockingManager.display(DockingManager.getDockable("mapView"));
        Preferences prefs = Preferences.userNodeForPackage(JOverseerClient.class);
        String winSize = prefs.get("windowSize", null);
        if (winSize != null) {
            ApplicationWindow window = Application.instance().getActiveWindow();
            String parts[] = winSize.split(",");
            if (parts.length == 5) {
                int state = Integer.parseInt(parts[0]);
                window.getControl().setState(state);
                if (state == JFrame.NORMAL) {
                    int x = Integer.parseInt(parts[1]);
                    int y = Integer.parseInt(parts[2]);
                    int w = Integer.parseInt(parts[3]);
                    int h = Integer.parseInt(parts[4]);
                    window.getControl().setLocation(new Point(x, y));
                    window.getControl().setSize(new Dimension(w, h));
                }
            }
        }
    }

}
