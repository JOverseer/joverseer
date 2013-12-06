package org.joverseer.support.readers.newXml;

import java.util.ArrayList;

import org.joverseer.domain.Army;
import org.joverseer.domain.Character;
import org.joverseer.game.Game;
import org.joverseer.game.Turn;
import org.joverseer.support.readers.pdf.OrderResult;
import org.joverseer.support.readers.xml.TurnXmlReader;

public class ScoutHexResult implements OrderResult {

	ArrayList<Army> armies = new ArrayList<Army>();

	@Override
	public void updateGame(Game game, Turn turn, int nationNo, String character) {
		Character c = turn.getCharByName(character);
		for (Army a : this.armies) {
			a.setX(c.getX());
			a.setY(c.getY());
			TurnXmlReader.addArmy(a, game, turn, true);
		}
	}

	public void addArmy(Army a) {
		this.armies.add(a);
	}

}
