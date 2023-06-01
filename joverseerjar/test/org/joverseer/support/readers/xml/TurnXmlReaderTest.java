package org.joverseer.support.readers.xml;

import java.util.ArrayList;

import org.joverseer.domain.PopulationCenter;
import org.joverseer.domain.PopulationCenterSizeEnum;
import org.joverseer.game.Turn;
import org.joverseer.metadata.GameMetadata;
import org.joverseer.support.infoSources.InfoSource;
import org.junit.Test;

public class TurnXmlReaderTest {

	@Test
	public final void test() {
		int turnNo=1, tiNationNo=1, nationCapitalHex=101;
		ArrayList<PopCenterWrapper> pcws = new ArrayList<PopCenterWrapper>();
		ArrayList<PopulationCenter> currentNationPops = new ArrayList<PopulationCenter>();
		GameMetadata gm = new GameMetadata();
		InfoSource infoSource = new InfoSource();
		Turn turn = new Turn();
		PopulationCenter pcT0 = new PopulationCenter();
		PopCenterWrapper pcwT1 = new PopCenterWrapper();
		pcT0.setX(31);
		pcT0.setY(23);
		pcT0.setNationNo(2);
		pcT0.setSize(PopulationCenterSizeEnum.village);
		pcwT1.setHexID(pcT0.getHexNo());
		pcwT1.setNation(1);
		pcwT1.setSize(PopulationCenterSizeEnum.camp.getCode());
		
		TurnXmlReader.updateOldPCs(turn, turnNo, tiNationNo, nationCapitalHex, pcws, infoSource, currentNationPops, gm);
	}

}
