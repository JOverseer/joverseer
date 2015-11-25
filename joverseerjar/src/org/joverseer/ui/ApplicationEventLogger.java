package org.joverseer.ui;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joverseer.ui.support.JOverseerEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

/**
 * Utility class that logs all thrown Application Events to the program log
 * 
 * @author Marios Skounakis
 */
public class ApplicationEventLogger implements ApplicationListener {

    Log log = LogFactory.getLog(ApplicationEventLogger.class);

    @Override
	public void onApplicationEvent(ApplicationEvent arg0) {
        if (arg0 instanceof JOverseerEvent) {
            JOverseerEvent e = (JOverseerEvent) arg0;
            this.log.info("** Event " + e.getEventType());
        }
    }


}
