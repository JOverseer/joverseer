package org.joverseer.domain;

import java.io.Serializable;


public class ProductPrice implements Serializable{
    ProductEnum product;
    int sellPrice;
    int buyPrice;
    
    public int getBuyPrice() {
        return buyPrice;
    }
    
    public void setBuyPrice(int buyPrice) {
        this.buyPrice = buyPrice;
    }
    
    public ProductEnum getProduct() {
        return product;
    }
    
    public void setProduct(ProductEnum product) {
        this.product = product;
    }
    
    public int getSellPrice() {
        return sellPrice;
    }
    
    public void setSellPrice(int sellPrice) {
        this.sellPrice = sellPrice;
    }
    
    
}
