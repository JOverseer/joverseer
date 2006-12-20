package org.joverseer.domain;

import java.io.Serializable;


public class SpellProficiency implements Serializable {
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
        return proficiency;
    }

    public void setProficiency(int proficiency) {
        this.proficiency = proficiency;
    }

    public int getSpellId() {
        return spellId;
    }

    public void setSpellId(int spellId) {
        this.spellId = spellId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
