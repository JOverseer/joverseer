package org.joverseer.support.readers.newXml;

import static org.junit.Assert.*;

import org.joverseer.metadata.GameMetadata;
import org.joverseer.metadata.domain.Nation;
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
		
		or = cmw.getOwnedLAOrderResult("She was ordered to cast a lore spell. Locate Artifact - Dwarven Ring of Power #10may be possessed by\r\nThrelin at or near 3218.");
		assertNotNull(or);
		assertEquals("org.joverseer.support.readers.pdf.LocateArtifactResultWrapper", or.getClass().getName());
		assertEquals("Dwarven Ring of Power",((org.joverseer.support.readers.pdf.LocateArtifactResultWrapper)or).getArtifactName());
		assertEquals(10,((org.joverseer.support.readers.pdf.LocateArtifactResultWrapper)or).getArtifactNo());
		assertEquals("Threlin",((org.joverseer.support.readers.pdf.LocateArtifactResultWrapper)or).getOwner());
		assertEquals(3218,((org.joverseer.support.readers.pdf.LocateArtifactResultWrapper)or).getHexNo());
		
		// artifact appearing twice
		or = cmw.getRAResult("He was ordered to cast a lore spell.  Research Artifact -  Calris Light Cleaver #164 is a Sword - allegiance: None - increases combat damage by 1000 pts. Possession of the artifact can allow casting of the spell Divine Nation Forces.  Research Artifact -  Castamir’s Bane #165 is an Axe - allegiance: None - increases combat damage by 750 pts. Possession of the artifact can allow casting of the spell Heal True.  Research Artifact -  Castamir’s Bane #165 is an Axe - allegiance: None - increases combat damage by 750 pts. Possession of the artifact can allow casting of the spell Heal True.", is);
		assertNotNull(or);
		assertEquals("org.joverseer.support.readers.newXml.RAResultWrapper", or.getClass().getName());
		
		or = cmw.getScryResult("He was ordered to cast a lore spell. Scry Area - Foreign armies identified:" + System.lineSeparator() + 
			" Vinitharya of the  Greensward with about 100 troops at 2913" + System.lineSeparator() + 
			". See report below. ",
			is,gm);
		assertNotNull(or);
		assertEquals("org.joverseer.support.readers.newXml.ReconResultWrapper", or.getClass().getName());
		// currently not pulling out the armies...
		//assertEquals(1,((org.joverseer.support.readers.newXml.ReconResultWrapper)or).armies.size());
		
		or = cmw.getOwnedLATOrderResult("He was ordered to cast a lore spell. Locate Artifact True - artifact #200, a Sword,  is possessed by Cadell of Einion in the Shore/Plains at 1614.");
		assertNotNull(or);
		assertEquals("org.joverseer.support.readers.pdf.LocateArtifactTrueResultWrapper", or.getClass().getName());
		assertEquals(1614,((org.joverseer.support.readers.pdf.LocateArtifactResultWrapper)or).getHexNo());
		// fails
		//assertEquals("a sword",((org.joverseer.support.readers.pdf.LocateArtifactResultWrapper)or).getArtifactName());
		//assertEquals("Cadell of Einion",((org.joverseer.support.readers.pdf.LocateArtifactResultWrapper)or).getOwner());
		assertEquals(200,((org.joverseer.support.readers.pdf.LocateArtifactResultWrapper)or).getArtifactNo());
		
		// older format:
		or = cmw.getLATOrderResult("He was ordered to cast a lore spell. Locate Artifact True - artifact #29, a Ring, is located in the Open Plains at 3029. ");
		assertNotNull(or);
		assertEquals("org.joverseer.support.readers.pdf.LocateArtifactTrueResultWrapper", or.getClass().getName());

		//
		or = cmw.getOwnedLATOrderResult("She was ordered to cast a lore spell. Locate Artifact True - Ring of Wind #99 is possessed by Jí Indûr in the Mountains at 1234."); 
		assertNotNull(or);
		assertEquals("org.joverseer.support.readers.pdf.LocateArtifactTrueResultWrapper", or.getClass().getName());
		assertEquals(99,((org.joverseer.support.readers.pdf.LocateArtifactResultWrapper)or).getArtifactNo());
		assertEquals(1234,((org.joverseer.support.readers.pdf.LocateArtifactResultWrapper)or).getHexNo());
		assertEquals("Ring of Wind",((org.joverseer.support.readers.pdf.LocateArtifactResultWrapper)or).getArtifactName());
		assertEquals("Jí Indûr",((org.joverseer.support.readers.pdf.LocateArtifactResultWrapper)or).getOwner());
		
		or = cmw.getLATOrderResult("He was ordered to cast a lore spell. Locate Artifact True - Boots of Tracelessness #99 is located in the Mountains at 1234."); 
		assertNotNull(or);
		assertEquals("org.joverseer.support.readers.pdf.LocateArtifactTrueResultWrapper", or.getClass().getName());
		assertEquals(99,((org.joverseer.support.readers.pdf.LocateArtifactResultWrapper)or).getArtifactNo());
		assertEquals("Boots of Tracelessness",((org.joverseer.support.readers.pdf.LocateArtifactResultWrapper)or).getArtifactName());
		assertEquals(1234,((org.joverseer.support.readers.pdf.LocateArtifactResultWrapper)or).getHexNo());
		
		or = cmw.getRCTOrderResult("He was ordered to cast a lore spell. Reveal Character True - Frodo is located in the Shore/Plains at 2324.");
		assertNotNull(or);
		assertEquals("org.joverseer.support.readers.pdf.RevealCharacterTrueResultWrapper", or.getClass().getName());
		assertEquals(2324,((org.joverseer.support.readers.pdf.RevealCharacterTrueResultWrapper)or).getHexNo());
		assertEquals("Frodo",((org.joverseer.support.readers.pdf.RevealCharacterTrueResultWrapper)or).getCharacterName());
		
		gm.getNations().add(new Nation(1,"Rivendell","Riv"));
		or = cmw.getPalantirResult("He was ordered to use a scrying artifact. The Arkenstone #17 was used. Foreign armies identified:  Éowyn of the Rivendell with about 100 troops at 3423 . See report below.",is,gm);
		assertNotNull(or);
		assertEquals("org.joverseer.support.readers.newXml.ReconResultWrapper", or.getClass().getName());
		assertEquals(1,((org.joverseer.support.readers.newXml.ReconResultWrapper)or).armies.size());
		assertEquals(1,((org.joverseer.support.readers.newXml.ReconResultWrapper)or).armies.get(0).getNationNo().intValue());
		assertEquals("3423",((org.joverseer.support.readers.newXml.ReconResultWrapper)or).armies.get(0).getHexNo());
		assertEquals("Éowyn",((org.joverseer.support.readers.newXml.ReconResultWrapper)or).armies.get(0).getCommanderName());

	}

}
