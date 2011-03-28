package org.joverseer.support.readers.newXml;

import java.util.ArrayList;

import org.joverseer.domain.Army;
import org.joverseer.domain.ArmySizeEnum;
import org.joverseer.domain.Character;
import org.joverseer.domain.InformationSourceEnum;
import org.joverseer.domain.PopulationCenter;
import org.joverseer.domain.ProductEnum;
import org.joverseer.game.Game;
import org.joverseer.game.Turn;
import org.joverseer.metadata.domain.Nation;
import org.joverseer.support.NationMap;
import org.joverseer.support.readers.pdf.OrderResult;
import org.joverseer.support.readers.xml.TurnXmlReader;

public class ScoutPopResult implements OrderResult {

	PopulationCenter pc;
	ArrayList<String> foreignArmies = new ArrayList<String>();

	public void updateGame(Game game, Turn turn, int nationNo, String character) {
		Character c = turn.getCharByName(character);
		PopulationCenter oldPop = turn.getPopCenter(pc.getName());
		if (oldPop != null) {
			// do not replace oldPop, simply update it...
			if (oldPop.getLoyalty() == 0)
				oldPop.setLoyalty(pc.getLoyalty());
			if (oldPop.getCapital() != pc.getCapital()) {
				PopulationCenter oldCapital = turn.getCapital(pc.getNationNo());
				if (oldCapital != null) {
					oldCapital.setCapital(false);
					turn.getPopulationCenters().refreshItem(oldCapital);
				}
				oldPop.setCapital(pc.getCapital());
				oldPop.setNationNo(pc.getNationNo());
			}
			for (ProductEnum p : ProductEnum.values()) {
				oldPop.setProduction(p, pc.getProduction(p));
				oldPop.setStores(p, pc.getStores(p));
			}
			turn.getPopulationCenters().refreshItem(oldPop);

		} else {
			turn.getPopulationCenters().addItem(pc);
		}
		for (String nation : foreignArmies) {
			Army a = new Army();
			a.setCommanderName("Unknown (Map Icon)");
			a.setCommanderTitle("");
			Nation n = NationMap.getNationFromName(nation);
			a.setNationNo(n.getNumber());
			a.setInformationSource(InformationSourceEnum.someMore);
			a.setInfoSource(pc.getInfoSource());
			a.setNationAllegiance(n.getAllegiance());
			a.setX(c.getX());
			a.setY(c.getY());
			a.setSize(ArmySizeEnum.unknown);
			TurnXmlReader.addArmy(a, game, turn, true);
		}
	}

	public void addArmy(String nationName) {
		foreignArmies.add(nationName);
	}

	public void setPopulationCenter(PopulationCenter pc) {
		this.pc = pc;
	}
}
