package org.joverseer.support.readers.newXml;

import static org.junit.Assert.*;

import org.joverseer.support.readers.pdf.OrderResult;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class CharacterMessageWrapperTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		CharacterMessageWrapper cmw = new CharacterMessageWrapper();
		OrderResult or = null;
		or = cmw.getOwnedLATOrderResult("He was ordered to cast a lore spell. Locate Artifact True - artifact #200, a Sword,  is possessed by Cadell of Einion in the Shore/Plains at 1614.");
		assertNotNull(or);
		or = cmw.getLATOrderResult("He was ordered to cast a lore spell. Locate Artifact True - artifact #29, a Ring, is located in the Open Plains at 3029. ");
		assertNotNull(or);
	}

}
