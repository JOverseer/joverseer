package org.joverseer.support.readers.newXml;

import org.joverseer.domain.PopulationCenter;
import org.joverseer.domain.ProductEnum;

public class ProductionWrapper {
	String type;
	String currentStores;
	int expProduction;
	
	public String getCurrentStores() {
		return currentStores;
	}
	public void setCurrentStores(String currentStores) {
		this.currentStores = currentStores;
	}
	public int getExpProduction() {
		return expProduction;
	}
	public void setExpProduction(int expProduction) {
		this.expProduction = expProduction;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	public void updatePopCenter(PopulationCenter pc) {
		try {
			ProductEnum pe = ProductEnum.getFromCode(getType());
			pc.setProduction(pe, getExpProduction());
			try {
				pc.setStores(pe, Integer.parseInt(getCurrentStores()));
			}
			catch (Exception e) {
				pc.setStores(pe, 0);
			}
		}
		catch (Exception e) {
			//TODO add log message
		}
	}
}
