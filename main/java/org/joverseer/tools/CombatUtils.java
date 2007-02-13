package org.joverseer.tools;

import org.joverseer.domain.Army;
import org.joverseer.domain.ArmyElement;
import org.joverseer.domain.ArmyElementType;


public class CombatUtils {
    public static int getNakedHeavyInfantryEquivalent(Army a) {
        int nhi = 0;
        for (ArmyElement ae : a.getElements()) {
            if (ae.getArmyElementType().equals(ArmyElementType.HeavyCavalry)) {
                nhi += ae.getNumber() * 1.6;
            } else if (ae.getArmyElementType().equals(ArmyElementType.LightCavalry)) {
                nhi += ae.getNumber() * .8;
            } else if (ae.getArmyElementType().equals(ArmyElementType.HeavyInfantry)) {
                nhi += ae.getNumber();
            } else if (ae.getArmyElementType().equals(ArmyElementType.LightInfantry)) {
                nhi += ae.getNumber() * .5;
            } else if (ae.getArmyElementType().equals(ArmyElementType.Archers)) {
                nhi += ae.getNumber() * .4;
            } else if (ae.getArmyElementType().equals(ArmyElementType.MenAtArms)) {
                nhi += ae.getNumber() * .2;
            }  
        }
        return nhi;
    }
}
