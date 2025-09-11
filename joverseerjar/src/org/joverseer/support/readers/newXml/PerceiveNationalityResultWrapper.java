package org.joverseer.support.readers.newXml;

import org.joverseer.domain.Character;
import org.joverseer.game.Game;
import org.joverseer.game.Turn;
import org.joverseer.support.Container;
import org.joverseer.support.infoSources.InfoSource;
import org.joverseer.support.infoSources.TurnInfoSource;
import org.joverseer.support.infoSources.spells.DerivedFromPerceiveNationalityInfoSource;
import org.joverseer.support.infoSources.spells.DerivedFromSpellInfoSource;
import org.joverseer.support.readers.pdf.OrderResult;

public class PerceiveNationalityResultWrapper implements OrderResult {
	String characterName;
	int nationNo;

	@Override
	public void updateGame(Game game, Turn turn, int nationNoOfCaster, String casterName) {
		Container<Character> chars = turn.getCharacters();
		Character c = chars.findFirstByProperty("name", getCharacterName());
		DerivedFromPerceiveNationalityInfoSource is1 = new DerivedFromPerceiveNationalityInfoSource(turn.getTurnNo(), nationNoOfCaster, casterName);
		if (c == null) {
			// character not found, add
			c = new Character();
			c.setName(getCharacterName());
			c.setId(Character.getIdFromName(getCharacterName()));
			c.setHexNo(0);
			c.setNationNo(this.getNationNo());
			c.setInfoSource(is1);
			chars.addItem(c);
		} else {
			// character found
			// examine info source
			InfoSource is = c.getInfoSource();
			if (TurnInfoSource.class.isInstance(is)) {
				// turn import, do nothing
				return;
			} else if (DerivedFromSpellInfoSource.class.isInstance(is)) {
				// spell
				// add info source...
				if (!((DerivedFromSpellInfoSource) is).contains(is1)) {
					((DerivedFromSpellInfoSource) is).addInfoSource(is1);
				}
				if(c.getNationNo() == 0) c.setNationNo(this.getNationNo());				
			}
		}
		
	}

	public String getCharacterName() {
		return this.characterName;
	}

	public void setCharacterName(String characterName) {
		this.characterName = characterName;
	}

	public int getNationNo() {
		return this.nationNo;
	}

	public void setNationNo(int nationNo) {
		this.nationNo = nationNo;
	}

}
