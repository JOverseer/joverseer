package org.joverseer.domain;

import java.io.Serializable;

import org.joverseer.metadata.domain.Nation;
import org.joverseer.support.NationMap;
import org.joverseer.support.ProductContainer;
import org.joverseer.support.infoSources.InfoSource;

/**
 * Stores information about a population center from the pdf turns
 * 
 * @author Marios Skounakis
 */
public class PopulationCenter implements IBelongsToNation, IHasMapLocation, IMaintenanceCost, IEngineObject, Serializable {

    private static final long serialVersionUID = 5077983571531270227L;
    String name;
    int x;
    int y;

    PopulationCenterSizeEnum size;
    FortificationSizeEnum fortification;
    HarborSizeEnum harbor;

    Integer nationNo;

    boolean capital;
    boolean hidden;

    int loyalty;
    InfoSourceValue loyaltyEstimate;

    InformationSourceEnum informationSource;

    InfoSource infoSource;
    
    int turnSeenOnMap;

    ProductContainer production = new ProductContainer();
    ProductContainer stores = new ProductContainer();

    boolean lostThisTurn = false;
    
    int recruits; //used in Engine
    int foodCapacity; // used in Engine
    boolean improvedThisTurn = false; // used in Engine
    boolean sieged = false; // used in Engine
    
    public PopulationCenter() {
    	 this.harbor = HarborSizeEnum.none;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PopulationCenterSizeEnum getSize() {
        return this.size;
    }

    public void setSize(PopulationCenterSizeEnum size) {
        this.size = size;
    }

    @Override
	public int getX() {
        return this.x;
    }

    public void setX(int x) {
        this.x = x;
    }

    @Override
	public int getY() {
        return this.y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public FortificationSizeEnum getFortification() {
        return this.fortification;
    }

    public void setFortification(FortificationSizeEnum fortification) {
        this.fortification = fortification;
    }

    public Nation getNation() {
        return NationMap.getNationFromNo(getNationNo());
    }

    public void setNation(Nation nation) {
        setNationNo(nation.getNumber());
    }

    @Override
	public Integer getNationNo() {
        return this.nationNo;
    }

    @Override
	public void setNationNo(Integer nationNo) {
        this.nationNo = nationNo;
    }

    public HarborSizeEnum getHarbor() {
    	// this fixes a data corruption when destroying a PC.
    	if (this.harbor == null)
    		return HarborSizeEnum.none;
    	else
    		return this.harbor;
    }

    public void setHarbor(HarborSizeEnum harbor) {
    	// and hopefully this stops the bug above happening.
        this.harbor = (harbor == null) ? HarborSizeEnum.none : harbor;
    }

    public boolean getCapital() {
        return this.capital;
    }

    public void setCapital(boolean capital) {
        this.capital = capital;
    }

    public boolean getHidden() {
        return this.hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public int getLoyalty() {
        return this.loyalty;
    }

    public void setLoyalty(int loyalty) {
        this.loyalty = loyalty;
    }

    public InformationSourceEnum getInformationSource() {
        return this.informationSource;
    }

    public void setInformationSource(InformationSourceEnum informationSource) {
        this.informationSource = informationSource;
    }

    public InfoSource getInfoSource() {
        return this.infoSource;
    }

    public void setInfoSource(InfoSource infoSource) {
        this.infoSource = infoSource;
    }
    
    public int getTurnSeenOnMap() {
    	return this.turnSeenOnMap;
    }
    
    public void setTurnSeenOnMap(int turnNo) {
    	this.turnSeenOnMap = turnNo;
    }

    public int getHexNo() {
        return getX() * 100 + getY();
    }

    public void setHexNo(int hexNo) {
        setX(hexNo / 100);
        setY(hexNo % 100);
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


    public boolean getLostThisTurn() {
        return this.lostThisTurn;
    }


    public void setLostThisTurn(boolean lostThisTurn) {
        this.lostThisTurn = lostThisTurn;
    }


    public InfoSourceValue getLoyaltyEstimate() {
        return this.loyaltyEstimate;
    }

    public void setLoyaltyEstimate(InfoSourceValue loyaltyEstimate) {
        this.loyaltyEstimate = loyaltyEstimate;
    }

    @Override
	public PopulationCenter clone() {
        PopulationCenter newPc = new PopulationCenter();
        newPc.setName(getName());
        newPc.setCapital(getCapital());
        newPc.setFortification(getFortification());
        newPc.setHarbor(getHarbor());
        newPc.setHidden(getHidden());
        newPc.setNationNo(getNationNo());
        newPc.setLoyalty(getLoyalty());
        newPc.setSize(getSize());
        newPc.setX(getX());
        newPc.setY(getY());

        // TODO BUG BUG
        // this is a bug, info source should be cloned too!
        newPc.setInfoSource(getInfoSource());
        newPc.setInformationSource(getInformationSource());

        for (ProductEnum p : ProductEnum.values()) {
            newPc.setProduction(p, getProduction(p));
            newPc.setStores(p, getStores(p));
        }
        return newPc;
    }

	@Override
	public Integer getMaintenance() {
		if (getSize().equals(PopulationCenterSizeEnum.ruins)) return 0;
		int cost = 0;
		if (getHarbor().equals(HarborSizeEnum.harbor)) {
			cost += 250;
		} else if (getHarbor().equals(HarborSizeEnum.port)) {
			cost += 500;
		} 
		cost += getFortification().getSize() * 500;
		return cost;
	}

	public int getRecruits() {
		return this.recruits;
	}

	public void setRecruits(int recruits) {
		this.recruits = recruits;
	}

	public int getFoodCapacity() {
		if (this.foodCapacity==0) {
			int foodCapacity1 = this.lookupSize(new int[]{0, 100, 200, 1000, 2500, 5000}); 
			setFoodCapacity(foodCapacity1);
		}
		return this.foodCapacity;
	}

	public void setFoodCapacity(int foodCapacity) {
		this.foodCapacity = foodCapacity;
	}

	public boolean isImprovedThisTurn() {
		return this.improvedThisTurn;
	}

	public void setImprovedThisTurn(boolean improvedThisTurn) {
		this.improvedThisTurn = improvedThisTurn;
	}
	
	


	public boolean isSieged() {
		return this.sieged;
	}

	public void setSieged(boolean sieged) {
		this.sieged = sieged;
	}

	@Override
	public void initialize() {
		int recruits1 = getSize().getCode() * 100;
		setRecruits(recruits1);
		int foodCapacity1 = this.lookupSize(new int[]{0, 100, 200, 1000, 2500, 5000}); 
		setFoodCapacity(foodCapacity1);
		setImprovedThisTurn(false); // TODO move to dif method
		setSieged(false);
	}
	/**
	 * convert the population centre size to the matching number from an array.
	 * @param lookup the 6 element array, index 0 matches camp
	 * @return
	 */
    public int lookupSize(int[] lookup) {
    	return PopulationCenterSizeEnum.lookupSize(this.size, lookup);
    }
    public void defaultName() {
		if (this.name == null || this.name.equals("")) {
			this.setName(org.joverseer.ui.support.GraphicUtils.UNKNOWN_ARMY_MAP_ICON);
		}
    }
    public boolean isDefaultName() {
    	return this.name.equals(org.joverseer.ui.support.GraphicUtils.UNKNOWN_ARMY_MAP_ICON);
    }
    public void checkForCapital(int tiNationNo,int nationCapitalHex) {
		if (this.getNationNo() == tiNationNo) {
			this.setCapital(this.getHexNo() == nationCapitalHex);
		}
    }
    // used to make an educated guess that the data is from an incomplete PC report.
    public boolean isUnlikelyToBeComplete() {
    	return (this.getLoyalty() == 0) && (this.getNationNo() == 0);
    }
    public void copyProduction(PopulationCenter from) {
		for (ProductEnum p : ProductEnum.values()) {
			this.setProduction(p, from.getProduction(p));
			this.setStores(p, from.getStores(p));
		}
    }
}
