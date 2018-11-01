package org.joverseer.support;

import org.joverseer.ui.support.JOverseerEvent;
import org.springframework.context.ApplicationListener;

public interface JOverseerEventListener extends ApplicationListener{

	void onApplicationEvent(JOverseerEvent event);

}
