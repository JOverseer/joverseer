package org.joverseer.ui.domain;

import org.joverseer.domain.IBelongsToNation;
import org.joverseer.domain.IHasMapLocation;
import org.joverseer.domain.ProductEnum;

/**
 * Wraps data for the nation economy potential list view
 * 
 * @author Marios Skounakis
 */
public class NationEconomyPotential implements IBelongsToNation, IHasMapLocation {
    Integer nationNo;
    int hexNo;
    int surplus;
    int reserve;
    int total;
    int oneNatSell;
    int twoNatSells;
    int charsInCapital;
    ProductEnum oneNatSellProduct;
    ProductEnum twoNatSellProduct;
    
    public NationEconomyPotential() {};
    
    public int getCharsInCapital() {
        return this.charsInCapital;
    }
    
    public void setCharsInCapital(int charsInCapital) {
        this.charsInCapital = charsInCapital;
    }
    
    @Override
	public Integer getNationNo() {
        return this.nationNo;
    }
    
    @Override
	public void setNationNo(Integer nationNo) {
        this.nationNo = nationNo;
    }
    
    public int getOneNatSell() {
        return this.oneNatSell;
    }
    
    public void setOneNatSell(int oneNatSell) {
        this.oneNatSell = oneNatSell;
    }
    
    public int getReserve() {
        return this.reserve;
    }
    
    public void setReserve(int reserve) {
        this.reserve = reserve;
    }
    
    public int getSurplus() {
        return this.surplus;
    }
    
    public void setSurplus(int surplus) {
        this.surplus = surplus;
    }
    
    public int getTotal() {
        return this.total;
    }
    
    public void setTotal(int total) {
        this.total = total;
    }
    
    public int getTwoNatSells() {
        return this.twoNatSells;
    }
    
    public void setTwoNatSells(int twoNatSells) {
        this.twoNatSells = twoNatSells;
    }

    
    public int getHexNo() {
        return this.hexNo;
    }

    
    public void setHexNo(int hexNo) {
        this.hexNo = hexNo;
    }

    @Override
	public int getX() {
        return getHexNo() / 100;
    }

    @Override
	public int getY() {
        return getHexNo() % 100;
    }

    
    public ProductEnum getOneNatSellProduct() {
        return this.oneNatSellProduct;
    }

    
    public void setOneNatSellProduct(ProductEnum oneNatSellProduct) {
        this.oneNatSellProduct = oneNatSellProduct;
    }

    
    public ProductEnum getTwoNatSellProduct() {
        return this.twoNatSellProduct;
    }

    
    public void setTwoNatSellProduct(ProductEnum twoNatSellProduct) {
        this.twoNatSellProduct = twoNatSellProduct;
    }
    
    
}
