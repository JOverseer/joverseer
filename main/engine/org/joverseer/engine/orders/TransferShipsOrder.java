package org.joverseer.engine.orders;

import org.joverseer.domain.ArmyElement;
import org.joverseer.domain.ArmyElementType;
import org.joverseer.domain.Order;
import org.joverseer.engine.ErrorException;
import org.joverseer.engine.ExecutingOrder;
import org.joverseer.engine.ExecutingOrderUtils;
import org.joverseer.game.Game;
import org.joverseer.game.Turn;
import org.joverseer.metadata.domain.Hex;
import org.joverseer.metadata.domain.HexTerrainEnum;

public class TransferShipsOrder extends ExecutingOrder {

	public TransferShipsOrder(Order order) {
		super(order);

	}

	@Override
	public void doExecute(Game game, Turn turn) throws ErrorException {
		addMessage("{char} was ordered to transfer some ships.");
		
		if (!loadArmyByCommander(turn) && !loadArmyByMember(turn)) {
			addMessage("{char} could not transfer ships because {gp} was not with an army.");
			return;
		}
		String id = getParameter(0);
		if (!loadCharacter2(turn, id)) {
			addMessage("{char} could not transfer ships because no character with id " + id + " was found.");
			return;
		}
		if (!loadArmy2ByCommander(turn)) {
			addMessage("{char} could not transfer ships to {char2} because {char2} is not an army commander.");
			return;
		}
		
		if (!areCharsAtSameHex()) {
			addMessage("{char} could not transfer ships to {char2} because they are not in the same location.");
			return;
		}
		
		if (!areCharsOfSameNation()) {
			if (!ExecutingOrderUtils.checkFriendly(turn, getArmy(), getArmy2())) {
				addMessage("{char} could not transfer ships to {char2} because of their nation relations.");
				return;
			}
		}
		
		int warshipsToTransfer = getParameterInt(1);
		int transportsToTransfer = getParameterInt(2);
		
		int existingWarships = getArmy().getNumber(ArmyElementType.Warships);
		int existingTransports = getArmy().getNumber(ArmyElementType.Transports);
		
		int requiredTransports = getArmy().getNumberOfRequiredTransports();
		
		Hex hex = game.getMetadata().getHex(getHex());
		if (!hex.getTerrain().isOpenSea()) {
			requiredTransports = 0;
		}
		
		if (transportsToTransfer > existingTransports - requiredTransports) {
			addMessage("{char} could not transfer ships because there are not enough available transports to transfer.");
			return;
		}
		if (warshipsToTransfer > existingWarships) {
			addMessage("{char} could not transfer ships because there are not enough available warships to transfer.");
			return;
		}
		
		ExecutingOrderUtils.removeElementTroops(getArmy(), ArmyElementType.Warships, warshipsToTransfer);
		ExecutingOrderUtils.removeElementTroops(getArmy(), ArmyElementType.Transports, transportsToTransfer);
		
		ExecutingOrderUtils.addElement(getArmy2(), new ArmyElement(ArmyElementType.Warships, warshipsToTransfer));
		ExecutingOrderUtils.addElement(getArmy2(), new ArmyElement(ArmyElementType.Transports, transportsToTransfer));
		
		addMessage(warshipsToTransfer + " warships and " + transportsToTransfer + " transports were transfered to {char2}'s army.");
		ExecutingOrderUtils.cleanupArmy(turn, getArmy());
		
	}
	
	

}
