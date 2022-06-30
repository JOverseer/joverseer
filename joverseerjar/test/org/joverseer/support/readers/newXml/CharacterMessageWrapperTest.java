package org.joverseer.support.readers.newXml;

import static org.junit.Assert.*;

import org.joverseer.metadata.GameMetadata;
import org.joverseer.support.infoSources.InfoSource;
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
		InfoSource is = new InfoSource();
		is.setTurnNo(1);
		GameMetadata gm = new GameMetadata();
		or = cmw.getScryResult("He was ordered to cast a lore spell. Scry Area - Foreign armies identified:" + System.lineSeparator() + 
			" Vinitharya of the  Greensward with about 100 troops at 2913" + System.lineSeparator() + 
			". See report below. ",
			is,gm);
			
		assertNotNull(or);
		or = cmw.getOwnedLATOrderResult("He was ordered to cast a lore spell. Locate Artifact True - artifact #200, a Sword,  is possessed by Cadell of Einion in the Shore/Plains at 1614.");
		assertNotNull(or);
		or = cmw.getLATOrderResult("He was ordered to cast a lore spell. Locate Artifact True - artifact #29, a Ring, is located in the Open Plains at 3029. ");
		assertNotNull(or);
	}

}
