package org.joverseer.support.readers.pdf;

import org.joverseer.domain.Challenge;

/**
 * Stores information about challenges:
 * - character
 * - narration
 * 
 * @author Marios Skounakis
 */
public class ChallengeWrapper extends CombatWrapper {
    String character;
    
    
    public String getCharacter() {
        return this.character;
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
