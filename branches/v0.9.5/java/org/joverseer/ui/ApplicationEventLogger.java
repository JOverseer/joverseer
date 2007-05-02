package org.joverseer.ui;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joverseer.ui.support.JOverseerEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

public class ApplicationEventLogger implements ApplicationListener {
	Log log = LogFactory.getLog(ApplicationEventLogger.class);

	public void onApplicationEvent(ApplicationEvent arg0) {
		if (arg0 instanceof JOverseerEvent) {
            JOverseerEvent e = (JOverseerEvent)arg0;
            log.info("** Event " + e.getEventType());
		}
	}

	
	
}
