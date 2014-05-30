package org.joverseer.ui.support;

import org.joverseer.support.GameHolder;
import org.joverseer.ui.support.dialogs.ErrorDialog;

/**
 * Checks if the program has an active game and shows an error message if no active game is found
 * 
 * @author Marios Skounakis
 */
public class ActiveGameChecker {
    public static boolean checkActiveGameExists() {
        if (!GameHolder.hasInitializedGame()) {
            // show error, cannot import when game not initialized
            ErrorDialog md = new ErrorDialog(
                    Messages.getString("errorActionNotAvailable"));
            md.showDialog();
            return false;
        }
        return true;
    }
}
