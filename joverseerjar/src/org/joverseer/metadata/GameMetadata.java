package org.joverseer.metadata;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.logging.Logger;

import org.joverseer.domain.Army;
import org.joverseer.domain.Character;
import org.joverseer.domain.ClimateEnum;
import org.joverseer.domain.PopulationCenter;
import org.joverseer.game.Game;
import org.joverseer.metadata.domain.ArtifactInfo;
import org.joverseer.metadata.domain.ClimateModifier;
import org.joverseer.metadata.domain.Hex;
import org.joverseer.metadata.domain.HexTerrainEnum;
import org.joverseer.metadata.domain.Nation;
import org.joverseer.metadata.domain.NationAllegianceEnum;
import org.joverseer.metadata.domain.NationMapRange;
import org.joverseer.metadata.domain.SpellInfo;
import org.joverseer.metadata.domain.TerrainModifier;
import org.joverseer.metadata.orders.OrderMetadata;
import org.joverseer.support.CommentedBufferedReader;
import org.joverseer.support.Container;
import org.joverseer.support.GameHolder;
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
	String colourSet;

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
	Container<TerrainModifier> terrainModifiers = new Container<TerrainModifier>();
	Container<ClimateModifier> climateModifiers = new Container<ClimateModifier>();

	String basePath;

	public GameTypeEnum getGameType() {
		return this.gameType;
	}

	public void setGameType(GameTypeEnum gameType) {
		this.gameType = gameType;
	}

	public void setGame(Game game) {
		this.game = game;
	}
	
	public void loadCombatModifiers(boolean override) throws IOException, MetadataReaderException {
		//load defaults from config files that are not included in saved game
		if (this.climateModifiers == null || this.climateModifiers.items.isEmpty() || override) {
			this.basePath = "metadata";
			CombatModifierReader r = new CombatModifierReader();
			r.load(this);
		}
	}

	public String getAdditionalNations() {
		return this.additionalNations;
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
		return this.hexes.getItems();
	}

	protected Hex getHexFromMetadata(int hexNo) {
		Hex h = this.hexes.findFirstByProperties(new String[] { "hexNo" }, new Object[] { Integer.valueOf(hexNo) });
		return h;
	}

	public Hex getHex(int hexNo) {
		return getHexForTurn(this.game.getCurrentTurn(), hexNo);
	}

	public void setHexes(Collection<Hex> hexes) {
		for (Hex h : hexes) {
			this.hexes.addItem(h);
		}
	}

	public ArrayList<Nation> getNations() {
		return this.nations;
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
		return this.readers;
	}

	public void setReaders(ArrayList<MetadataReader> readers) {
		this.readers = readers;
	}

	public int getGameNo() {
		return this.gameNo;
	}

	public void setGameNo(int gameNo) {
		this.gameNo = gameNo;
	}

	public Container<Character> getStartDummyCharacters() {
		return this.startDummyCharacters;
	}

	public void setStartDummyCharacters(Container<Character> startDummyCharacters) {
		this.startDummyCharacters = startDummyCharacters;
	}

	public boolean getNewXmlFormat() {
		return this.newXmlFormat;
	}

	public void setNewXmlFormat(boolean newXmlFormat) {
		this.newXmlFormat = newXmlFormat;
	}
	
	public String getColourSet() {
		return this.colourSet;
	}

	public void setColourSet(String colourSet) {
		this.colourSet = colourSet;
	}

	private void writeObject(java.io.ObjectOutputStream out) throws IOException {
		out.writeObject(getCharacters());
		out.writeObject(getArtifacts());
		out.writeObject(getSpells());
		out.writeObject(getOrders());
		out.writeObject(this.hexes);
		out.writeObject(getNations());
		out.writeObject(getNationMapRanges());
		out.writeObject(getGameType());
		out.writeObject(Integer.valueOf(getGameNo()));
		out.writeObject(Integer.valueOf(getNationNo()));
		out.writeObject(getNewXmlFormat());
		out.writeObject(getStartDummyCharacters());
		out.writeObject(this.hexOverrides);
		out.writeObject(getClimateModifiers());
		out.writeObject(getTerrainModifiers());
		out.writeObject(this.colourSet);
	}

	private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException, MetadataReaderException {
		this.characters = (Container<Character>) in.readObject();
		this.artifacts = (Container<ArtifactInfo>) in.readObject();
		this.spells = (Container<SpellInfo>) in.readObject();
		this.orders = (Container<OrderMetadata>) in.readObject();
		this.hexes = (Container<Hex>) in.readObject();
		this.nations = (ArrayList<Nation>) in.readObject();
		this.nationMapRanges = (Container<NationMapRange>) in.readObject();
		setGameType((GameTypeEnum) in.readObject());
		setGameNo(((Integer) in.readObject()).intValue());
		setNationNo(((Integer) in.readObject()).intValue());
		try {
			setNewXmlFormat(((Boolean) in.readObject()).booleanValue());
		} catch (Exception e) {
			// do nothing, this may have not been set
		}
		try {
			setStartDummyCharacters((Container<Character>) in.readObject());
		} catch (Exception e) {
			// do nothing, this may have not been set
		}
		try {
			this.hexOverrides = (HashMap) in.readObject();
		} catch (Exception e) {
			// do nothing, this may have not been set
		}
		try {
			setClimateModifiers((Container<ClimateModifier>) in.readObject());
		} catch (Exception e) {
			// do nothing, this may have not been set
		}		
		try {
			setTerrainModifiers((Container<TerrainModifier>) in.readObject());
		} catch (Exception e) {
			// do nothing, this may have not been set
		}	
		try {
			setColourSet((String) in.readObject());
		} catch (Exception e) {
			
		}
		
	}

	public Nation getNationByNum(int number) {
		for (Nation n : getNations()) {
			if (n.getNumber().intValue() == number) {
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
		return this.artifacts;
	}

	public void setArtifacts(Container<ArtifactInfo> artifacts) {
		this.artifacts = artifacts;
	}

	public Container<PopulationCenter> getPopulationCenters() {
		return this.populationCenters;
	}

	public void setPopulationCenters(Container<PopulationCenter> populationCenters) {
		this.populationCenters = populationCenters;
	}
	
	public Container<TerrainModifier> getTerrainModifiers() {
		return this.terrainModifiers;
	}

	public void setTerrainModifiers(Container<TerrainModifier> terrainModifiers) {
		this.terrainModifiers = terrainModifiers;
	}
	
	public Container<ClimateModifier> getClimateModifiers() {
		return this.climateModifiers;
	}

	public void setClimateModifiers(Container<ClimateModifier> climateModifiers) {
		this.climateModifiers = climateModifiers;
	}
	
	public void clearModifiers(int clearNation) {
		//this.terrainModifiers.removeAllByProperties("NationNo", Integer.valueOf(clearNation));
		//this.climateModifiers.removeAllByProperties("NationNo", Integer.valueOf(clearNation));
		// The above don't work, not sure why
		
		ArrayList<TerrainModifier> removeTerrain = new ArrayList<TerrainModifier>();
				
		for (TerrainModifier tm : this.terrainModifiers.items) {
			if (tm.NationNo == clearNation) {
				removeTerrain.add(tm);
			}
		}
		
		for (TerrainModifier tm : removeTerrain) {
			this.terrainModifiers.removeItem(tm);
		}
		
		ArrayList<ClimateModifier> removeClimate = new ArrayList<ClimateModifier>();
		
		for (ClimateModifier cm : this.climateModifiers.items) {
			if (cm.NationNo == clearNation) {
				removeClimate.add(cm);
			}
		}
		
		for (ClimateModifier cm : removeClimate) {
			this.climateModifiers.removeItem(cm);
		}		
	}
	
	public Container<OrderMetadata> getOrders() {
		if (this.orders != null) {
			OrderMetadata om = this.orders.findFirstByProperty("number", Integer.valueOf(225));
			if (om.getSkillRequirement().equals("MS")) {
				GameMetadata gm = GameMetadata.instance();
				gm.setGameType(getGameType());

				try {
					gm.load();
					this.orders = gm.getOrders();
				} catch (Exception exc) {
					// do nothing
				}
			}
		}
		return this.orders;
	}

	public void setOrders(Container<OrderMetadata> orders) {
		this.orders = orders;
	}

	public String getBasePath() {
		File f = new File(this.basePath);
		try {
			if (f.exists())
				return f.getCanonicalPath();
			File cd = new File(".");
			String p = cd.getCanonicalPath() + "/" + this.basePath;
			return p;
		} catch (IOException exc) {
			return this.basePath;
		}
	}

	public void setBasePath(String basePath) {
		this.basePath = basePath;
	}
	public Container<Character> getCharacters() {
		return this.characters;
	}

	public void setCharacters(Container<Character> characters) {
		this.characters = characters;
	}

	public int getNationNo() {
		return this.nationNo;
	}

	public void setNationNo(int nationNo) {
		this.nationNo = nationNo;
	}

	public Container<NationMapRange> getNationMapRanges() {
		return this.nationMapRanges;
	}

	public void setNationMapRanges(Container<NationMapRange> nationMapRanges) {
		this.nationMapRanges = nationMapRanges;
	}

	public Container<Army> getArmies() {
		return this.armies;
	}

	public void setArmies(Container<Army> armies) {
		this.armies = armies;
	}

	public Container<SpellInfo> getSpells() {
		return this.spells;
	}

	public void setSpells(Container<SpellInfo> spells) {
		this.spells = spells;
	}

	public Container<Hex> getHexOverrides(int turnNo) {
		if (this.hexOverrides == null)
			this.hexOverrides = new HashMap<Integer, Container<Hex>>();
		if (this.hexOverrides.containsKey(Integer.valueOf(turnNo))) {
			return this.hexOverrides.get(Integer.valueOf(turnNo));
		}
		return new Container<Hex>();
	}

	@SuppressWarnings("hiding")
	public Hex getHexOverride(Container<Hex> hexes, int hexNo) {
		return hexes.findFirstByProperty("hexNo", Integer.valueOf(hexNo));
	}

	public Hex getHexForTurn(int turnNo, int hexNo) {
		Hex h = getHexOverride(getHexOverrides(turnNo), hexNo);
		if (h != null)
			return h;
		return getHexFromMetadata(hexNo);
	}

	public void addHexOverride(int turnNo, Hex hex) {
		Container<Hex> hc;
		if (!this.hexOverrides.containsKey(Integer.valueOf(turnNo))) {
			hc = new Container<Hex>(new String[] { "hexNo" });
			this.hexOverrides.put(Integer.valueOf(turnNo), hc);
		}
		hc = getHexOverrides(turnNo);
		Hex h = getHexOverride(hc, hex.getHexNo());
		if (h != null)
			hc.removeItem(h);
		hc.addItem(hex);
	}


	//from https://stackoverflow.com/questions/1835430/byte-order-mark-screws-up-file-reading-in-java
	public BufferedReader getUTF8Resource(String filename,boolean ignoreComments) throws IOException
	{
		return getUTF8Resource(getResource(filename),ignoreComments);
	}
	public static BufferedReader getUTF8Resource(Resource res,boolean ignoreComments) throws IOException
	{
		if (ignoreComments) {
			return getUTF8Resource(res,new CommentedBufferedReader(new InputStreamReader(res.getInputStream(),"UTF-8")));
		} else {
			return getUTF8Resource(res,new BufferedReader(new InputStreamReader(res.getInputStream(),"UTF-8")));
		}
	}
/*	public static BufferedReader getUTF8Resource(Resource res) throws IOException
	{
		return getUTF8Resource(res,true);
	}
*/	public static BufferedReader getUTF8Resource(Resource res,BufferedReader reader) throws IOException
	{
	    reader.mark(1);
        char[] possibleBOM = new char[1];
        reader.read(possibleBOM);

        if (possibleBOM[0] != '\ufeff')
        {
            reader.reset();
        }
        return reader;
	}
	// search order is:
	// files first.
	// then check classpath
	// note that getBasePath is not just a getter.
	public BufferedReader getUTF8ResourceByGame(String filename, boolean ignoreComments) throws IOException {
		try {
			return getUTF8Resource(getGameType().toString() + "." + filename, ignoreComments);
		} catch (IOException e) {
			if (this.gameType != GameTypeEnum.gameCMF) {
				throw e;
			}
			return getUTF8Resource(GameTypeEnum.gameCME.toString() + "." + filename, ignoreComments);
		}
	}
	public Resource getResource(String resourceName) throws IOException {
		try {
			Resource r = Application.instance().getApplicationContext().getResource("file:///" + getBasePath() + "/" + resourceName);
			new InputStreamReader(r.getInputStream());
			return r;
		} catch (Exception exc) {
			try {
				Resource r = Application.instance().getApplicationContext().getResource("classpath:" + this.basePath + "/" + resourceName);
				new InputStreamReader(r.getInputStream());
				return r;
			} catch (Exception ex) {
				Logger.getLogger(this.getClass().getName()).warning(resourceName);
				Logger.getLogger(this.getClass().getName()).warning(ex.getMessage());
				throw new IOException(resourceName + " not found.");
			}
		}
	}
	static public GameMetadata instance()
	{
		return (GameMetadata) Application.instance().getApplicationContext().getBean("gameMetadata");
	}

	// note only returns non-null if a game has been initialized.
	static public GameMetadata lazyLoadGameMetadata(GameHolder gh) {
		if (gh != null) {
			if (gh.isGameInitialized()) {
				return gh.getGame().getMetadata();
			}
		}
		return null;
	}
	//hide the details of which property we are using.
	public ArtifactInfo findFirstArtifactByNumber(int number) {
		return this.getArtifacts().findFirstByProperty("no", number);
	}
	public ArtifactInfo findFirstArtifactByName(String name) {
		String noAccents = Normalizer.normalize(name, Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "").replaceAll("\u2019", "'");

		return this.getArtifacts().findFirstByProperty("unAccentedName", noAccents);
	}
	public boolean neutralNationsExist( ) {
		switch (this.gameType) {
		case gameCME:
		case gameBOFA:
			return false;
		}
		for (Nation n : getNations()) {
			if (n.getAllegiance().equals(NationAllegianceEnum.Neutral) && !n.getEliminated() && !n.getRemoved()) {
				return true;
			}
		}
		return false;
	}
	public void resetArtifactNumbers()
	{
		for (ArtifactInfo ai : this.getArtifacts().getItems()) {
			ai.setNo(0);
		}
	}

	public int getTerrainModifier(int nation, HexTerrainEnum terrain) {
		
//		try {
//			TerrainModifier tm =  this.terrainModifiers.findFirstByProperties(new String[] {"NationNo", "Terrain"}, new Object[] {nation, terrain});
//			return (int) Math.round(tm.Modifier * 100);
//		}  catch (Exception ex) {
//			return 100;
//		}
//	For some reason I am having no luck with containers so looping manually...
		
		for (TerrainModifier tm : this.terrainModifiers) {
			if (tm.NationNo == nation && tm.Terrain == terrain) {
				return (int) Math.round(tm.Modifier * 100);
			}
		}
		
		return 100;
		
	}

	public int getClimateModifier(int nation, ClimateEnum climate) {
//		try {
//			ClimateModifier cm =  this.climateModifiers.findFirstByProperties(new String[] {"NationNo", "Climate"}, new Object[] {nation, climate});
//			return (int) Math.round(cm.Modifier * 100);
//		}  catch (Exception ex) {
//			return 100;
//		}
		
		for (ClimateModifier cm : this.climateModifiers) {
			if (cm.NationNo == nation && cm.Climate == climate) {
				return (int) Math.round(cm.Modifier * 100);
			}
		}
		
		return 100;
		
	}

}