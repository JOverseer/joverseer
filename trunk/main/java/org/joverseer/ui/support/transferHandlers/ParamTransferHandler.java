package org.joverseer.ui.support.transferHandlers;

import javax.swing.JComponent;


public class ParamTransferHandler extends StringTransferHandler {
    int artiNo;
    
    public ParamTransferHandler(int artiNo) {
        super("");
        this.artiNo = artiNo;
    }

    protected String exportString(JComponent c) {
        return String.valueOf(artiNo);
    }
    
    
}
