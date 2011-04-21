package org.joverseer.engine.orders;

import org.joverseer.domain.Army;
import org.joverseer.domain.ArmyElement;
import org.joverseer.domain.ArmyElementType;
import org.joverseer.domain.Order;
import org.joverseer.engine.ErrorException;
import org.joverseer.engine.ExecutingOrder;
import org.joverseer.engine.ExecutingOrderUtils;
import org.joverseer.game.Game;
import org.joverseer.game.Turn;

public class TransferTroopsOrder extends ExecutingOrder {

	public TransferTroopsOrder(Order order) {
		super(order);
	}

	@Override
	public void doExecute(Game game, Turn turn) throws ErrorException {
		String id = getParameter(0);
		int hc = getParameterInt(1);
		int lc = getParameterInt(2);
		int hi = getParameterInt(3);
		int li = getParameterInt(4);
		int ar = getParameterInt(5);
		int ma = getParameterInt(6);

		addMessage("{char} was ordered to transfer some troops.");
		if (!loadArmyByCommander(turn) && !loadArmyByMember(turn)) {
			addMessage("{char} was unable to transfer troops because he was not with an army.");
			return;
		}
		if (!loadCharacter2(turn, id)) {
			addMessage("{char} was unable to transfer troops because no character with id " + id + " was found.");
			return;
		}
		if (!loadArmy2ByCommander(turn)) {
			addMessage("{char} was unable to transfer troops because to {char2} because {gp2} did not command an army.");
			return;
		}
		if (!areCharsOfSameNation()) {
			addMessage("{char} was unable to transfer troops because to {char2} because {gp2} was not of the same nation.");
			return;
		}
		if (!areCharsAtSameHex()) {
			addMessage("{char} was unable to transfer troops because to {char2} because {gp2} was not in the same hex.");
			return;
		}
		if (!isCommander())
			return;

		ArmyElement ae = splitArmyElement(getArmy(), ArmyElementType.HeavyCavalry, hc);
		ArmyElement dae = getArmy2().getElement(ArmyElementType.HeavyCavalry);
		if (dae == null) {
			getArmy2().setElement(ae);
		} else {
			dae.mergeWith(ae);
		}
		ae = splitArmyElement(getArmy(), ArmyElementType.LightCavalry, lc);
		dae = getArmy2().getElement(ArmyElementType.LightCavalry);
		if (dae == null) {
			getArmy2().setElement(ae);
		} else {
			dae.mergeWith(ae);
		}
		ae = splitArmyElement(getArmy(), ArmyElementType.HeavyInfantry, hi);
		dae = getArmy2().getElement(ArmyElementType.HeavyInfantry);
		if (dae == null) {
			getArmy2().setElement(ae);
		} else {
			dae.mergeWith(ae);
		}
		ae = splitArmyElement(getArmy(), ArmyElementType.LightInfantry, li);
		dae = getArmy2().getElement(ArmyElementType.LightInfantry);
		if (dae == null) {
			getArmy2().setElement(ae);
		} else {
			dae.mergeWith(ae);
		}
		ae = splitArmyElement(getArmy(), ArmyElementType.Archers, ar);
		dae = getArmy2().getElement(ArmyElementType.Archers);
		if (dae == null) {
			getArmy2().setElement(ae);
		} else {
			dae.mergeWith(ae);
		}
		ae = splitArmyElement(getArmy(), ArmyElementType.MenAtArms, ma);
		dae = getArmy2().getElement(ArmyElementType.MenAtArms);
		if (dae == null) {
			getArmy2().setElement(ae);
		} else {
			dae.mergeWith(ae);
		}

		addMessage("Troops were transfered.");
		ExecutingOrderUtils.cleanupArmy(turn, getArmy());
	}

	protected ArmyElement splitArmyElement(Army origin, ArmyElementType aet, int splitNumber) {
		ArmyElement ae = origin.getElement(aet);
		if (ae == null)
			return null;
		splitNumber = Math.min(ae.getNumber(), splitNumber);
		if (splitNumber == 0)
			return null;
		ArmyElement nae = new ArmyElement(aet, splitNumber);
		nae.setTraining(ae.getTraining());
		nae.setWeapons(ae.getWeapons());
		nae.setArmor(ae.getArmor());
		ae.setNumber(ae.getNumber() - splitNumber);
		return nae;
	}

}
