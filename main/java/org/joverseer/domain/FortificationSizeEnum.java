package org.joverseer.domain;

import java.io.Serializable;

import org.joverseer.ui.support.UIUtils;

/**
 * Enumeration for population center fortification sizes
 * 
 * @author Marios Skounakis
 * 
 */
public enum FortificationSizeEnum implements Serializable {
	none(0), tower(1), fort(2), castle(3), keep(4), citadel(5);

	private final int size;

	FortificationSizeEnum(int size) {
		this.size = size;
	}

	public int getSize() {
		return this.size;
	}

	public static FortificationSizeEnum getFromSize(int size) {
		for (FortificationSizeEnum f : values()) {
			if (f.getSize() == size)
				return f;
		}
		return null;
	}

	public String getRenderString() {
		return UIUtils.enumToString(this);
	}

	public static FortificationSizeEnum getFromText(String text) {
		if (text.equals("None"))
			return FortificationSizeEnum.none;
		if (text.equals("Tower"))
			return FortificationSizeEnum.tower;
		if (text.equals("Fort"))
			return FortificationSizeEnum.fort;
		if (text.equals("Castle"))
			return FortificationSizeEnum.castle;
		if (text.equals("Keep"))
			return FortificationSizeEnum.keep;
		if (text.equals("Citadel"))
			return FortificationSizeEnum.citadel;
		return null;
	}

}
