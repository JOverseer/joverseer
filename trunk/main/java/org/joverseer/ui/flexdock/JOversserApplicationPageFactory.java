package org.joverseer.ui.flexdock;

import org.springframework.richclient.application.flexdock.FlexDockApplicationPageFactory;
import org.springframework.richclient.application.ApplicationWindow;
import org.springframework.richclient.application.ApplicationPage;
import org.springframework.richclient.application.PageDescriptor;
import org.flexdock.docking.DockingManager;
import org.flexdock.perspective.PerspectiveManager;
import org.joverseer.ui.flexdock.JOverseerApplicationPage;

/**
 * Created by IntelliJ IDEA.
 * User: mskounak
 * Date: 28 Οκτ 2006
 * Time: 9:08:27 μμ
 * To change this template use File | Settings | File Templates.
 */
public class JOversserApplicationPageFactory extends FlexDockApplicationPageFactory {
    public ApplicationPage createApplicationPage( ApplicationWindow window, PageDescriptor descriptor ) {
           final JOverseerApplicationPage page = new JOverseerApplicationPage();
           page.setApplicationWindow( window );
           page.setDescriptor( descriptor );

           DockingManager.setDockableFactory( page );
           // TODO uncomment for persistence
            //DockingManager.setAutoPersist(true);

           PerspectiveManager.setFactory( getPerspectiveFactory() );
           PerspectiveManager.getInstance().setCurrentPerspective( getDefaultPerspective(), true );
           // TODO define how the file name or persister will be passed in the app context
//         PersistenceHandler persister = FilePersistenceHandler.createDefault("test-flexdock.xml");
//         PerspectiveManager.setPersistenceHandler( persister );
//        PerspectiveManager.setRestoreFloatingOnLoad(true);
           page.loadLayout();

           return page;
       }

}
