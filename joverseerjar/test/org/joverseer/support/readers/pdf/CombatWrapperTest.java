package org.joverseer.support.readers.pdf;

import static org.junit.Assert.*;

import org.junit.Test;

public class CombatWrapperTest {

	@Test
	public final void testParseAll() {
		CombatWrapper cw = new CombatWrapper();
		cw.parseAll("Battle at 2119\r\n"
				+ "\r\n"
				+ "In the Mild climate of the Open Plains of 2119, armies prepared for battle in the early morning hours under a clear sky.\r\n"
				+ "\r\n"
				+ "At the head of a highly energetic army rode Commander Éomer of the nation of the Rivendell.  In his hands was borne the glowing Sword called Gúthwinë. The mount on which he rode cantered anxiously along the side of the battle lines.  Behind him the forming ranks were filled with:\r\n"
				+ "  1219 Heavy Cavalry with bronze weapons, bronze armor, ragged ranks\r\n"
				+ "\r\n"
				+ "At the head of a calm army rode Lord King of the Dead of the nation of the Erebor.  The mount on which he rode moved calmly to the front of the battle lines.  Behind him the forming ranks were filled with:\r\n"
				+ "  4981 Heavy Infantry with bronze weapons, bronze armor, ragged ranks\r\n"
				+ "\r\n"
				+ "At the head of a demoralized army rode Hero Ren the Unclean of the nation of the Isengard.  In his hands was borne the glowing Sword called Burning Blade. The mount on which he rode stood cautiously at the rear of the battle lines.  Behind him the forming ranks were filled with:\r\n"
				+ "  800 Heavy Infantry with wooden weapons, none armor, a mob\r\n"
				+ "\r\n"
				+ "The Major Town of Isengard flying the flag of the Isengard is situated in the Open Plains here. It is fortified by a Castle, and it is under siege or attack.\r\n"
				+ "Report from Éomer … \r\n"
				+ "My commanders moved among the troops before battle, readying them, bolstering their resolve, and issuing last minute orders. These troops didn't need much encouragement and waved and joked with their Commanders, offering bets on who would find the best plunder. \r\n"
				+ "Finally the order was given in loud commands.  \"Charge!! Charge!!\"Éomer reports that against the forces of Ren the Unclean, they met our charge with their standard formation.\r\n"
				+ "After the battle had joined in earnest, heroes made their presence known all over the battlefield.\r\n"
				+ "Éomer  rode into the enemy ranks with his glowing Sword and cut down a score of foes before they knew what had happened.\r\n"
				+ "Ren the Unclean  fought side by side with the troops and diverted many a blade during the pitched battle with his glowing Sword.\r\n"
				+ "After the battle. …Éomer's forces were victorious in the battle, but suffered minor losses.  Éomer appeared to have survived. King of the Dead's forces were victorious in the battle, but suffered minor losses.  King of the Dead appeared to have survived. Ren the Unclean's forces were destroyed/routed in the battle. Ren the Unclean appeared to have survived but suffers from serious wounds.  The battle for Isengard  was over even before it began. The attackers were so numerous and strong that the defending militia had little chance to save themselves. The battle was over in just a few hours!! After the attack on the population center. …Éomer's army survived the attack on the Major Town, but suffered some losses.  Éomer appeared to have survived. King of the Dead's army survived the attack on the Major Town, but suffered some losses.  King of the Dead appeared to have survived. The Major Town has been reduced to a Town.  The Castle has not been affected. The Town has been under siege/attack this turn. The Town now flies the flag of the Erebor.");
		assertEquals("failed to spot PC capture","captured",cw.getPopCenterOutcome());
		assertEquals("failed to extrace nation name of winner","Erebor",cw.getPopOutcomeNation());
		assertEquals("hex no should not be.",0,cw.hexNo);
		assertEquals("naval",false,cw.naval);
		assertEquals("unrecognised fortification","Castle",cw.getPopFort());
		assertEquals("unrecognised PC name","Isengard",cw.getPopName());
		// note this is the starting size.
		assertEquals("unrecognised PC size","Major Town",cw.getPopSize());
	}
	@Test
	public final void testNoForcesToFight()
	{
		CombatWrapper cw = new CombatWrapper();
		cw.parseAll("Battle at 3123\r\n"
				+ "\r\n"
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
		assertEquals("failed to spot PC capture","destroyed",cw.getPopCenterOutcome());
		assertEquals("failed to extract nation name of winner",null,cw.getPopOutcomeNation());
		assertEquals("hex no should not be.",0,cw.hexNo);
		assertEquals("unrecognised fortification",null,cw.getPopFort());
	}

}
