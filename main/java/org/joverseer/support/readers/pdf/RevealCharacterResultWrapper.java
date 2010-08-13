package org.joverseer.support.readers.pdf;

import org.joverseer.domain.Character;
import org.joverseer.game.Game;
import org.joverseer.game.Turn;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.support.Container;
import org.joverseer.support.infoSources.InfoSource;
import org.joverseer.support.infoSources.TurnInfoSource;
import org.joverseer.support.infoSources.spells.DerivedFromRevealCharacterInfoSource;
import org.joverseer.support.infoSources.spells.DerivedFromSpellInfoSource;

/**
 * Holds information about Reveal Character order results
 * @author Marios Skounakis
 */
public class RevealCharacterResultWrapper implements OrderResult {
    String characterName;
    int hexNo;
    
    public String getCharacterName() {
        return characterName;
    }
    
    public void setCharacterName(String characterName) {
        this.characterName = characterName;
    }
    
    public int getHexNo() {
        return hexNo;
    }
    
    public void setHexNo(int hexNo) {
        this.hexNo = hexNo;
    }
    
    public void updateGame(Game game, Turn turn, int nationNo, String casterName) {
        Container chars = turn.getContainer(TurnElementsEnum.Character);
        Character c = (Character)chars.findFirstByProperty("name", getCharacterName());
        DerivedFromRevealCharacterInfoSource is1 = new DerivedFromRevealCharacterInfoSource(turn.getTurnNo(), nationNo, casterName, getHexNo());
        if (c == null) {
            // character not found, add
            c = new Character();
            c.setName(getCharacterName());
            c.setId(Character.getIdFromName(getCharacterName()));
            c.setHexNo(getHexNo());
            c.setNationNo(0);
            c.setInfoSource(is1);
            chars.addItem(c);
        } else {
            // character found
            // examine info source
            InfoSource is = c.getInfoSource();
            if (TurnInfoSource.class.isInstance(is)) {
                // turn import, do nothing
                return;
            } else if (DerivedFromSpellInfoSource.class.isInstance(is)) {
                // spell
                // add info source...
                if (!((DerivedFromSpellInfoSource)is).contains(is1)) {
                    ((DerivedFromSpellInfoSource)is).addInfoSource(is1);
                }
            } 
        }
        
    }
}
