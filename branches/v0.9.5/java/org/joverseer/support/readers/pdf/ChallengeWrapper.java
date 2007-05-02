package org.joverseer.support.readers.pdf;

import org.joverseer.domain.Challenge;


public class ChallengeWrapper extends CombatWrapper {
    String character;
    
    
    public String getCharacter() {
        return character;
    }

    
    public void setCharacter(String character) {
        this.character = character;
    }

    public Challenge getChallenge() {
        Challenge c = new Challenge();
        c.setHexNo(getHexNo());
        c.setCharacter(getCharacter());
        c.setDescription(getNarration());
        return c;
    }

}
