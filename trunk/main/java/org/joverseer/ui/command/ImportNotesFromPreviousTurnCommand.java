package org.joverseer.ui.command;

import java.util.ArrayList;

import org.joverseer.domain.Army;
import org.joverseer.domain.Note;
import org.joverseer.domain.Character;
import org.joverseer.domain.PopulationCenter;
import org.joverseer.game.Game;
import org.joverseer.game.Turn;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.support.GameHolder;
import org.joverseer.ui.LifecycleEventsEnum;
import org.joverseer.ui.support.ActiveGameChecker;
import org.joverseer.ui.support.JOverseerEvent;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.command.ActionCommand;

/**
 * Imports all persistent notes from the previous turn
 * 
 * @author Marios Skounakis
 */
public class ImportNotesFromPreviousTurnCommand extends ActionCommand {
    public ImportNotesFromPreviousTurnCommand() {
        super("importNotesFromPreviousTurnCommand");
    }

    @Override
	protected void doExecuteCommand() {
        if (!ActiveGameChecker.checkActiveGameExists()) return;
        final Game g = ((GameHolder)Application.instance().getApplicationContext().getBean("gameHolder")).getGame();
        Turn previousTurn = null;
        for (int i=g.getCurrentTurn()-1; i>=0; i--) {
            if (g.getTurn(i) != null) {
                previousTurn = g.getTurn(i);
                break;
            }
        }
        if (previousTurn == null) return;
        Turn turn = g.getTurn();
        
        for (Note n : (ArrayList<Note>)previousTurn.getContainer(TurnElementsEnum.Notes).findAllByProperty("persistent", true)) {
            boolean copy = false;
            Object target = null;
            if (Integer.class.isInstance(n.getTarget())) {
                // hexNo target, copy
                copy = true;
                target = n.getTarget();
            } else if (Character.class.isInstance(n.getTarget())) {
                Character c = (Character)n.getTarget();
                // check if char exists
                target = turn.getContainer(TurnElementsEnum.Character).findFirstByProperty("name", c.getName());
                if (target != null) {
                    copy = true;
                    
                }
            } else if (PopulationCenter.class.isInstance(n.getTarget())) {
                PopulationCenter pc = (PopulationCenter)n.getTarget();
                // check if pop center exists
                if ((target = turn.getContainer(TurnElementsEnum.PopulationCenter).findFirstByProperty("name", pc.getName())) != null) {
                    copy = true;
                }
            } else if (Army.class.isInstance(n.getTarget())) {
                Army a = (Army)n.getTarget();
                // check if pop center exists
                if ((target = turn.getContainer(TurnElementsEnum.Army).findFirstByProperty("commanderName", a.getCommanderName())) != null) {
                    copy = true;
                }
            }
            if (copy) {
                if (turn.getContainer(TurnElementsEnum.Notes).findFirstByProperty("id", n.getId()) == null) {
                    Note newNote = new Note();
                    newNote.setId(n.getId());
                    newNote.setTarget(target);
                    newNote.setText(n.getText() + "");
                    newNote.setPersistent(n.getPersistent());
                    newNote.setNationNo(n.getNationNo());
                    turn.getContainer(TurnElementsEnum.Notes).addItem(newNote);
                }
            }
        }
        Application.instance().getApplicationContext().publishEvent(
                new JOverseerEvent(LifecycleEventsEnum.ListviewRefreshItems.toString(), this, this));

    }
}
