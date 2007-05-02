package org.joverseer.support.readers.pdf;

import org.joverseer.domain.HarborSizeEnum;
import org.joverseer.domain.PopulationCenter;
import org.joverseer.domain.ProductEnum;

public class PopCenterWrapper {
	String name;
	int hexNo;
	String docks;
	ProductAmountWrapper production;
	ProductAmountWrapper stores;
        String climate;
	
	public String getDocks() {
		return docks;
	}
	public void setDocks(String docks) {
		this.docks = docks;
	}
	public int getHexNo() {
		return hexNo;
	}
	public void setHexNo(int hexNo) {
		this.hexNo = hexNo;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public ProductAmountWrapper getProduction() {
		return production;
	}
	public void setProduction(ProductAmountWrapper production) {
		this.production = production;
	}
	public ProductAmountWrapper getStores() {
		return stores;
	}
	public void setStores(ProductAmountWrapper stores) {
		this.stores = stores;
	}
        
        public String getClimate() {
            return climate;
        }
        
        public void setClimate(String climate) {
            this.climate = climate;
        }
        
    public void updatePopCenter(PopulationCenter pc) {
            pc.setProduction(ProductEnum.Leather, Integer.parseInt(getProduction().getLeather()));
            pc.setProduction(ProductEnum.Bronze, Integer.parseInt(getProduction().getBronze()));
            pc.setProduction(ProductEnum.Steel, Integer.parseInt(getProduction().getSteel()));
            pc.setProduction(ProductEnum.Mithril, Integer.parseInt(getProduction().getMithril()));
            pc.setProduction(ProductEnum.Food, Integer.parseInt(getProduction().getFood()));
            pc.setProduction(ProductEnum.Timber, Integer.parseInt(getProduction().getTimber()));
            pc.setProduction(ProductEnum.Mounts, Integer.parseInt(getProduction().getMounts()));
            pc.setProduction(ProductEnum.Gold, Integer.parseInt(getProduction().getGold()));
            
            pc.setStores(ProductEnum.Leather, Integer.parseInt(getStores().getLeather()));
            pc.setStores(ProductEnum.Bronze, Integer.parseInt(getStores().getBronze()));
            pc.setStores(ProductEnum.Steel, Integer.parseInt(getStores().getSteel()));
            pc.setStores(ProductEnum.Mithril, Integer.parseInt(getStores().getMithril()));
            pc.setStores(ProductEnum.Food, Integer.parseInt(getStores().getFood()));
            pc.setStores(ProductEnum.Timber, Integer.parseInt(getStores().getTimber()));
            pc.setStores(ProductEnum.Mounts, Integer.parseInt(getStores().getMounts()));
            pc.setStores(ProductEnum.Gold, 0);
        }
	
	
}
