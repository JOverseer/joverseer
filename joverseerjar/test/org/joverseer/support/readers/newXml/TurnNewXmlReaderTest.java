package org.joverseer.support.readers.newXml;

import static org.junit.Assert.*;

import org.joverseer.domain.PopulationCenter;
import org.joverseer.domain.PopulationCenterSizeEnum;
import org.joverseer.game.Game;
import org.joverseer.game.Turn;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.support.Container;
import org.joverseer.support.infoSources.XmlExtraTurnInfoSource;
import org.junit.Test;

public class TurnNewXmlReaderTest {

	@Test
	public final void testUpdateBattles() {
		// minimal setup as a framework to test parsing battle outcomes.
		final int nationNo = 1;
		Game game = new Game();
		Turn turn = new Turn();
		try {
			game.addTurn(turn);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			assertFalse("exception when adding a turn",true);
		}
		TurnNewXmlReader xml = new TurnNewXmlReader(game, null, nationNo);
		xml.turnInfo = new TurnInfo();
		xml.turnInfo.battles = new Container<BattleWrapper>();
		// don't think this how the battles appear in real life.
		BattleWrapper bw = new BattleWrapper();
		BattleLine bl = new BattleLine();
		bl.setText("Battle at 3123\r\n");
		bw.addLine(bl);
		bl = new BattleLine();
		bl.setText("\r\n"
				+ "In the Cool climate of the Mountains of 3123, armies prepared for battle in the early morning hours under a omen-filled sky.\r\n"
				+ "\r\n"
				+ "At the head of a demoralized army rode Hero Éowyn of the nation of the Rivendell.  The mount on which she rode stood cautiously at the rear of the battle lines.  Behind her the forming ranks were filled with:\r\n"
				+ "  400 Heavy Infantry with wooden weapons, none armor, a mob\r\n"
				+ "\r\n"
				+ "The Camp of Torech Ungol flying the flag of the Mordor is situated in the Mountains here.\r\n"
				+ "Éowyn's forces found no enemy armies to fight.\r\n"
				+ "The battle for Torech Ungol  was over even before it began. The attackers were so numerous and strong that the defending militia had little chance to save themselves. The battle was over in just a few hours!!\r\n"
				+ "After the attack on the population center. …\r\n"
				+ "Éowyn's army survived the attack on the Camp, but suffered minor losses.  Éowyn appeared to have survived.\r\n"
				+ "The Camp has been reduced to a Ruins.\r\n"
				+ "The Ruins of Torech Ungol now flies no flag.\r\n");
		bw.addLine(bl);
		xml.turnInfo.battles.addItem(bw);
		
		xml.infoSource = new XmlExtraTurnInfoSource(game.getMaxTurn(), nationNo);
		xml.turn = game.getTurn(game.getMaxTurn());
		
		Container<PopulationCenter> pcs = turn.getContainer(TurnElementsEnum.PopulationCenter);
		PopulationCenter pc = new PopulationCenter();
		pc.setHexNo(3123);
		pc.setNationNo(4);
		pc.setName("Torech Ungol");
		pc.setSize(PopulationCenterSizeEnum.camp);
		pcs.addItem(pc);
		xml.updateBattles(game);
		assertEquals("was expecting ruins",PopulationCenterSizeEnum.ruins, pc.getSize());
		assertEquals("was expecting unknown",0, pc.getNationNo().intValue());
	}

}
