package org.joverseer.ui.flexdock;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.springframework.richclient.application.flexdock.FlexDockApplicationPageFactory;
import org.springframework.richclient.application.ApplicationWindow;
import org.springframework.richclient.application.ApplicationPage;
import org.springframework.richclient.application.PageDescriptor;
import org.flexdock.docking.DockingManager;
import org.flexdock.perspective.DockingStateListener;
import org.flexdock.perspective.PerspectiveManager;
import org.flexdock.perspective.persist.FilePersistenceHandler;
import org.flexdock.perspective.persist.PersistenceHandler;
import org.joverseer.ui.flexdock.JOverseerApplicationPage;

import com.jgoodies.looks.plastic.PlasticXPLookAndFeel;


public class JOverseerApplicationPageFactory extends FlexDockApplicationPageFactory {
    public ApplicationPage createApplicationPage( ApplicationWindow window, PageDescriptor descriptor ) {
    	
           final JOverseerApplicationPage page = new JOverseerApplicationPage();
           page.setApplicationWindow( window );
           page.setDescriptor( descriptor );

           DockingManager.setDockableFactory( page );
           // TODO uncomment for persistence
           
           
           PerspectiveManager.setFactory( getPerspectiveFactory() );
           PerspectiveManager.getInstance().setCurrentPerspective( getDefaultPerspective(), true );
           // TODO define how the file name or persister will be passed in the app context
           page.loadLayout();
           
           DockingManager.setAutoPersist(true);

           return page;
       }

}
