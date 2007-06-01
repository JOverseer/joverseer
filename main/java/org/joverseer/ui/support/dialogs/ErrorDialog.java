package org.joverseer.ui.support.dialogs;

import org.springframework.richclient.dialog.MessageDialog;

/**
 * Standardized simplified error dialog
 * @author Marios Skounakis
 */
//TODO Externalize strings
public class ErrorDialog extends MessageDialog {

    public ErrorDialog(String arg1) {
        super("Error", arg1);
    }
    
    public ErrorDialog(Exception exc) {
        super("Error", (exc != null ? exc.getMessage() : "Unexpected error."));
    }
    
}
