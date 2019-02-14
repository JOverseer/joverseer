package org.joverseer.ui.command;

import org.joverseer.JOApplication;
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
        final Game g = JOApplication.getGame();
        g.getTurn().getContainer(TurnElementsEnum.Notes).removeItem(this.note);
        JOApplication.publishEvent(LifecycleEventsEnum.ListviewRefreshItems, this, this);
        JOApplication.publishEvent(LifecycleEventsEnum.NoteUpdated, this.note, this);
        JOApplication.publishEvent(LifecycleEventsEnum.RefreshMapItems, this.note, this);

    }

}
