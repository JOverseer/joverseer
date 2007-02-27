package org.joverseer.ui.support;

import java.util.Locale;

import org.joverseer.support.GameHolder;
import org.springframework.context.MessageSource;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.dialog.MessageDialog;


public class ActiveGameChecker {
    public static boolean checkActiveGameExists() {
        if (!GameHolder.hasInitializedGame()) {
            // show error, cannot import when game not initialized
            MessageSource ms = (MessageSource)Application.services().getService(MessageSource.class);
            MessageDialog md = new MessageDialog(
                    ms.getMessage("errorDialog.title", new String[]{}, Locale.getDefault()),
                    ms.getMessage("errorActionNotAvailable", new String[]{}, Locale.getDefault()));
            md.showDialog();
            return false;
        }
        return true;
    }
}
