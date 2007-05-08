package org.joverseer.ui.support.transferHandlers;

import javax.swing.JComponent;


public class ParamTransferHandler extends StringTransferHandler {
    Object param;
    
    public ParamTransferHandler(Object param) {
        super("");
        this.param = param;
    }

    protected String exportString(JComponent c) {
        return param.toString();
    }
    
    
}
