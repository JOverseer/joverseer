package org.joverseer.domain;

import java.io.Serializable;
import java.util.ArrayList;

import org.joverseer.game.Turn;
import org.joverseer.preferences.PreferenceRegistry;
import org.joverseer.support.Container;
import org.joverseer.support.GameHolder;
import org.joverseer.support.ProductContainer;

/**
 * Stores the economy calculator data for a given nation for this turn.
 * 
 * This class is used to store various information such as the market buys and
 * sells for a give nation, the new tax rate, etc.
 * 
 * @author Marios Skounakis
 * 
 */

public class EconomyCalculatorData implements Serializable, IBelongsToNation {

	private static final long serialVersionUID = 3226500573957331722L;

	Integer nationNo;

	ProductContainer sellUnits = new ProductContainer();
	ProductContainer sellPct = new ProductContainer();
	ProductContainer buyUnits = new ProductContainer();
	ProductContainer bidPrices = new ProductContainer();
	ProductContainer bidUnits = new ProductContainer();

	boolean sellBonus;
	int ordersCost;
	int productionFactor = 100;
	Integer taxRate = null;
	Integer goldProduction;

	public int getBuyUnits(ProductEnum p) {
		return this.buyUnits.getProduct(p) == null ? 0 : this.buyUnits.getProduct(p).intValue();
	}

	public void setBuyUnits(ProductEnum p, Integer amount) {
		this.buyUnits.setProduct(p, amount);
	}

	@Override
	public Integer getNationNo() {
		return this.nationNo;
	}

	@Override
	public void setNationNo(Integer nationNo) {
		this.nationNo = nationNo;
	}

	public int getSellPct(ProductEnum p) {
		return this.sellPct.getProduct(p) == null ? 0 : this.sellPct.getProduct(p).intValue();
	}

	public void setSellPct(ProductEnum p, Integer amount) {
		this.sellPct.setProduct(p, amount);
	}

	public int getSellUnits(ProductEnum p) {
		return this.sellUnits.getProduct(p) == null ? 0 : this.sellUnits.getProduct(p).intValue();
	}

	public void setSellUnits(ProductEnum p, Integer amount) {
		this.sellUnits.setProduct(p, amount);
	}

	public int getBidUnits(ProductEnum p) {
		if (this.bidUnits == null) {
			this.bidUnits = new ProductContainer();
		}
		return this.bidUnits.getProduct(p) == null ? 0 : this.bidUnits.getProduct(p).intValue();
	}

	public void setBidUnits(ProductEnum p, Integer amount) {
		if (this.bidUnits == null) {
			this.bidUnits = new ProductContainer();
		}
		this.bidUnits.setProduct(p, amount);
	}

	public int getBidPrice(ProductEnum p) {
		if (this.bidPrices == null) {
			this.bidPrices = new ProductContainer();
		}
		return this.bidPrices.getProduct(p) == null ? 0 : this.bidPrices.getProduct(p).intValue();
	}

	public void setBidPrice(ProductEnum p, Integer amount) {
		if (this.bidPrices == null) {
			this.bidPrices = new ProductContainer();
		}
		this.bidPrices.setProduct(p, amount);
	}

	public int getOrdersCost() {
		return this.ordersCost;
	}

	public void setOrdersCost(int ordersCost) {
		this.ordersCost = ordersCost;
	}

	public int getProductionFactor() {
		return this.productionFactor;
	}

	public void setProductionFactor(int productionFactor) {
		this.productionFactor = productionFactor;
	}

	protected NationEconomy getNationEconomy() {
		Turn t = GameHolder.instance().getGame().getTurn();
		if (t == null)
			return null;
		NationEconomy ne = t.getNationEconomies().findFirstByProperty("nationNo", getNationNo());
		return ne;
	}

	public int getProduction(ProductEnum p) {
		Turn t = GameHolder.instance().getGame().getTurn();
		if (t == null)
			return 0;
		Container<PopulationCenter> pcs = t.getPopulationCenters();
		ArrayList<PopulationCenter> natpcs = pcs.findAllByProperties(new String[] { "nationNo", "lostThisTurn" }, new Object[] { getNationNo(), true });
		int sum = 0;
		for (PopulationCenter pc : natpcs) {
			if (pc.getProduction(p) != null) {
				sum += pc.getProduction(p).intValue() * getProductionFactor() / 100;
			}
		}
		return getNationEconomy().getProduction(p).intValue() * getProductionFactor() / 100 - sum;
	}

	public int getStores(ProductEnum p) {
		Turn t = GameHolder.instance().getGame().getTurn();
		if (t == null)
			return 0;
		Container<PopulationCenter> pcs = t.getPopulationCenters();
		ArrayList<PopulationCenter> natpcs = pcs.findAllByProperties(new String[] { "nationNo", "lostThisTurn" }, new Object[] { getNationNo(), true });
		int sum = 0;
		for (PopulationCenter pc : natpcs) {
			if (pc.getStores(p) != null) {
				sum += pc.getStores(p).intValue();
			}
		}
		return getNationEconomy().getStores(p).intValue() - sum;
	}

	public int getTotal(ProductEnum p) {
		return getProduction(p) + getStores(p);
	}

	public int getSellPrice(ProductEnum p) {
		Turn t = GameHolder.instance().getGame().getTurn();
		if (t == null)
			return 0;
		Container<ProductPrice> pps = t.getProductPrices();
		ProductPrice pp = pps.findFirstByProperty("product", p);
		return pp.getSellPrice();
	}

	public int getBuyPrice(ProductEnum p) {
		Turn t = GameHolder.instance().getGame().getTurn();
		if (t == null)
			return 0;
		Container<ProductPrice> pps = t.getProductPrices();
		ProductPrice pp = pps.findFirstByProperty("product", p);
		return pp.getBuyPrice();
	}

	public int getMarketTotal(ProductEnum p) {
		Turn t = GameHolder.instance().getGame().getTurn();
		if (t == null)
			return 0;
		Container<ProductPrice> pps = t.getProductPrices();
		ProductPrice pp = pps.findFirstByProperty("product", p);
		return pp.getMarketTotal();
	}

	public int getMarketProfits() {
		int profits = 0;
		Turn t = GameHolder.instance().getGame().getTurn();
		if (t == null)
			return 0;
		for (ProductEnum p : ProductEnum.values()) {
			if (p == ProductEnum.Gold)
				continue;
			int productProfit = getSellUnits(p) * getSellPrice(p) * getSellBonusFactor() / 100 + (getTotal(p) - getSellUnits(p)) * getSellPct(p) / 100 * getSellPrice(p) * getSellBonusFactor() / 100 - getBuyUnits(p) * getBuyPrice(p) * getBuyBonusFactor() / 100 - getBidUnits(p) * getBidPrice(p) * getBuyBonusFactor() / 100;
			profits += productProfit;
		}

		String pval = PreferenceRegistry.instance().getPreferenceValue("general.strictMarketLimit");
		if (pval.equals("yes")) {
			int marketLimit = 20000;
			pval = PreferenceRegistry.instance().getPreferenceValue("general.marketSellLimit");
			try {
				marketLimit = Integer.parseInt(pval);
			} catch (NumberFormatException exc) {
			}

			return marketLimit > profits ? profits : marketLimit;
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
		if (t == null)
			return 0;
		return getSellUnits(p) * getSellPrice(p) * getSellBonusFactor() / 100 + getTotal(p) * getSellPct(p) / 100 * getSellPrice(p) * getSellBonusFactor() / 100 - getBuyUnits(p) * getBuyPrice(p) * getBuyBonusFactor() / 100 - getBidUnits(p) * getBidPrice(p) * getBuyBonusFactor() / 100;
	}

	public boolean getSellBonus() {
		return this.sellBonus;
	}

	public void setSellBonus(boolean sellBonus) {
		this.sellBonus = sellBonus;
	}

	public Integer getGoldProduction() {
		return this.goldProduction;
	}

	public void setGoldProduction(Integer goldProduction) {
		this.goldProduction = goldProduction;
	}

	public Integer getTaxRate() {
		return this.taxRate;
	}

	public void setTaxRate(Integer taxRate) {
		this.taxRate = taxRate;
	}

	public void updateMarketFromOrders() {
		for (ProductEnum p : ProductEnum.values()) {
			setSellPct(p, 0);
			setSellUnits(p, 0);
			setBuyUnits(p, 0);
			setBidUnits(p, 0);
			setBidPrice(p, 0);
		}
		for (Character c : GameHolder.instance().getGame().getTurn().getCharacters().findAllByProperty("nationNo", this.nationNo)) {
			for (int i = 0; i < c.getNumberOfOrders(); i++) {
				if (c.getOrders()[i].isBlank())
					continue;
				Order o = c.getOrders()[i];
				int no = o.getOrderNo();
				if (no != 310 && no != 315 && no != 320 && no != 325)
					continue;
				ProductEnum p = ProductEnum.getFromCode(o.getP0());
				Integer p1 = null;
				try {
					p1 = Integer.parseInt(o.getP1());
				} catch (NumberFormatException exc) {
					// nothing
				}
				if (p == null || p1 == null)
					continue;

				if (no == 310) {
					try {
						setBidPrice(p, Integer.parseInt(o.getP2()));
						setBidUnits(p, p1);
					} catch (NumberFormatException exc) {
						// nothing
					}
				} else if (no == 315) {
					setBuyUnits(p, p1);
				} else if (no == 320) {
					setSellUnits(p, p1);
				} else if (no == 325) {
					setSellPct(p, p1);
				}
			}

		}
	}

	public boolean isInitialized() {
		Turn t = GameHolder.instance().getGame().getTurn();
		if (t == null)
			return false;
		Container<ProductPrice> pps = t.getProductPrices();
		ProductPrice pp = pps.findFirstByProperty("product", ProductEnum.Food);
		if (pp == null)
			return false;
		return true;
	}

}
