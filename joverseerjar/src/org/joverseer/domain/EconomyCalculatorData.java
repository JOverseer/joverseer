package org.joverseer.domain;

import java.io.Serializable;
import java.util.ArrayList;

import org.joverseer.game.Turn;
import org.joverseer.game.TurnElementsEnum;
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
	int sellBonusAmount = 20;
	int ordersCost;
	int productionFactor = 100;
	Integer taxRate = null;
	Integer goldProduction;
	Integer taxRevenue;
	
	public Integer getTaxRevenue() {
		if(this.taxRevenue == null) {
			int nationTaxRevenue = 0;
            for (PopulationCenter pc : (ArrayList<PopulationCenter>)this.getTurn().getContainer(TurnElementsEnum.PopulationCenter).findAllByProperty("nationNo", this.getNationNo().intValue())) {
                nationTaxRevenue += (pc.getSize().getCode() - 1) * 2500 * this.getNationEconomy().getTaxRate() / 100;
            }
            this.taxRevenue = nationTaxRevenue;
		}
		return this.taxRevenue;
	}

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
		Turn t = getTurn();
		if (t == null)
			return null;
		NationEconomy ne = t.getNationEconomies().findFirstByProperty("nationNo", getNationNo());
		return ne;
	}

	public int getProduction(ProductEnum p) {
		Turn t = getTurn();
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
		NationEconomy ne = getNationEconomy();
		if (ne == null) {
			return 0;
		}
		return ne.getProduction(p).intValue() * getProductionFactor() / 100 - sum;
	}

	public int getStores(ProductEnum p) {
		Turn t = getTurn();
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
		NationEconomy ne = getNationEconomy();
		if (ne == null) {
			return 0;
		}
		return getNationEconomy().getStores(p).intValue() - sum;
	}

	public int getTotal(ProductEnum p) {
		return getProduction(p) + getStores(p);
	}

	public int getSellPrice(ProductEnum p) {
		Turn t = getTurn();
		if (t == null)
			return 0;
		Container<ProductPrice> pps = t.getProductPrices();
		ProductPrice pp = pps.findFirstByProperty("product", p);
		if (pp==null) {
			return 0;
		}
		return pp.getSellPrice();
	}

	public int getBuyPrice(ProductEnum p) {
		Turn t = getTurn();
		if (t == null)
			return 0;
		Container<ProductPrice> pps = t.getProductPrices();
		ProductPrice pp = pps.findFirstByProperty("product", p);
		return pp.getBuyPrice();
	}

	public int getMarketTotal(ProductEnum p) {
		Turn t = getTurn();
		if (t == null)
			return 0;
		Container<ProductPrice> pps = t.getProductPrices();
		ProductPrice pp = pps.findFirstByProperty("product", p);
		return pp.getMarketTotal();
	}

	public int getMarketSales() {
		long sales=0;
		Turn t = getTurn();
		if (t == null)
			return 0;
		for (ProductEnum p : ProductEnum.values()) {
			if (p == ProductEnum.Gold)
				continue;
			long productSale = ( get100xProductSales(p) + ((getTotal(p) - getSellUnits(p)) * get100xProductSaleCommission(p)) )/100;
			sales += productSale;
		}
		String pval = getPreferenceValue("general.strictMarketLimit");
		if (pval.equals("yes")) {
			int marketLimit = 20000;
			pval = getPreferenceValue("general.marketSellLimit");
			try {
				marketLimit = Integer.parseInt(pval);
			} catch (NumberFormatException exc) {
			}

			return (int) (marketLimit > sales ? sales : marketLimit);
		}
		return (int)sales;
	}
	public int getMarketSpend() {
		long spend=0;
		Turn t = getTurn();
		if (t == null)
			return 0;
		for (ProductEnum p : ProductEnum.values()) {
			if (p == ProductEnum.Gold)
				continue;
			long productSpend = ( get100xProductPurchases(p)/100 + get100xProductBidPurchase(p) );
			spend += productSpend;
		}
		return (int)-spend;
	}
	public int getMarketProfits() {
		Turn t = getTurn();
		if (t == null)
			return 0;
		return getMarketSales() + getMarketSpend();
	}

	// note 100x to avoid early rounding.
	protected long get100xProductSales(ProductEnum p)
	{
		return getSellUnits(p) * getSellPrice(p) * getSellBonusFactor();
	}
	protected long get100xProductPurchases(ProductEnum p)
	{
		return getBuyUnits(p) * getBuyPrice(p) * getBuyBonusFactor();
	}
	protected long get100xProductBidPurchase(ProductEnum p)
	{
		return getBidUnits(p) * getBidPrice(p);
	}
	protected long get100xProductSaleCommission(ProductEnum p)
	{
		return  getSellPrice(p) * getSellBonusFactor() * getSellPct(p)/ 100;
	}
	public int getSellBonusFactor() {
		return getSellBonus() ? getSellBonusAmount() + 100 : 100;
	}

	public int getBuyBonusFactor() {
		return getSellBonus() ? 100 - getSellBonusAmount() : 100;
	}

	public int getMarketProfits(ProductEnum p) {
		Turn t = getTurn();
		if (t == null)
			return 0;
		return getSellUnits(p) * getSellPrice(p) * getSellBonusFactor() / 100 + getTotal(p) * getSellPct(p) / 100 * getSellPrice(p) * getSellBonusFactor() / 100 - getBuyUnits(p) * getBuyPrice(p) * getBuyBonusFactor() / 100 - getBidUnits(p) * getBidPrice(p) /*/ 100*/;
	}
	
	public void setSellBonusAmount(int sellBonusAmount) {
		this.sellBonusAmount = sellBonusAmount;
	}
	
	public int getSellBonusAmount() {
		return this.sellBonusAmount;
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

	private void recordBidPrice(ProductEnum p, Integer amount) {
		if (amount > this.getBidPrice(p)) {
			this.setBidPrice(p, amount);
		}
	}
	private void recordBidUnits(ProductEnum p, Integer amount) {
		this.setBidUnits(p, getBidUnits(p) + amount);
	}
	private void recordBuyUnits(ProductEnum p, Integer amount) {
		this.setBuyUnits(p, getBuyUnits(p) + amount);
	}
	private void recordSellUnits(ProductEnum p, Integer amount) {
		this.setSellUnits(p, getSellUnits(p) + amount);
	}
	private void recordSellPct(ProductEnum p, Integer amount) {
		int residual = 100 - this.getSellPct(p);
		if (residual < 0) {
			setSellPct(p,100);
		} else {
			setSellPct(p,this.getSellPct(p) + (residual * amount /100));
		}
	}
	public void updateMarketFromOrders() {
		for (ProductEnum p : ProductEnum.values()) {
			setSellPct(p, 0);
			setSellUnits(p, 0);
			setBuyUnits(p, 0);
			setBidUnits(p, 0);
			setBidPrice(p, 0);
		}
		for (Character c : getTurn().getCharacters().findAllByProperty("nationNo", this.nationNo)) {
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
						recordBidPrice(p, Integer.parseInt(o.getP2()));
						recordBidUnits(p, p1);
					} catch (NumberFormatException exc) {
						// nothing
					}
				} else if (no == 315) {
					recordBuyUnits(p, p1);
				} else if (no == 320) {
					recordSellUnits(p, p1);
				} else if (no == 325) {
					recordSellPct(p, p1);
				}
			}
		}
	}

	public boolean isInitialized() {
		Turn t = getTurn();
		if (t == null)
			return false;
		Container<ProductPrice> pps = t.getProductPrices();
		ProductPrice pp = pps.findFirstByProperty("product", ProductEnum.Food);
		if (pp == null)
			return false;
		return true;
	}
	private Turn getTurn() {
		return GameHolder.instance().getGame().getTurn();
	}
	private String getPreferenceValue(String value) {
		return PreferenceRegistry.instance().getPreferenceValue(value);
	}


}
