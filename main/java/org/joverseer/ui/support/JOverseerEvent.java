package org.joverseer.ui.support;

import org.springframework.richclient.application.event.LifecycleApplicationEvent;


public class JOverseerEvent extends LifecycleApplicationEvent {
    Object sender;

    public JOverseerEvent(String string, Object object, Object sender) {
        super(string, object);
        this.sender = sender;
    }

    public Object getSender() {
        return sender;
    }

    public Object getData() {
        return super.getObject();
    }

}
