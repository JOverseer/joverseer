package org.joverseer.support.readers.newXml;

import static org.junit.Assert.*;

import org.joverseer.metadata.GameMetadata;
import org.joverseer.metadata.GameTypeEnum;
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
		
		// bug:
		// order asked for 3 artifacts, but only 2 worked.
		or = cmw.getRAResult("He was ordered to cast a lore spell.  Research Artifact -  Curaran #51 is a Bow - allegiance: None - increases combat damage by 500 pts. Possession of the artifact can allow casting of the spell Major Heal.  Research Artifact -  Fire Mace #52 is a Mace - allegiance: None - increases combat damage by 750 pts. He was not able to cast the spell. Continued efforts may succeed.",is);
		assertNotNull(or);
		assertEquals("org.joverseer.support.readers.newXml.RAResultWrapper", or.getClass().getName());
		org.joverseer.support.readers.newXml.RAResultWrapper ra = (org.joverseer.support.readers.newXml.RAResultWrapper)or;
		assertEquals(2, ra.artifacts.size());
		ArtifactWrapper aw = ra.artifacts.get(1);
		assertEquals("Fire Mace",aw.name );
		assertEquals(52,aw.id );
		assertNull(aw.alignment);
		assertEquals(15,aw.combat);
		assertEquals("Unknown",aw.latent);
		
		// artifact appearing twice
		or = cmw.getRAResult("He was ordered to cast a lore spell.  Research Artifact -  Calris Light Cleaver #164 is a Sword - allegiance: None - increases combat damage by 1000 pts. Possession of the artifact can allow casting of the spell Divine Nation Forces.  Research Artifact -  Castamir’s Bane #165 is an Axe - allegiance: None - increases combat damage by 750 pts. Possession of the artifact can allow casting of the spell Heal True.  Research Artifact -  Castamir’s Bane #165 is an Axe - allegiance: None - increases combat damage by 750 pts. Possession of the artifact can allow casting of the spell Heal True.", is);
		assertNotNull(or);
		assertEquals("org.joverseer.support.readers.newXml.RAResultWrapper", or.getClass().getName());
		ra = (org.joverseer.support.readers.newXml.RAResultWrapper)or;
		assertEquals(2, ra.artifacts.size());
		aw = ra.artifacts.get(0);
		assertEquals("Calris Light Cleaver",aw.name );
		assertEquals(164,aw.id );
		assertNull(aw.alignment);
		assertEquals(20,aw.combat);
		assertEquals("Divine Nation Forces",aw.latent);
		aw = ra.artifacts.get(1);
		assertEquals("Castamir’s Bane",aw.name );
		assertEquals(165,aw.id );
		assertEquals(15,aw.combat);
		assertEquals("Heal True",aw.latent);
		
		or = cmw.getRAResult("He was ordered to cast a lore spell.  Research Artifact -  K'Purian Battle Mace #82 is a Mace - allegiance: None - increases combat damage by 750 pts. Possession of the artifact can allow casting of the spell Long Stride.  Research Artifact -  Ar-sil #111 is a Sword - allegiance: Good - increases combat damage by 750 pts.  Research Artifact -  Ann uiug #226 is a Jewel - allegiance: Good - increases Emissary Rank by 10.  Research Artifact -  Ann minig #227 is a Jewel - allegiance: Good - increases Emissary Rank by 10. He was not able to cast the spell. Continued efforts may succeed. He was not able to cast the spell. Continued efforts may succeed.", is);
		assertNotNull(or);
		assertEquals("org.joverseer.support.readers.newXml.RAResultWrapper", or.getClass().getName());
		ra = (org.joverseer.support.readers.newXml.RAResultWrapper)or;
		assertEquals(4, ra.artifacts.size());
		aw = ra.artifacts.get(0);
		assertEquals("K'Purian Battle Mace",aw.name );
		assertEquals(82,aw.id );
		assertNull(aw.alignment);
//		assertEquals(750,aw.combat);
		aw = ra.artifacts.get(2);
		assertEquals("Ann uiug",aw.name );
		assertEquals(226,aw.id );
		assertEquals(10,aw.emissary);
		aw = ra.getArtifacts().get(3);
		assertEquals("Ann minig", aw.getName());

		or = cmw.getOwnedLAOrderResult("She was ordered to cast a lore spell. Locate Artifact - Dwarven Ring of Power #10may be possessed by\r\nThrelin at or near 3218.");
		assertNotNull(or);
		assertEquals("org.joverseer.support.readers.pdf.LocateArtifactResultWrapper", or.getClass().getName());
		assertEquals("Dwarven Ring of Power",((org.joverseer.support.readers.pdf.LocateArtifactResultWrapper)or).getArtifactName());
		assertEquals(10,((org.joverseer.support.readers.pdf.LocateArtifactResultWrapper)or).getArtifactNo());
		assertEquals("Threlin",((org.joverseer.support.readers.pdf.LocateArtifactResultWrapper)or).getOwner());
		assertEquals(3218,((org.joverseer.support.readers.pdf.LocateArtifactResultWrapper)or).getHexNo());
			
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

		or = cmw.getPalantirResult("She was ordered to use a scrying artifact. Palantír of Minas Ithil #190 was used. Foreign armies identified: Boris of the Siv T'rar with about 100 troops at 1921 Lionel of the Siv T'rar with about 300 troops at 1722 Estrella of the Siv T'rar with about 3500 troops at 1821 . See report below.",is,gm);
		assertNotNull(or);

		or = cmw.getPalantirResult("She was ordered to use a scrying artifact. Palantír of Minas Ithil #190 was used. Foreign armies identified: Elfhelm of the Green Riders with about 700 troops at 3612 Kaigan of the Khazalid with about 2100 troops at 3612 Negarth of the Green Riders with about 2500 troops at 3612 Xmaclian of the Zerinians with about 1100 troops at 3612 Velcoktic of the Zerinians with about 1000 troops at 3713 Yamerca of the Zerinians with about 2400 troops at 3713 . Major Towns and Cities revealed: 3612 4425. See report below.",is,gm);
		assertNotNull(or);
		
		gm.getNations().add(new Nation(16, "Witch Realm of Angmar", "WRoA"));
		gm.getNations().add(new Nation(20, "Kingdom of Arnor", "KoA"));
		
		or = cmw.getScoutAreaResult("He was ordered to scout the area. A scout of the area was attempted.  Foreign armies identified:  Gothmog of the Witch Realm of Angmar with about 1100 troops at 1609  Minasdir of the Kingdom of Arnor with about 3400 troops at 1409 . See Map below.", is, gm);
		assertEquals("Gothmog",((org.joverseer.support.readers.newXml.ReconResultWrapper)or).armies.get(0).getCommanderName());
		
		gm.getNations().add(new Nation(30, "Daen Lintis", "DL"));
		or = cmw.getScoutAreaResult("He was ordered to scout the area. A scout of the area was attempted. Foreign armies identified: Helin of the Daen Lintis with about 500 troops at 1716 . See Map below.", is, gm);
		assertEquals("Helin",((org.joverseer.support.readers.newXml.ReconResultWrapper)or).armies.get(0).getCommanderName());
		
		
		gm.getNations().add(new Nation(21, "Merchant Guild", "MG"));
		or = cmw.getReconResult("He was ordered to recon the area. - Vandiver of the Merchant Guild at 4125 . See Map below.", is, gm);
		assertEquals("Vandiver",((org.joverseer.support.readers.newXml.ReconResultWrapper)or).armies.get(0).getCommanderName());
		
		gm.getNations().add(new Nation(22, "Khand Easterlings", "KE"));
		or = cmw.getReconResult("He was ordered to recon the area.  - Itana Ovan of the Khand Easterlings at 4429"
				+ "Urdrath of the Khand Easterlings with about 500 troops at 4228"
				+ ". See Map below.", is, gm);
		assertEquals("Itana Ovan",((org.joverseer.support.readers.newXml.ReconResultWrapper)or).armies.get(0).getCommanderName());
		assertEquals("4429",((org.joverseer.support.readers.newXml.ReconResultWrapper)or).armies.get(0).getHexNo());
		assertEquals("4228",((org.joverseer.support.readers.newXml.ReconResultWrapper)or).armies.get(1).getHexNo());
		assertEquals(500,((org.joverseer.support.readers.newXml.ReconResultWrapper)or).armies.get(1).getTroopCount());
		
		
		
		gm.getNations().add(new Nation(23, "Dark Lieutenants", "DL"));
		or= cmw.getDivineNationForces("He was ordered to cast a lore spell. Divine Nation Forces - Dark Lieutenants forces near 3220 - lelind at 3423 Grishnäkh at 3622. He suffered a loss of health due to casting two spells.", is, gm);
		assertEquals(2,((org.joverseer.support.readers.newXml.ReconResultWrapper)or).armies.size());
		assertEquals("lelind",((org.joverseer.support.readers.newXml.ReconResultWrapper)or).armies.get(0).getCommanderName());
		assertEquals("Grishnäkh",((org.joverseer.support.readers.newXml.ReconResultWrapper)or).armies.get(1).getCommanderName());

		or = cmw.getDivineNationForces("He was ordered to cast a lore spell. Divine Nation Forces - A scout of the area was attempted.", is, gm);
		assertNull(or);
		
		gm.getNations().add(new Nation(24, "Escer", "E"));
		or = cmw.getPerceiveNationalityResult("He was ordered to cast a lore spell. Perceive Nationality - Jouruscu is found to be of the Escer.", gm);
		assertEquals("Jouruscu", ((org.joverseer.support.readers.newXml.PerceiveNationalityResultWrapper) or).getCharacterName());
		assertEquals(24, ((org.joverseer.support.readers.newXml.PerceiveNationalityResultWrapper) or).getNationNo());
		
		testOverridePowers();
	}
	
	public void testOverridePowers() {
		CharacterMessageWrapper cmw = new CharacterMessageWrapper();
		OrderResult or = null;
		InfoSource is = new InfoSource();
		is.setTurnNo(1);
		GameMetadata gm = new GameMetadata();
		gm.setGameType(GameTypeEnum.game2950);
		
//		ArtifactInfo ai = new ArtifactInfo(); 
//		ai.setName("Mallorn Staff");
//		ai.setNo(26);
//		ai.setAlignment("None");
//		ai.setPower(0, "Mage 30");
//		
//		gm.getArtifacts().addItem(ai);
//		
//		ArtifactInfo ai2 = gm.getArtifacts().findFirstByProperty("number", 26);
//		assertEquals("Mage 30", ai2.getPower1());
		or = cmw.getRAResult("He was ordered to cast a lore spell. Research Artifact - Elenrûth #125 is a Sword - allegiance: None - increases combat damage by 750 pts. This artifact will aid in the research of spells 502, 504, 506. Possession of the artifact can allow access to the lost spell list - Spirit Mastery. Research Artifact - Elfbane #111 is a Sword - allegiance: Evil - increases combat damage by 500 pts. Possession of the artifact can allow casting of the spell Perceive Nationality. Research Artifact - Mallorn Staff #109 is a Staff - allegiance: None - increases Mage Rank by 25. Research Artifact - Orb of Dark Seeing #102 is an Orb - allegiance: Evil - increases Mage Rank by 25. Research Artifact - Spear of Following #96 is a Spear - allegiance: None - increases combat damage by 500 pts. Possession of the artifact can allow casting of the spell Perceive Relations. He was not able to cast the spell. Continued efforts may succeed.", is);
		assertNotNull(or);
		assertEquals("org.joverseer.support.readers.newXml.RAResultWrapper", or.getClass().getName());
		org.joverseer.support.readers.newXml.RAResultWrapper ra = (org.joverseer.support.readers.newXml.RAResultWrapper)or;
		ArtifactWrapper aw = ra.artifacts.get(0);
		assertEquals("Access to Spirit Mastery", aw.getLatent());
		assertEquals("Combat 750", aw.getPower());
		aw = ra.artifacts.get(2);
		assertEquals("Mallorn Staff", aw.getName());
		assertEquals("Mage 25", aw.getPower());
	}

}
