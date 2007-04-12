package org.joverseer.ui.domain;

import org.joverseer.domain.IBelongsToNation;
import org.joverseer.domain.ProductEnum;
import org.joverseer.support.ProductContainer;


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
        return bronze;
    }
    
    public void setBronze(Integer bronze) {
        this.bronze = bronze;
    }
    
    public Integer getFood() {
        return food;
    }
    
    public void setFood(Integer food) {
        this.food = food;
    }
    
    public Integer getGold() {
        return gold;
    }
    
    public void setGold(Integer gold) {
        this.gold = gold;
    }
    
    public Integer getLeather() {
        return leather;
    }
    
    public void setLeather(Integer leather) {
        this.leather = leather;
    }
    
    public Integer getMithril() {
        return mithril;
    }
    
    public void setMithril(Integer mithril) {
        this.mithril = mithril;
    }
    
    public Integer getMounts() {
        return mounts;
    }
    
    public void setMounts(Integer mounts) {
        this.mounts = mounts;
    }
    
    public Integer getSteel() {
        return steel;
    }
    
    public void setSteel(Integer steel) {
        this.steel = steel;
    }
    
    public Integer getTimber() {
        return timber;
    }
    
    public void setTimber(Integer timber) {
        this.timber = timber;
    }

    
    public String getDescr() {
        return descr;
    }

    
    public void setDescr(String descr) {
        this.descr = descr;
    }

    
    public Integer getNationNo() {
        return nationNo;
    }

    
    public void setNationNo(Integer nationNo) {
        this.nationNo = nationNo;
    }

    
    public int getIdx() {
        return idx;
    }

    
    public void setIdx(int idx) {
        this.idx = idx;
    }
    
    
}
