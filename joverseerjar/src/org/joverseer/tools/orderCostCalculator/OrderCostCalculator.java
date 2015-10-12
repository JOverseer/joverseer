package org.joverseer.tools.orderCostCalculator;

import java.util.ArrayList;

import org.joverseer.domain.Character;
import org.joverseer.domain.FortificationSizeEnum;
import org.joverseer.domain.NationEconomy;
import org.joverseer.domain.Order;
import org.joverseer.domain.PopulationCenter;
import org.joverseer.domain.PopulationCenterSizeEnum;
import org.joverseer.domain.ProductEnum;
import org.joverseer.domain.ProductPrice;
import org.joverseer.game.Game;
import org.joverseer.game.Turn;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.metadata.SNAEnum;
import org.joverseer.metadata.domain.Hex;
import org.joverseer.metadata.domain.HexSideElementEnum;
import org.joverseer.metadata.domain.HexSideEnum;
import org.joverseer.metadata.domain.Nation;
import org.joverseer.support.GameHolder;
import org.joverseer.support.ProductContainer;

/**
 * Utility class that calculates the cost for a give order.
 * 
 * Currently it computes the gold cost, but work is in process (and unfinished)
 * to also compute product costs
 * 
 * @author Marios Skounakis
 */

// TODO finish
public class OrderCostCalculator {
	ProductContainer cont = new ProductContainer();

	public int getTotalOrderCostForNation(Turn turn, int nationNo) {
		int totalCost = 0;
		for (Character c : (ArrayList<Character>) turn.getContainer(TurnElementsEnum.Character).findAllByProperty("nationNo", nationNo)) {
			for (int i = 0; i < c.getNumberOfOrders(); i++) {
				if (c.getOrders()[i].isBlank())
					continue;
				int no = c.getOrders()[i].getOrderNo();
				if (no == 320 || no == 315 || no == 310 || no == 325)
					continue;
				int cost = getOrderCost(c.getOrders()[i], turn);
				if (cost > 0) {
					totalCost += cost;
				}
			}
		}
		return totalCost;
	}

	public int getTotalTranCarOrderCostForNation(Turn turn, int nationNo) {
		int totalCost = 0;
		for (Character c : (ArrayList<Character>) turn.getContainer(TurnElementsEnum.Character).findAllByProperty("nationNo", nationNo)) {
			for (int i = 0; i < c.getNumberOfOrders(); i++) {
				if (c.getOrders()[i].isBlank())
					continue;
				int no = c.getOrders()[i].getOrderNo();
				if (no != 948)
					continue;
				if (no == 320 || no == 315 || no == 310 || no == 325)
					continue;
				int cost = getOrderCost(c.getOrders()[i], turn);
				if (cost > 0) {
					totalCost += cost;
				}
			}
		}
		return totalCost;
	}

	public int getOrderCost(Order o, Turn t) {
		switch (o.getOrderNo()) {
		case 400:
			return computeRecruitCost(o);
		case 404:
			return computeRecruitCost(o);
		case 408:
			return computeRecruitCost(o);
		case 412:
			return computeRecruitCost(o);
		case 416:
			return computeRecruitCost(o);
		case 420:
			return computeRecruitCost(o);
		case 490:
			return buildBridgeCost(o);
		case 494:
			return fortifyPopCenterCost(o);
		case 552:
			return postCampCost(o);
		case 725:
			return nameNewCharCost(o);
		case 728:
			return nameNewCommanderCost(o);
		case 770:
			return hireArmyCost(o);
		case 950:
			return relocateCapitalCost(o);
		case 440:
			return makeWarMachinesCost(o);
		case 452:
			return makeWarshipsCost(o);
		case 456:
			return makeTransportsCost(o);
		case 731:
			return nameNewAgentCost(o);
		case 660:
			return offerRansomCost(o);
		case 505:
			return bribeCost(o);
		case 530:
			return improveHarborCost(o);
		case 535:
			return addHarborCost(o);
		case 550:
			return improvePopCenterCost(o);
		case 555:
			return createCampCost(o);
		case 734:
			return nameNewEmissaryCost(o);
		case 737:
			return nameNewMageCost(o);
		case 705:
			return researchSpellCost(o);
		case 310:
			return bidFromCaravanCost(o);
		case 315:
			return purchaseFromCaravanCost(o);
		case 320:
			return sellToCaravanCost(o);
		case 325:
			return natSellToCaravanCost(o);
		case 948:
			return tranCarOrderCost(o);
		case 942:
			return moveTurnMapCost(o, t);
		}
		return 0;
	}

	public int moveTurnMapCost(Order o, Turn t) {
		if (t.getTurnNo() <= 12)
			return 5000;
		return 3000;
	}

	public int computeRecruitCost(Order o) {
		try {
			String troopType = "hc";
			if (o.getOrderNo() == 404) {
				troopType = "lc";
			} else if (o.getOrderNo() == 408) {
				troopType = "hi";
			} else if (o.getOrderNo() == 412) {
				troopType = "li";
			} else if (o.getOrderNo() == 416) {
				troopType = "ar";
			} else if (o.getOrderNo() == 420) {
				troopType = "ma";
			}
			Integer number = Integer.parseInt(o.getParameter(0));
			String weapons = o.getParameter(1);
			String armor = o.getParameter(2);
			if (troopType.equals("hc") || troopType.equals("lc")) {
				this.cont.setProduct(ProductEnum.Mounts, number);
				this.cont.setProduct(ProductEnum.Leather, number * 2);
			}
			if (!weapons.equals("wo")) {
				ProductEnum p = ProductEnum.getFromCode(weapons);
				this.cont.setProduct(p, number);
			}
			if (!armor.equals("no")) {
				ProductEnum p = ProductEnum.getFromCode(weapons);
				Integer v = this.cont.getProduct(p);
				if (v == null)
					v = 0;
				v += number;
				this.cont.setProduct(p, v);
			}
		} catch (Exception exc) {
		}
		;
		return 0;
	}

	public int bidFromCaravanCost(Order o) {
		ProductEnum pe = ProductEnum.getFromCode(o.getParameter(0));
		if (pe == null)
			return 0;
		try {
			int no = Integer.parseInt(o.getParameter(1));
			int bid = Integer.parseInt(o.getParameter(2));
			int cost = no * bid;
			this.cont.setProduct(ProductEnum.Gold, cost);
			this.cont.setProduct(pe, -no);
			return cost;
		} catch (Exception exc) {
			return -1;
		}
	}

	public int purchaseFromCaravanCost(Order o) {
		ProductEnum pe = ProductEnum.getFromCode(o.getParameter(0));
		if (pe == null)
			return 0;
		try {
			int no = Integer.parseInt(o.getParameter(1));
			ProductPrice pp = (ProductPrice) GameHolder.instance().getGame().getTurn().getContainer(TurnElementsEnum.ProductPrice).findFirstByProperty("product", pe);
			int cost = no * pp.getBuyPrice();
			this.cont.setProduct(ProductEnum.Gold, cost);
			this.cont.setProduct(pe, -no);
			return cost;
		} catch (Exception exc) {
			return -1;
		}
	}

	public int sellToCaravanCost(Order o) {
		ProductEnum pe = ProductEnum.getFromCode(o.getParameter(0));
		if (pe == null)
			return 0;
		try {
			int no = Integer.parseInt(o.getParameter(1));
			ProductPrice pp = (ProductPrice) GameHolder.instance().getGame().getTurn().getContainer(TurnElementsEnum.ProductPrice).findFirstByProperty("product", pe);
			int gain = -no * pp.getSellPrice();
			this.cont.setProduct(ProductEnum.Gold, gain);
			this.cont.setProduct(pe, no);
			return gain;
		} catch (Exception exc) {
			return -1;
		}
	}

	public int natSellToCaravanCost(Order o) {
		ProductEnum pe = ProductEnum.getFromCode(o.getParameter(0));
		if (pe == null)
			return 0;
		try {
			int pct = Integer.parseInt(o.getParameter(1));
			NationEconomy ne = (NationEconomy) GameHolder.instance().getGame().getTurn().getContainer(TurnElementsEnum.NationEconomy).findFirstByProperty("nationNo", o.getCharacter().getNationNo());
			ProductPrice pp = (ProductPrice) GameHolder.instance().getGame().getTurn().getContainer(TurnElementsEnum.ProductPrice).findFirstByProperty("product", pe);
			int amt = (ne.getStores().getProduct(pe) + ne.getProduction().getProduct(pe)) * pct / 100;
			int gain = -amt * pp.getSellPrice();
			this.cont.setProduct(ProductEnum.Gold, gain);
			this.cont.setProduct(pe, amt);
			return gain;
		} catch (Exception exc) {
			return -1;
		}
	}

	public int researchSpellCost(Order o) {
		this.cont.setProduct(ProductEnum.Gold, 1000);
		return 1000;
	}

	public int nameNewMageCost(Order o) {
		this.cont.setProduct(ProductEnum.Gold, 5000);
		return 5000;
	}

	public int nameNewEmissaryCost(Order o) {
		this.cont.setProduct(ProductEnum.Gold, 5000);
		return 5000;
	}

	public int createCampCost(Order o) {
		this.cont.setProduct(ProductEnum.Gold, 2000);
		return 2000;
	}

	public int improvePopCenterCost(Order o) {
		PopulationCenter pc = (PopulationCenter) GameHolder.instance().getGame().getTurn().getContainer(TurnElementsEnum.PopulationCenter).findFirstByProperty("hexNo", o.getCharacter().getHexNo());
		if (pc == null)
			return -1;
		int cost = 0;
		if (pc.getSize() == PopulationCenterSizeEnum.camp) {
			cost = 4000;
		} else if (pc.getSize() == PopulationCenterSizeEnum.village) {
			cost = 6000;
		} else if (pc.getSize() == PopulationCenterSizeEnum.town) {
			cost = 8000;
		} else if (pc.getSize() == PopulationCenterSizeEnum.majorTown) {
			cost = 10000;
		} else if (pc.getSize() == PopulationCenterSizeEnum.city) {
			cost = -1;
		}
		this.cont.setProduct(ProductEnum.Gold, cost);
		return cost;
	}

	public int addHarborCost(Order o) {
		this.cont.setProduct(ProductEnum.Gold, 2500);
		this.cont.setProduct(ProductEnum.Timber, 5000);
		return 2500;
	}

	public int improveHarborCost(Order o) {
		this.cont.setProduct(ProductEnum.Gold, 4000);
		this.cont.setProduct(ProductEnum.Timber, 7500);
		return 4000;
	}

	public int bribeCost(Order o) {
		try {
			int no = Integer.parseInt(o.getParameter(1));
			this.cont.setProduct(ProductEnum.Gold, no);
			return no;
		} catch (Exception exc) {
			return -1;
		}
	}

	public int offerRansomCost(Order o) {
		try {
			int no = Integer.parseInt(o.getParameter(1));
			this.cont.setProduct(ProductEnum.Gold, no);
			return no;
		} catch (Exception exc) {
			return -1;
		}
	}

	public int nameNewAgentCost(Order o) {
		this.cont.setProduct(ProductEnum.Gold, 5000);
		return 5000;
	}

	public int makeTransportsCost(Order o) {
		try {
			int no = Integer.parseInt(o.getParameter(0));
			this.cont.setProduct(ProductEnum.Gold, no * 1000);
			int f = 1;
			if (getMetadataNation(o).getSnas().contains(SNAEnum.ShipsWith500Timber)) {
				f = 3;
			} else if (getMetadataNation(o).getSnas().contains(SNAEnum.ShipsWith750Timber)) {
				f = 2;
			}
			this.cont.setProduct(ProductEnum.Timber, no * 1500 / f);
			return no * 1000;
		} catch (Exception exc) {
			return -1;
		}
	}

	public int makeWarshipsCost(Order o) {
		try {
			int no = Integer.parseInt(o.getParameter(0));
			this.cont.setProduct(ProductEnum.Gold, no * 1000);
			int f = 1;
			if (getMetadataNation(o).getSnas().contains(SNAEnum.ShipsWith500Timber)) {
				f = 3;
			} else if (getMetadataNation(o).getSnas().contains(SNAEnum.ShipsWith750Timber)) {
				f = 2;
			}
			this.cont.setProduct(ProductEnum.Timber, no * 1500 / f);
			return no * 1000;
		} catch (Exception exc) {
			return -1;
		}
	}

	public int makeWarMachinesCost(Order o) {
		try {
			int no = Integer.parseInt(o.getParameter(0));
			this.cont.setProduct(ProductEnum.Timber, no * 500);
			return 0;
		} catch (Exception exc) {
			return -1;
		}
	}

	public int relocateCapitalCost(Order o) {
		this.cont.setProduct(ProductEnum.Gold, 30000);
		return 30000;
	}

	public int hireArmyCost(Order o) {
		int cost = 5000;
		Nation n = GameHolder.instance().getGame().getMetadata().getNationByNum(o.getCharacter().getNationNo());
		if (n != null && n.hasSna(SNAEnum.FreeHire)) {
			cost = 0;
		}
		this.cont.setProduct(ProductEnum.Gold, cost);

		// get troop type
		try {
			String troopType = o.getParameter(1);
			Integer number = Integer.parseInt(o.getParameter(0));
			String weapons = o.getParameter(2);
			String armor = o.getParameter(3);
			Integer food = Integer.parseInt(o.getParameter(4));
			this.cont.setProduct(ProductEnum.Food, food);
			if (troopType.equals("hc") || troopType.equals("lc")) {
				this.cont.setProduct(ProductEnum.Mounts, number);
				this.cont.setProduct(ProductEnum.Leather, number * 2);
			}
			if (!weapons.equals("wo")) {
				ProductEnum p = ProductEnum.getFromCode(weapons);
				this.cont.setProduct(p, number);
			}
			if (!armor.equals("no")) {
				ProductEnum p = ProductEnum.getFromCode(weapons);
				Integer v = this.cont.getProduct(p);
				if (v == null)
					v = 0;
				v += number;
				this.cont.setProduct(p, v);
			}

		} catch (Exception exc) {

		}

		return this.cont.getProduct(ProductEnum.Gold);
	}

	public int nameNewCommanderCost(Order o) {
		this.cont.setProduct(ProductEnum.Gold, 5000);
		return 5000;
	}

	public int nameNewCharCost(Order o) {
		this.cont.setProduct(ProductEnum.Gold, 10000);
		return 10000;
	}

	public int postCampCost(Order o) {
		this.cont.setProduct(ProductEnum.Gold, 4000);
		return 4000;
	}

	public int fortifyPopCenterCost(Order o) {
		PopulationCenter pc = (PopulationCenter) GameHolder.instance().getGame().getTurn().getContainer(TurnElementsEnum.PopulationCenter).findFirstByProperty("hexNo", o.getCharacter().getHexNo());
		if (pc == null)
			return -1;
		if (pc.getFortification() == FortificationSizeEnum.none) {
			this.cont.setProduct(ProductEnum.Gold, 1000);
			this.cont.setProduct(ProductEnum.Timber, 1000);
		} else if (pc.getFortification() == FortificationSizeEnum.tower) {
			this.cont.setProduct(ProductEnum.Gold, 3000);
			this.cont.setProduct(ProductEnum.Timber, 3000);
		} else if (pc.getFortification() == FortificationSizeEnum.fort) {
			this.cont.setProduct(ProductEnum.Gold, 5000);
			this.cont.setProduct(ProductEnum.Timber, 5000);
		} else if (pc.getFortification() == FortificationSizeEnum.castle) {
			this.cont.setProduct(ProductEnum.Gold, 8000);
			this.cont.setProduct(ProductEnum.Timber, 8000);
		} else if (pc.getFortification() == FortificationSizeEnum.keep) {
			this.cont.setProduct(ProductEnum.Gold, 12000);
			this.cont.setProduct(ProductEnum.Timber, 12000);
		} else if (pc.getFortification() == FortificationSizeEnum.citadel) {
			return -1;
		}

		// check SNA
		if (getMetadataNation(o).getSnas().contains(SNAEnum.FortificationsWithHalfTimber)) {
			this.cont.setProduct(ProductEnum.Timber, this.cont.getProduct(ProductEnum.Timber) / 2);
		}

		return this.cont.getProduct(ProductEnum.Gold);
	}

	public int buildBridgeCost(Order o) {
		Hex hex = GameHolder.instance().getGame().getMetadata().getHex(o.getCharacter().getHexNo());
		if (hex == null) {
			return -1;
		}

		String dir = o.getParameter(0);
		HexSideEnum side = null;
		if (dir.equals("ne")) {
			side = HexSideEnum.TopRight;
		} else if (dir.equals("nw")) {
			side = HexSideEnum.TopLeft;
		} else if (dir.equals("w")) {
			side = HexSideEnum.Left;
		} else if (dir.equals("sw")) {
			side = HexSideEnum.BottomLeft;
		} else if (dir.equals("se")) {
			side = HexSideEnum.BottomRight;
		} else if (dir.equals("e")) {
			side = HexSideEnum.Right;
		}
		if (side == null)
			return -1;
		if (hex.getHexSideElements(side).contains(HexSideElementEnum.MajorRiver)) {
			this.cont.setProduct(ProductEnum.Gold, 5000);
			this.cont.setProduct(ProductEnum.Timber, 10000);
			return 5000;
		}
		if (hex.getHexSideElements(side).contains(HexSideElementEnum.MinorRiver)) {
			this.cont.setProduct(ProductEnum.Gold, 2500);
			this.cont.setProduct(ProductEnum.Timber, 5000);
			return 2500;
		}
		return 0;
	}

	public int tranCarOrderCost(Order order) {
		if (order.getLastParamIndex() < 3)
			return 0;
		if (order.getParameter(2).equals("go")) {
			int g = 0;
			try {
				g = Integer.parseInt(order.getParameter(3));
			} catch (Exception e) {
				// do nothing
			}
			float d = g;
			d = d * 1.1f;
			return Math.round(d);
		}
		return 0;
	}

	public static Nation getMetadataNation(Order order) {
		Game g = GameHolder.instance().getGame();
		Nation n = g.getMetadata().getNationByNum(order.getCharacter().getNationNo());
		if (n == null) {
			n = new Nation(0, "", "");
		}
		return n;
	}

	public ProductContainer getContainer() {
		return this.cont;
	}

}
