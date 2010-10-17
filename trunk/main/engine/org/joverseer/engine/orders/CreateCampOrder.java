package org.joverseer.engine.orders;

import org.joverseer.domain.FortificationSizeEnum;
import org.joverseer.domain.HarborSizeEnum;
import org.joverseer.domain.InformationSourceEnum;
import org.joverseer.domain.Order;
import org.joverseer.domain.PopulationCenter;
import org.joverseer.domain.PopulationCenterSizeEnum;
import org.joverseer.engine.ErrorException;
import org.joverseer.engine.ExecutingOrder;
import org.joverseer.engine.Randomizer;
import org.joverseer.game.Game;
import org.joverseer.game.Turn;
import org.joverseer.game.TurnElementsEnum;

public class CreateCampOrder extends ExecutingOrder {

	public CreateCampOrder(Order order) {
		super(order);
	}

	@Override
	public void doExecute(Game game, Turn turn) throws ErrorException {
		addMessage("{char} was ordered to create a camp.");
		if (!isEmissary()) {
			return;
		}
		if (loadPopCenter(turn)) {
			if (!getPop().getSize().equals(PopulationCenterSizeEnum.ruins)) {
				addMessage("{char} was unable to create a camp because there is already a population center at {start}.");
				return;
			}
		}
		if (getPop() == null) {
			int roll = Randomizer.roll(getCharacter().getEmmisaryTotal() + 40);
			if (!Randomizer.success(roll)) {
				addMessage("{char} was unable to create a camp because there was not enough populace.");
				// TODO continued efforts
				return;
			} 
			
			consumeCost(game, turn);
			
			PopulationCenter pop = new PopulationCenter();
			pop.setFortification(FortificationSizeEnum.none);
			pop.setHexNo(getHex());
			pop.setName(getParameter(0));
			turn.getContainer(TurnElementsEnum.PopulationCenter).addItem(pop);
			setPop(pop);
		} else {
			consumeCost(game, turn);
		}
		getPop().setInformationSource(InformationSourceEnum.exhaustive);
		getPop().setInfoSource(getInfoSource(turn));
		getPop().setSize(PopulationCenterSizeEnum.camp);
		getPop().setNationNo(getNationNo());
		getPop().setCapital(false);
		getPop().setHidden(false);
		getPop().setHarbor(HarborSizeEnum.none);
		getPop().setLoyalty(getCharacter().getEmmisaryTotal() / 2);
		addMessage("A camp named {pc} was created.");
		int si = Randomizer.skillIncrease(getCharacter().getEmmisary(), 1, 5);
		modifyProperty(getCharacter(), "emmisary", si, 0, 100);
		modifyProperty(getCharacter(), "emmisaryTotal", si, 0, 1000);
		addMessage("{char}'s emissary rank was improved by " + si + " points.");
	}
	
	

}
