package org.joverseer.ui.domain.mapItems;

import org.joverseer.domain.Army;
import org.joverseer.support.movement.MovementUtils;

import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: mskounak
 * Date: 8 Δεκ 2006
 * Time: 10:12:46 μμ
 * To change this template use File | Settings | File Templates.
 */
public class ArmyRangeMapItem extends AbstractMapItem {
    Army army;
    HashMap rangeHexes;
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
