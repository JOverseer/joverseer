package org.joverseer.metadata;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import org.joverseer.domain.Army;
import org.joverseer.domain.Character;
import org.joverseer.domain.PopulationCenter;
import org.joverseer.game.Game;
import org.joverseer.metadata.domain.ArtifactInfo;
import org.joverseer.metadata.domain.Hex;
import org.joverseer.metadata.domain.Nation;
import org.joverseer.metadata.domain.NationMapRange;
import org.joverseer.metadata.domain.SpellInfo;
import org.joverseer.metadata.orders.OrderMetadata;
import org.joverseer.support.Container;
import org.springframework.core.io.Resource;
import org.springframework.richclient.application.Application;

/**
 * Holds metadata about the game such as 1. the game type and other game
 * instance stuff 2. information that depends on the game type, such as the
 * hexes, the artifacts, etc
 * 
 * @author Marios Skounakis
 */
public class GameMetadata implements Serializable {
	private static final long serialVersionUID = 8007105168749869584L;
	GameTypeEnum gameType;
	int gameNo;
	int nationNo;
	String additionalNations;
	boolean newXmlFormat = false;

	Game game;

	ArrayList<Nation> nations = new ArrayList<Nation>();
	Container<Hex> hexes = new Container<Hex>(new String[] { "hexNo" });
	Container<ArtifactInfo> artifacts = new Container<ArtifactInfo>(new String[] { "no" });
	Container<OrderMetadata> orders = new Container<OrderMetadata>(new String[] { "number" });
	Container<Character> characters = new Container<Character>(new String[] { "id", "name" });
	Container<Character> startDummyCharacters = new Container<Character>(new String[] { "id", "name" });
	Container<PopulationCenter> populationCenters = new Container<PopulationCenter>(new String[] { "hexNo" });
	Container<NationMapRange> nationMapRanges = new Container<NationMapRange>(new String[] { "nationNo" });
	Container<SpellInfo> spells = new Container<SpellInfo>(new String[] { "no" });
	Container<Army> armies = new Container<Army>(new String[] { "hexNo" });
	HashMap<Integer, Container<Hex>> hexOverrides = new HashMap<Integer, Container<Hex>>();
	ArrayList<MetadataReader> readers = new ArrayList<MetadataReader>();

	String basePath;

	public GameTypeEnum getGameType() {
		return gameType;
	}

	public void setGameType(GameTypeEnum gameType) {
		this.gameType = gameType;
	}

	public void setGame(Game game) {
		this.game = game;
	}

	public String getAdditionalNations() {
		return additionalNations;
	}

	public void setAdditionalNations(String additionalNations) {
		this.additionalNations = additionalNations;
		if (additionalNations != null) {
			String[] ps = additionalNations.split(",");
			additionalNations = "";
			for (String p : ps) {
				additionalNations = additionalNations + (additionalNations.equals("") ? "" : ",") + p;
			}
		}
	}

	public Collection<Hex> getHexes() {
		return hexes.getItems();
	}

	protected Hex getHexFromMetadata(int hexNo) {
		Hex h = hexes.findFirstByProperties(new String[] { "hexNo" }, new Object[] { hexNo });
		return h;
	}

	public Hex getHex(int hexNo) {
		return getHexForTurn(game.getCurrentTurn(), hexNo);
	}

	public void setHexes(Collection<Hex> hexes) {
		for (Hex h : hexes) {
			this.hexes.addItem(h);
		}
	}

	public ArrayList<Nation> getNations() {
		return nations;
	}

	public void setNations(Collection<Nation> nations) {
		this.nations.clear();
		this.nations.addAll(nations);
	}

	public void load() throws IOException, MetadataReaderException {
		for (MetadataReader r : getReaders()) {
			r.load(this);
		}
	}

	public ArrayList<MetadataReader> getReaders() {
		return readers;
	}

	public void setReaders(ArrayList<MetadataReader> readers) {
		this.readers = readers;
	}

	public int getGameNo() {
		return gameNo;
	}

	public void setGameNo(int gameNo) {
		this.gameNo = gameNo;
	}

	public Container<Character> getStartDummyCharacters() {
		return startDummyCharacters;
	}

	public void setStartDummyCharacters(Container<Character> startDummyCharacters) {
		this.startDummyCharacters = startDummyCharacters;
	}

	public boolean getNewXmlFormat() {
		return newXmlFormat;
	}

	public void setNewXmlFormat(boolean newXmlFormat) {
		this.newXmlFormat = newXmlFormat;
	}

	private void writeObject(java.io.ObjectOutputStream out) throws IOException {
		out.writeObject(getCharacters());
		out.writeObject(getArtifacts());
		out.writeObject(getSpells());
		out.writeObject(getOrders());
		out.writeObject(hexes);
		out.writeObject(getNations());
		out.writeObject(getNationMapRanges());
		out.writeObject(getGameType());
		out.writeObject(getGameNo());
		out.writeObject(getNationNo());
		out.writeObject(getNewXmlFormat());
		out.writeObject(getStartDummyCharacters());
		out.writeObject(hexOverrides);
	}

	@SuppressWarnings("unchecked")
	private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
		characters = (Container<Character>) in.readObject();
		artifacts = (Container<ArtifactInfo>) in.readObject();
		spells = (Container<SpellInfo>) in.readObject();
		orders = (Container<OrderMetadata>) in.readObject();
		hexes = (Container<Hex>) in.readObject();
		nations = (ArrayList<Nation>) in.readObject();
		nationMapRanges = (Container<NationMapRange>) in.readObject();
		setGameType((GameTypeEnum) in.readObject());
		setGameNo((Integer) in.readObject());
		setNationNo((Integer) in.readObject());
		try {
			setNewXmlFormat((Boolean) in.readObject());
		} catch (Exception e) {
			// do nothing, this may have not been set
		}
		try {
			setStartDummyCharacters((Container<Character>) in.readObject());
		} catch (Exception e) {
			// do nothing, this may have not been set
		}
		try {
			hexOverrides = (HashMap) in.readObject();
		} catch (Exception e) {
			// do nothing, this may have not been set
		}
	}

	public Nation getNationByNum(int number) {
		for (Nation n : getNations()) {
			if (n.getNumber() == number) {
				return n;
			}
		}
		return null;
	}

	public Nation getNationByName(String name) {
		for (Nation n : getNations()) {
			if (n.getName().equals(name)) {
				return n;
			}
		}
		return null;
	}

	public Container<ArtifactInfo> getArtifacts() {
		return artifacts;
	}

	public void setArtifacts(Container<ArtifactInfo> artifacts) {
		this.artifacts = artifacts;
	}

	public Container<PopulationCenter> getPopulationCenters() {
		return populationCenters;
	}

	public void setPopulationCenters(Container<PopulationCenter> populationCenters) {
		this.populationCenters = populationCenters;
	}

	public Container<OrderMetadata> getOrders() {
		if (orders != null) {
			OrderMetadata om = orders.findFirstByProperty("number", 225);
			if (om.getSkillRequirement().equals("MS")) {
				GameMetadata gm = (GameMetadata) Application.instance().getApplicationContext().getBean("gameMetadata");
				gm.setGameType(getGameType());

				try {
					gm.load();
					orders = gm.getOrders();
				} catch (Exception exc) {
					// do nothing
				}
			}
		}
		return orders;
	}

	public void setOrders(Container<OrderMetadata> orders) {
		this.orders = orders;
	}

	public String getBasePath() {
		File f = new File(basePath);
		try {
			if (f.exists())
				return f.getCanonicalPath();
			File cd = new File(".");
			String p = cd.getCanonicalPath() + "/" + basePath;
			return p;
		} catch (IOException exc) {
			return basePath;
		}
	}

	public void setBasePath(String basePath) {
		this.basePath = basePath;
	}

	public Container<Character> getCharacters() {
		return characters;
	}

	public void setCharacters(Container<Character> characters) {
		this.characters = characters;
	}

	public int getNationNo() {
		return nationNo;
	}

	public void setNationNo(int nationNo) {
		this.nationNo = nationNo;
	}

	public Container<NationMapRange> getNationMapRanges() {
		return nationMapRanges;
	}

	public void setNationMapRanges(Container<NationMapRange> nationMapRanges) {
		this.nationMapRanges = nationMapRanges;
	}

	public Container<Army> getArmies() {
		return armies;
	}

	public void setArmies(Container<Army> armies) {
		this.armies = armies;
	}

	public Container<SpellInfo> getSpells() {
		return spells;
	}

	public void setSpells(Container<SpellInfo> spells) {
		this.spells = spells;
	}

	public Container<Hex> getHexOverrides(int turnNo) {
		if (hexOverrides == null)
			hexOverrides = new HashMap<Integer, Container<Hex>>();
		if (hexOverrides.containsKey(turnNo)) {
			return hexOverrides.get(turnNo);
		}
		return new Container<Hex>();
	}

	public Hex getHexOverride(Container<Hex> hexes, int hexNo) {
		return hexes.findFirstByProperty("hexNo", hexNo);
	}

	public Hex getHexForTurn(int turnNo, int hexNo) {
		Hex h = getHexOverride(getHexOverrides(turnNo), hexNo);
		if (h != null)
			return h;
		return getHexFromMetadata(hexNo);
	}

	public void addHexOverride(int turnNo, Hex hex) {
		Container<Hex> hc;
		if (!hexOverrides.containsKey(turnNo)) {
			hc = new Container<Hex>(new String[] { "hexNo" });
			hexOverrides.put(turnNo, hc);
		}
		hc = getHexOverrides(turnNo);
		Hex h = getHexOverride(hc, hex.getHexNo());
		if (h != null)
			hc.removeItem(h);
		hc.addItem(hex);
	}

	public Resource getResource(String resourceName) {
		try {
			Resource r = Application.instance().getApplicationContext().getResource("file:///" + getBasePath() + "/" + resourceName);
			new InputStreamReader(r.getInputStream());
			return r;
		} catch (Exception exc) {
			try {
				System.out.println(exc.getMessage());
				Resource r = Application.instance().getApplicationContext().getResource("classpath:" + basePath + resourceName);
				new InputStreamReader(r.getInputStream());
				return r;
			} catch (Exception ex) {
				System.out.println(ex.getMessage());
				return null;
			}
		}
	}
}