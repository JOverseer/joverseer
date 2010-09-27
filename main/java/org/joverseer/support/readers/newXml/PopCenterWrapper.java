package org.joverseer.support.readers.newXml;

import java.util.ArrayList;

import org.joverseer.domain.PopulationCenter;

public class PopCenterWrapper {
	int hexNo;
	boolean sieged;
	String terrain;
	String climate;

	ArrayList<ProductionWrapper> products = new ArrayList<ProductionWrapper>();

	public void addProduct(ProductionWrapper pw) {
		products.add(pw);
	}

	public String getClimate() {
		return climate;
	}

	public void setClimate(String climate) {
		this.climate = climate;
	}

	public int getHexNo() {
		return hexNo;
	}

	public void setHexNo(int hexNo) {
		this.hexNo = hexNo;
	}

	public boolean isSieged() {
		return sieged;
	}

	public void setSieged(boolean sieged) {
		this.sieged = sieged;
	}

	public String getTerrain() {
		return terrain;
	}

	public void setTerrain(String terrain) {
		this.terrain = terrain;
	}

	public ArrayList<ProductionWrapper> getProducts() {
		return products;
	}

	public void updatePopCenter(PopulationCenter pc) {
		for (ProductionWrapper pw : products) {
			pw.updatePopCenter(pc);
		}
	}

}
