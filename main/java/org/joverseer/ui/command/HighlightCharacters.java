package org.joverseer.ui.command;

import java.util.Locale;

import org.joverseer.metadata.domain.NationAllegianceEnum;
import org.joverseer.support.GameHolder;
import org.springframework.context.MessageSource;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.command.ActionCommand;
import org.springframework.richclient.dialog.MessageDialog;


public class HighlightCharacters extends ActionCommand {
    public HighlightCharacters() {
        super("highlightCharactersCommand");
    }
    
    protected void doExecuteCommand() {
        if (!GameHolder.hasInitializedGame()) {
            // show error, cannot import when game not initialized
            MessageSource ms = (MessageSource)Application.services().getService(MessageSource.class);
            MessageDialog md = new MessageDialog(
                    ms.getMessage("errorDialog.title", new String[]{}, Locale.getDefault()),
                    ms.getMessage("errorImportingTurns", new String[]{}, Locale.getDefault()));
            md.showDialog();
            return;
        }

        MessageDialog dlg = new MessageDialog("Error", "Not implemented");
        dlg.showDialog();
    }
    
    private class HighlightOptions {
        Integer minCommandRank = null;
        Integer minAgentRank = null;
        Integer minEmmisaryRank = null;
        Integer minMageRank = null;
        Integer minStealthRank = null;
        Integer minChallengeRank = null;
        Integer nationNo = null;
        NationAllegianceEnum allegiance = null;
        //TODO
    }
}
