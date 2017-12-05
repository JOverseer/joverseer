package org.joverseer.ui.support;

import org.joverseer.ui.LifecycleEventsEnum;
import org.springframework.richclient.application.event.LifecycleApplicationEvent;


public class JOverseerEvent extends LifecycleApplicationEvent {
	Object sender;

    public JOverseerEvent(String string, Object object, Object sender) {
        super(string, object);
        this.sender = sender;
    }
    public JOverseerEvent(LifecycleEventsEnum type, Object object, Object sender) {
        super(type.toString(), object);
        this.sender = sender;
    }

    public Object getSender() {
        return this.sender;
    }

    public Object getData() {
        return super.getObject();
    }

    public boolean isLifecycleEvent(LifecycleEventsEnum type)
    {
    	return getEventType().equals(type.toString()); 
    }
}
