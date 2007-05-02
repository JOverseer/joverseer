package org.joverseer.ui.flexdock;

import org.springframework.richclient.application.flexdock.FlexDockApplicationPage;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.application.PageComponent;
import org.springframework.richclient.application.ViewDescriptor;
import org.springframework.richclient.image.ImageSource;
import org.flexdock.docking.DockingManager;
import org.flexdock.view.View;
import org.flexdock.view.ViewProps;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;


public class JOverseerApplicationPage extends FlexDockApplicationPage {
    private PropertyChangeListener activeHandler = new PropertyChangeListener() {
        public void propertyChange( PropertyChangeEvent evt ) {
            if( ViewProps.ACTIVE.equals( evt.getPropertyName() ) && Boolean.TRUE.equals( evt.getNewValue() ) ) {
                View view = (View) evt.getSource();
                PageComponent component = findPageComponent( view.getPersistentId() );
                setActiveComponent( component );
            }
        }
    };

    protected View createView( final PageComponent component ) {
        View view = new View( component.getId() );
        view.setTitle( component.getDisplayName() );
        view.setTabText( component.getDisplayName() );
        view.setTabIcon( component.getIcon() );
        view.setIcon( component.getIcon() );
        view.setContentPane( component.getControl() );

        view.getViewProperties().addPropertyChangeListener( activeHandler );

        configureView( component, view, getViewDescriptor( component.getId() ) );
        return view;
    }

    protected void configureView( final PageComponent component, View view, ViewDescriptor descriptor ) {
        boolean closable = true;
        boolean pinnable = true;
        boolean dockable = true;
        boolean hasTitle = true;

        if( descriptor instanceof JOverseerViewDescriptor ) {
            JOverseerViewDescriptor desc = (JOverseerViewDescriptor) descriptor;
            closable = desc.isClosable();
            pinnable = desc.isPinnable();
            dockable = desc.isDockable();
            hasTitle = desc.getHasTitleBar();
        }

        if (!hasTitle) {
            view.setTitlebar(null);
        }
        
        // get the icon from the image source
        try {
	        ImageSource imgSource = (ImageSource) Application.instance().getApplicationContext().getBean("imageSource");
	        Icon icon = new ImageIcon(imgSource.getImage(component.getId() + ".icon"));
	        view.setIcon(icon);
	        view.setTabIcon(icon);
        }
        catch (Exception exc) {
        	// do nothing if icon was not found
        }
        
        if( closable && hasTitle) {
            view.addAction( View.CLOSE_ACTION );
            // TODO fix this: this is the only way I found to find out if a dockable has
            // been closed by the user.
            AbstractButton btn = view.getActionButton( View.CLOSE_ACTION );
            btn.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    close( component );
                }
            } );
        }

        if( pinnable ) {
            view.addAction( View.PIN_ACTION );
        }

        view.getViewProperties().setDockingEnabled( dockable );
    }

}
