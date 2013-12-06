package org.joverseer.ui.listviews.filters;

import org.joverseer.domain.IHasMapLocation;
import org.joverseer.ui.listviews.AbstractListViewFilter;

public class HexFilter extends AbstractListViewFilter {

    int hex;

    public HexFilter(String description, int hex) {
        super(description);
        this.hex = hex;
    }

    @Override
	public boolean accept(Object obj) {
    	if (!IHasMapLocation.class.isInstance(obj)) return false;
    	IHasMapLocation o = (IHasMapLocation)obj;
    	int hexNo = o.getX() * 100 + o.getY();
    	return hexNo == this.hex;
    }
}
