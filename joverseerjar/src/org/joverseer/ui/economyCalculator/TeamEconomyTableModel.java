package org.joverseer.ui.economyCalculator;

import java.util.ArrayList;
import java.util.List;

import org.joverseer.domain.CharacterDeathReasonEnum;
import org.joverseer.domain.EconomyCalculatorData;
import org.joverseer.domain.NationEconomy;
import org.joverseer.domain.PopulationCenter;
import org.joverseer.domain.ProductEnum;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.metadata.domain.Nation;
import org.joverseer.support.GameHolder;
import org.joverseer.ui.domain.NationStatisticsWrapper;
import org.joverseer.ui.listviews.NationStatisticsTableModel;
import org.joverseer.ui.support.Messages;

/**
 * Table model for the Team Economy main table It basically shows a complete
 * economic picture for all the imported nations in tabular format
 *
 * @author Marios Skounakis
 */
@SuppressWarnings("serial")
public class TeamEconomyTableModel extends BaseEconomyTableModel {

	public static final int iProdStart = 1;
	public static final int iProdEnd = 7;
	public static final int iCaptialChars = 8;
	public static final int iReserves = 9;
	public static final int iSurplus = 10;
	public static final int iMarketSpending = 11;
	public static final int iMarketSales = 12;
	public static final int iLosses = 13;
	public static final int iOrders = 14;
	public static final int iFinalGold = 15;
	public static final int iHikedTaxRate = 16;
	public static final int iTaxRate = 17;
	public static final int iTaxBase = 18;

	// used to grab the tax base. needs to injected.
	NationStatisticsTableModel nswm = null;

	SummaryTypeEnum showProductsAs = SummaryTypeEnum.Total;

	//note: change code in the constructor if you change this.
	String[] columnHeaderTags = new String[] { "nation", "le", "br", "st", "mi", "fo", "ti", "mo", "cptChars", "reserves", "surplus","marketSpending","marketSales","losses","orders","finalGold","hikedTaxRate", "taxRate", "taxBase"};
	String[] columnNames;

	int[] columnWidths = new int[] { 42, 42, 42, 42, 42, 42, 42, 42, 58, 54, 54, 74, 74, 54, 42, 64, 64, 48, 60  };

	Class<?>[] classes = new Class[] { String.class, Integer.class, Integer.class, Integer.class,  Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class };

	ArrayList<EconomyCalculatorData> items = new ArrayList<EconomyCalculatorData>();

	public TeamEconomyTableModel(GameHolder gameHolder)
	{
		super(gameHolder);
		this.columnNames = new String[this.columnHeaderTags.length];
		this.columnNames[0]=Messages.getString("standardFields.Nation");
		for (int i=iProdStart;i<=iProdEnd;i++) {
			this.columnNames[i] = Messages.getString("ProductEnum."+this.columnHeaderTags[i]);
		}
		for (int i=iProdEnd+1;i<this.columnHeaderTags.length;i++) {
			this.columnNames[i] = Messages.getString("TeamEconomy."+this.columnHeaderTags[i]);
		}
	}
	@Override
	public int getColumnCount() {
		return this.columnNames.length;
	}

	@Override
	public String getColumnName(int column) {
		return this.columnNames[column];
	}

	public int getColumnWidth(int column) {
		return this.columnWidths[column];
	}

	@Override
	public Class<?> getColumnClass(int column) {
		return this.classes[column];
	}

	@Override
	public int getRowCount() {
		return this.items.size();
	}

	public void setRows(List<EconomyCalculatorData> items) {
		this.items.clear();
		this.items.addAll(items);
	}

	protected EconomyCalculatorData getEconomyCalculatorData(int row) {
		return this.items.get(row);
	}

	protected NationEconomy getNationEconomy(int nationNo1) {
		return (NationEconomy) this.gameHolder.getGame().getTurn().getContainer(TurnElementsEnum.NationEconomy).findFirstByProperty("nationNo", nationNo1);
	}

	public SummaryTypeEnum getShowProductsAs() {
		return this.showProductsAs;
	}

	public void setShowProductsAs(SummaryTypeEnum showProductsAs) {
		this.showProductsAs = showProductsAs;
	}

	protected int getProduct(EconomyCalculatorData ecd, ProductEnum p) {
		switch (this.showProductsAs) {
			case Total:
				return ecd.getTotal(p);
			case Gain:
				return ecd.getTotal(p) * ecd.getSellPrice(p);
			case Stores:
				return ecd.getStores(p);
			case Production:
				return ecd.getProduction(p);
			default:
				return ecd.getTotal(p);
		}
	}

	@Override
	public Object getValueAt(int row, int col) {
		if (row == getRowCount() - 1) {
			// totals
			if (col == 0) {
				return "total";
			}
			int sum = 0;
			for (int i = 0; i < getRowCount() - 1; i++) {
				Object v = getValueAt(i, col);
				if (Integer.class.isInstance(v)) {
					sum += (Integer) v;
				}
			}
			if (col == iTaxRate)
				sum = sum / Math.max((getRowCount() - 1), 1);
			return sum;
		}
		EconomyCalculatorData ecd = getEconomyCalculatorData(row);
		if (ecd == null)
			return null;
		NationEconomy ne = getNationEconomy(ecd.getNationNo());
		if (ne == null)
			return null;
		EconomyTotalsTableModel ettm = new EconomyTotalsTableModel(this.gameHolder);
		ettm.setNationNo(ecd.getNationNo());
		switch (col) {
		case 0:
			// nation
			Nation n = this.gameHolder.getGame().getMetadata().getNationByNum(ecd.getNationNo());
			if (n == null)
				return ecd.getNationNo();
			return n.getShortName();
		case iProdStart:
			// leather
			return getProduct(ecd, ProductEnum.Leather);
		case 2:
			// bronze
			return getProduct(ecd, ProductEnum.Bronze);
		case 3:
			// steel
			return getProduct(ecd, ProductEnum.Steel);
		case 4:
			// mithril
			return getProduct(ecd, ProductEnum.Mithril);
		case 5:
			// food
			return getProduct(ecd, ProductEnum.Food);
		case 6:
			// timber
			return getProduct(ecd, ProductEnum.Timber);
		case iProdEnd:
			// mounts
			return getProduct(ecd, ProductEnum.Mounts);
		case iCaptialChars:
			PopulationCenter capital = (PopulationCenter) this.gameHolder.getGame().getTurn().getContainer(TurnElementsEnum.PopulationCenter).findFirstByProperties(new String[] { "nationNo", "capital" }, new Object[] { ecd.getNationNo(), true });
			if (capital == null)
				return null;
			return this.gameHolder.getGame().getTurn().getContainer(TurnElementsEnum.Character).findAllByProperties(new String[] { "hexNo", "deathReason", "nationNo" }, new Object[] { capital.getHexNo(), CharacterDeathReasonEnum.NotDead, ecd.getNationNo() }).size();
		case iSurplus:
			// surplus
			return ettm.getSurplus();
		case iReserves:
			// reserves
			return ne.getReserve();
		case iLosses:
			return -ettm.computeLostGoldRevenue(ne.getNationNo()) - ettm.computeLostTaxRevenue(ne.getNationNo());
		case iMarketSales:
			return ecd.getMarketSales();
		case iMarketSpending:
			return ecd.getMarketSpend();
		case iOrders:
			// orders
			return ecd.getOrdersCost();
		case iFinalGold:
			// available gold
			return ettm.getFinalGold();
		case iHikedTaxRate:
			return ettm.getTaxIncrease() + ettm.getTaxRate();
		case iTaxRate:
			return ettm.getTaxRate();
		case iTaxBase:
			if (this.nswm == null) return 0;
			for (NationStatisticsWrapper nsw:(ArrayList<NationStatisticsWrapper>)this.nswm.getRows()) {
				if (nsw.getNationNo() != null) {
					if (nsw.getNationNo().intValue() == ecd.getNationNo().intValue()) {
						return nsw.getTaxBase();
					}
				}
			}
			return 0;
		}
		;
		return null;
	}

	public int getBestNatSellIndex(int row) {
		EconomyCalculatorData ecd = getEconomyCalculatorData(row);
		int gain = -1;
		int i = -1;
		int j = 0;
		for (ProductEnum pe : ProductEnum.values()) {
			if (pe == ProductEnum.Gold)
				continue;
			if (gain < ecd.getMarketProfits(pe)) {
				gain = ecd.getMarketProfits(pe);
				i = j;
			}
			j++;
		}
		return i + 1;
	}

	public int getSecondBestNatSellIndex(int row) {
		EconomyCalculatorData ecd = getEconomyCalculatorData(row);
		int best = -1;
		int secondBest = -1;
		int bi = -1;
		int sbi = -1;
		int j = 0;
		for (ProductEnum pe : ProductEnum.values()) {
			if (pe == ProductEnum.Gold)
				continue;
			if (best < ecd.getMarketProfits(pe)) {
				if (secondBest > -1) {
					secondBest = best;
					sbi = bi;
				}
				best = ecd.getMarketProfits(pe);
				bi = j;
			}
			j++;
		}
		return sbi + 1;
	}
	// a list of options for a combo box etc.
	static public String[] getSummaryOptions()
	{
		int count = SummaryTypeEnum.values().length;
		String[] options = new String[count];

		count = 0;
		for (SummaryTypeEnum option: SummaryTypeEnum.values()) {
			options[count++] = option.getRenderString();
		}
		return options;
	}
}
