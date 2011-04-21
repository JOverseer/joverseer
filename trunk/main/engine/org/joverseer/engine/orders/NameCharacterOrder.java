package org.joverseer.engine.orders;

import org.joverseer.domain.Character;
import org.joverseer.domain.CharacterDeathReasonEnum;
import org.joverseer.domain.InformationSourceEnum;
import org.joverseer.domain.Order;
import org.joverseer.engine.ErrorException;
import org.joverseer.engine.ExecutingOrder;
import org.joverseer.engine.ExecutingOrderUtils;
import org.joverseer.game.Game;
import org.joverseer.game.Turn;
import org.joverseer.metadata.SNAEnum;

public class NameCharacterOrder extends ExecutingOrder {

	public NameCharacterOrder(Order order) {
		super(order);
	}

	@Override
	public void doExecute(Game game, Turn turn) throws ErrorException {
		String name = getParameter(0);
		String gender = getParameter(1);
		int c = 0;
		int a = 0;
		int e = 0;
		int m = 0;
		if (getOrderNo() == 725) {
			if (!isCommander())
				return;
			c = getParameterInt(2);
			checkParamInt(c, "Invalid command rank");
			a = getParameterInt(3);
			checkParamInt(a, "Invalid agent rank");
			e = getParameterInt(4);
			checkParamInt(e, "Invalid emissary rank");
			m = getParameterInt(5);
			checkParamInt(m, "Invalid mage rank");
			if (c + a + e + m > 30) {
				throw new ErrorException("Total ranks > 30");
			}
		} else if (getOrderNo() == 728) {
			int min = 30;
			if (ExecutingOrderUtils.hasSNA(game, getNationNo(), SNAEnum.CommandersAt40))
				min = 40;
			c = Math.min(min, getCharacter().getCommandTotal());
			if (!isCommander()) {
				addMessage("{char} failed to execute the order because {gp} is not a commander.");
				return;
			}
		} else if (getOrderNo() == 731) {
			if (!isAgent()) {
				addMessage("{char} failed to execute the order because {gp} is not an agent.");
				return;
			}
			int min = 30;
			if (ExecutingOrderUtils.hasSNA(game, getNationNo(), SNAEnum.AgentsAt40))
				min = 40;
			a = Math.min(min, getCharacter().getAgentTotal());
		} else if (getOrderNo() == 734) {
			if (!isEmissary()) {
				addMessage("{char} failed to execute the order because {gp} is not an emissary.");
				return;
			}
			int min = 30;
			if (ExecutingOrderUtils.hasSNA(game, getNationNo(), SNAEnum.EmmisariesAt40))
				min = 40;
			e = Math.min(min, getCharacter().getEmmisaryTotal());
		} else if (getOrderNo() == 737) {
			if (!isMage()) {
				addMessage("{char} failed to execute the order because {gp} is not a mage.");
				return;
			}
			int min = 30;
			if (ExecutingOrderUtils.hasSNA(game, getNationNo(), SNAEnum.MagesAt40))
				min = 40;
			m = Math.min(min, getCharacter().getMageTotal());
		}

		loadPopCenter(turn);
		addMessage("{char} was ordered to name a new character.");
		if (!isAtCapital()) {
			addMessage("{char} was unable top name a new character because he was not at the capital.");
		}

		consumeCost(game, turn);

		Character newChar = new Character();
		newChar.setNationNo(getNationNo());

		if (name == null || name.equals("")) {
			name = getNewCharName(turn);
		} else {
			if (nameInUse(turn, name)) {
				addMessage("There is already a character named " + name + ".");
				name = getNewCharName(turn);
				addMessage("Using new name " + name + ".");
			}
		}
		newChar.setName(name);
		newChar.setCommand(c);
		newChar.setCommandTotal(c);
		newChar.setAgent(a);
		newChar.setAgentTotal(a);
		newChar.setMage(m);
		newChar.setMageTotal(m);
		newChar.setEmmisary(e);
		newChar.setEmmisaryTotal(e);
		newChar.setHealth(100);
		newChar.setHexNo(getHex());
		newChar.setInformationSource(InformationSourceEnum.exhaustive);
		newChar.setInfoSource(getInfoSource(turn));
		newChar.setDeathReason(CharacterDeathReasonEnum.NotDead);
		newChar.setId(Character.getIdFromName(name));
		turn.getCharacters().addItem(newChar);
		addMessage("A new character named " + name + " was named.");
	}

	protected boolean nameInUse(Turn turn, String name) {
		return ExecutingOrderUtils.getCharacter(turn, name) != null;
	}

	protected String getNewCharName(Turn turn) {
		String name = "Ch";
		int i = 1;
		do {
			if (nameInUse(turn, name + i)) {
				i++;
			} else {
				return name + i;
			}
		} while (true);
	}

}
