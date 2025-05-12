package org.joverseer.support.readers.newXml;

import java.util.ArrayList;

import org.joverseer.domain.Character;
import org.joverseer.game.Game;
import org.joverseer.game.Turn;
import org.joverseer.support.infoSources.InfoSource;
import org.joverseer.support.infoSources.PdfTurnInfoSource;
import org.joverseer.support.readers.pdf.OrderResult;

public class ScoutCharsResult implements OrderResult {

	ArrayList<Character> characters = new ArrayList<Character>();

	@Override
	public void updateGame(Game game, Turn turn, int nationNo, String character) {
		Character c = turn.getCharByName(character);
		int hexNo = c.getHexNo();
		InfoSource is = new PdfTurnInfoSource(turn.getTurnNo(), nationNo);
		for (Character ch : this.characters) {
			if (turn.getCharById(ch.getId()) != null) {
				Character tC = turn.getCharById(ch.getId());
				if(tC.getNationNo() == 0) tC.setNationNo(ch.getNationNo());
				if(tC.getTitle() == null) tC.setTitle(ch.getTitle());
				continue;
			}
			ch.setInfoSource(is);
			ch.setHexNo(hexNo);
			turn.getCharacters().addItem(ch);
		}

	}

	public void addCharacter(Character c) {
		this.characters.add(c);
	}

}
