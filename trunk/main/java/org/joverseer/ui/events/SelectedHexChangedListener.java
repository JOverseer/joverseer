package org.joverseer.ui.events;

import org.joverseer.ui.events.SelectedHexChangedEvent;

import java.util.EventListener;

/**
 * Created by IntelliJ IDEA.
 * User: mskounak
 * Date: Sep 18, 2006
 * Time: 9:38:53 PM
 * To change this template use File | Settings | File Templates.
 */
public interface SelectedHexChangedListener extends EventListener {
    public void eventOccured(SelectedHexChangedEvent ev);
}
