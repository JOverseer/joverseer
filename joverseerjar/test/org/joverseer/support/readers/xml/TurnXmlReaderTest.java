package org.joverseer.support.readers.xml;

import static org.junit.Assert.assertEquals;
import java.util.ArrayList;

import org.joverseer.domain.HarborSizeEnum;
import org.joverseer.domain.InformationSourceEnum;
import org.joverseer.domain.PopulationCenter;
import org.joverseer.domain.PopulationCenterSizeEnum;
import org.joverseer.game.Turn;
import org.joverseer.metadata.GameMetadata;
import org.joverseer.metadata.GameTypeEnum;
import org.joverseer.support.infoSources.InfoSource;
import org.junit.Test;

public class TurnXmlReaderTest {

	@Test
	public final void testNationAndNameAddedToUnknown() {
		// addresses a bug when these two reports were in the same import.
		//<PopCentre HexID="3028"><Name>Unknown (Map Icon)</Name><Nation>0</Nation><NationAllegience>0</NationAllegience><FortificationLevel>1</FortificationLevel><Size>3</Size><Dock>0</Dock><Capital>0</Capital><Loyalty>0</Loyalty><InformationSource>4</InformationSource><Hidden>0</Hidden></PopCentre>
		//<PopCentre HexID="3028"><Name>Tir Ethraid</Name><Nation>18</Nation><NationAllegience>0</NationAllegience><FortificationLevel>1</FortificationLevel><Size>3</Size><Dock>0</Dock><Capital>0</Capital><Loyalty>0</Loyalty><InformationSource>1</InformationSource><Hidden>0</Hidden></PopCentre>
		int turnNo=1, tiNationNo=1, nationCapitalHex=101;
		ArrayList<PopCenterWrapper> pcws = new ArrayList<PopCenterWrapper>();
		ArrayList<PopulationCenter> currentNationPops = new ArrayList<PopulationCenter>();
		GameMetadata gm = new GameMetadata();
		PopulationCenter pcUnderTest;
		gm.setGameType(GameTypeEnum.game1650);
		InfoSource infoSourceT0 = new InfoSource();
		infoSourceT0.setTurnNo(0);
		InfoSource infoSourceT20 = new InfoSource();
		infoSourceT20.setTurnNo(0);
		Turn turn = new Turn();
		PopulationCenter pcT0 = new PopulationCenter();
		PopCenterWrapper pcwT1 = new PopCenterWrapper();
		PopCenterWrapper pcwT2 = new PopCenterWrapper();
		pcT0.setX(30);
		pcT0.setY(28);
		pcT0.setNationNo(0);
		pcT0.setName("Unknown (Map Icon)");
		pcT0.setSize(PopulationCenterSizeEnum.town);
		pcT0.setInfoSource(infoSourceT0);
		turn.getPopulationCenters().addItem(pcT0);

		pcwT1.setHexID(pcT0.getHexNo());
		pcwT1.setNation(0);
		pcwT1.setName("Unknown (Map Icon)");
		pcwT1.setSize(PopulationCenterSizeEnum.town.getCode());
		pcwT1.setInformationSource(4); // this is xml# = vague
		pcwT2.setHexID(pcT0.getHexNo());
		pcwT2.setNation(18);
		pcwT2.setName("Tir Ethraid");
		pcwT2.setInformationSource(1); // this is xml# = limited
		pcwT1.setSize(PopulationCenterSizeEnum.town.getCode());
		pcws.add(pcwT1);
		pcws.add(pcwT2);
		
		// check arrangement worked. just to spell out what we are trying to test here.
		pcUnderTest = turn.getPopCenter(pcT0.getHexNo());
		assertEquals("failed to set nation", 0 ,pcUnderTest.getNationNo().intValue());
		assertEquals("failed to set name", "Unknown (Map Icon)" ,pcUnderTest.getName());
		
		TurnXmlReader.updateOldPCs(turn, turnNo, tiNationNo, nationCapitalHex, pcws, infoSourceT20, currentNationPops, gm);
		
		pcUnderTest = turn.getPopCenter(pcT0.getHexNo());
		assertEquals("failed to spot nation information", 18,pcUnderTest.getNationNo().intValue());
		assertEquals("failed to correct name", "Tir Ethraid" ,pcUnderTest.getName());
	}

	@Test
	public final void testDocks() {
		// addresses a bug when these two reports were in the same import.
		// n14 reports, PDF shows docks JO doesn't
		//<PopCentre HexID="2137"><Name>Unknown (Map Icon)</Name><Nation>0</Nation><NationAllegience>0</NationAllegience><FortificationLevel>2</FortificationLevel><Size>4</Size><Dock>2</Dock><Capital>0</Capital><Loyalty>0</Loyalty><InformationSource>4</InformationSource><Hidden>0</Hidden></PopCentre>
		//<PopCentre HexID="2137"><Name>Eithel Culroch</Name><Nation>6</Nation><NationAllegience>0</NationAllegience><FortificationLevel>2</FortificationLevel><Size>4</Size><Dock>0</Dock><Capital>0</Capital><Loyalty>0</Loyalty><InformationSource>1</InformationSource><Hidden>0</Hidden></PopCentre>
		//<HexID>2137</HexID><Terrain>5</Terrain><PopcentreName>Unknown (Map Icon)</PopcentreName><PopcentreSize>4</PopcentreSize><Forts>2</Forts><Ports>2</Ports><Roads>1</Roads><Bridges>1</Bridges><Fords>1</Fords><MinorRivers>1</MinorRivers><MajorRivers>1</MajorRivers><Free>0</Free><Dark>1</Dark><Neutral>0</Neutral><Index>23</Index></Hex>
		int turnNo=8, tiNationNo=14, nationCapitalHex=101;
		ArrayList<PopCenterWrapper> pcws = new ArrayList<PopCenterWrapper>();
		ArrayList<PopulationCenter> currentNationPops = new ArrayList<PopulationCenter>();
		GameMetadata gm = new GameMetadata();
		gm.setNewXmlFormat(true);
		PopulationCenter pcUnderTest;
		gm.setGameType(GameTypeEnum.gameKS);
		InfoSource infoSourceT7 = new InfoSource();
		infoSourceT7.setTurnNo(7);
		InfoSource infoSourceT8 = new InfoSource();
		infoSourceT8.setTurnNo(8);
		Turn turn = new Turn();
		PopulationCenter pcT0 = new PopulationCenter();
		pcT0.setX(21);
		pcT0.setY(37);
		pcT0.setNationNo(1);
		pcT0.setName("Eithel Culroch");
		pcT0.setSize(PopulationCenterSizeEnum.village);
		pcT0.setInfoSource(infoSourceT7);
		pcT0.setInformationSource(InformationSourceEnum.exhaustive);
		pcT0.setHarbor(HarborSizeEnum.none);
		turn.getPopulationCenters().addItem(pcT0);

		PopCenterWrapper pcwT1 = new PopCenterWrapper();
		pcwT1.setHexID(pcT0.getHexNo());
		pcwT1.setNation(0);
		pcwT1.setName("Unknown (Map Icon)");
		pcwT1.setSize(PopulationCenterSizeEnum.village.getCode());
		pcwT1.setDock(0);
		pcwT1.setInformationSource(4);
		pcws.add(pcwT1);

		PopCenterWrapper pcwT2 = new PopCenterWrapper();
		pcwT2.setHexID(pcT0.getHexNo());
		pcwT2.setNation(1);
		pcwT2.setName("Eithel Culroch");
		pcwT2.setInformationSource(1); 
		pcwT2.setSize(PopulationCenterSizeEnum.village.getCode());
		pcwT2.setDock(0);
		pcws.add(pcwT2);
		
		//check the mistaken mapping of XML InformationSource to enum is as expected.
		// PopCenterWrapper.getPopulationCenter() is slight
		PopulationCenter tempPC = pcwT1.getPopulationCenter();
		assertEquals("Information source of T1",InformationSourceEnum.limited, tempPC.getInformationSource());
		tempPC = pcwT2.getPopulationCenter();
		assertEquals("Information source of T2",InformationSourceEnum.exhaustive, tempPC.getInformationSource());
		TurnXmlReader.updateOldPCs(turn, turnNo, tiNationNo, nationCapitalHex, pcws, infoSourceT8, currentNationPops, gm);
		
		pcUnderTest = turn.getPopCenter(pcT0.getHexNo());
		assertEquals("failed to remove dock", HarborSizeEnum.none ,pcUnderTest.getHarbor());
		
	}
	
	@Test
	public final void testDocks2() {
		// addresses a bug when these two reports were in the same import.
		// n14 reports, PDF shows docks JO doesn't
		//<PopCentre HexID="2137"><Name>Unknown (Map Icon)</Name><Nation>0</Nation><NationAllegience>0</NationAllegience><FortificationLevel>2</FortificationLevel><Size>4</Size><Dock>2</Dock><Capital>0</Capital><Loyalty>0</Loyalty><InformationSource>4</InformationSource><Hidden>0</Hidden></PopCentre>
		//<PopCentre HexID="2137"><Name>Eithel Culroch</Name><Nation>6</Nation><NationAllegience>0</NationAllegience><FortificationLevel>2</FortificationLevel><Size>4</Size><Dock>0</Dock><Capital>0</Capital><Loyalty>0</Loyalty><InformationSource>1</InformationSource><Hidden>0</Hidden></PopCentre>
		//<HexID>2137</HexID><Terrain>5</Terrain><PopcentreName>Unknown (Map Icon)</PopcentreName><PopcentreSize>4</PopcentreSize><Forts>2</Forts><Ports>2</Ports><Roads>1</Roads><Bridges>1</Bridges><Fords>1</Fords><MinorRivers>1</MinorRivers><MajorRivers>1</MajorRivers><Free>0</Free><Dark>1</Dark><Neutral>0</Neutral><Index>23</Index></Hex>
		int turnNo=8, tiNationNo=14, nationCapitalHex=101;
		ArrayList<PopCenterWrapper> pcws = new ArrayList<PopCenterWrapper>();
		ArrayList<PopulationCenter> currentNationPops = new ArrayList<PopulationCenter>();
		GameMetadata gm = new GameMetadata();
		gm.setNewXmlFormat(true);
		PopulationCenter pcUnderTest;
		gm.setGameType(GameTypeEnum.gameKS);
		InfoSource infoSourceT7 = new InfoSource();
		infoSourceT7.setTurnNo(7);
		InfoSource infoSourceT8 = new InfoSource();
		infoSourceT8.setTurnNo(8);
		Turn turn = new Turn();
		PopulationCenter pcT0 = new PopulationCenter();
		pcT0.setX(21);
		pcT0.setY(37);
		pcT0.setNationNo(1);
		pcT0.setName("Eithel Culroch");
		pcT0.setSize(PopulationCenterSizeEnum.village);
		pcT0.setInfoSource(infoSourceT7);
		pcT0.setInformationSource(InformationSourceEnum.exhaustive);
		pcT0.setHarbor(HarborSizeEnum.harbor);
		turn.getPopulationCenters().addItem(pcT0);

//		PopCenterWrapper pcwT1 = new PopCenterWrapper();
//		pcwT1.setHexID(pcT0.getHexNo());
//		pcwT1.setNation(0);
//		pcwT1.setName("Unknown (Map Icon)");
//		pcwT1.setSize(PopulationCenterSizeEnum.village.getCode());
//		pcwT1.setDock(1);
//		pcwT1.setInformationSource(4);
//		pcws.add(pcwT1);

		PopCenterWrapper pcwT2 = new PopCenterWrapper();
		pcwT2.setHexID(pcT0.getHexNo());
		pcwT2.setNation(1);
		pcwT2.setName("Eithel Culroch");
		pcwT2.setInformationSource(1); 
		pcwT2.setSize(PopulationCenterSizeEnum.village.getCode());
		pcwT2.setDock(0);
		pcws.add(pcwT2);
		
		//check the mistaken mapping of XML InformationSource to enum is as expected.
		// PopCenterWrapper.getPopulationCenter() is slight
		//PopulationCenter tempPC = pcwT1.getPopulationCenter();
		//assertEquals("Information source of T1",InformationSourceEnum.limited, tempPC.getInformationSource());
		PopulationCenter tempPC = pcwT2.getPopulationCenter();
		assertEquals("Information source of T2",InformationSourceEnum.exhaustive, tempPC.getInformationSource());
		TurnXmlReader.updateOldPCs(turn, turnNo, tiNationNo, nationCapitalHex, pcws, infoSourceT8, currentNationPops, gm);
		
		pcUnderTest = turn.getPopCenter(pcT0.getHexNo());
		assertEquals("failed to keep dock", HarborSizeEnum.harbor ,pcUnderTest.getHarbor());
		
	}


}
