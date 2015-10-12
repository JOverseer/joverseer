package org.joverseer.ui.domain.mapItems;

import java.util.HashMap;

public abstract class AbstractRangeMapItem extends AbstractMapItem {
	protected HashMap rangeHexes;
    
	public HashMap getRangeHexes() {
        return this.rangeHexes;
    }
	
	public abstract boolean isFed();
}
