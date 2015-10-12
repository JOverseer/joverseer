package org.joverseer.ui.command;

import org.joverseer.domain.Note;
import org.joverseer.game.Game;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.support.GameHolder;
import org.joverseer.ui.LifecycleEventsEnum;
import org.joverseer.ui.support.ActiveGameChecker;
import org.joverseer.ui.support.JOverseerEvent;
import org.springframework.richclient.application.Application;
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
        final Game g = ((GameHolder)Application.instance().getApplicationContext().getBean("gameHolder")).getGame();
        g.getTurn().getContainer(TurnElementsEnum.Notes).removeItem(this.note);
        Application.instance().getApplicationContext().publishEvent(
                new JOverseerEvent(LifecycleEventsEnum.ListviewRefreshItems.toString(), this, this));
        Application.instance().getApplicationContext().publishEvent(
                new JOverseerEvent(LifecycleEventsEnum.NoteUpdated.toString(), this.note, this));
        Application.instance().getApplicationContext().publishEvent(
                new JOverseerEvent(LifecycleEventsEnum.RefreshMapItems.toString(), this.note, this));

    }

}
