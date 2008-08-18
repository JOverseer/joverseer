package org.joverseer.support.readers.newXml;

import java.util.ArrayList;

import org.apache.commons.digester.Digester;
import org.apache.commons.digester.RegexRules;
import org.apache.commons.digester.SetNestedPropertiesRule;
import org.apache.commons.digester.SimpleRegexMatcher;
import org.apache.log4j.Logger;
import org.joverseer.domain.Army;
import org.joverseer.domain.Artifact;
import org.joverseer.domain.Company;
import org.joverseer.domain.Character;
import org.joverseer.domain.Encounter;
import org.joverseer.domain.NationRelations;
import org.joverseer.domain.NationRelationsEnum;
import org.joverseer.domain.PopulationCenter;
import org.joverseer.game.Game;
import org.joverseer.game.Turn;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.metadata.GameTypeEnum;
import org.joverseer.metadata.domain.ArtifactInfo;
import org.joverseer.metadata.domain.Nation;
import org.joverseer.metadata.domain.NationAllegianceEnum;
import org.joverseer.support.AsciiUtils;
import org.joverseer.support.Container;
import org.joverseer.support.infoSources.InfoSource;
import org.joverseer.support.infoSources.XmlExtraTurnInfoSource;
import org.joverseer.support.readers.pdf.OrderResult;
import org.springframework.richclient.progress.ProgressMonitor;

public class TurnNewXmlReader implements Runnable {
	static Logger logger = Logger.getLogger(TurnNewXmlReader.class);

	TurnInfo turnInfo = null;
	Digester digester = null;
	Turn turn = null;
	InfoSource infoSource = null;
	ArrayList<PopulationCenter> currentNationPops = new ArrayList<PopulationCenter>();
	Game game;
	String filename;
	ProgressMonitor monitor;
	boolean errorOccured = false;
	int nationNo;

	public TurnNewXmlReader(Game game, String filename, int nationNo) {
		this.game = game;
		this.filename = filename;
		this.nationNo = nationNo;
	}

	public ProgressMonitor getMonitor() {
		return monitor;
	}

	public void setMonitor(ProgressMonitor monitor) {
		this.monitor = monitor;
	}
	
	public boolean getErrorOccured() {
		return errorOccured;
	}

	public void setErrorOccured(boolean errorOccured) {
		this.errorOccured = errorOccured;
	}

	public void readFile(String fileName) throws Exception {
		try {
			SetNestedPropertiesRule snpr;
			
			digester = new Digester();
			digester.setValidating(false);
			digester.setRules(new RegexRules(new SimpleRegexMatcher()));
			digester.addObjectCreate("METurn", TurnInfo.class);
			// parse properties
			//set season changing
            digester.addSetProperties("METurn/More/TurnInfo/Season", "changing", "seasonChanging");
            // set nested properties
            digester.addRule("METurn/More/TurnInfo",
                    snpr = new SetNestedPropertiesRule(new String[]{"Season", "NationAlignment"},
                            new String[]{"season", "alignment"}));
            snpr.setAllowUnknownChildElements(true);
			
			// create container for Non Hidden Artifactss
            digester.addObjectCreate("METurn/ArtifactInfo/NonHiddenArtifacts", "org.joverseer.support.Container");
            // add container to turn info
            digester.addSetNext("METurn/ArtifactInfo/NonHiddenArtifacts", "setNonHiddenArtifacts");
        	// create artifact wrapper
            digester.addObjectCreate("METurn/ArtifactInfo/NonHiddenArtifacts/Artifact", "org.joverseer.support.readers.newXml.ArtifactWrapper");
            // set id
            digester.addSetProperties("METurn/ArtifactInfo/NonHiddenArtifacts/Artifact", "ID", "id");
            // set nested properties
            digester.addRule("METurn/ArtifactInfo/NonHiddenArtifacts/Artifact",
                    snpr = new SetNestedPropertiesRule(new String[]{"Name", "MageSkill", "CommandSkill", "EmmisarySkill", "AgentSkill", "StealthSkill", "CombatSkill", "Alignment", "Latent", "Item"},
                            new String[]{"name", "mage", "command", "emissary", "agent", "stealth", "combat", "alignment", "latent", "item"}));
            snpr.setAllowUnknownChildElements(true);
            // add to container
            digester.addSetNext("METurn/ArtifactInfo/NonHiddenArtifacts/Artifact", "addItem", "org.joverseer.support.readers.newXml.ArtifactWrapper");
			// create container for Hidden Artifactss
            digester.addObjectCreate("METurn/ArtifactInfo/HiddenArtifacts", "org.joverseer.support.Container");
            // add container to turn info
            digester.addSetNext("METurn/ArtifactInfo/HiddenArtifacts", "setHiddenArtifacts");
        	// create artifact wrapper
            digester.addObjectCreate("METurn/ArtifactInfo/HiddenArtifacts/Artifact", "org.joverseer.support.readers.newXml.ArtifactWrapper");
            // set id
            digester.addSetProperties("METurn/ArtifactInfo/HiddenArtifacts/Artifact", "ID", "id");
            // set nested properties
            digester.addRule("METurn/ArtifactInfo/HiddenArtifacts/Artifact",
                    snpr = new SetNestedPropertiesRule(new String[]{"Name", "MageSkill", "CommandSkill", "EmmisarySkill", "AgentSkill", "StealthSkill", "CombatSkill", "Alignment", "Latent", "Item"},
                            new String[]{"name", "mage", "command", "emissary", "agent", "stealth", "combat", "alignment", "latent", "item"}));
            snpr.setAllowUnknownChildElements(true);
            // add to container
            digester.addSetNext("METurn/ArtifactInfo/HiddenArtifacts/Artifact", "addItem", "org.joverseer.support.readers.newXml.ArtifactWrapper");
            
            // create container for Pop Centers
            digester.addObjectCreate("METurn/More/PopCentres", "org.joverseer.support.Container");
            // add container to turn info
            digester.addSetNext("METurn/More/PopCentres", "setPopCentres");
            // create pop center wrapper
            digester.addObjectCreate("METurn/More/PopCentres/PopCentre", "org.joverseer.support.readers.newXml.PopCenterWrapper");
            // set hex no
            digester.addSetProperties("METurn/More/PopCentres/PopCentre", "HexID", "hexNo");
            // set nested properties
            digester.addRule("METurn/More/PopCentres/PopCentre",
                    snpr = new SetNestedPropertiesRule(new String[]{"Sieged", "Terrain", "Climate"},
                            new String[]{"sieged", "terrain", "climate"}));
            snpr.setAllowUnknownChildElements(true);
            // add to container
            digester.addSetNext("METurn/More/PopCentres/PopCentre", "addItem", "org.joverseer.support.readers.newXml.PopCenterWrapper");
            // create production wrapper
            digester.addObjectCreate("METurn/More/PopCentres/PopCentre/Product", "org.joverseer.support.readers.newXml.ProductionWrapper");
            // set type
            digester.addSetProperties("METurn/More/PopCentres/PopCentre/Product", "type", "type");
            // set nested properties
            digester.addRule("METurn/More/PopCentres/PopCentre/Product",
                    snpr = new SetNestedPropertiesRule(new String[]{"CurrentStores", "ExpProduction"},
                            new String[]{"currentStores", "expProduction"}));
            snpr.setAllowUnknownChildElements(true);
            // add to container
            digester.addSetNext("METurn/More/PopCentres/PopCentre/Product", "addProduct", "org.joverseer.support.readers.newXml.ProductionWrapper");
            
            // create container for Nation Relations
            digester.addObjectCreate("METurn/NationRelations", "org.joverseer.support.Container");
            // add container to turn info
            digester.addSetNext("METurn/NationRelations", "setNationRelations");
            // create nation relation wrapper
            digester.addObjectCreate("METurn/NationRelations/NationRelation", "org.joverseer.support.readers.newXml.NationRelationWrapper");
            // set hex no
            digester.addSetProperties("METurn/NationRelations/NationRelation", "ID", "nationNo");
            // set relation
            digester.addCallMethod("METurn/NationRelations/NationRelation", "setRelation", 1);
            digester.addCallParam("METurn/NationRelations/NationRelation", 0);
            // add to container
            digester.addSetNext("METurn/NationRelations/NationRelation", "addItem", "org.joverseer.support.readers.newXml.NationRelationWrapper");
            
            
            // create container for Companies
            digester.addObjectCreate("METurn/Companies", "org.joverseer.support.Container");
            // add container to turn info
            digester.addSetNext("METurn/Companies", "setCompanies");
            // create pop center wrapper
            digester.addObjectCreate("METurn/Companies/Company", "org.joverseer.support.readers.newXml.CompanyWrapper");
            // set hex no
            digester.addSetProperties("METurn/Companies/Company", "HexID", "hexNo");
            // set nested properties
            digester.addRule("METurn/Companies/Company",
                    snpr = new SetNestedPropertiesRule(new String[]{"CompanyCO"},
                            new String[]{"commander"}));
            snpr.setAllowUnknownChildElements(true);
            // set members
            digester.addCallMethod("METurn/Companies/Company/CompanyMember", "addMember", 1);
            digester.addCallParam("METurn/Companies/Company/CompanyMember", 0);
            // add to container
            digester.addSetNext("METurn/Companies/Company", "addItem", "org.joverseer.support.readers.newXml.CompanyWrapper");
            
            // create container for armies
            digester.addObjectCreate("METurn/More/Armies", "org.joverseer.support.Container");
            // add container to turn info
            digester.addSetNext("METurn/More/Armies", "setArmies");
            // create army wrapper
            digester.addObjectCreate("METurn/More/Armies/Army", "org.joverseer.support.readers.newXml.ArmyWrapper");
            //  set commander
            digester.addSetProperties("METurn/More/Armies/Army", "Commander", "commander");
            // set nested properties
            digester.addRule("METurn/More/Armies/Army",
                    snpr = new SetNestedPropertiesRule(new String[]{"Food", "Morale", "Warships", "Transports", "Warships", "Climate"},
                            new String[]{"food", "morale", "warships", "transports", "warships"}));
            snpr.setAllowUnknownChildElements(true);
        	// add to container
            digester.addSetNext("METurn/More/Armies/Army", "addItem", "org.joverseer.support.readers.newXml.ArmyWrapper");
            
            // create army regiment wrapper
            digester.addObjectCreate("METurn/More/Armies/Army/Troops", "org.joverseer.support.readers.newXml.ArmyRegimentWrapper");
            // set troop type
            digester.addSetProperties("METurn/More/Armies/Army/Troops", "Type", "troopType");
            // set nested properties
            digester.addRule("METurn/More/Armies/Army/Troops",
                    snpr = new SetNestedPropertiesRule(new String[]{"Number", "Training", "Weapons", "Armor", "Description"},
                            new String[]{"number", "training", "weapons", "armor", "description"}));
            snpr.setAllowUnknownChildElements(true);
        	// add regiment to army
            digester.addSetNext("METurn/More/Armies/Army/Troops", "addRegiment", "org.joverseer.support.readers.newXml.ArmyRegimentWrapper");

            // character messages
            digester.addObjectCreate("METurn/More/Characters/CharacterMessages", "org.joverseer.support.Container");
            // add container to turn info
            digester.addSetNext("METurn/More/Characters/CharacterMessages", "setCharMessages");
            // create character message wrapper
            digester.addObjectCreate("METurn/More/Characters/CharacterMessages/CharacterMessage", "org.joverseer.support.readers.newXml.CharacterMessageWrapper");
            //  set char
            digester.addSetProperties("METurn/More/Characters/CharacterMessages/CharacterMessage", "CharID", "charId");
            // add lines
            digester.addCallMethod("METurn/More/Characters/CharacterMessages/CharacterMessage/Lines/Line", "addLine", 1);
            digester.addCallParam("METurn/More/Characters/CharacterMessages/CharacterMessage/Lines/Line", 0);
        	// add to container
            digester.addSetNext("METurn/More/Characters/CharacterMessages/CharacterMessage", "addItem", "org.joverseer.support.readers.newXml.CharacterMessageWrapper");

            
            // create container for Encounters
            digester.addObjectCreate("METurn/EncounterMessages", "org.joverseer.support.Container");
            // add container to turn info
            digester.addSetNext("METurn/EncounterMessages", "setEncounters");
            // create encounter wrapper
            digester.addObjectCreate("METurn/EncounterMessages/Encounter", "org.joverseer.support.readers.newXml.EncounterWrapper");
            // set attributes
            digester.addSetProperties("METurn/EncounterMessages/Encounter", "CharID", "charId");
            digester.addSetProperties("METurn/EncounterMessages/Encounter", "Hex", "hex");
            digester.addSetProperties("METurn/EncounterMessages/Encounter", "Reacting", "reacting");
            // set nested properties
            digester.addRule("METurn/EncounterMessages/Encounter",
                    snpr = new SetNestedPropertiesRule(new String[]{"EncounterText"},
                            new String[]{"text"}));
            snpr.setAllowUnknownChildElements(true);
            // add to container
            digester.addSetNext("METurn/EncounterMessages/Encounter", "addItem", "org.joverseer.support.readers.newXml.EncounterWrapper");
            
            // create container for Hexes
            digester.addObjectCreate("METurn/Hexes", "org.joverseer.support.Container");
            // add container to turn info
            digester.addSetNext("METurn/Hexes", "setHexes");
            // create hex wrapper
            digester.addObjectCreate("METurn/Hexes/Hex", "org.joverseer.support.readers.newXml.HexWrapper");
            // set attributes
            // set nested properties
            digester.addRule("METurn/Hexes/Hex",
                    snpr = new SetNestedPropertiesRule(new String[]{"HexID", "Terrain", "PopcenterName", "PopcenterSize", "Roads", "Bridges", "Fords", "MinorRivers", "MajorRivers"},
                            new String[]{"hexID", "terrain", "popCenterName", "popCenterSize", "roads", "bridges", "fords", "minorRivers", "majorRivers"}));
            snpr.setAllowUnknownChildElements(true);
            // add to container
            digester.addSetNext("METurn/Hexes/Hex", "addItem", "org.joverseer.support.readers.newXml.HexWrapper");
            
            turnInfo = (TurnInfo) digester.parse(fileName);
		}

		catch (Exception exc) {
			// todo fix
			logger.error(exc);
			throw new Exception("Error parsing Xml Turn file.", exc);
		}
	}

	public void run() {
		try {
			readFile(filename);
		} catch (Exception exc) {
			monitor.subTaskStarted("Error : failed to read xml file ("
					+ exc.getMessage() + ")");
			errorOccured = true;
		}
		try {
			if (turnInfo == null) {
				return;
			}
			updateGame(game);
			game.setCurrentTurn(game.getMaxTurn());
			Thread.sleep(100);
		} catch (Exception exc) {
			errorOccured = true;
		}
	}
	
	public void updateGame(Game game) throws Exception {
        try {
        	infoSource = new XmlExtraTurnInfoSource(game.getMaxTurn(), nationNo);

        	turn = game.getTurn(game.getMaxTurn());
        	
        	if (getMonitor() != null) {
                getMonitor().worked(0);
                getMonitor().subTaskStarted("Updating pop centers...");
            }
            try {
                updatePopCenters(game);
            }
            catch (Exception exc) {
                logger.error(exc);
                errorOccured = true;
                getMonitor().subTaskStarted("Error: " + exc.getMessage());
            }
            if (getMonitor() != null) {
                getMonitor().worked(10);
                getMonitor().subTaskStarted("Updating artifacts...");
            }
            try {
                updateArtifacts(game);
            }
            catch (Exception exc) {
                logger.error(exc);
                errorOccured = true;
                getMonitor().subTaskStarted("Error: " + exc.getMessage());
            }
            if (getMonitor() != null) {
                getMonitor().worked(20);
                getMonitor().subTaskStarted("Updating relations...");
            }
            try {
                updateRelations(game);
            }
            catch (Exception exc) {
                logger.error(exc);
                errorOccured = true;
                getMonitor().subTaskStarted("Error: " + exc.getMessage());
            }
            if (getMonitor() != null) {
                getMonitor().worked(30);
                getMonitor().subTaskStarted("Updating companies...");
            }
            try {
                updateCompanies(game);
            }
            catch (Exception exc) {
                logger.error(exc);
                errorOccured = true;
                getMonitor().subTaskStarted("Error: " + exc.getMessage());
            }
            if (getMonitor() != null) {
                getMonitor().worked(40);
                getMonitor().subTaskStarted("Updating armies...");
            }
            try {
                updateArmies(game);
            }
            catch (Exception exc) {
                logger.error(exc);
                errorOccured = true;
                getMonitor().subTaskStarted("Error: " + exc.getMessage());
            }
            if (getMonitor() != null) {
                getMonitor().worked(50);
                getMonitor().subTaskStarted("Updating characters...");
            }
            try {
                updateCharacterMessages(game);
            }
            catch (Exception exc) {
                logger.error(exc);
                errorOccured = true;
                getMonitor().subTaskStarted("Error: " + exc.getMessage());
            }
            if (getMonitor() != null) {
                getMonitor().worked(60);
                getMonitor().subTaskStarted("Updating encounters...");
            }
            try {
                updateEncounters(game);
            }
            catch (Exception exc) {
                logger.error(exc);
                errorOccured = true;
                getMonitor().subTaskStarted("Error: " + exc.getMessage());
            }
            if (getMonitor() != null) {
                getMonitor().worked(70);
                getMonitor().subTaskStarted("Updating hexes...");
            }
            try {
                updateHexes(game);
            }
            catch (Exception exc) {
                logger.error(exc);
                errorOccured = true;
                getMonitor().subTaskStarted("Error: " + exc.getMessage());
            }
            if (getMonitor() != null) {
                getMonitor().worked(100);
            }
        }
        catch (Exception exc) {
        	
        }
	}
	
	private void updateHexes(Game game) throws Exception {
		Container hws = turnInfo.getHexes();
		if (hws == null) return;
		for (HexWrapper hw : (ArrayList<HexWrapper>)hws.getItems()) {
			hw.updateGame(game);
		}
	}
	
	private void updateEncounters(Game game) throws Exception {
		Container ews = turnInfo.getEncounters();
        if (ews == null) return;
        Container encounters = game.getTurn().getContainer(TurnElementsEnum.Encounter);
        for (EncounterWrapper ew : (ArrayList<EncounterWrapper>)ews.getItems()) {
            Encounter e = (Encounter)encounters.findFirstByProperties(new String[]{"character", "hexNo"}, new Object[]{ew.getCharId(), Integer.parseInt(ew.getHex())});
            if (e != null) {
                encounters.removeItem(e);
            }
            e = ew.getEncounter();
            Character c = (Character)game.getTurn().getContainer(TurnElementsEnum.Character).findFirstByProperty("id", e.getCharacter());
            e.setCharacter(c.getName());
            encounters.addItem(e);
        }
	}
	
	private void updateCharacterMessages(Game game) throws Exception {
		Container nrws = turnInfo.getCharMessages();
		Container cs = turn.getContainer(TurnElementsEnum.Character);
		for (CharacterMessageWrapper cmw : (ArrayList<CharacterMessageWrapper>)nrws.getItems()) {
			Character c = (Character)cs.findFirstByProperty("id", cmw.getCharId());
			if (c != null) {
				cmw.updateCharacter(c, game);
			}
			for (OrderResult or : (ArrayList<OrderResult>)cmw.getOrderResults()) {
				or.updateGame(turn, turnInfo.nationNo, c.getName());
			}
		}
        
	}
	
	private void updateRelations(Game game) throws Exception {
		Container nrws = turnInfo.getNationRelations();
        Container nationRelations = turn.getContainer(TurnElementsEnum.NationRelation);
        String pcsNotFound = "";
        
        Nation nation = game.getMetadata().getNationByNum(nationNo);
        if (nation == null) {
        	throw new Exception("Failed to find nation with number " + nationNo);
        }
        Container nrs = turn.getContainer(TurnElementsEnum.NationRelation);
        NationRelations nr = (NationRelations)nrs.findFirstByProperty("nationNo", nationNo);
        
        if (turnInfo.getAlignment() == 1) {
            nation.setAllegiance(NationAllegianceEnum.FreePeople);
        } else if (turnInfo.getAlignment() == 2) {
            nation.setAllegiance(NationAllegianceEnum.DarkServants);
        } else if (turnInfo.getAlignment() == 3) {
            nation.setAllegiance(NationAllegianceEnum.Neutral);
        } 
        if (nr == null) {
        	throw new Exception("Failed to retrieve NationRelations object for nation " + nationNo);
        }
        nr.setAllegiance(nation.getAllegiance());
        
        String problematicNations = "";
        for (NationRelationWrapper nrw : (ArrayList<NationRelationWrapper>)nrws.getItems()) {
        	Nation n = game.getMetadata().getNationByNum(nrw.getNationNo());
        	if (n == null) {
        		problematicNations += (problematicNations.equals("") ? "" : ", ") + nrw.getNationNo();
        	} else {
	            int natNo = n.getNumber();
	            NationRelationsEnum relation = NationRelationsEnum.Tolerated;
	            if (nrw.getRelation().equals("Friendly")) {
	                relation = NationRelationsEnum.Friendly;
	            } else if (nrw.getRelation().equals("Tolerated")) {
	                relation = NationRelationsEnum.Tolerated;
	            } else if (nrw.getRelation().equals("Neutral")) {
	                relation = NationRelationsEnum.Neutral;
	            } else if (nrw.getRelation().equals("Disliked")) {
	                relation = NationRelationsEnum.Disliked;
	            } else if (nrw.getRelation().equals("Hated")) {
	                relation = NationRelationsEnum.Hated;
	            }  
	            nr.setRelationsFor(natNo, relation);
        	}
        }
        if (!problematicNations.equals("")) {
        	throw new Exception("Failed to update relations with nations " + problematicNations + " because the nation names were invalid.");
        }
	}
	
	private void updateCompanies(Game game) {
		Container cws = turnInfo.getCompanies();
		Container cs = turn.getContainer(TurnElementsEnum.Company);
		for (CompanyWrapper cw : (ArrayList<CompanyWrapper>)cws.getItems()) {
			Company newC = cw.getCompany();
			newC.setInfoSource(infoSource);
            Company oldC = (Company)cs.findFirstByProperty("commander", newC.getCommander());
            if (oldC != null) {
                cs.removeItem(oldC);
            }
            cs.addItem(newC);
		}
	}
	
	private void updateArmies(Game game) {
		Container aws = turnInfo.getArmies();
		Container as = turn.getContainer(TurnElementsEnum.Army);
		for (ArmyWrapper aw : (ArrayList<ArmyWrapper>)aws.getItems()) {
            Army a = (Army)as.findFirstByProperty("commanderName", aw.getCommander());
            if (a != null) {
                aw.updateArmy(a);
            }
        }
	}
	
	private void updatePopCenters(Game game) {
		Container pcws = turnInfo.getPopCentres();
        Container pcs = turn.getContainer(TurnElementsEnum.PopulationCenter);
        String pcsNotFound = "";
        for (PopCenterWrapper pcw : (ArrayList<PopCenterWrapper>)pcws.getItems()) {
            PopulationCenter pc = (PopulationCenter)pcs.findFirstByProperty("hexNo", pcw.getHexNo());
            pcw.updatePopCenter(pc);
        }
	}
	
	private void updateArtifacts(Game game) {
		Container aws0 = turnInfo.getNonHiddenArtifacts();
		Container aws1 = turnInfo.getHiddenArtifacts();
		ArrayList<ArtifactWrapper> aws = new ArrayList<ArtifactWrapper>();
		aws.addAll(aws0.getItems());
		aws.addAll(aws1.getItems());
        for (ArtifactWrapper aw : (ArrayList<ArtifactWrapper>)aws) {
            // for FA game, update artifact numbers
        	try {
            if (game.getMetadata().getGameType() == GameTypeEnum.gameFA ||
            		game.getMetadata().getGameType() == GameTypeEnum.gameKS) {
                String artiNameInAscii = AsciiUtils.convertNonAscii(aw.getName().trim());
                boolean found = false;
                for (ArtifactInfo ai : (ArrayList<ArtifactInfo>)game.getMetadata().getArtifacts().getItems()) {
                    if (AsciiUtils.convertNonAscii(ai.getName()).equalsIgnoreCase(artiNameInAscii)) {
                        found = true;
                        ai.setNo(aw.getId());
                        break;
                    }
                }
                if (!found) {
                    // add artifact
                    ArtifactInfo ai = new ArtifactInfo();
                    ai.setName(aw.getName().trim());
                    ai.setNo(aw.getId());
                    game.getMetadata().getArtifacts().addItem(ai);
                }
            };
            
            // for all arties update powers
            ArtifactInfo ai = (ArtifactInfo)game.getMetadata().getArtifacts().findFirstByProperty("no", aw.getId());
            if (ai != null && aw.getPower() != null && !aw.getPower().equals("")) {
                // parse power
            	// TODO handle open seas, scry etc
                String power = aw.getPower();
                
                if (!power.equals(ai.getPower1())) {
                    power += "*"; // mark power as updated for this game
                    if (ai.getPowers().size() == 0) {
                    	ai.getPowers().add(power);
                    } else {
                    	ai.getPowers().set(0, power);
                    }
                }
            }
            
            // update hidden artifacts
            // TODO - existed in pdf, does not exist in New Xml
//            if (aws1.contains(aw)) {
//                Artifact a = (Artifact)game.getTurn().getContainer(TurnElementsEnum.Artifact).findFirstByProperty("number", aw.getId());
//                if (a == null) {
//                    a = new Artifact();
//                    a.setNumber(aw.getId());
//                    a.setName(aw.getName().trim());
//                    //a.setHexNo(aw.get());
//                    //a.setOwner(turnInfo.getNationName());
//                    a.setInfoSource(infoSource);
//                    game.getTurn().getContainer(TurnElementsEnum.Artifact).addItem(a);
//                }
//            }
        	}
        	catch (Exception exc) {
        		logger.error(exc);
        	}
        }
	}
}