package org.joverseer.engine.orders;

import org.joverseer.domain.Order;
import org.joverseer.domain.PopulationCenter;
import org.joverseer.engine.ErrorException;
import org.joverseer.engine.ExecutingOrder;
import org.joverseer.game.Game;
import org.joverseer.game.Turn;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.metadata.domain.ArtifactInfo;

public class UseHidingArtifactOrder extends ExecutingOrder {

	public UseHidingArtifactOrder(Order order) {
		super(order);
	}

	@Override
	public void doExecute(Game game, Turn turn) throws ErrorException {
		int artiNo = getParameterInt(0);
		
		addMessage("{char} was ordered to use a hiding artifact.");
		
		if (!loadPopCenter(turn)) {
			addMessage("{char} failed to execute the order because there was no population center at {starthex}.");
			return;
		}
		
		ArtifactInfo ai = (ArtifactInfo)game.getMetadata().getArtifacts().findFirstByProperty("no", artiNo);
		
		if (!getCharacter().getArtifacts().contains(artiNo)) {
			addMessage("{char} failed to execute the order because {gp} did not posess artifact " + artiNo + ".");
			return;
		}
		
		if (ai == null) {
			throw new ErrorException("Artifact " + artiNo + " not found!");
		}
		
		if (!ai.getPower1().contains("Hide PC") &&
				!ai.getPower2().contains("Hide PC")) {
			addMessage("{char} failed to execute the order because " + ai.getName() + " cannot be used to hide a population center.");
			return;
		}
		
		if (ai.getCurrentlyHiddenPopCenter() != -1) {
			PopulationCenter pc = (PopulationCenter)turn.getContainer(TurnElementsEnum.PopulationCenter).findFirstByProperty("hexNo", ai.getCurrentlyHiddenPopCenter());
			if (pc != null) {
				pc.setHidden(false);
				addMessage(pc.getName() + " is no longer hidden.");
			}
		}
		getPop().setHidden(true);
		addMessage("{pc} is now hidden.");
		
	}
	
	

}
