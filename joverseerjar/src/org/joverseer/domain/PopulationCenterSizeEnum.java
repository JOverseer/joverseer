package org.joverseer.domain;

import java.io.Serializable;

import org.joverseer.ui.support.UIUtils;

/**
 * Enumeration for population center sizes
 * 
 * @author Marios Skounakis
 */
public enum PopulationCenterSizeEnum implements Serializable {
	ruins(0), camp(1), village(2), town(3), majorTown(4), city(5);

	private final int size;

	PopulationCenterSizeEnum(int size) {
		this.size = size;
	}

	public Class<PopulationCenterSizeEnum> getType() {
		return PopulationCenterSizeEnum.class; // To change body of implemented
		// methods use File | Settings |
		// File Templates.
	}

	@SuppressWarnings("boxing")
	public Integer getCode() {
		return this.size;
	}

	public static PopulationCenterSizeEnum getFromCode(int code) {
		for (PopulationCenterSizeEnum p : values()) {
			if (p.getCode() == code)
				return p;
		}
		return null;
	}

	public static PopulationCenterSizeEnum getFromLabel(String label) {
		for (PopulationCenterSizeEnum v : values()) {
			if (v.getLabel().equals(label))
				return v;
		}
		return null;
	}

	public String getLabel() {
		switch (this.size) {
		case 0:
			return "Ruins";
		case 1:
			return "Camp";
		case 2:
			return "Village";
		case 3:
			return "Town";
		case 4:
			return "Major Town";
		case 5:
			return "City";
		}
		return "";
	}
	/**
	 * convert the population centre size to the matching number from an array.
	 * @param lookup the 6 element array, index 0 matches ruin
	 * @return
	 */
    public static int lookupSize(PopulationCenterSizeEnum size, int[] lookup) {
    	
    	switch (size) {
    	case ruins:
    		return lookup[0];
    	case camp:
    		return lookup[1];
    	case village:
    		return lookup[2];
    	case town:
    		return lookup[3];
    	case majorTown:
    		return lookup[4];
    	case city:
    		return lookup[5];
    	default:
    		assert false;    		
    		return lookup[0];
    	}
    }
	public String getRenderString() {
		return UIUtils.enumToString(this);
	}

}
