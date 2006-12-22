package org.joverseer.ui.command;

import org.joverseer.metadata.domain.NationAllegianceEnum;
import org.springframework.richclient.command.ActionCommand;
import org.springframework.richclient.dialog.MessageDialog;


public class HighlightCharacters extends ActionCommand {
    public HighlightCharacters() {
        super("highlightCharactersCommand");
    }
    
    protected void doExecuteCommand() {
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
