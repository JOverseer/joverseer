package org.joverseer.support.readers.pdf;

import org.joverseer.game.Game;
import org.joverseer.game.Turn;
import org.joverseer.domain.Character;
import org.joverseer.domain.CharacterDeathReasonEnum;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.support.infoSources.PdfTurnInfoSource;

/**
 * Stores information about assassination results (target, etc)
 * 
 * @author Marios Skounakis
 */
public class AssassinationResultWrapper implements OrderResult {
        int hexNo;
        String character;
        
        public void updateGame(Game game, Turn turn, int nationNo, String orderCharacter) {
            Character c = (Character)turn.getContainer(TurnElementsEnum.Character).findFirstByProperty("name", character);
            if (c == null) {
                c = new Character();
                c.setName(character);
                c.setId(Character.getIdFromName(character));
                c.setNationNo(0);
                c.setInfoSource(new PdfTurnInfoSource(turn.getTurnNo(), nationNo));
                turn.getContainer(TurnElementsEnum.Character).addItem(c);
            }
            c.setDeathReason(CharacterDeathReasonEnum.Assassinated);
        }

        
        public String getCharacter() {
            return character;
        }

        
        public void setCharacter(String character) {
            this.character = character;
        }

        
        public int getHexNo() {
            return hexNo;
        }

        
        public void setHexNo(int hexNo) {
            this.hexNo = hexNo;
        }
        
        
}
