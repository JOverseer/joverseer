package org.joverseer.ui.support.transferHandlers;

import javax.swing.JComponent;


public class ArtifactIdTransferHandler extends StringTransferHandler {
    int artiNo;
    
    public ArtifactIdTransferHandler(int artiNo) {
        super("");
        this.artiNo = artiNo;
    }

    protected String exportString(JComponent c) {
        return String.valueOf(artiNo);
    }
    
    
}
