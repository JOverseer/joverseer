package org.joverseer.engine.orders;

import org.joverseer.domain.Army;
import org.joverseer.domain.ArmyElement;
import org.joverseer.domain.ArmyElementType;
import org.joverseer.domain.Character;
import org.joverseer.domain.Company;
import org.joverseer.domain.Order;
import org.joverseer.engine.ErrorException;
import org.joverseer.engine.ExecutingOrder;
import org.joverseer.engine.ExecutingOrderUtils;
import org.joverseer.game.Game;
import org.joverseer.game.Turn;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.metadata.SNAEnum;

public class SplitArmyOrder extends ExecutingOrder {

	public SplitArmyOrder(Order order) {
		super(order);
	}

	@Override
	public void doExecute(Game game, Turn turn) throws ErrorException {
		String destId = getParameter(0);
		int hc = getParameterInt(1);
		int lc = getParameterInt(2);
		int hi = getParameterInt(3);
		int li = getParameterInt(4);
		int ar = getParameterInt(5);
		int ma = getParameterInt(6);

		addMessage("{char} was ordered to split the army.");
		
		if (!loadArmyByMember(turn)) {
			addMessage("{char} was not unable to split the army because {gp} was not with an army.");
			return;
		}
		if (!loadCharacter2(turn, destId)) {
			addMessage("{char} was not unable to split the army because a character with id " + destId + " was not present.");
			return;
		}
		if (!areCharsAtSameHex()) {
			addMessage("{char} was not unable to split the army because {char2} was not present at the same hex.");
			return;
		}
		if (!areCharsOfSameNation()) {
			addMessage("{char} was not unable to split the army because {char2} was not of the same nation.");
			return;
		}
		if (loadArmy2ByCommander(turn)) {
			addMessage("{char} was not unable to split the army because {char2} already commands an army.");
			return;
		}
		if (isCompanyCommander2(turn)) {
			addMessage("{char} was not unable to split the army because {char2} commands a company.");
			return;
		}
		if (!isCommander()) return;
		
		Company c = ExecutingOrderUtils.findCompany(turn, getCharacter2());
		if (c != null) ExecutingOrderUtils.removeCharacterFromCompany(turn, c, getCharacter2());
		
		Army a = ExecutingOrderUtils.findArmy(turn, getCharacter2());
		if (a != null) ExecutingOrderUtils.removeCharacterFromArmy(turn, a, getCharacter2());

		int originTroops = getArmy().computeNumberOfMen();
		
		Army army = ExecutingOrderUtils.createArmy(getCharacter2(), getInfoSource(turn));
		army.setFed(getArmy().isFed());
		army.setMorale(30);
		ArmyElement ae = splitArmyElement(getArmy(), ArmyElementType.HeavyCavalry, hc);
		if (ae != null) army.setElement(ae);
		ae = splitArmyElement(getArmy(), ArmyElementType.LightCavalry, lc);
		if (ae != null) army.setElement(ae);
		ae = splitArmyElement(getArmy(), ArmyElementType.HeavyInfantry, hi);
		if (ae != null) army.setElement(ae);
		ae = splitArmyElement(getArmy(), ArmyElementType.LightInfantry, li);
		if (ae != null) army.setElement(ae);
		ae = splitArmyElement(getArmy(), ArmyElementType.Archers, ar);
		if (ae != null) army.setElement(ae);
		ae = splitArmyElement(getArmy(), ArmyElementType.MenAtArms, ma);
		if (ae != null) army.setElement(ae);
		turn.getContainer(TurnElementsEnum.Army).addItem(army);
		int splitTroops = army.computeNumberOfMen();
		
		Integer originFood = getArmy().getFood();
		if (originFood == null) originFood = 0;
		int splitFood = originFood * splitTroops / originTroops;
		getArmy().setFood(originFood - splitFood);
		army.setFood(splitFood);
		
		addMessage(splitTroops + " troops and " + splitFood + " food were split to {char2}.");
		
		ExecutingOrderUtils.cleanupArmy(turn, getArmy());
		
		
	}
	
	protected ArmyElement splitArmyElement(Army origin, ArmyElementType aet, int splitNumber) {
		ArmyElement ae = origin.getElement(aet);
		if (ae == null) return null;
		splitNumber = Math.min(ae.getNumber(), splitNumber);
		if (splitNumber == 0) return null;
		ArmyElement nae = new ArmyElement(aet, splitNumber);
		nae.setTraining(ae.getTraining());
		nae.setWeapons(ae.getWeapons());
		nae.setArmor(ae.getArmor());
		ExecutingOrderUtils.removeElementTroops(origin, aet, splitNumber);
		return nae;
	}
	
	

}
