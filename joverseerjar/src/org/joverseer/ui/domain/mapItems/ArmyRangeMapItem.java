package org.joverseer.ui.domain.mapItems;

import org.joverseer.domain.Army;
import org.joverseer.support.movement.MovementUtils;
import org.joverseer.ui.support.Messages;

/**
 * Class that handles the drawing of army ranges on the map.
 * 
 * It contains options to draw:
 * - an existing army, ignoring or not enemy pops
 * - a new army, given flags for cavalry and fed and the starting position
 * 
 * @author Marios Skounakis
 */
public class ArmyRangeMapItem extends AbstractRangeMapItem {
    private static final long serialVersionUID = 7197917745582629886L;
    Army army;
    boolean cav;
    boolean fed;
    
    public ArmyRangeMapItem(int hexNo, boolean cav, boolean fed) {
        this.rangeHexes = MovementUtils.calculateArmyRangeHexes(hexNo, cav, fed, true, null);
        this.cav = cav;
        this.fed = fed;
    }
    
    public ArmyRangeMapItem(Army a, boolean ignoreEnemyPops) {
        this.army = a;
        int hexNo = Integer.parseInt(this.army.getHexNo());
        Boolean cav1 = a.computeCavalry();
        if (cav1 == null) cav1 = false;
        Boolean fed1 = a.computeFed();
        if (fed1 == null) fed1 = false;
        this.rangeHexes = MovementUtils.calculateArmyRangeHexes(hexNo, cav1, fed1, ignoreEnemyPops, this.army.getNationAllegiance());
    }

    public Army getArmy() {
        return this.army;
    }

    @Override
	public String getDescription() {
        return Messages.getString("ArmyRangeMapItem.RangeForArmy", new Object[]{getArmy().getCommanderName(), getArmy().getHexNo()}); //$NON-NLS-1$
    }

    
    public boolean isCav() {
        return this.cav;
    }

    
    public void setCav(boolean cav) {
        this.cav = cav;
    }

    
    @Override
	public boolean isFed() {
        return this.fed;
    }

    
    public void setFed(boolean fed) {
        this.fed = fed;
    }

	@Override
	public boolean isEquivalent(AbstractMapItem mi) {
		return (mi instanceof ArmyRangeMapItem)
				&& (this.army == ((ArmyRangeMapItem)mi).army) 
				&& (this.cav == ((ArmyRangeMapItem)mi).cav) 
				&& (this.fed == ((ArmyRangeMapItem)mi).fed);
	}
    
    
}
