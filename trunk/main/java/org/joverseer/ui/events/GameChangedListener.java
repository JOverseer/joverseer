package org.joverseer.ui.events;

import java.util.EventListener;

/**
 * Created by IntelliJ IDEA.
 * User: mskounak
 * Date: 30 Σεπ 2006
 * Time: 3:26:52 μμ
 * To change this template use File | Settings | File Templates.
 */
public interface GameChangedListener extends EventListener {
    public void eventOccured(GameChangedEvent ev);
}
