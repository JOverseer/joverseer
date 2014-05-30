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
import org.joverseer.ui.support.Messages;
import org.joverseer.ui.support.UIUtils;
import org.springframework.rules.reporting.SummingVisitor;

/**
 * Table model for the Team Economy main table It basically shows a complete
 * economic picture for all the imported nations in tabular format
 * 
 * @author Marios Skounakis
 */
public class TeamEconomyTableModel extends BaseEconomyTableModel {
	
	public static int iFinalGold = 15;
	public static int iMarket = 13;
	public static int iProdStart = 1;
	public static int iProdEnd = 7;
	public static int iSurplus = 8;
	public static int iTaxRate = 11;

	SummaryTypeEnum showProductsAs = SummaryTypeEnum.Total;

	//note: change code in the constructor if you change this.
	String[] columnHeaderTags = new String[] { "nation", "le", "br", "st", "mi", "fo", "ti", "mo", "surplus","reserves","losses","taxRate","cptChars","market","orders","finalGold"};
	String[] columnNames;

	int[] columnWidths = new int[] { 42, 42, 42, 42, 42, 42, 42, 42, 54, 54, 54, 42, 58, 42, 42, 64 };

	Class<?>[] classes = new Class[] { String.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class };

	ArrayList<EconomyCalculatorData> items = new ArrayList<EconomyCalculatorData>();

	public TeamEconomyTableModel()
	{
		super();
		this.columnNames = new String[this.columnHeaderTags.length];
		this.columnNames[0]=Messages.getString("standardFields.Nation");
		for (int i=1;i<8;i++) {
			this.columnNames[i] = Messages.getString("ProductEnum."+this.columnHeaderTags[i]);
		}
		for (int i=8;i<this.columnHeaderTags.length;i++) {
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
		return (NationEconomy) GameHolder.instance().getGame().getTurn().getContainer(TurnElementsEnum.NationEconomy).findFirstByProperty("nationNo", nationNo1);
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
		EconomyTotalsTableModel ettm = new EconomyTotalsTableModel();
		ettm.setNationNo(ecd.getNationNo());
		switch (col) {
		case 0:
			// nation
			Nation n = GameHolder.instance().getGame().getMetadata().getNationByNum(ecd.getNationNo());
			if (n == null)
				return ecd.getNationNo();
			return n.getShortName();
		case 1:
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
		case 7:
			// mounts
			return getProduct(ecd, ProductEnum.Mounts);
		case 8:
			// surplus
			return ettm.getSurplus();
		case 9:
			// reserves
			return ne.getReserve();
		case 10:
			return -EconomyTotalsTableModel.computeLostGoldRevenue(ne.getNationNo()) - EconomyTotalsTableModel.computeLostTaxRevenue(ne.getNationNo());
		case 11:
			// tax rate
			return ettm.getTaxRate();
		case 12:
			// chars in capital
			PopulationCenter capital = (PopulationCenter) GameHolder.instance().getGame().getTurn().getContainer(TurnElementsEnum.PopulationCenter).findFirstByProperties(new String[] { "nationNo", "capital" }, new Object[] { ecd.getNationNo(), true });
			if (capital == null)
				return null;
			return GameHolder.instance().getGame().getTurn().getContainer(TurnElementsEnum.Character).findAllByProperties(new String[] { "hexNo", "deathReason", "nationNo" }, new Object[] { capital.getHexNo(), CharacterDeathReasonEnum.NotDead, ecd.getNationNo() }).size();
		case 13:
			// market
			return ecd.getMarketProfits();
		case 14:
			// orders
			return ecd.getOrdersCost();
		case 15:
			// available gold
			return ettm.getFinalGold();
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
