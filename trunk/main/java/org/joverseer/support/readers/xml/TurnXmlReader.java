package org.joverseer.support.readers.xml;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.digester.Digester;
import org.apache.commons.digester.RegexRules;
import org.apache.commons.digester.SetNestedPropertiesRule;
import org.apache.commons.digester.SimpleRegexMatcher;
import org.apache.log4j.Logger;
import org.joverseer.domain.Army;
import org.joverseer.domain.ArmySizeEnum;
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
import org.joverseer.preferences.PreferenceRegistry;
import org.joverseer.support.Container;
import org.joverseer.support.TurnInitializer;
import org.joverseer.support.infoSources.DerivedFromArmyInfoSource;
import org.joverseer.support.infoSources.InfoSource;
import org.joverseer.support.infoSources.MetadataSource;
import org.joverseer.support.infoSources.PopCenterXmlInfoSource;
import org.joverseer.support.infoSources.XmlTurnInfoSource;
import org.joverseer.tools.nationMessages.NationMessageParser;
import org.springframework.richclient.progress.ProgressMonitor;


public class TurnXmlReader implements Runnable{
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

    public ProgressMonitor getMonitor() {
        return monitor;
    }

    public void setMonitor(ProgressMonitor monitor) {
        this.monitor = monitor;
    }


    public void readFile(String fileName) throws Exception {
        try {
            digester = new Digester();
            digester.setValidating(false);
            digester.setRules(new RegexRules(new SimpleRegexMatcher()));
            // parse turn info
            digester.addObjectCreate("METurn", TurnInfo.class);
            // parse turn info attributes
            SetNestedPropertiesRule snpr = new SetNestedPropertiesRule();
            snpr = new SetNestedPropertiesRule(new String[]{"GameNo", "TurnNo", "NationNo", "GameType", "Secret", "DueDate", "Player", "Account"},
                    new String[]{"gameNo", "turnNo", "nationNo", "gameType", "securityCode", "dueDate", "playerName", "accountNo"});
            snpr.setAllowUnknownChildElements(true);
            digester.addRule("METurn/TurnInfo", snpr);

            // parse PCs
            // create container for pcs
            digester.addObjectCreate("METurn/PopCentres", "org.joverseer.support.Container");
            // add container to turn info
            digester.addSetNext("METurn/PopCentres", "setPopCentres");
            // create pc
            digester.addObjectCreate("METurn/PopCentres/PopCentre", "org.joverseer.support.readers.xml.PopCenterWrapper");
            // set hex
            digester.addSetProperties("METurn/PopCentres/PopCentre", "HexID", "hexID");
            // set nested properties
            digester.addRule("METurn/PopCentres/PopCentre",
                    snpr = new SetNestedPropertiesRule(new String[]{"Name", "Nation", "NationAllegience", "Size", "FortificationLevel", "Size", "Dock", "Capital", "Hidden", "Loyalty", "InformationSource", "Hidden"},
                            new String[]{"name", "nation", "nationAllegience", "size", "fortificationLevel", "size", "dock", "capital", "hidden", "loyalty", "informationSource", "hidden"}));
            // add to container
            digester.addSetNext("METurn/PopCentres/PopCentre", "addItem", "org.joverseer.support.readers.xml.PopCenterWrapper");

            // parse characters
            // create characters container
            digester.addObjectCreate("METurn/Characters", Container.class);
            // add contianer to turn info
            digester.addSetNext("METurn/Characters", "setCharacters");
            // create character object
            digester.addObjectCreate("METurn/Characters/Character", "org.joverseer.support.readers.xml.CharacterWrapper");
            // set id
            digester.addSetProperties("METurn/Characters/Character", "ID", "id");
            // set nested properties
            digester.addRule("METurn/Characters/Character",
                    snpr = new SetNestedPropertiesRule(
                            new String[]{"Name", "Nation", "Location", "Command", "TotalCommand", "Agent", "TotalAgent", "Mage", "TotalMage", "Emmisary", "TotalEmmisary", "Stealth", "TotalStealth", "Challenge", "Health", "Title", "InformationSource"},
                            new String[]{"name", "nation", "location", "command", "totalCommand", "agent", "totalAgent", "mage", "totalMage", "emmisary", "totalEmmisary", "stealth", "totalStealth", "challenge", "health", "title", "informationSource"}));
            snpr.setAllowUnknownChildElements(true);
            // add character to container
            digester.addSetNext("METurn/Characters/Character", "addItem", "org.joverseer.support.readers.xml.CharacterWrapper");
            // parse character artifacts
            // create artifact arraylist
            digester.addObjectCreate("METurn/Characters/Character/Artifacts", ArrayList.class);
            // add arraylist to character
            digester.addSetNext("METurn/Characters/Character/Artifacts", "setArtifacts");
            // prepare call to arraylist.add
            digester.addCallMethod("METurn/Characters/Character/Artifacts/Artifact", "add", 1);
            digester.addCallParam("METurn/Characters/Character/Artifacts/Artifact", 0);
            // parse character spells
            // create spell arraylist
            digester.addObjectCreate("METurn/Characters/Character/Spells", ArrayList.class);
            // add arraylist to character
            digester.addSetNext("METurn/Characters/Character/Spells", "setSpells");
            // prepare call to arraylist.add
            digester.addCallMethod("METurn/Characters/Character/Spells/Spell", "add", 1);
            digester.addCallParam("METurn/Characters/Character/Spells/Spell", 0);

            // parse armies
            // create armies container
            digester.addObjectCreate("METurn/Armies", Container.class);
            // add contianer to turn info
            digester.addSetNext("METurn/Armies", "setArmies");
            // create army object
            digester.addObjectCreate("METurn/Armies/Army", "org.joverseer.support.readers.xml.ArmyWrapper");
            // set hexId
            digester.addSetProperties("METurn/Armies/Army", "HexID", "hexID");
            // set nested properties
            digester.addRule("METurn/Armies/Army",
                    snpr = new SetNestedPropertiesRule(
                            new String[]{"Nation", "NationAllegience", "Size", "TroopCount", "Commander", "CommanderTitle", "ExtraInfo", "Navy", "InformationSource", "CharsTravellingWith"},
                            new String[]{"nation", "nationAllegience", "size", "troopCount", "commander", "commanderTitle", "extraInfo", "navy", "informationSource", "charsTravellingWith"}));
            snpr.setAllowUnknownChildElements(true);
            // add army to container
            digester.addSetNext("METurn/Armies/Army", "addItem", "org.joverseer.support.readers.xml.CharacterWrapper");

            // parse rumors
            // create nationinfo object
            digester.addObjectCreate("METurn/NationInfo", NationInfoWrapper.class);
            // read EmptyPopHexes
            digester.addCallMethod("METurn/NationInfo/EmptyPopHexes", "setEmptyPopHexes", 1);
            digester.addCallParam("METurn/NationInfo/EmptyPopHexes", 0, "HexIDList");
            // read PopHexes
            digester.addCallMethod("METurn/NationInfo/PopHexes", "setPopHexes", 1);
            digester.addCallParam("METurn/NationInfo/PopHexes", 0, "HexIDList");
            // add contianer to turn info
            digester.addSetNext("METurn/NationInfo", "setNationInfoWrapper");
            // create rumors arraylist
            digester.addObjectCreate("METurn/NationInfo/NationMessages", ArrayList.class);
            // add arraylist to nation info
            digester.addSetNext("METurn/NationInfo/NationMessages", "setRumors");
            // prepare call to arraylist.add
            digester.addCallMethod("METurn/NationInfo/NationMessages/NationMessage", "add", 1);
            digester.addCallParam("METurn/NationInfo/NationMessages/NationMessage", 0);

            // parse economy
            // create economy object
            digester.addObjectCreate("METurn/Economy", EconomyWrapper.class);
            // add economy
            digester.addSetNext("METurn/Economy", "setEconomy");
            // set properties
             digester.addRule("METurn/Economy/Nation",
                    snpr = new SetNestedPropertiesRule(
                            new String[]{"ArmyMaint", "PopMaint", "CharMaint", "TotalMaint", "TaxRate", "Revenue", "Surplus", "Reserve", "TaxBase"},
                            new String[]{"armyMaint", "popMaint", "charMaint", "totalMaint", "taxRate", "revenue", "surplus", "reserve", "taxBase"}));
            snpr.setAllowUnknownChildElements(true);
            // create product object
            digester.addObjectCreate("METurn/Economy/Market/Product", ProductWrapper.class);
            digester.addSetNext("METurn/Economy/Market/Product", "addProduct");
            digester.addSetProperties("METurn/Economy/Market/Product", "type", "type");
            // set nested properties
            digester.addRule("METurn/Economy/Market/Product",
                    snpr = new SetNestedPropertiesRule(
                            new String[]{"BuyPrice", "SellPrice", "MarketAvail", "NationStores", "NationProduction"},
                            new String[]{"buyPrice", "sellPrice", "marketAvail", "nationStores", "nationProduction"}));
            snpr.setAllowUnknownChildElements(true);
            if (getMonitor() != null) {
                getMonitor().subTaskStarted(String.format("Parsing file %s...", new Object[]{fileName}));
                getMonitor().worked(5);
            }
            turnInfo = (TurnInfo) digester.parse(fileName);
        }
        catch (Exception exc) {
            //todo fix
            throw new Exception("Error parsing Xml Turn file.", exc);
        }
    }

    public void run() {
        try {
            readFile(filename);
        }
        catch (Exception exc) {
        	monitor.subTaskStarted("Error : failed to read xml file (" + exc.getMessage() + ")");
        	errorOccured = true;
        }
        try {
            updateGame(game);
            game.setCurrentTurn(game.getMaxTurn());
            Thread.sleep(100);
        }
        catch (Exception exc) {
        	errorOccured = true;
        }
    }

    public void updateGame(Game game) throws Exception {
        if (turnInfo.getTurnNo() < game.getMaxTurn()) {
            //todo fix
            throw new Exception("Cannot import past turns.");
        }
        try {
            turn = null;
            if (turnInfo.getTurnNo() == game.getMaxTurn()) {
                turn = game.getTurn(game.getMaxTurn());
            } else {
                turn = new Turn();
                turn.setTurnNo(turnInfo.getTurnNo());
                TurnInitializer ti = new TurnInitializer();
                Turn lastTurn = game.getTurn();
                ti.initializeTurnWith(turn, lastTurn);
                game.addTurn(turn);
            }
            currentNationPops = new ArrayList<PopulationCenter>();
            
            // update player info
            PlayerInfo pi = (PlayerInfo)turn.getContainer(TurnElementsEnum.PlayerInfo).findFirstByProperty("nationNo", turnInfo.getNationNo());
            if (pi != null) {
                turn.getContainer(TurnElementsEnum.PlayerInfo).removeItem(pi);
            }
            pi = new PlayerInfo();
            pi.setNationNo(turnInfo.getNationNo());
            pi.setPlayerName(turnInfo.getPlayerName());
            pi.setDueDate(turnInfo.getDueDate());
            pi.setSecret(turnInfo.getSecurityCode());
            pi.setAccountNo(turnInfo.getAccountNo());
            turn.getContainer(TurnElementsEnum.PlayerInfo).addItem(pi);
            
            infoSource = new XmlTurnInfoSource(turnInfo.getTurnNo(), turnInfo.getNationNo());
            if (getMonitor() != null) {
                getMonitor().worked(50);
                getMonitor().subTaskStarted("Updating PCs...");
            }
            updatePCs();
            if (getMonitor() != null) {
                getMonitor().worked(60);
                getMonitor().subTaskStarted("Updating Chars...");
            }
            updateChars();
            if (getMonitor() != null) {
                getMonitor().worked(70);
                getMonitor().subTaskStarted("Updating Armies...");
            }
            updateArmies();
            if (getMonitor() != null) {
                getMonitor().worked(80);
                getMonitor().subTaskStarted("Updating Nation Info...");
            }
            updateNationInfo();
            if (getMonitor() != null) {
                getMonitor().worked(90);
                getMonitor().subTaskStarted("Updating Nation Messages...");
            }
            updateNationMessages();
            if (getMonitor() != null) {
                getMonitor().worked(100);
            }
        }
        catch (Exception exc) {
            if (getMonitor() != null) {
                getMonitor().worked(100);
                getMonitor().subTaskStarted("Error : '" + exc.getMessage() + "'.");
                errorOccured = true;
            }
            throw new Exception("Error updating game from Xml file.", exc);
        }
    }

    private void updatePCs() throws Exception {
        Container pcs = turn.getContainer(TurnElementsEnum.PopulationCenter);
        for (PopCenterWrapper pcw : (ArrayList<PopCenterWrapper>) turnInfo.getPopCentres().getItems()) {
            PopCenterXmlInfoSource pcInfoSource = new PopCenterXmlInfoSource(infoSource.getTurnNo(), turnInfo.getNationNo(), infoSource.getTurnNo());
            PopulationCenter newPc;
            try {
                newPc = pcw.getPopulationCenter();
                logger.debug(String.format("Handling Pop Centre at {0},{1} with information source {2}",
                        String.valueOf(newPc.getX()),
                        String.valueOf(newPc.getY()),
                        newPc.getInformationSource().toString()));
                newPc.setInfoSource(pcInfoSource);
                if (newPc.getNationNo() == turnInfo.getNationNo()) {
                    currentNationPops.add(newPc);
                }
                if (newPc.getName() == null || newPc.getName().equals("")) {
                	newPc.setName("Unknown (Map Icon)");
                }
                System.out.println("NEW POP " + turnInfo.getNationNo());
                System.out.println("new:" + newPc.getHexNo() + " " + newPc.getName() + " "  + newPc.getNationNo() + " " + pcInfoSource.getTurnNo() + " " + ((PopCenterXmlInfoSource)pcInfoSource).getPreviousTurnNo());
                
                PopulationCenter oldPc = (PopulationCenter) pcs.findFirstByProperties(new String[]{"x", "y"}, new Object[]{newPc.getX(), newPc.getY()});
                if (oldPc == null) {
                    // no pc found - add newPc
                    logger.debug("No Pop Centre found in turn, add.");
                    pcs.addItem(newPc);
                } else {
                    System.out.println("old:" + oldPc.getHexNo() + " " + oldPc.getName() + " "  + oldPc.getNationNo() + " " + oldPc.getInfoSource().getTurnNo());
                    logger.debug("Pop Centre found in turn.");
                    // distinguish cases
                    if (oldPc != null && oldPc.getInfoSource().getTurnNo() == turnInfo.getTurnNo() &&
                            ((XmlTurnInfoSource)oldPc.getInfoSource()).getNationNo() == oldPc.getNationNo()) {
                        System.out.println("old pop too good - do not replace");
                    } else if (newPc.getNationNo() == ((XmlTurnInfoSource)newPc.getInfoSource()).getNationNo()) {
                        System.out.println("pop center of same nation - replace");
                        pcs.removeItem(oldPc);
                        pcs.addItem(newPc);
                    } else if (newPc.getNationNo() > 0) {
                    	System.out.println("simply replace");
                    	pcs.removeItem(oldPc);
                    	pcs.addItem(newPc);
//                        if (newPc.getLoyalty() == 0 && oldPc.getLoyalty() > 0) {
//                            newPc.setLoyalty(oldPc.getLoyalty());
//                        }
                    } else {
                    	System.out.println("replace/update");
                    	pcs.removeItem(oldPc);
                    	pcs.addItem(newPc);
                    	newPc.setNationNo(oldPc.getNationNo());
                    	newPc.setName(oldPc.getName());
                        if (newPc.getLoyalty() == 0 && oldPc.getLoyalty() > 0) {
                            //newPc.setLoyalty(oldPc.getLoyalty());
                        }
                    	int prevTurnNo = oldPc.getInfoSource().getTurnNo();
                    	if (PopCenterXmlInfoSource.class.isInstance(oldPc.getInfoSource())) {
                    		prevTurnNo = ((PopCenterXmlInfoSource)oldPc.getInfoSource()).getPreviousTurnNo();
                    	}
                    	((PopCenterXmlInfoSource)newPc.getInfoSource()).setPreviousTurn(prevTurnNo);
                    	System.out.println("updated new:" + newPc.getHexNo() + " " + newPc.getName() + " "  + newPc.getNationNo() + " " + pcInfoSource.getTurnNo() + " " + ((PopCenterXmlInfoSource)pcInfoSource).getPreviousTurnNo());                    }
                    
                    if (newPc.getName().equals("Unknown (Map Icon)")) {
                    	newPc.setName(oldPc.getName());
                    }
                    if (oldPc != null && oldPc.getInfoSource().getTurnNo() == turnInfo.getTurnNo() &&
                            ((XmlTurnInfoSource)oldPc.getInfoSource()).getNationNo() == oldPc.getNationNo() &&
                            oldPc.getCapital()) {
                        newPc.setCapital(true);
                    }
//                    if (newPc.getInformationSource().getValue() > oldPc.getInformationSource().getValue() ||
//                            // added to handle issue with Unknown map icons overwritting pcs derived from previous turn
//                            (newPc.getInformationSource().getValue() == oldPc.getInformationSource().getValue() &&
//                              newPc.getInfoSource().getTurnNo() > oldPc.getInfoSource().getTurnNo()))
//                    {
//                        if (newPc.getName().equals("Unknown (Map Icon)")) {
//                            newPc.setName(oldPc.getName());
//                        }
//                        if (newPc.getNationNo() == 0) {
//                             newPc.setNationNo(oldPc.getNationNo());
//                        }
//                        pcs.removeItem(oldPc);
//                        pcs.addItem(newPc);
//                    } else if (MetadataSource.class.isInstance(oldPc.getInfoSource())) {
//                        // replace and keep name and nation if unknown
//                        if (newPc.getName().equals("Unknown (Map Icon)")) {
//                            newPc.setName(oldPc.getName());
//                            newPc.setNationNo(oldPc.getNationNo());
//                        }
//                        pcs.removeItem(oldPc);
//                        pcs.addItem(newPc);
//                    }
//                    else if (oldPc.getInfoSource().getTurnNo() < turnInfo.getTurnNo()) {
//                        if (newPc.getInformationSource().getValue() < oldPc.getInformationSource().getValue() || MetadataSource.class.isInstance(oldPc.getInfoSource())) {
//                            newPc.setName(oldPc.getName());
//                            //newPc.setNationNo(oldPc.getNationNo());
//                        }
//                        pcs.removeItem(oldPc);
//                        pcs.addItem(newPc);
//                    } 
                }
                
                // handle pops that are "reported" as belonging to the current nation
                // but the current nation did not have them in the xml
                ArrayList<PopulationCenter> potentiallyLostPops = (ArrayList<PopulationCenter>)turn.getContainer(TurnElementsEnum.PopulationCenter).findAllByProperty("nationNo", turnInfo.getNationNo());
                for (PopulationCenter pop : potentiallyLostPops) {
                    if (pop.getInfoSource().getTurnNo() == turnInfo.getTurnNo() && pop.getInformationSource().getValue() >= InformationSourceEnum.detailed.getValue()) continue;
                    if (currentNationPops.contains(pop)) continue;
                    
                    // pop has been lost
                    pop.setNationNo(0);
                    pop.setLoyalty(0);
                }
            }
            catch (Exception exc) {
                throw exc;
            }

        }
    }
    private void updateChars() throws Exception {
        Container chars = turn.getContainer(TurnElementsEnum.Character);
        for (CharacterWrapper cw : (ArrayList<CharacterWrapper>) turnInfo.getCharacters().getItems()) {
            Character newCharacter;
            Character oldCharacter;
            try {
                newCharacter = cw.getCharacter();
                oldCharacter = (Character) chars.findFirstByProperties(new String[]{"id"}, new Object[]{newCharacter.getId()});
                newCharacter.setInfoSource(infoSource);
                logger.debug(String.format("Handling Character {3} at {0},{1} with information source {2}",
                        String.valueOf(newCharacter.getX()),
                        String.valueOf(newCharacter.getY()),
                        newCharacter.getInformationSource().toString(),
                        newCharacter.getId()));
                if (oldCharacter == null) {
                    // no char found - add
                    logger.debug("No Character found in turn, add.");
                    chars.addItem(newCharacter);
                } else {
                    // char found
                    logger.debug("Character found in turn.");
                    if (DerivedFromArmyInfoSource.class.isInstance(oldCharacter.getInfoSource()) ||
                            (newCharacter.getInformationSource().getValue() > oldCharacter.getInformationSource().getValue()))
                    {
                        logger.debug("Replace.");
                        chars.removeItem(oldCharacter);
                        chars.addItem(newCharacter);
                    }
                }
            }
            catch (Exception exc) {
                throw exc;
            }
        }
    }

    private void updateArmies() throws Exception {
        String UNKNOWN_MAP_ICON = "Unknown (Map Icon)";
        Container chars = turn.getContainer(TurnElementsEnum.Character);
        Container armies = turn.getContainer(TurnElementsEnum.Army);
        for (ArmyWrapper aw : (ArrayList<ArmyWrapper>) turnInfo.getArmies().getItems()) {
            Army newArmy;
            Army oldArmy;
            try {
                newArmy = aw.getArmy();
                if (!aw.getCommander().startsWith(UNKNOWN_MAP_ICON)) {
                    // known army, try to find army with same commander name
                    oldArmy = (Army) armies.findFirstByProperties(new String[]{"commanderName"}, new Object[]{newArmy.getCommanderName()});
                    if (oldArmy == null) {
                        // try to find unknown army with same allegience in same hex
                        oldArmy = (Army) armies.findFirstByProperties(
                                new String[]{"commanderName", "hexNo", "nationAllegiance"},
                                new Object[]{UNKNOWN_MAP_ICON, newArmy.getHexNo(), newArmy.getNationAllegiance()});
                        if (oldArmy != null) {
                            int a = 1;
                        }
                    }
                } else {
                    // try to find unknown army of same allegiance in hex
                    oldArmy = (Army) armies.findFirstByProperties(
                            new String[]{"commanderName", "hexNo", "nationAllegiance"},
                            new Object[]{newArmy.getCommanderName(), newArmy.getHexNo(), newArmy.getNationAllegiance()});
                    if (oldArmy == null) {
                        // try to find known army of same allegiance in hex
                        oldArmy = (Army) armies.findFirstByProperties(
                            new String[]{"hexNo", "nationAllegiance"},
                            new Object[]{newArmy.getHexNo(), newArmy.getNationAllegiance()});
                    }
                }
                newArmy.setInfoSource(infoSource);
                logger.debug(String.format("Handling Army {3} at {0},{1} with information source {2}",
                        String.valueOf(newArmy.getX()),
                        String.valueOf(newArmy.getY()),
                        newArmy.getInformationSource().toString(),
                        newArmy.getCommanderName()));
                if (oldArmy== null) {
                    // look for "Unknown map icon" army at same hex with same allegiance
                    ArrayList oldArmies = armies.findAllByProperties(new String[]{"x", "y"}, new Object[]{newArmy.getX(), newArmy.getY()});

                    // no army found - add
                    logger.debug("No Army found in turn, add.");
                    if (newArmy.getCommanderName().toUpperCase().startsWith("UNKNOWN ")) {
                        // new army is Unknown
                        // check that there is not already an army of the same allegiance that is known
                        boolean bFound = false;
                        for (Army oa : (ArrayList<Army>)oldArmies) {
                            if (!oa.getCommanderName().toUpperCase().startsWith("UNKNOWN ") ||
                                    oa.getNationAllegiance() == newArmy.getNationAllegiance()) {
                                bFound = true;
                            }
                        }
                        if (!bFound) { // if no known army, add
                            armies.addItem(newArmy);
                        }
                    } else {
                        for (Army oa : (ArrayList<Army>)oldArmies) {
                            if (oa.getCommanderName().toUpperCase().startsWith("UNKNOWN ") &&
                                    oa.getNationAllegiance() == newArmy.getNationAllegiance()) {
                                armies.removeItem(oa);
                            }
                        }
                        armies.addItem(newArmy);
                    }

                } else {
                    // army found
                    logger.debug("Army found in turn.");
                    if (newArmy.getInformationSource().getValue() > oldArmy.getInformationSource().getValue() || newArmy.getNationNo() == turnInfo.getNationNo())
                    {
                        logger.debug("Replace.");
                        armies.removeItem(oldArmy);
                        armies.addItem(newArmy);
                        if (newArmy.getSize() == ArmySizeEnum.unknown) {
                            newArmy.setSize(oldArmy.getSize());
                        }
                    }
                }

                // look for commander
                String commanderName = newArmy.getCommanderName();
                // do not generate character for unknown armies
                if (!commanderName.toUpperCase().startsWith("UNKNOWN ")) {
                    String commanderId = Character.getIdFromName(commanderName);
                    Character ch = (Character)chars.findFirstByProperty("id", commanderId);
                    if (ch == null) {
                        // no found, add
                        Character cmd = new Character();
                        cmd.setName(commanderName);
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
            catch (Exception exc) {
                throw exc;
            }
        }

    }

    private void updateNationInfo() {
        Container pcs = turn.getContainer(TurnElementsEnum.PopulationCenter);
        Container nationEconomies = turn.getContainer(TurnElementsEnum.NationEconomy);
        NationEconomy oldNe = (NationEconomy)nationEconomies.findFirstByProperty("nationNo", turnInfo.getNationNo());
        if (oldNe != null) {
            nationEconomies.removeItem(oldNe);
        }
        NationEconomy ne = turnInfo.getEconomy().getNationEconomy();
        ne.setNationNo(turnInfo.getNationNo());
        nationEconomies.addItem(ne);
        
        turnInfo.getEconomy().updateProductPrices(turn);

        Container hexInfos = turn.getContainer(TurnElementsEnum.HexInfo);

        ArrayList newHexInfos = turnInfo.getNationInfoWrapper().getHexInfos(turnInfo.getNationNo());
        for (HexInfo hi : (ArrayList<HexInfo>)newHexInfos) {
            HexInfo oldHi = (HexInfo)hexInfos.findFirstByProperty("hexNo", hi.getHexNo());
            if (oldHi == null) {
                hexInfos.addItem(hi);
            } else {
                oldHi.merge(hi);
            }
        }
        
        // handle current nation pops
        for (PopulationCenter pc : currentNationPops) {
            HexInfo hi = (HexInfo)hexInfos.findFirstByProperty("hexNo", pc.getHexNo());
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
        ArrayList toRemove = new ArrayList();
        
        String hiddenPopsPreferenceVal = PreferenceRegistry.instance().getPreferenceValue("map.hiddenPops");
        boolean keepHiddenPops = hiddenPopsPreferenceVal==null || hiddenPopsPreferenceVal.equals("alwaysShow"); 
        
        for (PopulationCenter pc : (ArrayList<PopulationCenter>)pcs.getItems()) {
            if (pc.getInfoSource().getTurnNo() == turnInfo.getTurnNo() && pc.getInformationSource().getValue() >= InformationSourceEnum.detailed.getValue() && !MetadataSource.class.isInstance(pc.getInfoSource())) continue;
            if (pc.getSize() == PopulationCenterSizeEnum.ruins) continue;
            HexInfo hi = (HexInfo)hexInfos.findFirstByProperty("hexNo", pc.getHexNo());
            if (hi.getVisible() && !hi.getHasPopulationCenter()) {
                if (keepHiddenPops && pc.getHidden()) continue;
                toRemove.add(pc);
            }
        }
        pcs.removeAll(toRemove);
    }

    private void updateNationMessages() {
        Container nationMessages = turn.getContainer(TurnElementsEnum.NationMessage);
        nationMessages.removeAllByProperties("nationNo", turnInfo.getNationNo());

        NationMessageParser nmp = new NationMessageParser(turnInfo.getTurnNo());
        
        ArrayList nationMsgs = turnInfo.getNationInfoWrapper().getRumors();
        Pattern hexLoc = Pattern.compile("at (\\d\\d\\d\\d)");
        for (String msg : (ArrayList<String>)nationMsgs) {
            NationMessage nm = new NationMessage();
            nm.setMessage(msg);
            nm.setNationNo(turnInfo.getNationNo());
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
    }

	public boolean getErrorOccured() {
		return errorOccured;
	}

	public void setErrorOccured(boolean errorOccured) {
		this.errorOccured = errorOccured;
	}
    
    
}
