package org.joverseer.ui.domain.mapItems;

import java.awt.Color;

public class HexArrowMapItem extends AbstractMapItem {
	int originHex;
	int destinationHex;
	Color color;

	public HexArrowMapItem(int originHex, int destinationHex, Color color) {
		super();
		this.originHex = originHex;
		this.destinationHex = destinationHex;
		this.color = color;
	}

	public int getOriginHex() {
		return originHex;
	}

	public void setOriginHex(int originHex) {
		this.originHex = originHex;
	}

	public int getDestinationHex() {
		return destinationHex;
	}

	public void setDestinationHex(int destinationHex) {
		this.destinationHex = destinationHex;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	@Override
	public String getDescription() {
		return null;
	}

}
