package org.joverseer.ui.command;

import org.joverseer.joApplication;
import org.joverseer.domain.Note;
import org.joverseer.game.Game;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.ui.LifecycleEventsEnum;
import org.joverseer.ui.support.ActiveGameChecker;
import org.springframework.richclient.command.ActionCommand;


/**
 * Delete the given note from the turn
 * 
 * @author Marios Skounakis
 */
public class DeleteNoteCommand extends ActionCommand {
    Note note;
    
    public DeleteNoteCommand(Note note) {
        super("deleteNoteCommand");
        this.note = note;
    }

    @Override
	protected void doExecuteCommand() {
        if (!ActiveGameChecker.checkActiveGameExists()) return;
        final Game g = joApplication.getGame();
        g.getTurn().getContainer(TurnElementsEnum.Notes).removeItem(this.note);
        joApplication.publishEvent(LifecycleEventsEnum.ListviewRefreshItems, this, this);
        joApplication.publishEvent(LifecycleEventsEnum.NoteUpdated, this.note, this);
        joApplication.publishEvent(LifecycleEventsEnum.RefreshMapItems, this.note, this);

    }

}
