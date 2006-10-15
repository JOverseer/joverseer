package org.joverseer.ui.support;

import org.springframework.richclient.application.event.LifecycleApplicationEvent;

/**
 * Created by IntelliJ IDEA.
 * User: mskounak
 * Date: 14 Οκτ 2006
 * Time: 3:16:29 μμ
 * To change this template use File | Settings | File Templates.
 */
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
