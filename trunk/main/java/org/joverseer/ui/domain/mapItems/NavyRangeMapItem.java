package org.joverseer.ui.domain.mapItems;

import org.joverseer.domain.Army;
import org.joverseer.support.movement.MovementUtils;

public class NavyRangeMapItem extends AbstractRangeMapItem {
	private static final long serialVersionUID = -802327536773943344L;
	Army army;
	boolean fed;
	boolean openSeas;

	public NavyRangeMapItem(int hexNo, boolean fed, boolean openSeas) {
        this.rangeHexes = MovementUtils.calculateNavyRangeHexes(hexNo, openSeas, fed);
        this.openSeas = openSeas;
        this.fed = fed;
    }
	
	
    @Override
	public String getDescription() {
        return String.format("Range for army {0} located at {1}.", new Object[]{getArmy().getCommanderName(), getArmy().getHexNo()});
    }

	public Army getArmy() {
		return this.army;
	}

	public void setArmy(Army army) {
		this.army = army;
	}

	@Override
	public boolean isFed() {
		return this.fed;
	}

	public void setFed(boolean fed) {
		this.fed = fed;
	}

	public boolean isOpenSeas() {
		return this.openSeas;
	}

	public void setOpenSeas(boolean openSeas) {
		this.openSeas = openSeas;
	}

	
    
}
