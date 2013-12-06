package org.joverseer.ui.support.transferHandlers;

import javax.swing.JComponent;

/**
 * Generic string transfer handler
 * 
 * @author Marios Skounakis
 */
//TODO It's obsolete - the GenericExportTransferHandler should be used instead
public class ParamTransferHandler extends StringTransferHandler {
    Object param;
    
    public ParamTransferHandler(Object param) {
        super("");
        this.param = param;
    }

    @Override
	protected String exportString(JComponent c) {
        return this.param.toString();
    }
    
    
}
