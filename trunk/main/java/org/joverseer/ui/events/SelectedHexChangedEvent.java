package org.joverseer.ui.events;

import java.util.EventObject;

/**
 * Created by IntelliJ IDEA.
 * User: mskounak
 * Date: Sep 18, 2006
 * Time: 9:37:11 PM
 * To change this template use File | Settings | File Templates.
 */
public class SelectedHexChangedEvent extends EventObject {
	private static final long serialVersionUID = 1L;
	
    public SelectedHexChangedEvent(Object source) {
        super(source);
    }

}
