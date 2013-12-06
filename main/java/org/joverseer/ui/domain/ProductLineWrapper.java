package org.joverseer.ui.domain;

import org.joverseer.domain.IBelongsToNation;
import org.joverseer.domain.ProductEnum;
import org.joverseer.support.ProductContainer;

/**
 * Wraps information for a product line (for the Economy Calculator Market Table)
 * 
 * @author Marios Skounakis
 */
public class ProductLineWrapper implements IBelongsToNation {
    int idx;
    Integer leather;
    Integer food;
    Integer bronze;
    Integer steel;
    Integer mithril;
    Integer gold;
    Integer timber;
    Integer mounts;
    Integer nationNo;
    String descr;
    
    public ProductLineWrapper() {}
    
    public ProductLineWrapper(ProductContainer pc) {
        setFood(pc.getProduct(ProductEnum.Food));
        setLeather(pc.getProduct(ProductEnum.Leather));
        setMithril(pc.getProduct(ProductEnum.Mithril));
        setMounts(pc.getProduct(ProductEnum.Mounts));
        setBronze(pc.getProduct(ProductEnum.Bronze));
        setSteel(pc.getProduct(ProductEnum.Steel));
        setGold(pc.getProduct(ProductEnum.Gold));
        setTimber(pc.getProduct(ProductEnum.Timber));
    }
    
    public Integer getBronze() {
        return this.bronze;
    }
    
    public void setBronze(Integer bronze) {
        this.bronze = bronze;
    }
    
    public Integer getFood() {
        return this.food;
    }
    
    public void setFood(Integer food) {
        this.food = food;
    }
    
    public Integer getGold() {
        return this.gold;
    }
    
    public void setGold(Integer gold) {
        this.gold = gold;
    }
    
    public Integer getLeather() {
        return this.leather;
    }
    
    public void setLeather(Integer leather) {
        this.leather = leather;
    }
    
    public Integer getMithril() {
        return this.mithril;
    }
    
    public void setMithril(Integer mithril) {
        this.mithril = mithril;
    }
    
    public Integer getMounts() {
        return this.mounts;
    }
    
    public void setMounts(Integer mounts) {
        this.mounts = mounts;
    }
    
    public Integer getSteel() {
        return this.steel;
    }
    
    public void setSteel(Integer steel) {
        this.steel = steel;
    }
    
    public Integer getTimber() {
        return this.timber;
    }
    
    public void setTimber(Integer timber) {
        this.timber = timber;
    }

    
    public String getDescr() {
        return this.descr;
    }

    
    public void setDescr(String descr) {
        this.descr = descr;
    }

    
    @Override
	public Integer getNationNo() {
        return this.nationNo;
    }

    
    @Override
	public void setNationNo(Integer nationNo) {
        this.nationNo = nationNo;
    }

    
    public int getIdx() {
        return this.idx;
    }

    
    public void setIdx(int idx) {
        this.idx = idx;
    }
    
    
}
