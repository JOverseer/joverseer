package org.joverseer.support.readers.newXml;

import org.joverseer.domain.PopulationCenter;
import org.joverseer.domain.ProductEnum;

public class ProductionWrapper {
	String type;
	String currentStores;
	int expProduction;
	
	public String getCurrentStores() {
		return this.currentStores;
	}
	public void setCurrentStores(String currentStores) {
		this.currentStores = currentStores;
	}
	public int getExpProduction() {
		return this.expProduction;
	}
	public void setExpProduction(int expProduction) {
		this.expProduction = expProduction;
	}
	public String getType() {
		return this.type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	public void updatePopCenter(PopulationCenter pc) {
		ProductEnum pe = ProductEnum.getFromCode(getType());
		pc.setProduction(pe, new Integer(getExpProduction()));
		try {
			pc.setStores(pe, new Integer(Integer.parseInt(getCurrentStores())));
		} catch (NumberFormatException e) {
			pc.setStores(pe, new Integer(0));
		}
	}
}
