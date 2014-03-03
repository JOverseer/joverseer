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
		return this.originHex;
	}

	public void setOriginHex(int originHex) {
		this.originHex = originHex;
	}

	public int getDestinationHex() {
		return this.destinationHex;
	}

	public void setDestinationHex(int destinationHex) {
		this.destinationHex = destinationHex;
	}

	public Color getColor() {
		return this.color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	@Override
	public String getDescription() {
		return null;
	}

	@Override
	public boolean isEquivalent(AbstractMapItem mi) {
		return (mi instanceof HexArrowMapItem)
				&& (this.originHex == ((HexArrowMapItem)mi).originHex) 
				&& (this.destinationHex == ((HexArrowMapItem)mi).destinationHex) 
				&& (this.color == ((HexArrowMapItem)mi).color);
	}

}
