package org.joverseer.ui.support;

import org.springframework.richclient.application.event.LifecycleApplicationEvent;


@SuppressWarnings("serial")
public class JOverseerEvent extends LifecycleApplicationEvent {
    Object sender;

    public JOverseerEvent(String string, Object object, Object sender) {
        super(string, object);
        this.sender = sender;
    }

    public Object getSender() {
        return this.sender;
    }

    public Object getData() {
        return super.getObject();
    }

}
