package org.joverseer.ui.listviews;

import org.joverseer.domain.ClimateEnum;
import org.joverseer.domain.HexInfo;
import org.joverseer.domain.PopulationCenter;
import org.joverseer.domain.PopulationCenterSizeEnum;
import org.joverseer.domain.ProductEnum;
import org.joverseer.game.Game;
import org.joverseer.game.Turn;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.metadata.domain.Hex;
import org.joverseer.metadata.domain.HexTerrainEnum;

public class HexProductionWrapper {
	Integer hexNo;
	HexTerrainEnum terrain;
	ClimateEnum climate;
	int leather;
	int bronze;
	int steel;
	int mithril;
	int food;
	int timber;
	int mounts;
	int gold;
	
	int count = 0;
	
	public int getBronze() {
		return this.bronze;
	}



	public void setBronze(int bronze) {
		this.bronze = bronze;
	}



	public ClimateEnum getClimate() {
		return this.climate;
	}



	public void setClimate(ClimateEnum climate) {
		this.climate = climate;
	}



	public int getFood() {
		return this.food;
	}



	public void setFood(int food) {
		this.food = food;
	}



	public int getGold() {
		return this.gold;
	}



	public void setGold(int gold) {
		this.gold = gold;
	}



	public Integer getHexNo() {
		return this.hexNo;
	}



	public void setHexNo(Integer hexNo) {
		this.hexNo = hexNo;
	}



	public int getLeather() {
		return this.leather;
	}



	public void setLeather(int leather) {
		this.leather = leather;
	}



	public int getMithril() {
		return this.mithril;
	}



	public void setMithril(int mithril) {
		this.mithril = mithril;
	}



	public int getMounts() {
		return this.mounts;
	}



	public void setMounts(int mounts) {
		this.mounts = mounts;
	}



	public int getSteel() {
		return this.steel;
	}



	public void setSteel(int steel) {
		this.steel = steel;
	}



	public HexTerrainEnum getTerrain() {
		return this.terrain;
	}



	public void setTerrain(HexTerrainEnum terrain) {
		this.terrain = terrain;
	}



	public int getTimber() {
		return this.timber;
	}



	public void setTimber(int timber) {
		this.timber = timber;
	}


	public HexProductionWrapper() {
		
	}

	public HexProductionWrapper(PopulationCenter pc, Game game, Turn turn) {
		setHexNo(pc.getHexNo());
		Hex h = (Hex)game.getMetadata().getHex(pc.getHexNo());
		if (h != null) setTerrain(h.getTerrain());
		HexInfo hi = (HexInfo)turn.getContainer(TurnElementsEnum.HexInfo).findFirstByProperty("hexNo", pc.getHexNo());
		if (hi != null) setClimate(hi.getClimate());
		
		int factor = 100;
		if (pc.getSize() == PopulationCenterSizeEnum.village) {
			factor = 80;
		} else if (pc.getSize() == PopulationCenterSizeEnum.town) {
			factor = 60;
		} else if (pc.getSize() == PopulationCenterSizeEnum.majorTown) {
			factor = 40;
		} else if (pc.getSize() == PopulationCenterSizeEnum.city) {
			factor = 29;
		} 
		
		setLeather(pc.getProduction(ProductEnum.Leather) * 100 / factor);
		setBronze(pc.getProduction(ProductEnum.Bronze) * 100 / factor);
		setSteel(pc.getProduction(ProductEnum.Steel) * 100 / factor);
		setFood(pc.getProduction(ProductEnum.Food) * 100 / factor);
		setTimber(pc.getProduction(ProductEnum.Timber) * 100 / factor);
		setMounts(pc.getProduction(ProductEnum.Mounts) * 100 / factor);
		setGold(pc.getProduction(ProductEnum.Gold) * 100 / factor);
		setMithril(pc.getProduction(ProductEnum.Mithril) * 100 / factor);
	}
	
	public void add(HexProductionWrapper pw) {
		setLeather(getLeather() + pw.getLeather());
		setBronze(getBronze() + pw.getBronze());
		setSteel(getSteel() + pw.getSteel());
		setFood(getFood() + pw.getFood());
		setTimber(getTimber() + pw.getTimber());
		setMounts(getMounts() + pw.getMounts());
		setMithril(getMithril() + pw.getMithril());
		setGold(getGold() + pw.getGold());
		this.count++;
	}

	public void divideByCount() {
		setLeather(getLeather() / this.count);
		setBronze(getBronze() / this.count);
		setSteel(getSteel() / this.count);
		setFood(getFood() / this.count);
		setTimber(getTimber() / this.count);
		setMounts(getMounts() / this.count);
		setMithril(getMithril() / this.count);
		setGold(getGold() / this.count);
	}

}
