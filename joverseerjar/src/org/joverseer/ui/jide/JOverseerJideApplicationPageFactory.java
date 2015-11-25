package org.joverseer.ui.jide;

import org.springframework.richclient.application.ApplicationPage;
import org.springframework.richclient.application.ApplicationPageFactory;
import org.springframework.richclient.application.ApplicationWindow;
import org.springframework.richclient.application.PageDescriptor;

/**
 * Implements ApplicationPageFactory to create JOverseerJideApplicationPages
 * @author Marios Skounakis
 */
public class JOverseerJideApplicationPageFactory implements ApplicationPageFactory {

    @Override
	public ApplicationPage createApplicationPage(ApplicationWindow window, PageDescriptor pageDescriptor) {

        JOverseerJideApplicationPage page = new JOverseerJideApplicationPage(window, pageDescriptor);
        return page;
    }

}
