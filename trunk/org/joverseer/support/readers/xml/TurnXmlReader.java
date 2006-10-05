package org.joverseer.support.readers.xml;

import org.apache.commons.digester.Digester;
import org.apache.commons.digester.RegexRules;
import org.apache.commons.digester.SimpleRegexMatcher;
import org.apache.commons.digester.SetNestedPropertiesRule;
import org.apache.log4j.Logger;
import org.joverseer.support.Container;
import org.joverseer.support.TurnInitializer;
import org.joverseer.support.infoSources.XmlTurnInfoSource;
import org.joverseer.game.Turn;
import org.joverseer.game.Game;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.domain.PopulationCenter;
import org.joverseer.domain.InformationSourceEnum;

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
                snpr = new SetNestedPropertiesRule(new String[]{"Name", "Nation", "Location", "Command", "TotalCommand", "Agent", "TotalAgent", "Mage", "TotalMage", "Emmisary", "TotalEmmisary", "Stealth", "TotalStealth", "Challenge", "Health", "Title", "InformationSource" },
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

        turnInfo = (TurnInfo)digester.parse(fileName);
    }

    public void updateGame(Game game) throws Exception {
        if (turnInfo.getTurnNo() < game.getMaxTurn()) {
            //todo fix
            throw new Exception("Cannot import past turns.");
        }

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

        Container pcs = turn.getContainer(TurnElementsEnum.PopulationCenter);
        for (PopCenterWrapper pcw : (ArrayList<PopCenterWrapper>)turnInfo.getPopCentres().getItems()) {
            PopulationCenter newPc = pcw.getPopulationCenter();
            logger.debug(String.format("Handling Pop Centre at {0},{1} with information source {2}",
                            String.valueOf(newPc.getX()),
                            String.valueOf(newPc.getY()),
                            newPc.getInformationSource().toString()));
            newPc.setInfoSource(new XmlTurnInfoSource(turnInfo.getTurnNo(), turnInfo.getNationNo()));
            PopulationCenter oldPc = (PopulationCenter)pcs.findFirstByProperties(new String[]{"x", "y"}, new Object[]{newPc.getX(), newPc.getY()});
            if (oldPc == null) {
                // no pc found - add newPc
                logger.debug("No Pop Centre found in previous turn, add.");
                pcs.addItem(newPc);
            } else {
                logger.debug("Pop Centre found in previous turn.");
                // distinguish cases
                if (newPc.getInformationSource() == InformationSourceEnum.exhaustive ||
                    newPc.getInformationSource() == InformationSourceEnum.detailed) {
                    logger.debug("Replace.");
                    pcs.removeItem(oldPc);
                    pcs.addItem(newPc);
                } else if (newPc.getInformationSource() == InformationSourceEnum.some) {
                    logger.debug("Replace.");
                    pcs.removeItem(oldPc);
                    pcs.addItem(newPc);
                } else if (newPc.getInformationSource() == InformationSourceEnum.limited) {
                    logger.debug("Replace.");
                    pcs.removeItem(oldPc);
                    pcs.addItem(newPc);
                }
            }
        }



    }
}
