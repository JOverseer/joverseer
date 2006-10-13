package org.joverseer.domain;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: mskounak
 * Date: Sep 17, 2006
 * Time: 9:08:33 PM
 * To change this template use File | Settings | File Templates.
 */
public class SpellProficiency implements Serializable {
    int spellId;
    int proficiency;

    public SpellProficiency() {
    }

    public SpellProficiency(int spellId, int proficiency) {
        this.spellId = spellId;
        this.proficiency = proficiency;
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
}
