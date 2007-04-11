package org.joverseer.ui.domain.mapItems;

import org.joverseer.domain.Army;
import org.joverseer.support.movement.MovementUtils;

import java.util.HashMap;


public class ArmyRangeMapItem extends AbstractMapItem {
    Army army;
    HashMap rangeHexes;
    boolean cav;
    boolean fed;
    
    public ArmyRangeMapItem(int hexNo, boolean cav, boolean fed) {
        rangeHexes = MovementUtils.calculateArmyRangeHexes(hexNo, cav, fed, true, null);
        this.cav = cav;
        this.fed = fed;
    }
    
    public ArmyRangeMapItem(Army a, boolean ignoreEnemyPops) {
        army = a;
        int hexNo = Integer.parseInt(army.getHexNo());
        Boolean cav = a.computeCavalry();
        if (cav == null) cav = false;
        Boolean fed = a.computeFed();
        if (fed == null) fed = false;
        rangeHexes = MovementUtils.calculateArmyRangeHexes(hexNo, cav, fed, ignoreEnemyPops, army.getNationAllegiance());
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

    
    public boolean isCav() {
        return cav;
    }

    
    public void setCav(boolean cav) {
        this.cav = cav;
    }

    
    public boolean isFed() {
        return fed;
    }

    
    public void setFed(boolean fed) {
        this.fed = fed;
    }
    
    
}
