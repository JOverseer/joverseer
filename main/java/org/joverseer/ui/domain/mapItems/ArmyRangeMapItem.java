package org.joverseer.ui.domain.mapItems;

import org.joverseer.domain.Army;
import org.joverseer.support.movement.MovementUtils;

import java.util.HashMap;


public class ArmyRangeMapItem extends AbstractMapItem {
    Army army;
    HashMap rangeHexes;
    
    public ArmyRangeMapItem(int hexNo, boolean cav, boolean fed) {
        rangeHexes = MovementUtils.calculateArmyRangeHexes(hexNo, cav, fed);
    }
    
    public ArmyRangeMapItem(Army a) {
        army = a;
        int hexNo = Integer.parseInt(army.getHexNo());
        Boolean cav = a.computeCavalry();
        if (cav == null) cav = false;
        Boolean fed = a.computeFed();
        if (fed == null) fed = false;
        rangeHexes = MovementUtils.calculateArmyRangeHexes(hexNo, cav, fed);
    }

    public Army getArmy() {
        return army;
    }

    public HashMap getRangeHexes() {
        return rangeHexes;
    }

    public String getDescription() {
        return String.format("Range for army {0} located at {1}.", new Object[]{getArmy().getCommanderName(), getArmy().getHexNo()});
    }
}
