package org.joverseer.ui.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import org.apache.commons.beanutils.BeanComparator;
import org.springframework.richclient.application.ApplicationServices;
import org.springframework.richclient.application.ApplicationServicesLocator;
import org.springframework.richclient.application.ApplicationWindow;
import org.springframework.richclient.application.ViewDescriptor;
import org.springframework.richclient.application.ViewDescriptorRegistry;
import org.springframework.richclient.application.config.ApplicationWindowAware;
import org.springframework.richclient.command.CommandGroup;

/**
 * A menu containing a collection of sub-menu items that each display a given view.
 * 
 * @author Keith Donald
 */
public class JOverseerShowViewMenu extends CommandGroup implements ApplicationWindowAware {

    /** The identifier of this command. */
    public static final String ID = "showViewMenu";

    private ApplicationWindow window;

    /**
     * Creates a new {@code ShowViewMenu} with an id of {@value #ID}.
     */
    public JOverseerShowViewMenu() {
        super(ID);
    }

    /**
     * {@inheritDoc}
     */
    public void setApplicationWindow(ApplicationWindow window) {
        this.window = window;
    }

    /**
     * Called after dependencies have been set, populates this menu with action command objects 
     * that will each show a given view when executed. The collection of 'show view' commands will
     * be determined by querying the {@link ViewDescriptorRegistry} retrieved from 
     * {@link ApplicationServices}. 
     */
    public void afterPropertiesSet() {
        super.afterPropertiesSet();
        //TODO should this be confirming that 'this.window' is not null?
        populate();
    }

    private void populate() {
        ViewDescriptorRegistry viewDescriptorRegistry 
                = (ViewDescriptorRegistry) ApplicationServicesLocator
                                           .services()
                                           .getService(ViewDescriptorRegistry.class);
        
        ViewDescriptor[] views = viewDescriptorRegistry.getViewDescriptors();
        HashMap<String,ViewDescriptor> viewMap = new HashMap<String,ViewDescriptor>(); 
        for (ViewDescriptor vd : views) {
            viewMap.put(vd.getCaption().replace("&", ""), vd);
        }
        ArrayList<String> captions = new ArrayList<String>();
        captions.addAll(viewMap.keySet());
        
        Collections.sort(captions);
        for(String caption : captions) {
            addInternal(viewMap.get(caption).createShowViewCommand(window));
        }
    }
    
}
