package org.joverseer.support.readers.xml;

import java.util.ArrayList;

import org.joverseer.domain.HexInfo;

/**
 * Holds the nation info from xml turns (empty pop hexes and population hexes)
 * 
 * @author Marios Skounakis
 */
public class NationInfoWrapper {
	String emptyPopHexes;
	String popHexes;

	ArrayList<String> rumors = new ArrayList<String>();

	public ArrayList<String> getRumors() {
		return rumors;
	}

	public void setRumors(ArrayList<String> rumors) {
		this.rumors = rumors;
	}

	public String getEmptyPopHexes() {
		return emptyPopHexes;
	}

	public void setEmptyPopHexes(String emptyPopHexes) {
		this.emptyPopHexes = emptyPopHexes;
	}

	public String getPopHexes() {
		return popHexes;
	}

	public void setPopHexes(String popHexes) {
		this.popHexes = popHexes;
	}

	public ArrayList<HexInfo> getHexInfos(int nationNo) {
		ArrayList<HexInfo> ret = new ArrayList<HexInfo>();
		String[] emptyHexes = getEmptyPopHexes().split(",");
		String[] popHexes = getPopHexes().split(",");

		for (String eh : emptyHexes) {
			int ehi = Integer.parseInt(eh);
			HexInfo hi = new HexInfo();
			hi.getNationSources().add(nationNo);
			hi.setVisible(true);
			hi.setHasPopulationCenter(false);
			hi.setHexNo(ehi);
			ret.add(hi);
		}

		for (String ph : popHexes) {
			int phi = Integer.parseInt(ph);
			HexInfo hi = new HexInfo();
			hi.getNationSources().add(nationNo);
			hi.setVisible(true);
			hi.setHasPopulationCenter(true);
			hi.setHexNo(phi);
			ret.add(hi);
		}
		return ret;
	}
}
