package org.joverseer.ui.support;

import org.joverseer.ui.LifecycleEventsEnum;
import org.springframework.richclient.application.event.LifecycleApplicationEvent;


@SuppressWarnings("serial")
public class JOverseerEvent extends LifecycleApplicationEvent {
	Object sender;
	LifecycleEventsEnum type;

/*    public JOverseerEvent(String string, Object object, Object sender) {
        super(string, object);
        this.sender = sender;
    }
*/    public JOverseerEvent(LifecycleEventsEnum type, Object object, Object sender) {
        super(type.toString(), object);
        this.sender = sender;
        this.type = type;
    }

    public Object getSender() {
        return this.sender;
    }

    public Object getData() {
        return super.getObject();
    }
    /**
     * This lets us be more efficient in testing the type of the event.
     * @return
     */
    public LifecycleEventsEnum getType() {
    	return this.type;
    }
    /**
     * Use this to test if the event is of a specific lifecycle.
     * @param type
     * @return
     */
    public boolean isLifecycleEvent(LifecycleEventsEnum aType)
    {
    	return getEventType().equals(aType.toString());
    }
}
