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
		// clearly wrong.
		assertEquals("Ringlin", challenge.victor);
		

	}


}
