package org.joverseer.engine.orders;

import java.util.ArrayList;

import org.joverseer.domain.Army;
import org.joverseer.domain.Order;
import org.joverseer.domain.PopulationCenter;
import org.joverseer.engine.ErrorException;
import org.joverseer.engine.ExecutingOrder;
import org.joverseer.engine.ExecutingOrderUtils;
import org.joverseer.game.Game;
import org.joverseer.game.Turn;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.tools.combatCalc.Combat;
import org.joverseer.tools.combatCalc.CombatArmy;
import org.joverseer.tools.combatCalc.CombatPopCenter;
import org.joverseer.tools.combatCalc.TacticEnum;

public class AttackOrder extends ExecutingOrder {

	public AttackOrder(Order order) {
		super(order);
	}

	@Override
	public void doExecute(Game game, Turn turn) throws ErrorException {
		String tac = getParameter(0);
		if (getOrderNo() == 235) {
			tac = getParameter(1);
		}
		TacticEnum tactic = null;
		if (tac.equals("ch")) {
			tactic = TacticEnum.Charge;
		} else if (tac.equals("fl")) {
			tactic = TacticEnum.Flank;
		} else if (tac.equals("am")) {
			tactic = TacticEnum.Ambush;
		} else if (tac.equals("su")) {
			tactic = TacticEnum.Surround;
		} else if (tac.equals("hr")) {
			tactic = TacticEnum.HitAndRun;
		} else if (tac.equals("st")) {
			tactic = TacticEnum.Standard;
		} 
		if (tactic == null) {
			throw new ErrorException("Invalid tactic " + tac + ".");
		}
		
		String command = "attack";
		boolean attackPop = false;
		boolean enemiesFound = false;
		
		if (getOrderNo() == 250) {
			command = " destroy population center ";
			attackPop = true;
		} else if (getOrderNo() == 255) {
			command = " capture population center ";
			attackPop = true;
		}
		
		int attackNation = -1;
		if (getOrderNo() == 235) {
			attackNation = getParameterInt(0);
		}
		
		if (!isArmyCommander(turn)) {
			addMessage("{char} could not " + command + " because he was not an army commander.");
			return;
		}
		loadArmyByCommander(turn);
		if (attackPop) {
			if (!loadPopCenter(turn) || getPop().getHidden()) {
				addMessage("{char} could not " + command + " because no population center was found at {starthex}.");
				return;
			}
			if (!ExecutingOrderUtils.canAttack(turn, getCharacter(), getPop())) {
				addMessage("{char} could not " + command + " {pc} because of nation relations."); // TODO fix
				return;
			}
			attackNation = getPop().getNationNo();
			enemiesFound = true;
		} 
		
		addMessage("{char} was ordered to " + command + ".");
		
		// find opposing armies
		ArrayList<Army> opposing = new ArrayList<Army>();
		ArrayList<Army> armies = ExecutingOrderUtils.getArmies(turn, getHex());
		for (Army a : armies) {
			if (attackNation > -1 && !a.getNationNo().equals(attackNation)) continue;
			if (!ExecutingOrderUtils.canAttack(turn, getCharacter(), a)) continue;
			opposing.add(a);
			enemiesFound = true;
		}
	
		if (!enemiesFound) {
			addMessage("{char} found no enemies to fight."); 
			return;
		}
		
		if (ExecutingOrderUtils.anchorShips(turn, getArmy())) {
			addMessage("Ships were anchored.");
		}
		
		addMessage("See combat messages.");
		// see if there is a combat in the hex already
		Combat combat = null;
		int side = -1;
		for (Combat c : (ArrayList<Combat>)turn.getContainer(TurnElementsEnum.CombatCalcCombats).findAllByProperty("hexNo", getHex())) {
			if (inCombat(c)) return;
			side = getEnemySide(c, opposing, getPop());
			if (side > -1) {
				combat = c;
				side = (side + 1) % 2;
				break;
			}
		}
		if (side == -1) {
			combat = new Combat();
			combat.setHexNo(getHex());
			combat.addToSide(0, new CombatArmy(getArmy()));
			if (attackPop && getPop() != null) combat.setSide2Pc(new CombatPopCenter(getPop()));
			for (Army a : opposing) {
				combat.addToSide(1, new CombatArmy(a));
			}
			combat.setDescription("Combat at " + getHex() + " - " + getName());
			turn.getContainer(TurnElementsEnum.CombatCalcCombats).addItem(combat);
		} else {
			combat.addToSide(side, new CombatArmy(getArmy()));
		}
		if (attackPop) {
			combat.setAttackPopCenter(true);
		}
		
	}
	
	private boolean inCombat(Combat c) {
		for (CombatArmy ca : c.getSide1()) {
			if (ca == null) continue;
			if (ca.getCommander().equals(getName())) return true;
		}
		for (CombatArmy ca : c.getSide2()) {
			if (ca == null) continue;
			if (ca.getCommander().equals(getName())) return true;
		}
		return false;
	}
	
	private int getEnemySide(Combat c, ArrayList<Army> opposingArmies, PopulationCenter pop) {
		if (pop != null && c.getSide1Pc() != null && c.getSide1Pc().getHexNo() == pop.getHexNo()) return 0;
		if (pop != null && c.getSide2Pc() != null && c.getSide2Pc().getHexNo() == pop.getHexNo()) return 1;
		for (CombatArmy ca : c.getSide1()) {
			if (ca == null) continue;
			for (Army a : opposingArmies) {
				if (ca.getCommander().equals(a.getCommanderName())) return 0;
			}
		}
		for (CombatArmy ca : c.getSide2()) {
			if (ca == null) continue;
			for (Army a : opposingArmies) {
				if (ca.getCommander().equals(a.getCommanderName())) return 1;
			}
		}
		return -1;
	}

}
