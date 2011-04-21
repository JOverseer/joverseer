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

	public int getBidUnits(ProductEnum p) {
		if (bidUnits == null) {
			bidUnits = new ProductContainer();
		}
		return bidUnits.getProduct(p) == null ? 0 : bidUnits.getProduct(p);
	}

	public void setBidUnits(ProductEnum p, Integer amount) {
		if (bidUnits == null) {
			bidUnits = new ProductContainer();
		}
		this.bidUnits.setProduct(p, amount);
	}

	public int getBidPrice(ProductEnum p) {
		if (bidPrices == null) {
			bidPrices = new ProductContainer();
		}
		return bidPrices.getProduct(p) == null ? 0 : bidPrices.getProduct(p);
	}

	public void setBidPrice(ProductEnum p, Integer amount) {
		if (bidPrices == null) {
			bidPrices = new ProductContainer();
		}
		this.bidPrices.setProduct(p, amount);
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
				sum += pc.getProduction(p) * getProductionFactor() / 100;
			}
		}
		return getNationEconomy().getProduction(p) * getProductionFactor() / 100 - sum;
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
			} catch (Exception exc) {
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

	public Integer getTaxRate() {
		return taxRate;
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
		for (Character c : GameHolder.instance().getGame().getTurn().getCharacters().findAllByProperty("nationNo", nationNo)) {
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
				} catch (Exception exc) {
					// nothing
				}
				if (p == null || p1 == null)
					continue;

				if (no == 310) {
					try {
						setBidPrice(p, Integer.parseInt(o.getP2()));
						setBidUnits(p, p1);
					} catch (Exception exc) {
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
