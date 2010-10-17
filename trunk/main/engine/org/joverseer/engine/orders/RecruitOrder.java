package org.joverseer.engine.orders;

import org.joverseer.domain.Army;
import org.joverseer.domain.ArmyElement;
import org.joverseer.domain.ArmyElementType;
import org.joverseer.domain.Company;
import org.joverseer.domain.InformationSourceEnum;
import org.joverseer.domain.Order;
import org.joverseer.domain.PopulationCenter;
import org.joverseer.domain.ProductEnum;
import org.joverseer.engine.ErrorException;
import org.joverseer.engine.ExecutingOrder;
import org.joverseer.engine.ExecutingOrderUtils;
import org.joverseer.game.Game;
import org.joverseer.game.Turn;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.metadata.SNAEnum;

public class RecruitOrder extends ExecutingOrder {

	public RecruitOrder(Order order) {
		super(order);
	}

	@Override
	public void doExecute(Game game, Turn turn) throws ErrorException {
		if (!loadPopCenter(turn)) {
			addMessage("No pop center at hex.");
			return;
		}
		if (!isCommander()) return;
		Army army;
		if (getOrderNo() == 770) {
			if (loadArmyByCommander(turn)) {
				addMessage("{char} is already an army commander.");
				return;
			}
			// TODO can a character already in an army hire a new army?
			army = ExecutingOrderUtils.createArmy(getCharacter(), getInfoSource(turn));
			if (ExecutingOrderUtils.hasSNA(game, getNationNo(), SNAEnum.ArmiesAt40Morale)) {
				army.setMorale(40);
			}
		} else {
			army = ExecutingOrderUtils.getArmy(turn, getHex(), getCharacter().getName());
			if (army == null) {
				for (Army a : ExecutingOrderUtils.getArmies(turn, getHex())) {
					if (ExecutingOrderUtils.getCharsWithArmy(turn, a, true).contains(getCharacter())) {
						army = a;
					}
				}
			}
		}
		if (army == null) {
			addMessage("Character not with army.");
			return;
		}
		setArmy(army);
		loadPopCenter(turn);
		if (!isPopCenterOfNation()) {
			addMessage("Pop center not of the same nation.");
			return;
		}
		String type = "";
		int number = 0;
		String weapons = "wo";
		String armor = "no";
		if (getOrderNo() == 770) {
			addMessage("{char} was ordered to hire an army.");
			type = getParameter(1).toUpperCase();
			number = getParameterInt(0);
			weapons = getParameter(2);
			armor = getParameter(3);
		} else {
			addMessage("{char} was ordered to recruit some troops.");
			if (getOrderNo() == 400) type="HC";
			if (getOrderNo() == 404) type="LC";
			if (getOrderNo() == 408) type="HI";
			if (getOrderNo() == 412) type="LI";
			if (getOrderNo() == 416) type="AR";
			if (getOrderNo() == 420) type="MA";
			number = getParameterInt(0);
			weapons = getParameter(1);
			armor = getParameter(2);
		}
		if (number < 0) throw new ErrorException("Invalid number");
		
		ArmyElementType aet = ArmyElementType.getFromString(type);
		if (aet == null) throw new ErrorException("Invalid troop type " + type);
			
		int recruits = getPop().getRecruits();
		if (recruits == 0) {
			throw new ErrorException("Not enough recruits. No men were hired.");
		} else if (recruits < number) {
			addMessage("Not enough recruits - recruiting " + number + " men.");
			number = recruits;
		}
		
		if (aet.isCavalry()) {
			// check leather and mounts
			int leather = ExecutingOrderUtils.getStores(getPop(), ProductEnum.Leather);
			int mounts = ExecutingOrderUtils.getStores(getPop(), ProductEnum.Mounts);
			int newNumber = Math.min(number, Math.min(mounts, leather / 2));
			if (newNumber == 0) {
				throw new ErrorException("Not enough mounts or leather. No men were hired.");
			}
			if (newNumber < number) {
				addMessage("Not enough leather or mounts - recruiting " + number + " men.");
			}
			number = newNumber;
			ExecutingOrderUtils.consumeProduct(getPop(), ProductEnum.Leather, number * 2);
			ExecutingOrderUtils.consumeProduct(getPop(), ProductEnum.Mounts, number);
		}
		
		consumeCost(game, turn);
		
		getPop().setRecruits(getPop().getRecruits() - number);
		
		ArmyElement ae = army.getElement(aet);
		ArmyElement nae = new ArmyElement(aet, number);
		if (weapons.equals("br")) {
			if (ExecutingOrderUtils.hasAvailableProduct(getPop(), ProductEnum.Bronze, number)) {
				nae.setWeapons(30);
				ExecutingOrderUtils.consumeProduct(getPop(), ProductEnum.Bronze, number);
			} else {
				addMessage("Not enough bronze - recruiting with wooden weapons.");
				weapons = "wo";
			}
		} else if (weapons.equals("st")) {
			if (ExecutingOrderUtils.hasAvailableProduct(getPop(), ProductEnum.Steel, number)) {
				nae.setWeapons(60);
				ExecutingOrderUtils.consumeProduct(getPop(), ProductEnum.Steel, number);
			} else {
				addMessage("Not enough steel - recruiting with wooden weapons.");
				weapons = "wo";
			}
		} else if (weapons.equals("mi")) {
			if (ExecutingOrderUtils.hasAvailableProduct(getPop(), ProductEnum.Mithril, number)) {
				nae.setWeapons(100);
				ExecutingOrderUtils.consumeProduct(getPop(), ProductEnum.Mithril, number);
			} else {
				addMessage("Not enough mithril - recruiting with wooden weapons.");
				weapons = "wo";
			}
		}
		if (weapons.equals("wo")) {
			nae.setWeapons(10);
		} 
		
		if (armor.equals("le")) {
			if (ExecutingOrderUtils.hasAvailableProduct(getPop(), ProductEnum.Leather, number)) {
				nae.setArmor(10);
				ExecutingOrderUtils.consumeProduct(getPop(), ProductEnum.Leather, number);
			} else {
				addMessage("Not enough leather - recruiting with no armor.");
				armor = "no";
			}
		} else if (armor.equals("br")) {
			if (ExecutingOrderUtils.hasAvailableProduct(getPop(), ProductEnum.Bronze, number)) {
				nae.setArmor(30);
				ExecutingOrderUtils.consumeProduct(getPop(), ProductEnum.Bronze, number);
			} else {
				addMessage("Not enough bronze - recruiting with no armor.");
				armor = "no";
			}
		} else if (armor.equals("st")) {
			if (ExecutingOrderUtils.hasAvailableProduct(getPop(), ProductEnum.Steel, number)) {
				nae.setArmor(60);
				ExecutingOrderUtils.consumeProduct(getPop(), ProductEnum.Steel, number);
			} else {
				addMessage("Not enough steel - recruiting with no armor.");
				armor = "no";
			}
		} else if (armor.equals("mi")) {
			if (ExecutingOrderUtils.hasAvailableProduct(getPop(), ProductEnum.Mithril, number)) {
				nae.setArmor(100);
				ExecutingOrderUtils.consumeProduct(getPop(), ProductEnum.Mithril, number);
			} else {
				addMessage("Not enough mithril - recruiting with no armor.");
				armor = "no";
			}
		}
		if (armor.equals("no")) {
			nae.setArmor(0);
		}
		int training = 10;
		if (ExecutingOrderUtils.hasSNA(game, getNationNo(), SNAEnum.TroopsAt20Training)) {
			training = 20;
		} else if (ExecutingOrderUtils.hasSNA(game, getNationNo(), SNAEnum.TroopsAt25Training)) {
			training = 25;
		} else if (aet.equals(ArmyElementType.MenAtArms) && ExecutingOrderUtils.hasSNA(game, getNationNo(), SNAEnum.MAAt25Training)) {
			training = 25;
		}
		nae.setTraining(training);
		nae.mergeWith(ae);
		army.setElement(nae);
		String msg = number + " " + type + " with " + weapons + " weapons and " + armor + " armor ";
		if (getOrderNo() == 770) {
			// remove character from existing army or company
			Army a = ExecutingOrderUtils.findArmy(turn, getCharacter());
			if (a != null) ExecutingOrderUtils.removeCharacterFromArmy(turn, a, getCharacter());
			
			Company c = ExecutingOrderUtils.findCompany(turn, getCharacter());
			if (c != null) ExecutingOrderUtils.removeCharacterFromCompany(turn, c, getCharacter());
			
			turn.getContainer(TurnElementsEnum.Army).addItem(army);
			
			int food = getParameterInt(4);
			food = Math.max(food, 0);
			int availableFood = ExecutingOrderUtils.getStores(getPop(), ProductEnum.Food);
			food = Math.min(food, availableFood);
			ExecutingOrderUtils.consumeProduct(getPop(), ProductEnum.Food, food);
			getArmy().setFood(food);
			addMessage("A new army with " + msg + " was hired.");
		} else {
			addMessage(msg + "were recruited.");
		}
		
	}

	

	
}
