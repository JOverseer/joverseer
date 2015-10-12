package org.joverseer.domain;

import java.io.Serializable;

/**
 * Stores the market info (sell/buy price and market total) for a given product 
 * 
 * @author Marios Skounakis
 */

public class ProductPrice implements Serializable{
    private static final long serialVersionUID = 8315948351042885231L;
    ProductEnum product;
    int sellPrice;
    int buyPrice;
    int marketTotal;
    
    
    public int getMarketTotal() {
        return this.marketTotal;
    }

    
    public void setMarketTotal(int marketTotal) {
        this.marketTotal = marketTotal;
    }

    public int getBuyPrice() {
        return this.buyPrice;
    }
    
    public void setBuyPrice(int buyPrice) {
        this.buyPrice = buyPrice;
    }
    
    public ProductEnum getProduct() {
        return this.product;
    }
    
    public void setProduct(ProductEnum product) {
        this.product = product;
    }
    
    public int getSellPrice() {
        return this.sellPrice;
    }
    
    public void setSellPrice(int sellPrice) {
        this.sellPrice = sellPrice;
    }
    
    
}
