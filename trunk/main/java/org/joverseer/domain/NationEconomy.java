package org.joverseer.domain;

import java.io.Serializable;

import org.joverseer.support.ProductContainer;

/**
 * Stores information about the economy of a given nation
 *  
 * @author Marios Skounakis
 *
 */
public class NationEconomy implements IBelongsToNation, Serializable {
    private static final long serialVersionUID = -6860023083453300940L;

    Integer nationNo;

    int armyMaintenance;
    int popMaintenance;
    int charMaintenance;
    int totalMaintenance;
    int taxRate;
    int revenue;
    int surplus;
    int reserve;
    int taxBase;
    int goldProduction;
    int availableGold; // use in Engine

    ProductContainer production = new ProductContainer();
    ProductContainer stores = new ProductContainer();
    
    public int getArmyMaintenance() {
        return this.armyMaintenance;
    }

    public void setArmyMaintenance(int armyMaintenance) {
        this.armyMaintenance = armyMaintenance;
    }

    public int getCharMaintenance() {
        return this.charMaintenance;
    }

    public void setCharMaintenance(int charMaintenance) {
        this.charMaintenance = charMaintenance;
    }

    @Override
	public Integer getNationNo() {
        return this.nationNo;
    }

    @Override
	public void setNationNo(Integer nationNo) {
        this.nationNo = nationNo;
    }

    public int getPopMaintenance() {
        return this.popMaintenance;
    }

    public void setPopMaintenance(int popMaintenance) {
        this.popMaintenance = popMaintenance;
    }

    public int getReserve() {
        return this.reserve;
    }

    public void setReserve(int reserve) {
        this.reserve = reserve;
    }

    public int getRevenue() {
        return this.revenue;
    }

    public void setRevenue(int revenue) {
        this.revenue = revenue;
    }

    public int getSurplus() {
        return this.surplus;
    }

    public void setSurplus(int surplus) {
        this.surplus = surplus;
    }

    public int getTaxBase() {
        return this.taxBase;
    }

    public void setTaxBase(int taxBase) {
        this.taxBase = taxBase;
    }

    public int getTaxRate() {
        return this.taxRate;
    }

    public void setTaxRate(int taxRate) {
        this.taxRate = taxRate;
    }

    public int getTotalMaintenance() {
        return this.totalMaintenance;
    }

    public void setTotalMaintenance(int totalMaintenance) {
        this.totalMaintenance = totalMaintenance;
    }
    
    public Integer getProduction(ProductEnum p) {
        return this.production.getProduct(p);
    }

    public Integer getStores(ProductEnum p) {
        return this.stores.getProduct(p);
    }
    
    public void setProduction(ProductEnum p, Integer amount) {
        this.production.setProduct(p, amount);
    }

    public void setStores(ProductEnum p, Integer amount) {
        this.stores.setProduct(p, amount);
    }
    
    public ProductContainer getProduction() {
        return this.production;
    }
    
    public ProductContainer getStores() {
        return this.stores;
    }

    
    public int getGoldProduction() {
        return this.goldProduction;
    }

    
    public void setGoldProduction(int goldProduction) {
        this.goldProduction = goldProduction;
    }

	public int getAvailableGold() {
		return this.availableGold;
	}

	public void setAvailableGold(int availableGold) {
		this.availableGold = availableGold;
	}

	public void addAvailableGold(int amount) {
		setAvailableGold(getAvailableGold() + amount);
	}
    
}
