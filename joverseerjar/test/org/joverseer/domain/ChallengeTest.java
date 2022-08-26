package org.joverseer.domain;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ChallengeTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public final void testParse() {
		Challenge challenge = new Challenge();
		
		challenge.setDescription("Challenge from Ajia-Nan-Buhn at 1410 "
				+ "In the Hills &amp; Rough of 1410 a ritual duel began. A large circle was drawn in the parade grounds outside of the camp. "
				+ "As the residents of Tymerand gathered around, Ajia-Nan-Buhn, a healthy warrior stepped forth and called challenge. In answer, Targon, a healthy emissary stepped forth. Those watching calculated the odds at 2 to 1 in favour of the challenger. In a long and protracted battle lasting over 8 minutes, the combatants cut and slashed at each other, each apparently unable to fatally wound the other. "
				+ "In a sudden flurry of feints, disengages, and thrusts, Ajia-Nan-Buhn sensed an opening and drove his weapon into Targon's body, instantly killing him. "
				+ "Ajia-Nan-Buhn was noted to have suffered grievous wounds in the fight.");
		challenge.setHexNo(0);
		challenge.parse();
		assertEquals("Targon", challenge.loser);
		assertEquals("Ajia-Nan-Buhn", challenge.victor);
		
		
		challenge.setDescription("In the Open Plains of 2119 a ritual duel began. A large circle was drawn on the paving stones near the market. "
				+ "As Cykur's army stood by, Cykur, a healthy warrior stepped forth and called challenge. In answer, Anarion, a healthy agent stepped forth. Those watching calculated the odds at 2 to 1 in favour of the challenger. The fight began with Anarion taking the initiative. Anarion stepped forward with flashing daggers while Cykur ducked, parried and counterthrust. " 
		        + "Suddenly, Anarion slipped within his opponent's guard and dealt Cykur a fatal wound. " 
		        + "Anarion was noted to have suffered minor wounds in the fight.");
		challenge.setHexNo(0);
		challenge.parse();
		assertEquals("Cykur", challenge.loser);
		assertEquals("Anarion", challenge.victor);
		
		challenge.setDescription(". "
				+ ". "
				+ "As the residents of Betelgeuse gathered around, Barbossa, a healthy agent stepped forth and called challenge. "
				+ "In answer, Bubba, a sorely wounded warrior stepped forth. "
				+ "Bubba swung with a mightly blow and felled Barbossa immediately!");
		challenge.setHexNo(0);
		challenge.parse();
		assertEquals("Barbossa", challenge.loser);
		assertEquals("Bubba", challenge.victor);

		challenge.setDescription(". "
				+ ". "
				+ "As the residents of Betelgeuse gathered around, Amroth, a healthy agent stepped forth and called challenge. "
				+ "In answer, The Blue Wizard, a sorely wounded warrior stepped forth. "
				+ "Finally, Amroth fell to a savage barrage of spells by The Blue Wizard.");
		challenge.parse();
		assertEquals("Amroth", challenge.loser);
		assertEquals("The Blue Wizard", challenge.victor);

		challenge.setDescription(". "
				+ ". "
				+ "As the residents of Betelgeuse gathered around, Lorenet, a healthy agent stepped forth and called challenge. "
				+ "In answer, Throkmaw, a sorely wounded warrior stepped forth. "
				+ "Finally, Lorenet fell to a savage barrage of blows by Throkmaw. Throkmaw was noted to have suffered no wounds in the fight.");
		challenge.parse();
		assertEquals("Lorenet", challenge.loser);
		assertEquals("Throkmaw", challenge.victor);

		challenge.setDescription(". "
				+ ". "
				+ "As the residents of Betelgeuse gathered around, Ringlin, a healthy agent stepped forth and called challenge. "
				+ "In answer, Ringlin, a sorely wounded warrior stepped forth. "
				+ "Ringlin relinquished his spirit and gasped his last.");
		challenge.parse();
		assertEquals("Ringlin", challenge.loser);
		// clearly wrong. but kept for regression testing
		assertEquals("Ringlin", challenge.victor);
		

	}


}
