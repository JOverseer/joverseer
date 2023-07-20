package org.joverseer.ui.support.dialogs;

import org.apache.log4j.Logger;
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
        this( (exc != null) ? ((exc.getMessage() == null) ? Messages.getString("errorDialog.unexpectedError") :exc.getMessage()): Messages.getString("errorDialog.unexpectedError"));
    	Log(exc);
    }
    public ErrorDialog(Exception exc,String msg) {
    	this(msg);
    	Log(exc);
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
    public static boolean showErrorDialog(Exception exc) {
    	Log(exc);
    	return showErrorDialog(Messages.getString("errorDialog.title"),"errorDialog.unexpectedError");
    }
    public static boolean showErrorDialog(Exception exc,String messageId) {
    	Log(exc);
    	return showErrorDialog(Messages.getString("errorDialog.title"),messageId);
    }
    private static void Log(Exception exc) {
        if (exc != null) {
        	Logger.getRootLogger().error(getCustomStackTrace(exc));
        }
    }
    // from javapractices.com
    public static String getCustomStackTrace(Throwable throwable) {
        //add the class name and any message passed to constructor
        StringBuilder result = new StringBuilder( "Unexpected: " );
        result.append(throwable.toString());
        String NL = System.getProperty("line.separator");
        result.append(NL);

        //add each element of the stack trace
        for (StackTraceElement element : throwable.getStackTrace()){
          result.append(element);
          result.append(NL);
        }
        return result.toString();
      }
    
}
