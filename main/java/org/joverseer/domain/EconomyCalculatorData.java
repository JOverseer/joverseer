package org.joverseer.domain;

import java.io.Serializable;
import java.util.ArrayList;

import org.joverseer.game.Turn;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.support.Container;
import org.joverseer.support.GameHolder;
import org.joverseer.support.ProductContainer;


public class EconomyCalculatorData implements Serializable, IBelongsToNation {
    Integer nationNo;
    
    ProductContainer sellUnits = new ProductContainer();
    ProductContainer sellPct = new ProductContainer();
    ProductContainer buyUnits = new ProductContainer();
    
    int ordersCost;
    int productionFactor = 100;
    
    public int getBuyUnits(ProductEnum p) {
        return buyUnits.getProduct(p) == null ? 0 : buyUnits.getProduct(p);
    }
    
    public void setBuyUnits(ProductEnum p, Integer amount) {
        this.buyUnits.setProduct(p, amount);
    }
    
    public Integer getNationNo() {
        return nationNo;
    }
    
    public void setNationNo(Integer nationNo) {
        this.nationNo = nationNo;
    }
    
    public int getSellPct(ProductEnum p) {
        return sellPct.getProduct(p) == null ? 0 : sellPct.getProduct(p);
    }
    
    public void setSellPct(ProductEnum p, Integer amount) {
        this.sellPct.setProduct(p, amount);
    }
    
    public int getSellUnits(ProductEnum p) {
        return sellUnits.getProduct(p) == null ? 0 : sellUnits.getProduct(p);
    }
    
    public void setSellUnits(ProductEnum p, Integer amount) {
        this.sellUnits.setProduct(p, amount);
    }

    
    public int getOrdersCost() {
        return ordersCost;
    }

    
    public void setOrdersCost(int ordersCost) {
        this.ordersCost = ordersCost;
    }
    
    public int getProductionFactor() {
        return productionFactor;
    }

    
    public void setProductionFactor(int productionFactor) {
        this.productionFactor = productionFactor;
    }
    
    protected NationEconomy getNationEconomy() {
        Turn t = GameHolder.instance().getGame().getTurn();
        Container nes = t.getContainer(TurnElementsEnum.NationEconomy);
        NationEconomy ne = (NationEconomy) nes.findFirstByProperty("nationNo", getNationNo());
        return ne;
    }
    
    public int getProduction(ProductEnum p) {
        Turn t = GameHolder.instance().getGame().getTurn();
        Container pcs = t.getContainer(TurnElementsEnum.PopulationCenter);
        ArrayList<PopulationCenter> natpcs = (ArrayList<PopulationCenter>)pcs.findAllByProperties(new String[]{"nationNo", "lostThisTurn"}, new Object[]{getNationNo(), true});
        int sum = 0;
        for (PopulationCenter pc : natpcs) {
            if (pc.getProduction(p) != null) {
                sum += pc.getProduction(p) * getProductionFactor() / 100;
            }
        }
        return getNationEconomy().getProduction(p) * getProductionFactor() / 100 - sum;
    }

    public int getStores(ProductEnum p) {
        Turn t = GameHolder.instance().getGame().getTurn();
        Container pcs = t.getContainer(TurnElementsEnum.PopulationCenter);
        ArrayList<PopulationCenter> natpcs = (ArrayList<PopulationCenter>)pcs.findAllByProperties(new String[]{"nationNo", "lostThisTurn"}, new Object[]{getNationNo(), true});
        int sum = 0;
        for (PopulationCenter pc : natpcs) {
            if (pc.getStores(p) != null) {
                sum += pc.getStores(p);
            }
        }
        return getNationEconomy().getStores(p) - sum;
    }

    public int getTotal(ProductEnum p) {
        return getProduction(p) + getStores(p);
    }
    
    public int getSellPrice(ProductEnum p) {
        Turn t = GameHolder.instance().getGame().getTurn();
        Container pps = t.getContainer(TurnElementsEnum.ProductPrice);
        ProductPrice pp = (ProductPrice) pps.findFirstByProperty("product", p);
        return pp.getSellPrice();
    }

    public int getBuyPrice(ProductEnum p) {
        Turn t = GameHolder.instance().getGame().getTurn();
        Container pps = t.getContainer(TurnElementsEnum.ProductPrice);
        ProductPrice pp = (ProductPrice) pps.findFirstByProperty("product", p);
        return pp.getBuyPrice();
    }

    public int getMarketProfits() {
        int profits = 0;
        
        for (ProductEnum p : ProductEnum.values()) {
            if (p == ProductEnum.Gold) continue;
            int productProfit = getSellUnits(p) * getSellPrice(p) +
                                getTotal(p) * getSellPct(p) / 100 * getSellPrice(p) -
                                getBuyUnits(p) * getBuyPrice(p);
            profits += productProfit;
        }
        return profits;
    }
    
    public int getMarketProfits(ProductEnum p) {
        return getSellUnits(p) * getSellPrice(p) +
                getTotal(p) * getSellPct(p) / 100 * getSellPrice(p) -
                getBuyUnits(p) * getBuyPrice(p);
    }
}
