package org.joverseer.engine.orders;

import java.util.ArrayList;

import org.joverseer.domain.Character;
import org.joverseer.domain.Order;
import org.joverseer.engine.ErrorException;
import org.joverseer.engine.ExecutingOrder;
import org.joverseer.engine.ExecutingOrderUtils;
import org.joverseer.engine.Randomizer;
import org.joverseer.game.Game;
import org.joverseer.game.Turn;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.metadata.domain.ArtifactInfo;
import org.joverseer.support.movement.MovementDirection;
import org.joverseer.support.movement.MovementUtils;

public class CastLocationSpellOrder extends ExecutingOrder {

	public CastLocationSpellOrder(Order order) {
		super(order);
	}

	@Override
	public void doExecute(Game game, Turn turn) throws ErrorException {
		addMessage("{char} was ordered to cast a spell.");
		
		int spellId = getParameterInt(0);
		if (!loadSpell(spellId)) {
			addMessage("{char} was unable to cast {spell} because {gp} did not know it.");
			return;
		}
		
		int roll = ExecutingOrderUtils.spellCastRoll(getCharacter(), getSpellProficiency());
		
		if (Randomizer.success(roll)) {
			//TODO improve proficiency
			doExecuteSpell(game, turn);
		} else {
			addMessage("{char} failed to cast {spell}.");
		}
		
	}
	
	protected void doExecuteSpell(Game game, Turn turn) {
		addMessage("{spell} was cast.");
		int spellId = getSpellProficiency().getSpellId();
		if (spellId == 434) {
			int hexNo = getParameterInt(1);
			if (getHex() != hexNo) {
				addMessage("Nothing was revealed because {char} was not at " + hexNo + ".");
				return;
			}
			if (!loadPopCenter(turn, hexNo)) {
				addMessage("There is no population center at " + hexNo + ".");
				return;
			}
			if (!getPop().getHidden()) {
				addMessage("{pc} is not hidden.");
				return;
			}
			int roll = Randomizer.roll(getCharacter().getMageTotal());
			addMessage("{pc} is located at " + hexNo + ".");
			if (Randomizer.success(roll)) {
				getPop().setHidden(false);
				addMessage("{pc} is no longer hidden.");
			} else {
				addMessage("{pc} is still hidden.");
			}
		} else if (spellId == 430 || spellId == 420) {
			String id = getParameter(1);
			if (!loadCharacter2(turn, id)) {
				addMessage("No character with id " + id + " was found.");
				return;
			}
			int hexNo = getCharacter2().getHexNo();
			String str = "at";
			if (spellId == 420) // rc
			{
				int i = Randomizer.roll(0, 6);
				MovementDirection dir = MovementDirection.values()[i];
				hexNo = MovementUtils.getHexNoAtDir(hexNo, dir);
				str = "at or near";
			}
			addMessage("{char2} is located " + str + " " + hexNo + ".");
		} else if (spellId == 428 || spellId == 418) {
			int artiNo = getParameterInt(1);
			ArtifactInfo artifactInfo = (ArtifactInfo)game.getMetadata().getArtifacts().findFirstByProperty("no", artiNo);
			if (artifactInfo == null) {
				addMessage("No artifact with id " + artiNo + " was found.");
				return;
			}
			Character holder = null;
			for (Character c : (ArrayList<Character>)turn.getContainer(TurnElementsEnum.Character).getItems()) {
				for (Integer an : c.getArtifacts()) {
					if (an == artiNo) {
						holder = c;
						break;
					}
				}
			}
			if (holder == null) {
				//TODO fix
				addMessage(artifactInfo.getName() + " is not currently held by anyone.");
				return;
			} else {
				setCharacter2(holder);
				int hexNo = getCharacter2().getHexNo();
				String str = "at";
				if (spellId == 418) // la
				{
					int i = Randomizer.roll(0, 6);
					MovementDirection dir = MovementDirection.values()[i];
					hexNo = MovementUtils.getHexNoAtDir(hexNo, dir);
					str = "at or near";
				}
				addMessage(artifactInfo.getName() + " is held by {char2} " + str + " " + hexNo + ".");
			}
		}
	}
	
	

}
