package org.joverseer.support.readers.newXml;

import java.util.ArrayList;

import org.apache.commons.digester.Digester;
import org.apache.commons.digester.RegexRules;
import org.apache.commons.digester.SetNestedPropertiesRule;
import org.apache.commons.digester.SimpleRegexMatcher;
import org.apache.log4j.Logger;
import org.joverseer.domain.Artifact;
import org.joverseer.domain.PopulationCenter;
import org.joverseer.game.Game;
import org.joverseer.game.Turn;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.metadata.GameTypeEnum;
import org.joverseer.metadata.domain.ArtifactInfo;
import org.joverseer.support.AsciiUtils;
import org.joverseer.support.Container;
import org.joverseer.support.infoSources.InfoSource;
import org.joverseer.support.infoSources.XmlExtraTurnInfoSource;
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
                    snpr = new SetNestedPropertiesRule(new String[]{"Season"},
                            new String[]{"season"}));
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
            if (getMonitor() != null) {
                getMonitor().subTaskStarted(String.format("Parsing file %s for additional info...", new Object[]{fileName}));
                getMonitor().worked(5);
            }
            turnInfo = (TurnInfo) digester.parse(fileName);
		}

		catch (Exception exc) {
			// todo fix
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
        }
        catch (Exception exc) {
        	
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
            if (game.getMetadata().getGameType() == GameTypeEnum.gameFA) {
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
                    ai.getPowers().set(0, power);
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
	}
}
