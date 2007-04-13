package org.joverseer.ui.support.dialogs;

import org.springframework.richclient.dialog.MessageDialog;


public class ErrorDialog extends MessageDialog {

    public ErrorDialog(String arg1) {
        super("Error", arg1);
    }
    
    public ErrorDialog(Exception exc) {
        super("Error", exc.getMessage());
    }
    
}
