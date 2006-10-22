package org.joverseer.support.readers.xml;

import org.apache.commons.digester.Digester;
import org.apache.commons.digester.RegexRules;
import org.apache.commons.digester.SimpleRegexMatcher;
import org.apache.commons.digester.SetNestedPropertiesRule;
import org.apache.log4j.Logger;
import org.joverseer.support.Container;
import org.joverseer.support.TurnInitializer;
import org.joverseer.support.infoSources.XmlTurnInfoSource;
import org.joverseer.support.infoSources.InfoSource;
import org.joverseer.game.Turn;
import org.joverseer.game.Game;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.domain.*;
import org.joverseer.domain.Character;
import org.joverseer.metadata.domain.NationInfoWrapper;

import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: mskounak
 * Date: Sep 16, 2006
 * Time: 8:46:17 PM
 * To change this template use File | Settings | File Templates.
 */
public class TurnXmlReader {
    static Logger logger = Logger.getLogger(TurnXmlReader.class);

    TurnInfo turnInfo = null;

    public void readFile(String fileName) throws Exception {
        try {
            Digester digester = new Digester();
            digester.setValidating(false);
            digester.setRules(new RegexRules(new SimpleRegexMatcher()));
            // parse turn info
            digester.addObjectCreate("METurn", TurnInfo.class);
            // parse turn info attributes
            SetNestedPropertiesRule snpr = new SetNestedPropertiesRule();
            snpr = new SetNestedPropertiesRule(new String[]{"GameNo", "TurnNo", "NationNo", "GameType"},
                    new String[]{"gameNo", "turnNo", "nationNo", "gameType"});
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
            digester.addCallMethod("METurn/Characters/Character/Artifacts", "add", 1);
            digester.addCallParam("METurn/Characters/Character/Artifacts/Artifact", 0);
            // parse character spells
            // create spell arraylist
            digester.addObjectCreate("METurn/Characters/Character/Spells", ArrayList.class);
            // add arraylist to character
            digester.addSetNext("METurn/Characters/Character/Spells", "setSpells");
            // prepare call to arraylist.add
            digester.addCallParam("METurn/Characters/Character/Spells/Spell", 0);
            digester.addCallMethod("METurn/Characters/Character/Spells", "add", 1);

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
                            new String[]{"Nation", "NationAllegience", "Size", "TroopCount", "Commander", "CommanderTitle", "ExtraInfo", "Navy", "InformationSource", "CharsTravellingWith "},
                            new String[]{"nation", "nationAllegience", "size", "troopCount", "commander", "commanderTitle", "extraInfo", "navy", "informationSource", "charsTravellingWith "}));
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
            turnInfo = (TurnInfo) digester.parse(fileName);
        }
        catch (Exception exc) {
            //todo fix
            throw new Exception("Error parsing Xml Turn file.", exc);
        }
    }

    public void updateGame(Game game) throws Exception {
        if (turnInfo.getTurnNo() < game.getMaxTurn()) {
            //todo fix
            throw new Exception("Cannot import past turns.");
        }
        try {
            Turn turn = null;
            if (turnInfo.getTurnNo() == game.getMaxTurn()) {
                turn = game.getTurn();
            } else {
                turn = new Turn();
                turn.setTurnNo(turnInfo.getTurnNo());
                TurnInitializer ti = new TurnInitializer();
                Turn lastTurn = game.getTurn();
                ti.initializeTurnWith(turn, lastTurn);
                game.addTurn(turn);
            }

            InfoSource infoSource = new XmlTurnInfoSource(turnInfo.getTurnNo(), turnInfo.getNationNo());

            Container pcs = turn.getContainer(TurnElementsEnum.PopulationCenter);
            for (PopCenterWrapper pcw : (ArrayList<PopCenterWrapper>) turnInfo.getPopCentres().getItems()) {
                PopulationCenter newPc = null;
                try {
                    newPc = pcw.getPopulationCenter();
                    logger.debug(String.format("Handling Pop Centre at {0},{1} with information source {2}",
                            String.valueOf(newPc.getX()),
                            String.valueOf(newPc.getY()),
                            newPc.getInformationSource().toString()));
                    newPc.setInfoSource(infoSource);
                    PopulationCenter oldPc = (PopulationCenter) pcs.findFirstByProperties(new String[]{"x", "y"}, new Object[]{newPc.getX(), newPc.getY()});
                    if (oldPc == null) {
                        // no pc found - add newPc
                        logger.debug("No Pop Centre found in turn, add.");
                        pcs.addItem(newPc);
                    } else {
                        logger.debug("Pop Centre found in turn.");
                        // distinguish cases
                        if (newPc.getInformationSource().getValue() > oldPc.getInformationSource().getValue()) {
                            pcs.removeItem(oldPc);
                            pcs.addItem(newPc);
                        } else if (oldPc.getInfoSource().getTurnNo() < turnInfo.getTurnNo()) {
                            if (newPc.getInformationSource().getValue() < oldPc.getInformationSource().getValue()) {
                                newPc.setName(oldPc.getName());
                                newPc.setNationNo(oldPc.getNationNo());
                            }
                            pcs.removeItem(oldPc);
                            pcs.addItem(newPc);
                        }

//                        if (newPc.getInformationSource() == InformationSourceEnum.exhaustive ||
//                                newPc.getInformationSource() == InformationSourceEnum.detailed) {
//                            logger.debug("Replace.");
//                            pcs.removeItem(oldPc);
//                            pcs.addItem(newPc);
//                        } else if (newPc.getInformationSource() == InformationSourceEnum.some) {
//                            logger.debug("Replace.");
//                            pcs.removeItem(oldPc);
//                            pcs.addItem(newPc);
//                        } else if (newPc.getInformationSource() == InformationSourceEnum.limited) {
//                            logger.debug("Replace.");
//                            pcs.removeItem(oldPc);
//                            pcs.addItem(newPc);
//                        }
                    }
                }
                catch (Exception exc) {
                    throw exc;
                }

            }

            Container chars = turn.getContainer(TurnElementsEnum.Character);
            for (CharacterWrapper cw : (ArrayList<CharacterWrapper>) turnInfo.getCharacters().getItems()) {
                Character newCharacter = null;
                Character oldCharacter = null;
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
                        if (newCharacter.getInformationSource().getValue() > oldCharacter.getInformationSource().getValue())
                        {
                            logger.debug("Replace.");
                            chars.removeItem(newCharacter);
                            chars.addItem(newCharacter);
                        }
                    }
                }
                catch (Exception exc) {
                    throw exc;
                }
            }

            Container armies = turn.getContainer(TurnElementsEnum.Army);
            for (ArmyWrapper aw : (ArrayList<ArmyWrapper>) turnInfo.getArmies().getItems()) {
                Army newArmy = null;
                Army oldArmy = null;
                try {
                    newArmy = aw.getArmy();
                    oldArmy = (Army) armies.findFirstByProperties(new String[]{"commanderName"}, new Object[]{newArmy.getCommanderName()});
                    newArmy.setInfoSource(infoSource);
                    logger.debug(String.format("Handling Army {3} at {0},{1} with information source {2}",
                            String.valueOf(newArmy.getX()),
                            String.valueOf(newArmy.getY()),
                            newArmy.getInformationSource().toString(),
                            newArmy.getCommanderName()));
                    if (oldArmy== null) {
                        // no char found - add
                        logger.debug("No Army found in turn, add.");
                        armies.addItem(newArmy);
                    } else {
                        // char found
                        logger.debug("Army found in turn.");
                        if (newArmy.getInformationSource().getValue() > oldArmy.getInformationSource().getValue())
                        {
                            logger.debug("Replace.");
                            armies.removeItem(oldArmy);
                            armies.addItem(newArmy);
                        }
                    }
                }
                catch (Exception exc) {
                    throw exc;
                }
            }

            Container nationEconomies = turn.getContainer(TurnElementsEnum.NationEconomy);
            NationEconomy oldNe = (NationEconomy)nationEconomies.findFirstByProperty("nationNo", turnInfo.getNationNo());
            if (oldNe != null) {
                nationEconomies.removeItem(oldNe);
            }
            NationEconomy ne = turnInfo.getEconomy().getNationEconomy();
            ne.setNationNo(turnInfo.getNationNo());
            nationEconomies.addItem(ne);

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
        }
        catch (Exception exc) {
            throw new Exception("Error updating game from Xml file.", exc);
        }

    }
}
