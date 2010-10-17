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
import org.joverseer.support.infoSources.XmlTurnInfoSource;

public class AnchorShipsOrder extends ExecutingOrder {

	public AnchorShipsOrder(Order order) {
		super(order);
	}

	@Override
	public void doExecute(Game game, Turn turn) throws ErrorException {
		addMessage("{char} was ordered to anchor some ships.");
		
		if (!loadArmyByMember(turn)) {
			addMessage("{gp} was not able to anchor ships because {gp} was not with an army.");
			return;
		}
		
		if (!game.getMetadata().getHex(getHex()).getTerrain().isLand()) {
			addMessage("{gp} was not able to anchor ships because the army was not on land.");
			return;
		}
		
		int warships = getParameterInt(0);
		int transports = getParameterInt(1);
		
		warships = Math.min(warships, getArmy().getNumber(ArmyElementType.Warships));
		transports = Math.min(transports, getArmy().getNumber(ArmyElementType.Transports));
		
		if (warships + transports == 0) {
			addMessage("No ships were anchored.");
			return;
		}
		
		Army a = ExecutingOrderUtils.createArmy("[Anchored Ships]", getCharacter().getNationNo(), getCharacter().getNation().getAllegiance(), getHex(), new XmlTurnInfoSource(turn.getTurnNo(), getCharacter().getNationNo()));
		a.setElement(new ArmyElement(ArmyElementType.Warships, warships));
		a.setElement(new ArmyElement(ArmyElementType.Transports, transports));
		a.setNavy(true);
		a.setCavalry(false);
		a.setFed(false);
		turn.getContainer(TurnElementsEnum.Army).addItem(a);
		ExecutingOrderUtils.cleanupArmy(turn, a);
		
		ExecutingOrderUtils.removeElementTroops(getArmy(), ArmyElementType.Warships, warships);
		ExecutingOrderUtils.removeElementTroops(getArmy(), ArmyElementType.Transports, transports);
		
		addMessage(warships + " warships and "+ transports + " transports were anchored.");
		
	}
	
	

}
