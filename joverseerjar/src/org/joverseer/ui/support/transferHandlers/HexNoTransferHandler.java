package org.joverseer.ui.support.transferHandlers;

import javax.swing.JComponent;

import org.joverseer.ui.map.MapPanel;


/**
 * Transfer handler for hex number (from MapPanel)
 * 
 * @author Marios Skounakis
 */

//TODO embed in MapPanel as it is MapPanel specific
public class HexNoTransferHandler extends StringTransferHandler {
    public HexNoTransferHandler(String arg0) {
        super(arg0);
    }

    @Override
	protected String exportString(JComponent c) {
    	String v = ((MapPanel)c).getHex();
        return v;
    }
}
