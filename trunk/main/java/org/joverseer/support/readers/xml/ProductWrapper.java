package org.joverseer.support.readers.xml;


public class ProductWrapper {
    String type;
    int buyPrice;
    int sellPrice;
    int marketAvail;
    int nationStores;
    int nationProduction;

    public int getBuyPrice() {
        return buyPrice;
    }

    public void setBuyPrice(int buyPrice) {
        this.buyPrice = buyPrice;
    }

    public int getMarketAvail() {
        return marketAvail;
    }

    public void setMarketAvail(int marketAvail) {
        this.marketAvail = marketAvail;
    }

    public int getNationProduction() {
        return nationProduction;
    }

    public void setNationProduction(int nationProduction) {
        this.nationProduction = nationProduction;
    }

    public int getNationStores() {
        return nationStores;
    }

    public void setNationStores(int nationStores) {
        this.nationStores = nationStores;
    }

    public int getSellPrice() {
        return sellPrice;
    }

    public void setSellPrice(int sellPrice) {
        this.sellPrice = sellPrice;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
