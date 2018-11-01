package org.joverseer.ui.support.dialogs;

import org.joverseer.ui.support.Messages;
import org.springframework.richclient.dialog.MessageDialog;

/**
 * Standardized simplified error dialog
 * @author Marios Skounakis
 */
public class ErrorDialog extends MessageDialog {
// we use Messages.getString() instead of .getMessage() to allow eclipse to manage the externalised strings.
	
    public ErrorDialog(String arg1) {
        super(Messages.getString("errorDialog.title"), arg1);
    }
    
    public ErrorDialog(Exception exc) {
        this(exc != null ? exc.getMessage() : Messages.getString("errorDialog.unexpectedError"));
    }
    /**
     * Convenience method to display an Error dialog with a translated title and message and return false
     * @param titleId
     * @param messageId
     * @return always false;
     */
    public static boolean showErrorDialog(String titleId,String messageId) {
    	MessageDialog dlg = new MessageDialog(Messages.getString(titleId),Messages.getString(messageId));
    	dlg.showDialog();
    	return false;
    }
    /**
     * Convenience method to display an Error dialog with a standard translated title and message and return false
     * @param titleId
     * @param messageId
     * @return always false;
     */
    public static boolean showErrorDialog(String messageId) {
    	return showErrorDialog(Messages.getString("errorDialog.title"),messageId);
    }
    
}
