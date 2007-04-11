package org.joverseer.ui.orderEditor;

import java.util.ArrayList;

import org.joverseer.domain.Order;
import org.joverseer.domain.Character;
import org.joverseer.domain.SpellProficiency;
import org.joverseer.metadata.GameMetadata;
import org.joverseer.metadata.domain.SpellInfo;
import org.joverseer.support.GameHolder;

public class ResearchSpellNumberParameterOrderSubeditor extends SpellNumberParameterOrderSubeditor {

    public ResearchSpellNumberParameterOrderSubeditor(String paramName, Order o, int orderNo) {
        super(paramName, o, orderNo);
    }

    protected void loadSpellCombo() {
        GameMetadata gm = GameHolder.instance().getGame().getMetadata();
        Character c = getOrder().getCharacter();
        parameter.addItem("");
        for (SpellInfo si : (ArrayList<SpellInfo>) gm.getSpells().getItems()) {
            boolean found = false;
            for (SpellProficiency sp : c.getSpells()) {
                if (sp.getSpellId() == si.getNumber()) {
                    found = true;
                }
            }
            if (!found) {
                parameter.addItem(si.getNumber() + " - " + si.getName());
            }
        }
    }

}
