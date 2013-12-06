package org.joverseer.domain;

import java.io.Serializable;


/**
 * Stores a character's proficiency in casting a spell
 * 
 * @author Marios Skounakis
 */

public class SpellProficiency implements Serializable {
    private static final long serialVersionUID = -3411660721122678568L;
    int spellId;
    int proficiency;
    String name;

    public SpellProficiency() {
    }

    public SpellProficiency(int spellId, int proficiency, String name) {
        this.spellId = spellId;
        this.proficiency = proficiency;
        this.name = name;
    }

    public int getProficiency() {
        return this.proficiency;
    }

    public void setProficiency(int proficiency) {
        this.proficiency = proficiency;
    }

    public int getSpellId() {
        return this.spellId;
    }

    public void setSpellId(int spellId) {
        this.spellId = spellId;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
