package org.joverseer.ui.events;

import java.util.EventObject;

/**
 * Created by IntelliJ IDEA.
 * User: mskounak
 * Date: 30 Σεπ 2006
 * Time: 3:26:31 μμ
 * To change this template use File | Settings | File Templates.
 */
public class GameChangedEvent extends EventObject {
    public GameChangedEvent(Object source) {
        super(source);
    }
}
