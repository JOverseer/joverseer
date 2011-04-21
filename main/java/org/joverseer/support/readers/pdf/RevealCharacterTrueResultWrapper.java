package org.joverseer.support.readers.pdf;

import org.joverseer.domain.Character;
import org.joverseer.game.Game;
import org.joverseer.game.Turn;
import org.joverseer.support.Container;
import org.joverseer.support.infoSources.InfoSource;
import org.joverseer.support.infoSources.TurnInfoSource;
import org.joverseer.support.infoSources.spells.DerivedFromLocateArtifactInfoSource;
import org.joverseer.support.infoSources.spells.DerivedFromRevealCharacterInfoSource;
import org.joverseer.support.infoSources.spells.DerivedFromRevealCharacterTrueInfoSource;
import org.joverseer.support.infoSources.spells.DerivedFromSpellInfoSource;

public class RevealCharacterTrueResultWrapper implements OrderResult {
	String characterName;
	int hexNo;

	public String getCharacterName() {
		return characterName;
	}

	public void setCharacterName(String characterName) {
		this.characterName = characterName;
	}

	public int getHexNo() {
		return hexNo;
	}

	public void setHexNo(int hexNo) {
		this.hexNo = hexNo;
	}

	public void updateGame(Game game, Turn turn, int nationNo, String casterName) {
		Container<Character> chars = turn.getCharacters();
		Character c = chars.findFirstByProperty("name", getCharacterName());
		DerivedFromRevealCharacterTrueInfoSource is1 = new DerivedFromRevealCharacterTrueInfoSource(turn.getTurnNo(), nationNo, casterName, getHexNo());
		if (c == null) {
			// character not found, add
			c = new Character();
			c.setName(getCharacterName());
			c.setId(Character.getIdFromName(getCharacterName()));
			c.setHexNo(getHexNo());
			c.setNationNo(0);
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
				// check if it is LA or RC
				if (DerivedFromLocateArtifactInfoSource.class.isInstance(is) || DerivedFromRevealCharacterInfoSource.class.isInstance(is)) {
					// replace info source and hexNo
					c.setHexNo(hexNo);
					c.setInfoSource(is1);
				} else {
					// info source is LAT or RCT
					// add
					if (!((DerivedFromSpellInfoSource) is).contains(is1)) {
						((DerivedFromSpellInfoSource) is).addInfoSource(is1);
					}
				}
			}
		}

	}
}