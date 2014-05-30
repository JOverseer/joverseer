package org.joverseer.ui.support.dialogs;

import org.joverseer.ui.support.Messages;
import org.springframework.richclient.dialog.MessageDialog;

/**
 * Standardized simplified error dialog
 * @author Marios Skounakis
 */
public class ErrorDialog extends MessageDialog {

    public ErrorDialog(String arg1) {
        super(Messages.getString("errorDialog.title"), arg1);
    }
    
    public ErrorDialog(Exception exc) {
        this(exc != null ? exc.getMessage() : Messages.getString("errorDialog.unexpectedError"));
    }
    
}
