package org.joverseer.support.readers.xml;

import org.joverseer.domain.NationEconomy;
import org.joverseer.domain.ProductEnum;
import org.joverseer.domain.ProductPrice;
import org.joverseer.support.Container;

/**
 * Holds information about products (from xml turns) Basically it is the nation
 * product totals along with market info
 * 
 * @author Marios Skounakis
 */
public class ProductWrapper {
	String type;
	int buyPrice;
	int sellPrice;
	int marketAvail;
	int nationStores;
	int nationProduction;

	public int getBuyPrice() {
		return this.buyPrice;
	}

	public void setBuyPrice(int buyPrice) {
		this.buyPrice = buyPrice;
	}

	public int getMarketAvail() {
		return this.marketAvail;
	}

	public void setMarketAvail(int marketAvail) {
		this.marketAvail = marketAvail;
	}

	public int getNationProduction() {
		return this.nationProduction;
	}

	public void setNationProduction(int nationProduction) {
		this.nationProduction = nationProduction;
	}

	public int getNationStores() {
		return this.nationStores;
	}

	public void setNationStores(int nationStores) {
		this.nationStores = nationStores;
	}

	public int getSellPrice() {
		return this.sellPrice;
	}

	public void setSellPrice(int sellPrice) {
		this.sellPrice = sellPrice;
	}

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void updateNationEconomy(NationEconomy ne) {
		ProductEnum pe = ProductEnum.getFromCode(getType());
		ne.setProduction(pe, getNationProduction());
		ne.setStores(pe, getNationStores());
	}

	public void updateProductPrice(Container<ProductPrice> prices) {
		ProductEnum pe = ProductEnum.getFromCode(getType());
		ProductPrice pp = prices.findFirstByProperty("product", pe);
		if (pp == null) {
			pp = new ProductPrice();
			pp.setProduct(pe);
			prices.addItem(pp);
		}
		pp.setBuyPrice(getBuyPrice());
		pp.setSellPrice(getSellPrice());
		pp.setMarketTotal(getMarketAvail());
	}
}
