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
import org.joverseer.game.TurnElementsEnum;

public class PickupShipsOrder extends ExecutingOrder {

	public PickupShipsOrder(Order order) {
		super(order);
	}

	@Override
	public void doExecute(Game game, Turn turn) throws ErrorException {
		addMessage("{char} was ordered to pick up some ships.");
		if (!loadArmyByCommander(turn)) {
			addMessage("{char} was not able to pick up ships because {gp} was not commanding an army.");
			return;
		}
		
		Army ships = (Army)turn.getContainer(TurnElementsEnum.Army).findFirstByProperties(
				new String[]{"hexNo", "nationNo", "commanderName"},
				new Object[]{String.valueOf(getHex()), getNationNo(), "[Anchored Ships]"});
		if (ships == null) {
			addMessage("{char} was not able to pick up ships because no ships were anchored at {starthex}.");
			return;
		}
		
		int warships = getParameterInt(0);
		int transports = getParameterInt(1);
		
		ArmyElement existingWarships = ships.getElement(ArmyElementType.Warships);
		ArmyElement existingTransports = ships.getElement(ArmyElementType.Transports);
		
		int warshipsToTransfer = 0;
		int transportsToTransfer = 1;
		if (existingWarships != null && existingWarships.getNumber() > 0) {
			warshipsToTransfer = Math.min(existingWarships.getNumber(), warships);
			existingWarships.setNumber(existingWarships.getNumber() - warshipsToTransfer);
			if (existingWarships.getNumber() == 0) existingWarships = null;
		}
		if (existingTransports != null && existingTransports.getNumber() > 0) {
			transportsToTransfer = Math.min(existingTransports.getNumber(), transports);
			existingTransports.setNumber(existingTransports.getNumber() - transportsToTransfer);
			if (existingTransports.getNumber() == 0) existingTransports = null;
		}
		
		if (existingTransports == null && existingWarships == null) {
			turn.getContainer(TurnElementsEnum.Army).removeItem(ships);
		} 
		
		ExecutingOrderUtils.addElement(getArmy(), new ArmyElement(ArmyElementType.Warships, warshipsToTransfer));
		ExecutingOrderUtils.addElement(getArmy(), new ArmyElement(ArmyElementType.Transports, transportsToTransfer));
		addMessage(warshipsToTransfer + " warships and " + transportsToTransfer + " transports were picked up.");
		
		// check to become a navy
		if (!getArmy().isNavy()) {
			if (getArmy().getNumber(ArmyElementType.Transports) >= getArmy().getNumberOfRequiredTransports()) {
				getArmy().setNavy(true);
			}
		}
	}

}
