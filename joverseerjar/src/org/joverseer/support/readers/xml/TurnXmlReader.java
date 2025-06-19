package org.joverseer.support.readers.xml;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.digester.Digester;
import org.apache.commons.digester.RegexRules;
import org.apache.commons.digester.SetNestedPropertiesRule;
import org.apache.commons.digester.SimpleRegexMatcher;
import org.apache.log4j.Logger;
import org.joverseer.domain.Army;
import org.joverseer.domain.ArmySizeEnum;
import org.joverseer.domain.Artifact;
import org.joverseer.domain.Character;
import org.joverseer.domain.HexInfo;
import org.joverseer.domain.InformationSourceEnum;
import org.joverseer.domain.NationEconomy;
import org.joverseer.domain.NationMessage;
import org.joverseer.domain.PlayerInfo;
import org.joverseer.domain.PopulationCenter;
import org.joverseer.domain.PopulationCenterSizeEnum;
import org.joverseer.game.Game;
import org.joverseer.game.Turn;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.metadata.GameMetadata;
import org.joverseer.metadata.GameTypeEnum;
import org.joverseer.metadata.domain.ArtifactInfo;
import org.joverseer.metadata.domain.Hex;
import org.joverseer.metadata.domain.HexSideElementEnum;
import org.joverseer.metadata.domain.HexSideEnum;
import org.joverseer.metadata.domain.Nation;
import org.joverseer.metadata.domain.NationAllegianceEnum;
import org.joverseer.preferences.PreferenceRegistry;
import org.joverseer.support.AsciiUtils;
import org.joverseer.support.Container;
import org.joverseer.support.GameHolder;
import org.joverseer.support.TurnInitializer;
import org.joverseer.support.infoSources.DerivedFromArmyInfoSource;
import org.joverseer.support.infoSources.DoubleAgentInfoSource;
import org.joverseer.support.infoSources.InfoSource;
import org.joverseer.support.infoSources.MetadataSource;
import org.joverseer.support.infoSources.PdfTurnInfoSource;
import org.joverseer.support.infoSources.PopCenterXmlInfoSource;
import org.joverseer.support.infoSources.XmlTurnInfoSource;
import org.joverseer.support.infoSources.XmlExtraTurnInfoSource;
import org.joverseer.support.infoSources.spells.DerivedFromSpellInfoSource;
import org.joverseer.tools.nationMessages.NationMessageParser;
import org.joverseer.ui.views.Messages;
import org.springframework.richclient.progress.ProgressMonitor;

/**
 * Class tha reads xml turn files and updates the game
 *
 * The class uses Apache Digester to parse the xml files into objects. Then the
 * objects are parsed and the game is updated.
 *
 * The class is set-up to cooperate with a ProgressMonitor so that the progress
 * in parsing the pdf files can be shown in the gui layer.
 *
 * @author Marios Skounakis
 */
public class TurnXmlReader implements Runnable {
	static Logger logger = Logger.getLogger(TurnXmlReader.class);

	TurnInfo turnInfo = null;
	Digester digester = null;
	Turn turn = null;
	InfoSource infoSource = null;

	ArrayList<PopulationCenter> currentNationPops = new ArrayList<PopulationCenter>();

	Game game;
	String filename;

	ProgressMonitor monitor;

	boolean errorOccured = false;

	public TurnXmlReader(Game game, String filename) {
		this.game = game;
		this.filename = filename;
	}

	public TurnXmlReader(Game game) {
		this.game = game;
	}

	public TurnInfo getTurnInfo() {
		return this.turnInfo;
	}

	public void setTurnInfo(TurnInfo turnInfo) {
		this.turnInfo = turnInfo;
	}

	public ProgressMonitor getMonitor() {
		return this.monitor;
	}

	public void setMonitor(ProgressMonitor monitor) {
		this.monitor = monitor;
	}

	public void readFile(String fileName) throws Exception {
		try {
			this.digester = new Digester();
			this.digester.setValidating(false);
			this.digester.setRules(new RegexRules(new SimpleRegexMatcher()));
			// parse turn info
			this.digester.addObjectCreate("METurn", TurnInfo.class); //$NON-NLS-1$
			//More><TurnInfo><XXMLVersion>
			this.digester.addCallMethod("METurn/More/TurnInfo/XXMLVersion","setXxmlversion",0); //$NON-NLS-1$ //$NON-NLS-2$
			// parse turn info attributes
			SetNestedPropertiesRule snpr = new SetNestedPropertiesRule();
			snpr = new SetNestedPropertiesRule(new String[] { "GameNo", "TurnNo", "NationNo", "GameType", "Secret", "DueDate", "Player", "Account", "NationCapitalHex" }, new String[] { "gameNo", "turnNo", "nationNo", "gameType", "securityCode", "dueDate", "playerName", "accountNo", "nationCapitalHex" }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$ //$NON-NLS-10$ //$NON-NLS-11$ //$NON-NLS-12$ //$NON-NLS-13$ //$NON-NLS-14$ //$NON-NLS-15$ //$NON-NLS-16$ //$NON-NLS-17$ //$NON-NLS-18$
			snpr.setAllowUnknownChildElements(true);
			this.digester.addRule("METurn/TurnInfo", snpr); //$NON-NLS-1$

			// parse PCs
			// create container for pcs
			this.digester.addObjectCreate("METurn/PopCentres", "org.joverseer.support.Container"); //$NON-NLS-1$ //$NON-NLS-2$
			// add container to turn info
			this.digester.addSetNext("METurn/PopCentres", "setPopCentres"); //$NON-NLS-1$ //$NON-NLS-2$
			// create pc
			this.digester.addObjectCreate("METurn/PopCentres/PopCentre", "org.joverseer.support.readers.xml.PopCenterWrapper"); //$NON-NLS-1$ //$NON-NLS-2$
			// set hex
			this.digester.addSetProperties("METurn/PopCentres/PopCentre", "HexID", "hexID"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			// set nested properties
			this.digester.addRule("METurn/PopCentres/PopCentre", snpr = new SetNestedPropertiesRule(new String[] { "Name", "Nation", "NationAllegience", "Size", "FortificationLevel", "Size", "Dock", "Capital", "Hidden", "Loyalty", "InformationSource", "Hidden" }, new String[] { "name", "nation", "nationAllegience", "size", "fortificationLevel", "size", "dock", "capital", "hidden", "loyalty", "informationSource", "hidden" })); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$ //$NON-NLS-10$ //$NON-NLS-11$ //$NON-NLS-12$ //$NON-NLS-13$ //$NON-NLS-14$ //$NON-NLS-15$ //$NON-NLS-16$ //$NON-NLS-17$ //$NON-NLS-18$ //$NON-NLS-19$ //$NON-NLS-20$ //$NON-NLS-21$ //$NON-NLS-22$ //$NON-NLS-23$ //$NON-NLS-24$ //$NON-NLS-25$
			// add to container
			this.digester.addSetNext("METurn/PopCentres/PopCentre", "addItem", "org.joverseer.support.readers.xml.PopCenterWrapper"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

			// parse characters
			// create characters container
			this.digester.addObjectCreate("METurn/Characters", Container.class); //$NON-NLS-1$
			// add contianer to turn info
			this.digester.addSetNext("METurn/Characters", "setCharacters"); //$NON-NLS-1$ //$NON-NLS-2$
			// create character object
			this.digester.addObjectCreate("METurn/Characters/Character", "org.joverseer.support.readers.xml.CharacterWrapper"); //$NON-NLS-1$ //$NON-NLS-2$
			// set id
			this.digester.addSetProperties("METurn/Characters/Character", "ID", "id"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			// set nested properties
			this.digester.addRule("METurn/Characters/Character", snpr = new SetNestedPropertiesRule(new String[] { "Name", "Nation", "Location", "Command", "TotalCommand", "Agent", "TotalAgent", "Mage", "TotalMage", "Emmisary", "TotalEmmisary", "Stealth", "TotalStealth", "Challenge", "Health", "Title", "InformationSource", "OrdersAllowed" }, new String[] { "name", "nation", "location", "command", "totalCommand", "agent", "totalAgent", "mage", "totalMage", "emmisary", "totalEmmisary", "stealth", "totalStealth", "challenge", "health", "title", "informationSource", "ordersAllowed" })); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$ //$NON-NLS-10$ //$NON-NLS-11$ //$NON-NLS-12$ //$NON-NLS-13$ //$NON-NLS-14$ //$NON-NLS-15$ //$NON-NLS-16$ //$NON-NLS-17$ //$NON-NLS-18$ //$NON-NLS-19$ //$NON-NLS-20$ //$NON-NLS-21$ //$NON-NLS-22$ //$NON-NLS-23$ //$NON-NLS-24$ //$NON-NLS-25$ //$NON-NLS-26$ //$NON-NLS-27$ //$NON-NLS-28$ //$NON-NLS-29$ //$NON-NLS-30$ //$NON-NLS-31$ //$NON-NLS-32$ //$NON-NLS-33$ //$NON-NLS-34$ //$NON-NLS-35$ //$NON-NLS-36$ //$NON-NLS-37$
			snpr.setAllowUnknownChildElements(true);
			// add character to container
			this.digester.addSetNext("METurn/Characters/Character", "addItem", "org.joverseer.support.readers.xml.CharacterWrapper"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			// parse character artifacts
			// create artifact arraylist
			this.digester.addObjectCreate("METurn/Characters/Character/Artifacts", ArrayList.class); //$NON-NLS-1$
			// add arraylist to character
			this.digester.addSetNext("METurn/Characters/Character/Artifacts", "setArtifacts"); //$NON-NLS-1$ //$NON-NLS-2$
			// prepare call to arraylist.add
			this.digester.addCallMethod("METurn/Characters/Character/Artifacts/Artifact", "add", 1); //$NON-NLS-1$ //$NON-NLS-2$
			this.digester.addCallParam("METurn/Characters/Character/Artifacts/Artifact", 0); //$NON-NLS-1$
			// parse character spells
			// create spell arraylist
			this.digester.addObjectCreate("METurn/Characters/Character/Spells", ArrayList.class); //$NON-NLS-1$
			// add arraylist to character
			this.digester.addSetNext("METurn/Characters/Character/Spells", "setSpells"); //$NON-NLS-1$ //$NON-NLS-2$
			// prepare call to arraylist.add
			this.digester.addCallMethod("METurn/Characters/Character/Spells/Spell", "add", 1); //$NON-NLS-1$ //$NON-NLS-2$
			this.digester.addCallParam("METurn/Characters/Character/Spells/Spell", 0); //$NON-NLS-1$

			// parse armies
			// create armies container
			this.digester.addObjectCreate("METurn/Armies", Container.class); //$NON-NLS-1$
			// add contianer to turn info
			this.digester.addSetNext("METurn/Armies", "setArmies"); //$NON-NLS-1$ //$NON-NLS-2$
			// create army object
			this.digester.addObjectCreate("METurn/Armies/Army", "org.joverseer.support.readers.xml.ArmyWrapper"); //$NON-NLS-1$ //$NON-NLS-2$
			// set hexId
			this.digester.addSetProperties("METurn/Armies/Army", "HexID", "hexID"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			// set nested properties
			this.digester.addRule("METurn/Armies/Army", snpr = new SetNestedPropertiesRule(new String[] { "Nation", "NationAllegience", "Size", "TroopCount", "Commander", "CommanderTitle", "ExtraInfo", "Navy", "InformationSource", "CharsTravellingWith" }, new String[] { "nation", "nationAllegience", "size", "troopCount", "commander", "commanderTitle", "extraInfo", "navy", "informationSource", "charsTravellingWith" })); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$ //$NON-NLS-10$ //$NON-NLS-11$ //$NON-NLS-12$ //$NON-NLS-13$ //$NON-NLS-14$ //$NON-NLS-15$ //$NON-NLS-16$ //$NON-NLS-17$ //$NON-NLS-18$ //$NON-NLS-19$ //$NON-NLS-20$ //$NON-NLS-21$
			snpr.setAllowUnknownChildElements(true);
			// add army to container
			this.digester.addSetNext("METurn/Armies/Army", "addItem", "org.joverseer.support.readers.xml.CharacterWrapper"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

			// parse nations
			// create nations container
			this.digester.addObjectCreate("METurn/Nations", Container.class); //$NON-NLS-1$
			// add contianer to turn info
			this.digester.addSetNext("METurn/Nations", "setNations"); //$NON-NLS-1$ //$NON-NLS-2$
			// create nation object
			this.digester.addObjectCreate("METurn/Nations/Nation", "org.joverseer.support.readers.xml.NationWrapper"); //$NON-NLS-1$ //$NON-NLS-2$
			// set hexId
			this.digester.addSetProperties("METurn/Nations/Nation", "ID", "id"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			// set text
			this.digester.addCallMethod("METurn/Nations/Nation", "setName", 1); //$NON-NLS-1$ //$NON-NLS-2$
			this.digester.addCallParam("METurn/Nations/Nation", 0); //$NON-NLS-1$

			// add nation to container
			this.digester.addSetNext("METurn/Nations/Nation", "addItem", "org.joverseer.support.readers.xml.CharacterWrapper"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

			// parse rumors
			// create nationinfo object
			this.digester.addObjectCreate("METurn/NationInfo", NationInfoWrapper.class); //$NON-NLS-1$
			// read EmptyPopHexes
			this.digester.addCallMethod("METurn/NationInfo/EmptyPopHexes", "setEmptyPopHexes", 1); //$NON-NLS-1$ //$NON-NLS-2$
			this.digester.addCallParam("METurn/NationInfo/EmptyPopHexes", 0, "HexIDList"); //$NON-NLS-1$ //$NON-NLS-2$
			// read PopHexes
			this.digester.addCallMethod("METurn/NationInfo/PopHexes", "setPopHexes", 1); //$NON-NLS-1$ //$NON-NLS-2$
			this.digester.addCallParam("METurn/NationInfo/PopHexes", 0, "HexIDList"); //$NON-NLS-1$ //$NON-NLS-2$
			// add contianer to turn info
			this.digester.addSetNext("METurn/NationInfo", "setNationInfoWrapper"); //$NON-NLS-1$ //$NON-NLS-2$
			// create rumors arraylist
			this.digester.addObjectCreate("METurn/NationInfo/NationMessages", ArrayList.class); //$NON-NLS-1$
			// add arraylist to nation info
			this.digester.addSetNext("METurn/NationInfo/NationMessages", "setRumors"); //$NON-NLS-1$ //$NON-NLS-2$
			// prepare call to arraylist.add
			this.digester.addCallMethod("METurn/NationInfo/NationMessages/NationMessage", "add", 1); //$NON-NLS-1$ //$NON-NLS-2$
			this.digester.addCallParam("METurn/NationInfo/NationMessages/NationMessage", 0); //$NON-NLS-1$

			// parse economy
			// create economy object
			this.digester.addObjectCreate("METurn/Economy", EconomyWrapper.class); //$NON-NLS-1$
			// add economy
			this.digester.addSetNext("METurn/Economy", "setEconomy"); //$NON-NLS-1$ //$NON-NLS-2$
			// set properties
			this.digester.addRule("METurn/Economy/Nation", snpr = new SetNestedPropertiesRule(new String[] { "ArmyMaint", "PopMaint", "CharMaint", "TotalMaint", "TaxRate", "Revenue", "Surplus", "Reserve", "TaxBase" }, new String[] { "armyMaint", "popMaint", "charMaint", "totalMaint", "taxRate", "revenue", "surplus", "reserve", "taxBase" })); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$ //$NON-NLS-10$ //$NON-NLS-11$ //$NON-NLS-12$ //$NON-NLS-13$ //$NON-NLS-14$ //$NON-NLS-15$ //$NON-NLS-16$ //$NON-NLS-17$ //$NON-NLS-18$ //$NON-NLS-19$
			snpr.setAllowUnknownChildElements(true);
			// create product object
			this.digester.addObjectCreate("METurn/Economy/Market/Product", ProductWrapper.class); //$NON-NLS-1$
			this.digester.addSetNext("METurn/Economy/Market/Product", "addProduct"); //$NON-NLS-1$ //$NON-NLS-2$
			this.digester.addSetProperties("METurn/Economy/Market/Product", "type", "type"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			// set nested properties
			this.digester.addRule("METurn/Economy/Market/Product", snpr = new SetNestedPropertiesRule(new String[] { "BuyPrice", "SellPrice", "MarketAvail", "NationStores", "NationProduction" }, new String[] { "buyPrice", "sellPrice", "marketAvail", "nationStores", "nationProduction" })); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$ //$NON-NLS-10$ //$NON-NLS-11$
			snpr.setAllowUnknownChildElements(true);
			if (getMonitor() != null) {
				getMonitor().subTaskStarted(String.format("Parsing file %s...", new Object[] { fileName })); //$NON-NLS-1$
				getMonitor().worked(5);
			}
			this.turnInfo = (TurnInfo) this.digester.parse(fileName);
		} catch (Exception exc) {
			// todo fix
			throw new Exception("Error parsing Xml Turn file.", exc); //$NON-NLS-1$
		}
	}

	@Override
	public void run() {
		try {
			readFile(this.filename);
		} catch (Exception exc) {
			this.monitor.subTaskStarted(Messages.getString("subTask.ReadError") + exc.getMessage() + ")"); //$NON-NLS-1$ //$NON-NLS-2$
			this.errorOccured = true;
		}
		try {
			updateGame(this.game);
			this.game.setCurrentTurn(this.game.getMaxTurn());
			Thread.sleep(100);
		} catch (Exception exc) {
			this.errorOccured = true;
		}
	}
	// warning: updates newXMLFormat to true if a version is set.
	public void updateGame(Game game1) throws Exception {
		if (this.turnInfo.getTurnNo() < game1.getMaxTurn()) {
			// todo fix
			throw new Exception(Messages.getString("subTask.NoPastTurnImport")); //$NON-NLS-1$
		}
		if (this.turnInfo.getXxmlversion() != null) {
			this.game.getMetadata().setNewXmlFormat(true);
			if (getMonitor() != null) {
				getMonitor().worked(10);
				getMonitor().subTaskStarted(Messages.getString("subTask.newXML")); //$NON-NLS-1$
			}
		}
		try {
			this.turn = null;
			if (this.turnInfo.getTurnNo() == game1.getMaxTurn()) {
				this.turn = game1.getTurn(game1.getMaxTurn());
			} else {
				this.turn = new Turn();
				this.turn.setTurnNo(this.turnInfo.getTurnNo());
				Turn lastTurn = game1.getTurn();
				TurnInitializer.initializeTurnWith(this.turn, lastTurn, this.game.getMetadata());
				game1.addTurn(this.turn);
			}
			this.currentNationPops = new ArrayList<PopulationCenter>();

			// update player info
			PlayerInfo pi = this.turn.getPlayerInfo(this.turnInfo.getNationNo());
			if (pi != null) {
				this.turn.getPlayerInfo().removeItem(pi);
			} else {
				pi = new PlayerInfo();
			}
			pi.setNationNo(this.turnInfo.getNationNo());
			pi.setPlayerName(this.turnInfo.getPlayerName());
			pi.setDueDate(this.turnInfo.getDueDate());
			pi.setSecret(this.turnInfo.getSecurityCode());
			pi.setAccountNo(this.turnInfo.getAccountNo());
			
			//Probably should be better implemented... Transfers the users set "controlled nations" between turns
			try {
				pi.setControlledNations(game1.getTurn(game1.getMaxTurn()-1).getPlayerInfo(this.turnInfo.getNationNo()).getControlledNations());
			} catch (Exception e) {}
			
			this.turn.getPlayerInfo().addItem(pi);

			this.infoSource = new XmlTurnInfoSource(this.turnInfo.getTurnNo(), this.turnInfo.getNationNo());
			if (getMonitor() != null) {
				getMonitor().worked(20);
				getMonitor().subTaskStarted(Messages.getString("subTask.UpdatingNations")); //$NON-NLS-1$
			}
			updateNations(game1);
			if (getMonitor() != null) {
				getMonitor().worked(50);
				getMonitor().subTaskStarted(Messages.getString("subTask.UpdatingPCs")); //$NON-NLS-1$
			}
			updatePCs();
			if (getMonitor() != null) {
				getMonitor().worked(60);
				getMonitor().subTaskStarted(Messages.getString("subTask.UpdatingChars")); //$NON-NLS-1$
			}
			updateChars();
			if (getMonitor() != null) {
				getMonitor().worked(70);
				getMonitor().subTaskStarted(Messages.getString("subTask.UpdatingArmies")); //$NON-NLS-1$
			}
			updateArmies();
			if (getMonitor() != null) {
				getMonitor().worked(80);
				getMonitor().subTaskStarted(Messages.getString("subTask.UpdatingNationInfo")); //$NON-NLS-1$
			}
			updateNationInfo();
			if (getMonitor() != null) {
				getMonitor().worked(90);
				getMonitor().subTaskStarted(Messages.getString("subTask.NationMessages")); //$NON-NLS-1$
			}
			updateNationMessages();
			if (getMonitor() != null) {
				getMonitor().worked(100);
			}
		} catch (Exception exc) {
			exc.printStackTrace();
			if (getMonitor() != null) {
				getMonitor().worked(100);
				getMonitor().subTaskStarted(Messages.getString("subTask.Error") + exc.getMessage() + "'."); //$NON-NLS-1$ //$NON-NLS-2$
				this.errorOccured = true;
			}
			throw new Exception(Messages.getString("subTask.UpdateError"), exc); //$NON-NLS-1$
		}
	}

	private void updatePCs() throws Exception {
		updateOldPCs(this.turn, this.turnInfo.getTurnNo(),this.turnInfo.getNationNo(), this.turnInfo.getNationCapitalHex(),this.turnInfo.getPopCentres().getItems(),this.infoSource, this.currentNationPops, this.game.getMetadata());
	}

	private boolean willCharacterBeOverwrittenT0(Character c) {
		if (c.getInfoSource() == null) {
			return false;
		}
		if (MetadataSource.class.isInstance(c.getInfoSource())) {
			// it's starting information
			return true;
		}
		// it might be previous information imported.
		if (XmlExtraTurnInfoSource.class.isInstance(c.getInfoSource())) {
			return true;
		}
		if (PdfTurnInfoSource.class.isInstance(c.getInfoSource())) {
			return true;
		}
		if (XmlTurnInfoSource.class.isInstance(c.getInfoSource())) {
			return true;
		}
		return false;
	}
	private void updateChars() throws Exception {
		Container<Character> chars = this.turn.getCharacters();
		if (this.turn.getTurnNo() == 0) {
			
			// remove all character's import from metadata for given nation
			ArrayList<Character> metadataChars = chars.findAllByProperty("nationNo", this.turnInfo.getNationNo()); //$NON-NLS-1$
			ArrayList<Character> toRemove = new ArrayList<Character>();
			for (Character c : metadataChars) {
				if (willCharacterBeOverwrittenT0(c)) {
					toRemove.add(c);
				}
			}
			chars.removeAll(toRemove);
		}
		for (CharacterWrapper cw : this.turnInfo.getCharacters().getItems()) {
			
			Character newCharacter;
			Character oldCharacter;
			try {
				newCharacter = cw.getCharacter();
				oldCharacter = chars.findFirstByProperties(new String[] { "id" }, new Object[] { newCharacter.getId() }); //$NON-NLS-1$
				newCharacter.setInfoSource(this.infoSource);
				logger.debug(String.format("Handling Character {3} at {0},{1} with information source {2}", String.valueOf(newCharacter.getX()), String.valueOf(newCharacter.getY()), newCharacter.getInformationSource().toString(), newCharacter.getId())); //$NON-NLS-1$
				if (oldCharacter == null) {
					// no char found - add
					logger.debug("No Character found in turn, add."); //$NON-NLS-1$
					chars.addItem(newCharacter);
				} else {
					// char found
					logger.debug("Character found in turn."); //$NON-NLS-1$
					try {
						if (DerivedFromArmyInfoSource.class.isInstance(oldCharacter.getInfoSource()) || PdfTurnInfoSource.class.isInstance(oldCharacter.getInfoSource()) || DerivedFromSpellInfoSource.class.isInstance(oldCharacter.getInfoSource()) || (newCharacter.getInformationSource().getValue() > oldCharacter.getInformationSource().getValue())) {
							logger.debug("Replace."); //$NON-NLS-1$
							
							//Band aid solution to InformationEnum problem
							//Fixes double agents being overwritten by another nations xml
							if(DoubleAgentInfoSource.class.isInstance(oldCharacter.getInfoSource())) {
								if(oldCharacter.getDoubleAgent() != null)
									newCharacter.setDoubleAgent(oldCharacter.getDoubleAgent(), oldCharacter.getDoubleAgentForNationNo());
							}
							chars.removeItem(oldCharacter);
							chars.addItem(newCharacter);
						}
					} catch (Exception e) {

					}
				}

				if (newCharacter.getNationNo() == this.turnInfo.getNationNo()) {
					// if character is same nation, process PC existence in hex
					// if a PC is found in the hex with info source from
					// previous turn, the PC should be removed
					// this works on the assumption that if a PC exists in the
					// same hex as the character
					// the PC will be reported in the turn
					// Hidden pops are excluded from this check
					PopulationCenter pc = (PopulationCenter) this.turn.getContainer(TurnElementsEnum.PopulationCenter).findFirstByProperty("hexNo", newCharacter.getHexNo()); //$NON-NLS-1$
					if (pc != null) {
						if (pc.getInfoSource().getTurnNo() < this.turn.getTurnNo() && !pc.getHidden()) {
							logger.debug("Removing Pop Center " + pc.getName() + " at hex " + pc.getHexNo() + " because it was not reported in current turn and a character is present in the hex."); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
							this.turn.getPopulationCenters().removeItem(pc);
						}

					}
				}

			} catch (Exception exc) {
				logger.error(exc);
				throw exc;
			}
		}
	}

	private void updateArmies() throws Exception {
		for (ArmyWrapper aw : this.turnInfo.getArmies().getItems()) {
			Army newArmy;
			try {
				newArmy = aw.getArmy();
				addArmy(newArmy, this.game, this.turn, true);
			} catch (Exception exc) {
				throw exc;
			}
		}
	}

	private void updateNations(Game g) throws Exception {
		for (NationWrapper nw : this.turnInfo.getNations().getItems()) {
			Nation n = g.getMetadata().getNationByNum(nw.getId());
			n.setName(nw.getName());
		}
	}

	public static void addArmyBeta(Army newArmy, Game game, Turn turn) {

		Container<Character> chars = turn.getCharacters();
		Container<Army> armies = turn.getArmies();
		armies.addItem(newArmy);
		for (NationAllegianceEnum allegiance : NationAllegianceEnum.values()) {
			postProcessArmiesForHex(game, newArmy.getHexNo(), turn, allegiance);
		}
		// do not generate character for unknown armies
		if (!newArmy.getCommanderName().toUpperCase().startsWith("UNKNOWN ")) { //$NON-NLS-1$
			String commanderId = Character.getIdFromName(newArmy.getCommanderName());
			Character ch = chars.findFirstByProperty("id", commanderId); //$NON-NLS-1$
			if (ch == null) {
				// no found, add
				Character cmd = new Character();
				cmd.setName(newArmy.getCommanderName());
				cmd.setTitle(newArmy.getCommanderTitle());
				cmd.setId(commanderId);
				cmd.setNationNo(newArmy.getNationNo());
				cmd.setX(newArmy.getX());
				cmd.setY(newArmy.getY());
				DerivedFromArmyInfoSource is = new DerivedFromArmyInfoSource();
				InformationSourceEnum ise = InformationSourceEnum.some;
				cmd.setInformationSource(ise);
				cmd.setInfoSource(is);
				chars.addItem(cmd);
			}
		}
	}

	public static void postProcessArmiesForHex(Game game, String hexNo, Turn turn, NationAllegianceEnum allegiance) {
		Container<Army> armies = turn.getArmies();
		final Turn t = turn;
		ArrayList<Army> armiesInHex = armies.findAllByProperties(new String[] { "hexNo", "nationAllegiance" }, new Object[] { hexNo, allegiance }); //$NON-NLS-1$ //$NON-NLS-2$

		Collections.sort(armiesInHex, new Comparator<Army>() {

			@Override
			public int compare(Army arg0, Army arg1) {
				Army a1 = arg0;
				Army a2 = arg1;
				return -(a1.getInformationAmount(t.getTurnNo()) - a2.getInformationAmount(t.getTurnNo()));
			}
		});

		int i = 0;
		ArrayList<Army> toRemove = new ArrayList<Army>();
		while (i < armiesInHex.size()) {
			Army a1 = armiesInHex.get(i);
			for (int j = i + 1; j < armiesInHex.size(); j++) {
				boolean toRemoveB = false;
				Army a2 = armiesInHex.get(j);
				if (a1.getCommanderName().equals(a2.getCommanderName())) {
					// duplicate army
					
					//anchored ships being deleted
					if(a1.getCommanderName().equals("[Anchored Ships]")) {
						if (a1.getNationNo() > 0 && a2.getNationNo().equals(a1.getNationNo()));
						else continue;
					}
					
					
					toRemoveB = true;
					// hack for KS - update army size
					// this is needed because currently in KS when the army's
					// composition is reported, size is unknown
					// and the army is reported again with size <> unknown but a
					// larger InformationSource (i.e. less info)
					// JDS - Now apply to all games

						if (!a2.getSize().equals(ArmySizeEnum.unknown) && a1.getSize().equals(ArmySizeEnum.unknown)) {
							a1.setSize(a2.getSize());
						}

					updateWithInfo(a1, a2);
				} else if (a2.isDefaultName()) {
					if (a1.getNationNo() > 0 && a2.getNationNo().equals(a1.getNationNo())) {
						// duplicate nation
						toRemoveB = true;
					} else if (a2.getNationNo().equals(0)) {
						toRemoveB = true;
					}
				}
				if (toRemoveB)
					toRemove.add(a2);
			}
			armiesInHex.removeAll(toRemove);
			i++;
		}

		for (Army a : toRemove) {
			armies.removeItem(a);
		}
	}

	protected static void updateWithInfo(Army a1, Army a2) {
		if (a1.getCommanderTitle() == null || a1.getCommanderTitle().equals("")) { //$NON-NLS-1$
			a1.setCommanderTitle(a2.getCommanderTitle());
		}
		if (a1.getTroopCount() == 0 && a2.getTroopCount() > 0) {
			a1.setTroopCount(a2.getTroopCount());
		}
		// Stop recon removing navy flag 
		if (a2.isNavy()) {
			a1.setNavy(true);
		}
	}

	public static void addArmy(Army newArmy, Game game, Turn turn, boolean addCharacter) {
		addArmyBeta(newArmy, game, turn);

	}

	// public void addArmy(Army newArmy, Turn turn, boolean addCharacter) {
	// String UNKNOWN_MAP_ICON = "Unknown (Map Icon)";
	//
	// Container chars = turn.getContainer(TurnElementsEnum.Character);
	// Container armies = turn.getContainer(TurnElementsEnum.Army);
	// Army oldArmy;
	// if (!newArmy.getCommanderName().startsWith(UNKNOWN_MAP_ICON)) {
	// // known army, try to find army with same commander name
	// oldArmy = (Army) armies.findFirstByProperties(new
	// String[]{"commanderName"}, new Object[]{newArmy.getCommanderName()});
	// if (oldArmy == null) {
	// // try to find unknown army with same allegience in same hex
	// oldArmy = (Army) armies.findFirstByProperties(
	// new String[]{"commanderName", "hexNo", "nationAllegiance"},
	// new Object[]{UNKNOWN_MAP_ICON, newArmy.getHexNo(),
	// newArmy.getNationAllegiance()});
	// if (oldArmy != null) {
	// int a = 1;
	// }
	// }
	// } else {
	// // try to find unknown army of same allegiance in hex
	// oldArmy = (Army) armies.findFirstByProperties(
	// new String[]{"commanderName", "hexNo", "nationAllegiance"},
	// new Object[]{newArmy.getCommanderName(), newArmy.getHexNo(),
	// newArmy.getNationAllegiance()});
	// if (oldArmy == null) {
	// // try to find known army of same allegiance in hex
	// oldArmy = (Army) armies.findFirstByProperties(
	// new String[]{"hexNo", "nationAllegiance"},
	// new Object[]{newArmy.getHexNo(), newArmy.getNationAllegiance()});
	// }
	// }
	// newArmy.setInfoSource(infoSource);
	// logger.debug(String.format("Handling Army {3} at {0},{1} with information source {2}",
	// String.valueOf(newArmy.getX()),
	// String.valueOf(newArmy.getY()),
	// newArmy.getInformationSource().toString(),
	// newArmy.getCommanderName()));
	// if (oldArmy== null) {
	// // look for "Unknown map icon" army at same hex with same allegiance
	// ArrayList oldArmies = armies.findAllByProperties(new String[]{"x", "y"},
	// new Object[]{newArmy.getX(), newArmy.getY()});
	//
	// // no army found - add
	// logger.debug("No Army found in turn, add.");
	// if (newArmy.getCommanderName().toUpperCase().startsWith("UNKNOWN ")) {
	// // new army is Unknown
	// // check that there is not already an army of the same allegiance that is
	// known
	// boolean bFound = false;
	// for (Army oa : (ArrayList<Army>)oldArmies) {
	// if (!oa.getCommanderName().toUpperCase().startsWith("UNKNOWN ") ||
	// oa.getNationAllegiance() == newArmy.getNationAllegiance()) {
	// bFound = true;
	// }
	// }
	// if (!bFound) { // if no known army, add
	// armies.addItem(newArmy);
	// }
	// } else {
	// for (Army oa : (ArrayList<Army>)oldArmies) {
	// if (oa.getCommanderName().toUpperCase().startsWith("UNKNOWN ") &&
	// oa.getNationAllegiance() == newArmy.getNationAllegiance()) {
	// armies.removeItem(oa);
	// }
	// }
	// armies.addItem(newArmy);
	// }
	//
	// } else {
	// // army found
	// logger.debug("Army found in turn.");
	// if (newArmy.getInformationSource().getValue() >
	// oldArmy.getInformationSource().getValue() ||
	// (newArmy.getInformationSource().getValue() ==
	// oldArmy.getInformationSource().getValue() &&
	// MetadataSource.class.isInstance(oldArmy.getInfoSource())))
	// // condition below was removed on 7 June 2008 because the new xml format
	// files
	// // contain usually the same army twice, once with exhaustive info source
	// and one with
	// // "some" info source
	// // || newArmy.getNationNo() == turnInfo.getNationNo())
	// {
	// logger.debug("Replace.");
	// armies.removeItem(oldArmy);
	// armies.addItem(newArmy);
	// if (newArmy.getSize() == ArmySizeEnum.unknown) {
	// newArmy.setSize(oldArmy.getSize());
	// }
	// }
	// }
	//
	// if (addCharacter) {
	// // look for commander
	//
	// String commanderName = newArmy.getCommanderName();
	// // do not generate character for unknown armies
	// if (!commanderName.toUpperCase().startsWith("UNKNOWN ")) {
	// String commanderId = Character.getIdFromName(commanderName);
	// Character ch = (Character)chars.findFirstByProperty("id", commanderId);
	// if (ch == null) {
	// // no found, add
	// Character cmd = new Character();
	// cmd.setName(commanderName);
	// cmd.setTitle(newArmy.getCommanderTitle());
	// cmd.setId(commanderId);
	// cmd.setNationNo(newArmy.getNationNo());
	// cmd.setX(newArmy.getX());
	// cmd.setY(newArmy.getY());
	// DerivedFromArmyInfoSource is = new DerivedFromArmyInfoSource();
	// InformationSourceEnum ise = InformationSourceEnum.some;
	// cmd.setInformationSource(ise);
	// cmd.setInfoSource(is);
	// chars.addItem(cmd);
	// }
	// }
	// }
	//
	// }

	private void updateNationInfo() {
		Container<PopulationCenter> pcs = this.turn.getPopulationCenters();
		Container<NationEconomy> nationEconomies = this.turn.getNationEconomies();
		NationEconomy oldNe = nationEconomies.findFirstByProperty("nationNo", this.turnInfo.getNationNo()); //$NON-NLS-1$
		if (oldNe != null) {
			nationEconomies.removeItem(oldNe);
		}
		NationEconomy ne = this.turnInfo.getEconomy().getNationEconomy();
		ne.setNationNo(this.turnInfo.getNationNo());
		nationEconomies.addItem(ne);

		this.turnInfo.getEconomy().updateProductPrices(this.turn);

		Container<HexInfo> hexInfos = this.turn.getHexInfos();

		ArrayList<HexInfo> newHexInfos = this.turnInfo.getNationInfoWrapper().getHexInfos(this.turnInfo.getNationNo());
		for (HexInfo hi : newHexInfos) {
			HexInfo oldHi = hexInfos.findFirstByProperty("hexNo", hi.getHexNo()); //$NON-NLS-1$
			if (oldHi == null) {
				hexInfos.addItem(hi);
			} else {
				oldHi.merge(hi);
			}
		}

		// handle current nation pops
		for (PopulationCenter pc : this.currentNationPops) {
			HexInfo hi = hexInfos.findFirstByProperty("hexNo", pc.getHexNo()); //$NON-NLS-1$
			HexInfo nhi = new HexInfo();
			nhi.setVisible(true);
			nhi.setHasPopulationCenter(true);
			nhi.getNationSources().add(pc.getNationNo());
			nhi.setHexNo(pc.getHexNo());
			if (hi != null) {
				hi.merge(nhi);
			} else {
				hexInfos.addItem(hi);
			}
		}

		// remove PCs if HexInfo shows empty hex
		ArrayList<PopulationCenter> toRemove = new ArrayList<PopulationCenter>();

		String hiddenPopsPreferenceVal = PreferenceRegistry.instance().getPreferenceValue("map.hiddenPops"); //$NON-NLS-1$
		boolean keepHiddenPops = hiddenPopsPreferenceVal == null || hiddenPopsPreferenceVal.equals("alwaysShow"); //$NON-NLS-1$

		for (PopulationCenter pc : pcs.getItems()) {
			if (pc.getInfoSource().getTurnNo() == this.turnInfo.getTurnNo() && pc.getInformationSource().getValue() >= InformationSourceEnum.detailed.getValue() && !MetadataSource.class.isInstance(pc.getInfoSource()))
				continue;
			if (pc.getSize() == PopulationCenterSizeEnum.ruins)
				continue;
			HexInfo hi = hexInfos.findFirstByProperty("hexNo", pc.getHexNo()); //$NON-NLS-1$
			if (hi.getVisible() && !hi.getHasPopulationCenter()) {
				if (keepHiddenPops && pc.getHidden())
					continue;
				toRemove.add(pc);
			}
		}
		pcs.removeAll(toRemove);
	}

	private void updateNationMessages() {
		Container<NationMessage> nationMessages = this.turn.getNationMessages();
		nationMessages.removeAllByProperties("nationNo", this.turnInfo.getNationNo()); //$NON-NLS-1$

		NationMessageParser nmp = new NationMessageParser(this.turnInfo.getTurnNo(),GameHolder.instance());

		ArrayList<String> nationMsgs = this.turnInfo.getNationInfoWrapper().getRumors();
		Pattern hexLoc = Pattern.compile("at (\\d\\d\\d\\d)"); //$NON-NLS-1$
		for (String msg : nationMsgs) {
			NationMessage nm = new NationMessage();
			nm.setMessage(msg);
			nm.setNationNo(this.turnInfo.getNationNo());
			Matcher m = hexLoc.matcher(msg);
			if (m.find()) {
				String hexStr = m.group(1);
				int hexNo = Integer.parseInt(hexStr);
				int x = hexNo / 100;
				int y = hexNo % 100;
				nm.setX(x);
				nm.setY(y);
			}
			int hexNo = nmp.getHexNo(nm.getMessage());
			if (hexNo > 0) {
				nm.setX(hexNo / 100);
				nm.setY(hexNo % 100);
			}
			nationMessages.addItem(nm);
		}
		if (this.game.getMetadata().getGameType().equals(GameTypeEnum.gameKS)) {
			updateKSArtifactIDsFromNationMessages();
			updateKSArtifactIDsAndLocationsFromNationMessages();
		} else {
			updateHexOverridesFromBridgeSabotageRumors();
		}
		if (this.game.getMetadata().getGameType().equals(GameTypeEnum.gameFA) & this.getTurnInfo().getTurnNo() == 0)
			updateReportsSuggestNationMessages();
	}

	protected void updateHexOverridesFromBridgeSabotageRumors() {
		ArrayList<String> nationMsgs = this.turnInfo.getNationInfoWrapper().getRumors();
		String prefix = "A bridge was sabotaged at "; //$NON-NLS-1$
		for (String msg : nationMsgs) {
			if (msg.startsWith(prefix)) {
				String pcName = msg.substring(prefix.length(), msg.length() - 1);
				PopulationCenter pc = (PopulationCenter) this.game.getTurn().getContainer(TurnElementsEnum.PopulationCenter).findFirstByProperty("name", pcName); //$NON-NLS-1$
				if (pc != null) {
					int hexNo = pc.getHexNo();
					Hex h = this.game.getMetadata().getHexForTurn(this.game.getCurrentTurn(), hexNo);
					ArrayList<HexSideEnum> bridgeHexSides = h.getHexSidesWithElement(HexSideElementEnum.Bridge);
					if (bridgeHexSides.size() == 1) {
						HexSideEnum hse = bridgeHexSides.get(0);
						Hex newHex = h.clone();
						newHex.removeHexSideElement(hse, HexSideElementEnum.Bridge);
						this.game.getMetadata().addHexOverride(this.game.getCurrentTurn(), newHex);

						// remove bridge from neighbor hex too
						int neighborHexNo = hse.getHexNoAtSide(hexNo);
						Hex neighborHex = this.game.getMetadata().getHexForTurn(this.game.getCurrentTurn(), neighborHexNo);
						Hex newNeighborHex = neighborHex.clone();
						newNeighborHex.removeHexSideElement(hse.getOppositeSide(), HexSideElementEnum.Bridge);
						this.game.getMetadata().addHexOverride(this.game.getCurrentTurn(), newNeighborHex);
					}
				}
			}
		}
	}

	protected void updateReportsSuggestNationMessages() {
		ArrayList<String> nationMsgs = this.turnInfo.getNationInfoWrapper().getRumors();
		String prefix = "Reports suggest the presence of holdings/forces of the "; //$NON-NLS-1$
		String suffix = " at 0101." ; //$NON-NLS-1$
		for (String msg : nationMsgs) {
			if (msg.startsWith(prefix)) {
				String nationname = msg.substring(prefix.length(),msg.length()-suffix.length()) ;
				String hexno_string = msg.substring(msg.length()-5, msg.length() - 1);
				int hexno = Integer.parseInt(hexno_string);
				PopulationCenter pc = (PopulationCenter) this.game.getTurn().getPopCenter(hexno);
				if (pc != null) {
					Nation n = GameHolder.instance().getGame().getMetadata().getNationByName(nationname);
					if (n != null)
						pc.setNationNo(n.getNumber());
				}
			}
		}
	}
	
	private void updateKSArtifactIDsFromNationMessages() {
		ArrayList<String> nationMsgs = this.turnInfo.getNationInfoWrapper().getRumors();
		String prefix = "The artefact going by the name of "; //$NON-NLS-1$
		String middle = " has been identified as item #"; //$NON-NLS-1$
		for (String msg : nationMsgs) {
			if (msg.startsWith(prefix)) {
				int i = prefix.length();
				int j = msg.indexOf(middle);
				if (j > -1) {
					String artiName = msg.substring(i, j);
					String artiNoStr = msg.substring(j + middle.length(), msg.length() - 1);
					if (!updateArtifactNumber(artiName, artiNoStr)) {
						logger.error("Failed to parse artifact number " + artiNoStr + " from rumor " + msg); //$NON-NLS-1$ //$NON-NLS-2$
					}
				}
			}
		}
		middle = " is artifact "; //$NON-NLS-1$
		for (String msg : nationMsgs) {
			if (msg.contains(middle)) {
				if (msg.startsWith("The ")) { //$NON-NLS-1$
					msg = msg.substring(4);
				}
				int i = msg.indexOf(middle);
				String artiName = msg.substring(0, i);
				int j = msg.lastIndexOf(' ');
				if (j == -1)
					continue;
				String artiNoStr = msg.substring(j + 1);
				if (!updateArtifactNumber(artiName, artiNoStr)) {
					logger.error("Failed to parse artifact number " + artiNoStr + " from rumor " + msg); //$NON-NLS-1$ //$NON-NLS-2$
				}
			}
		}
	}
	//TODO: migrate to somewhere else, should probably use with
	// TurnNewXmlReader.updateArtifacts()
	// TurnPdfReader.updateArtifacts()
	// ArtifactInfoCollector.computeWrappersForTurn()
	// RAResultWrapper.updateGame();
	// which use slightly different methods
	// and consider using GameMetadata.findFirstArtifactByName()
	private boolean updateArtifactNumber(String artiName,String artiNoStr) {
		try {
			// try converting the number first, as if it fails no more work to do.
			int number = Integer.parseInt(artiNoStr);
			String artiNameInAscii = AsciiUtils.convertNonAscii(artiName.trim());
			for (ArtifactInfo ai : this.game.getMetadata().getArtifacts().getItems()) {
				if (AsciiUtils.convertNonAscii(ai.getName()).equalsIgnoreCase(artiNameInAscii)) {
					ai.setNo(number);
					break;
				}
			}
			return true;
		} catch (Exception exc) {
			return false;
		}
	}
	
	private void updateKSArtifactIDsAndLocationsFromNationMessages() {
		ArrayList<String> nationMsgs = this.turnInfo.getNationInfoWrapper().getRumors();
		String prefix = "The "; //$NON-NLS-1$
		String middle = Messages.getString("subTask.272"); //$NON-NLS-1$
		Container<Artifact> artis = this.turn.getArtifacts();
		for (String msg : nationMsgs) {
			if (msg.contains(middle)) {
				if (msg.startsWith("The ")) { //$NON-NLS-1$
					prefix = "The "; //$NON-NLS-1$
				} else {
					prefix = ""; //$NON-NLS-1$
				}
				int i = prefix.length();
				Integer artiNo = null;
				String artiName;
				int k = msg.indexOf(","); //$NON-NLS-1$
				if (k > -1) {
					artiName = msg.substring(i, k);
				} else {
					k = msg.indexOf(" was discovered"); //$NON-NLS-1$
					if (k == -1) {
						k = msg.indexOf(" were discovered"); //$NON-NLS-1$
					}
					if (k == -1)
						return;
					artiName = msg.substring(i, k);
				}
				artiName = artiName.trim();
				int l = msg.indexOf(",", k + 1); //$NON-NLS-1$
				if (l > -1) {
					String artiNoStr = msg.substring(k + 3, l);
					// special handling to allow for the rumor sentence having
					// or missing the period at the end

					try {
						artiNo = Integer.parseInt(artiNoStr);
					} catch (Exception exc) {
						artiNo = -1;
					}
				}
				int endsWithPeriod = msg.endsWith(".") ? 1 : 0; //$NON-NLS-1$
				String artiHex = msg.substring(msg.length() - 4 - endsWithPeriod, msg.length() - endsWithPeriod);

				String artiNameInAscii = AsciiUtils.convertNonAscii(artiName.trim());

				ArtifactInfo ai = null;
				for (ArtifactInfo iai : this.game.getMetadata().getArtifacts().getItems()) {
					String iaiName = AsciiUtils.convertNonAscii(iai.getName());
//					System.out.println(iaiName + " - " + artiNameInAscii + " " + iaiName.equalsIgnoreCase(artiNameInAscii));
					if (iaiName.equalsIgnoreCase(artiNameInAscii)) {
						ai = iai;
						if (artiNo != null)
							ai.setNo(artiNo);
						break;
					}
				}
				if (ai != null) {
					Artifact a = artis.findFirstByProperty("name", ai.getName()); //$NON-NLS-1$
					if (a == null) {
						a = new Artifact();
						a.setNumber(0);
						if (artiNo != null)
							a.setNumber(artiNo);
						a.setName(artiName);
						try {
							a.setHexNo(Integer.parseInt(artiHex));
						} catch (Exception exc) {

						}
						artis.addItem(a);
					} else {
						a.setHexNo(Integer.parseInt(artiHex));
						artis.refreshItem(a);
					}
				}

			}
		}
	}

	public boolean getErrorOccured() {
		return this.errorOccured;
	}

	public void setErrorOccured(boolean errorOccured) {
		this.errorOccured = errorOccured;
	}
	public static void updateOldPCs(Turn turn,int turnNo,int tiNationNo,int nationCapitalHex,ArrayList<PopCenterWrapper> pcws,InfoSource infoSource,ArrayList<PopulationCenter> currentNationPops,GameMetadata gm) {
		// some workarounds here.
		// Historically, XML with information source == 1 has been marked as exhaustive, which is not always correct.
		Container<PopulationCenter> pcs = turn.getPopulationCenters();
		for (PopCenterWrapper pcw : pcws) {
			PopCenterXmlInfoSource pcInfoSource = new PopCenterXmlInfoSource(infoSource.getTurnNo(), tiNationNo, infoSource.getTurnNo());
			PopulationCenter newPc;
			try {
				newPc = pcw.getPopulationCenter();
				newPc.checkForCapital(tiNationNo, nationCapitalHex);
				logger.debug("Handling Pop Centre at " + newPc.getHexNo() + " with information source " + newPc.getInformationSource().toString()); //$NON-NLS-1$ //$NON-NLS-2$
				newPc.setInfoSource(pcInfoSource);
				if (newPc.getNationNo() == tiNationNo) {
					currentNationPops.add(newPc);
				}
				newPc.defaultName();
				logger.debug("NEW POP " + tiNationNo); //$NON-NLS-1$
				logger.debug("new:" + newPc.getHexNo() + " " + newPc.getName() + " " + newPc.getNationNo() + " " + pcInfoSource.getTurnNo() + " " + (pcInfoSource).getPreviousTurnNo()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
				
				if (newPc.isDefaultName()) {
					newPc.setTurnSeenOnMap(turnNo);
				}

				PopulationCenter oldPc = pcs.findFirstByProperties(new String[] { "x", "y" }, new Object[] { newPc.getX(), newPc.getY() }); //$NON-NLS-1$ //$NON-NLS-2$
				// for KS, try to find starting pc with same name
				if (gm.getGameType().equals(GameTypeEnum.gameKS) && (!newPc.isDefaultName())) {
					PopulationCenter startingPc = pcs.findFirstByProperty("name", newPc.getName()); //$NON-NLS-1$
					if (startingPc != null) {
						if (MetadataSource.class.isInstance(startingPc.getInfoSource()) && startingPc.getHexNo() != newPc.getHexNo()) {
							// remove starting pc
							pcs.removeItem(startingPc);
						}
					}
				}
				//TODO: refactor this ... split into something like switch(whatToDoWith(oldPc,newPc)) { .... }
				if (oldPc == null) {
					// no pc found - add newPc
					logger.debug("No Pop Centre found in turn, add."); //$NON-NLS-1$
					pcs.addItem(newPc);
				} else {
					logger.debug("old:" + oldPc.getHexNo() + " " + oldPc.getName() + " " + oldPc.getNationNo() + " " + oldPc.getInfoSource().getTurnNo()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
					logger.debug("Pop Centre found in turn."); //$NON-NLS-1$
					// distinguish cases
					if ((oldPc.getInfoSource().getTurnNo() == turnNo && XmlTurnInfoSource.class.isInstance(oldPc.getInfoSource()) && ((XmlTurnInfoSource) oldPc.getInfoSource()).getNationNo() == oldPc.getNationNo())
							&& (oldPc.getInformationSource().getValue() > newPc.getInformationSource().getValue())) {
						logger.debug("old pop too good - do not replace"); //$NON-NLS-1$
					} else if (XmlTurnInfoSource.class.isInstance(oldPc.getInfoSource()) && newPc.getNationNo() == ((XmlTurnInfoSource) newPc.getInfoSource()).getNationNo()) {
						logger.debug("pop center of same nation - replace"); //$NON-NLS-1$
						pcs.removeItem(oldPc);
						pcs.addItem(newPc);
					} else if (newPc.getNationNo() > 0) {
						logger.debug("simply replace"); //$NON-NLS-1$
						pcs.removeItem(oldPc);
						// if information source is lacking, update harbor from old pop
						if ((newPc.getInformationSource().getValue() < InformationSourceEnum.detailed.getValue())
							// don't trust the information source!
							|| (newPc.isUnlikelyToBeComplete())
							// if this was a map icon seen this turn then docks will have been correct
							// this stops docks been overwritten by army/char report which doesn't include docks
							|| (oldPc.getTurnSeenOnMap() == turnNo)){
							if (newPc.getHarbor().getSize() == 0) {
								newPc.setHarbor(oldPc.getHarbor());
								newPc.setTurnSeenOnMap(oldPc.getTurnSeenOnMap());
							}
						}
						
						// exhaustive info in XML for PC in turn that is not of
						// same nation... this is most likely the case of oldPc
						// being generated by a ScoPopResult and newPc being
						// the PC reported in the XML part of a subsequently
						// imported turn
						if (newPc.getInformationSource().getValue() == InformationSourceEnum.exhaustive.getValue() && newPc.getNationNo() != tiNationNo) {
							if (oldPc.getInfoSource().getTurnNo() < turnNo) {
								if (newPc.getNationNo() == 0)
									newPc.setNationNo(oldPc.getNationNo());
								if (!newPc.isDefaultName()) newPc.setHarbor(oldPc.getHarbor());
							} else {
								if (newPc.getLoyalty() < oldPc.getLoyalty())
									newPc.setLoyalty(oldPc.getLoyalty());
								if (newPc.getNationNo() == 0)
									newPc.setNationNo(oldPc.getNationNo());
								if (!newPc.isDefaultName()) newPc.setHarbor(oldPc.getHarbor());
								if (newPc.getCapital() != oldPc.getCapital()) {
									PopulationCenter oldCapital = turn.getCapital(newPc.getNationNo());
									if (oldCapital != null) {
										oldCapital.setCapital(false);
										turn.getPopulationCenters().refreshItem(oldCapital);
									}
									newPc.setCapital(oldPc.getCapital());
								}
								newPc.copyProduction(oldPc);
							}
						}
						pcs.addItem(newPc);
					} else {
						//nation 0
						boolean update = true;
						if (gm.getNewXmlFormat()) {
							if ((oldPc.getInfoSource().getTurnNo() == turnNo) && (oldPc.getInformationSource().getValue() > newPc.getInformationSource().getValue()))
								update = false;
							
								if (newPc.getTurnSeenOnMap() == turnNo) {
									// newPC is a map icon seen this turn so docks will be accurate
									logger.debug("add docks from map icon"); //$NON-NLS-1$
									oldPc.setHarbor(newPc.getHarbor());
								}
						}
						if (update) {
							logger.debug("replace/update"); //$NON-NLS-1$
							pcs.removeItem(oldPc);
							if (newPc.getSize() != PopulationCenterSizeEnum.ruins ) {
								newPc.setNationNo(oldPc.getNationNo());
							}
							newPc.setName(oldPc.getName());
							pcs.addItem(newPc);
							if (newPc.getLoyalty() == 0 && oldPc.getLoyalty() > 0) {
								// newPc.setLoyalty(oldPc.getLoyalty());
							}
							if (
									((newPc.getInformationSource() == InformationSourceEnum.exhaustive) && newPc.isUnlikelyToBeComplete()) // this is a workaround.
								|| (newPc.getInformationSource().getValue() < InformationSourceEnum.detailed.getValue())) {
								// if information source is lacking, update
								// harbor
								// from old pop
								//
								if (newPc.getHarbor().getSize() == 0) {
									newPc.setHarbor(oldPc.getHarbor());
								}
								
								//ATTENTION
								//Fixes issue of first sighting of enemy popcenter removing flags, however unsure if this creates issues when capitals do change in the game.
								newPc.setCapital(oldPc.getCapital());
							}
							
							int prevTurnNo = oldPc.getInfoSource().getTurnNo();
							if (PopCenterXmlInfoSource.class.isInstance(oldPc.getInfoSource())) {
								prevTurnNo = ((PopCenterXmlInfoSource) oldPc.getInfoSource()).getPreviousTurnNo();
							}
							((PopCenterXmlInfoSource) newPc.getInfoSource()).setPreviousTurn(prevTurnNo);
							logger.debug("updated new:" + newPc.getHexNo() + " " + newPc.getName() + " " + newPc.getNationNo() + " " + pcInfoSource.getTurnNo() + " " + (pcInfoSource).getPreviousTurnNo()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
						}
					}

					if (newPc.isDefaultName()) {
						newPc.setName(oldPc.getName());
					}
					if (oldPc != null && (oldPc.getInfoSource().getTurnNo() == turnNo) && oldPc.getCapital()) {
						if (XmlTurnInfoSource.class.isInstance(oldPc.getInfoSource())) {
							if (((XmlTurnInfoSource) oldPc.getInfoSource()).getNationNo() == oldPc.getNationNo() ) {
								newPc.setCapital(true);
							}
						}
					}
				}

			} catch (Exception exc) {
				throw exc;
			}

		}
		// handle pops that are "reported" as belonging to the current nation
		// but the current nation did not have them in the xml
		ArrayList<PopulationCenter> potentiallyLostPops = turn.getPopulationCenters().findAllByProperty("nationNo", tiNationNo); //$NON-NLS-1$
		for (PopulationCenter pop : potentiallyLostPops) {
			if (pop.getInfoSource().getTurnNo() == turnNo && pop.getInformationSource().getValue() >= InformationSourceEnum.detailed.getValue())
				continue;
			if (currentNationPops.contains(pop))
				continue;

			// pop has been lost
			pop.setNationNo(0);
			pop.setLoyalty(0);
		}
		for (PopulationCenter pop : new ArrayList<PopulationCenter>(potentiallyLostPops)) {
			turn.getPopulationCenters().refreshItem(pop);
		}
	}
}


