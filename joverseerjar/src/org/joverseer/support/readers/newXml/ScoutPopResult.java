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

	@Override
	public void updateGame(Game game, Turn turn, int nationNo, String character) {
		Character c = turn.getCharByName(character);
		PopulationCenter oldPop = turn.getPopCenter(this.pc.getName());
		if (oldPop != null) {
			// do not replace oldPop, simply update it...
			if (oldPop.getLoyalty() == 0)
				oldPop.setLoyalty(this.pc.getLoyalty());
			if (oldPop.getCapital() != this.pc.getCapital()) {
				PopulationCenter oldCapital = turn.getCapital(this.pc.getNationNo());
				if (oldCapital != null) {
					oldCapital.setCapital(false);
					turn.getPopulationCenters().refreshItem(oldCapital);
				}
				oldPop.setCapital(this.pc.getCapital());
				oldPop.setNationNo(this.pc.getNationNo());
			}
			for (ProductEnum p : ProductEnum.values()) {
				oldPop.setProduction(p, this.pc.getProduction(p));
				oldPop.setStores(p, this.pc.getStores(p));
			}
			turn.getPopulationCenters().refreshItem(oldPop);

		} else {
			turn.getPopulationCenters().addItem(this.pc);
		}
		for (String nation : this.foreignArmies) {
			Army a = new Army();
			a.setCommanderName("Unknown (Map Icon)");
			a.setCommanderTitle("");
			Nation n = NationMap.getNationFromName(nation);
			a.setNationNo(n.getNumber());
			a.setInformationSource(InformationSourceEnum.someMore);
			a.setInfoSource(this.pc.getInfoSource());
			a.setNationAllegiance(n.getAllegiance());
			a.setX(c.getX());
			a.setY(c.getY());
			a.setSize(ArmySizeEnum.unknown);
			TurnXmlReader.addArmy(a, game, turn, true);
		}
	}

	public void addArmy(String nationName) {
		this.foreignArmies.add(nationName);
	}

	public void setPopulationCenter(PopulationCenter pc) {
		this.pc = pc;
	}
}
