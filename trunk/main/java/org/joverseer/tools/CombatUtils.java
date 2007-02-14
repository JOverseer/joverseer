package org.joverseer.tools;

import org.joverseer.domain.Army;
import org.joverseer.domain.ArmyElement;
import org.joverseer.domain.ArmyElementType;


public class CombatUtils {
    public static int getNakedHeavyInfantryEquivalent(Army a) {
        int nhi = 0;
        for (ArmyElement ae : a.getElements()) {
            double f = 0;
            if (ae.getArmyElementType().equals(ArmyElementType.HeavyCavalry)) {
                f = 1.6;
            } else if (ae.getArmyElementType().equals(ArmyElementType.LightCavalry)) {
                f = .8;
            } else if (ae.getArmyElementType().equals(ArmyElementType.HeavyInfantry)) {
                nhi += ae.getNumber();
            } else if (ae.getArmyElementType().equals(ArmyElementType.LightInfantry)) {
                f = .5;
            } else if (ae.getArmyElementType().equals(ArmyElementType.Archers)) {
                f = .4;
            } else if (ae.getArmyElementType().equals(ArmyElementType.MenAtArms)) {
                f = .2;
            }  
            nhi += new Double(ae.getNumber() * f).intValue();
        }
        return nhi;
    }
}
