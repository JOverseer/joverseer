package org.joverseer.support.readers.pdf;

import org.joverseer.domain.Character;
import org.joverseer.domain.CharacterDeathReasonEnum;
import org.joverseer.game.Game;
import org.joverseer.game.Turn;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.support.infoSources.PdfTurnInfoSource;

/**
 * Holds information about execution results
 * 
 * @author Marios Skounakis
 */
public class ExecutionResultWrapper implements OrderResult {
    String character;
    
    @Override
	public void updateGame(Game game, Turn turn, int nationNo, String orderCharacter) {
        Character c = (Character)turn.getContainer(TurnElementsEnum.Character).findFirstByProperty("name", this.character);
        if (c == null) {
            c = new Character();
            c.setName(this.character);
            c.setId(Character.getIdFromName(this.character));
            c.setNationNo(0);
            c.setInfoSource(new PdfTurnInfoSource(turn.getTurnNo(), nationNo));
            turn.getContainer(TurnElementsEnum.Character).addItem(c);
        }
        c.setDeathReason(CharacterDeathReasonEnum.Executed);
    }

    
    public String getCharacter() {
        return this.character;
    }

    
    public void setCharacter(String character) {
        this.character = character;
    }

}
