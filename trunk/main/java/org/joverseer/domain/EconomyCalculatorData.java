package org.joverseer.domain;

import java.io.Serializable;
import java.util.ArrayList;

import org.joverseer.game.Turn;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.support.Container;
import org.joverseer.support.GameHolder;
import org.joverseer.support.ProductContainer;


public class EconomyCalculatorData implements Serializable, IBelongsToNation {

    /**
     * 
     */
    private static final long serialVersionUID = 3226500573957331722L;

    Integer nationNo;

    ProductContainer sellUnits = new ProductContainer();
    ProductContainer sellPct = new ProductContainer();
    ProductContainer buyUnits = new ProductContainer();

    boolean sellBonus;
    int ordersCost;
    int productionFactor = 100;
    Integer goldProduction;

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
        if (t == null) return null;
        Container nes = t.getContainer(TurnElementsEnum.NationEconomy);
        NationEconomy ne = (NationEconomy) nes.findFirstByProperty("nationNo", getNationNo());
        return ne;
    }

    public int getProduction(ProductEnum p) {
        Turn t = GameHolder.instance().getGame().getTurn();
        if (t == null) return 0;
        Container pcs = t.getContainer(TurnElementsEnum.PopulationCenter);
        ArrayList<PopulationCenter> natpcs = (ArrayList<PopulationCenter>) pcs.findAllByProperties(new String[] {
                "nationNo", "lostThisTurn"}, new Object[] {getNationNo(), true});
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
        if (t == null) return 0;
        Container pcs = t.getContainer(TurnElementsEnum.PopulationCenter);
        ArrayList<PopulationCenter> natpcs = (ArrayList<PopulationCenter>) pcs.findAllByProperties(new String[] {
                "nationNo", "lostThisTurn"}, new Object[] {getNationNo(), true});
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
        if (t == null) return 0;
        Container pps = t.getContainer(TurnElementsEnum.ProductPrice);
        ProductPrice pp = (ProductPrice) pps.findFirstByProperty("product", p);
        return pp.getSellPrice();
    }

    public int getBuyPrice(ProductEnum p) {
        Turn t = GameHolder.instance().getGame().getTurn();
        if (t == null) return 0;
        Container pps = t.getContainer(TurnElementsEnum.ProductPrice);
        ProductPrice pp = (ProductPrice) pps.findFirstByProperty("product", p);
        return pp.getBuyPrice();
    }

    public int getMarketTotal(ProductEnum p) {
        Turn t = GameHolder.instance().getGame().getTurn();
        if (t == null) return 0;
        Container pps = t.getContainer(TurnElementsEnum.ProductPrice);
        ProductPrice pp = (ProductPrice) pps.findFirstByProperty("product", p);
        return pp.getMarketTotal();
    }

    public int getMarketProfits() {
        int profits = 0;
        Turn t = GameHolder.instance().getGame().getTurn();
        if (t == null) return 0;
        for (ProductEnum p : ProductEnum.values()) {
            if (p == ProductEnum.Gold)
                continue;
            int productProfit = getSellUnits(p) * getSellPrice(p) * getSellBonusFactor() / 100
                    + (getTotal(p) - getSellUnits(p)) * getSellPct(p) / 100 * getSellPrice(p) * getSellBonusFactor()
                    / 100 - getBuyUnits(p) * getBuyPrice(p) * getBuyBonusFactor() / 100;
            profits += productProfit;
        }
        return profits;
    }

    private int getSellBonusFactor() {
        return getSellBonus() ? 120 : 100;
    }

    private int getBuyBonusFactor() {
        return getSellBonus() ? 80 : 100;
    }

    public int getMarketProfits(ProductEnum p) {
    	Turn t = GameHolder.instance().getGame().getTurn();
        if (t == null) return 0;
    	return getSellUnits(p) * getSellPrice(p) * getSellBonusFactor() / 100 + getTotal(p) * getSellPct(p) / 100
                * getSellPrice(p) * getSellBonusFactor() / 100 - getBuyUnits(p) * getBuyPrice(p) * getBuyBonusFactor()
                / 100;
    }


    public boolean getSellBonus() {
        return sellBonus;
    }


    public void setSellBonus(boolean sellBonus) {
        this.sellBonus = sellBonus;
    }


    public Integer getGoldProduction() {
        return goldProduction;
    }


    public void setGoldProduction(Integer goldProduction) {
        this.goldProduction = goldProduction;
    }
    
    public boolean isInitialized() {
    	Turn t = GameHolder.instance().getGame().getTurn();
        if (t == null) return false;
        Container pps = t.getContainer(TurnElementsEnum.ProductPrice);
        ProductPrice pp = (ProductPrice) pps.findFirstByProperty("product", ProductEnum.Food);
        if (pp == null) return false;
        return true;
    }
}
