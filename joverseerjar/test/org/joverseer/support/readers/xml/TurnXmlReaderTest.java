package org.joverseer.support.readers.xml;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

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


}
