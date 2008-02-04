package org.joverseer.support.readers.newXml;

import java.util.ArrayList;

import org.apache.commons.digester.Digester;
import org.apache.commons.digester.RegexRules;
import org.apache.commons.digester.SetNestedPropertiesRule;
import org.apache.commons.digester.SimpleRegexMatcher;
import org.apache.log4j.Logger;
import org.joverseer.domain.PopulationCenter;
import org.joverseer.game.Game;
import org.joverseer.game.Turn;
import org.joverseer.support.infoSources.InfoSource;
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

	public TurnNewXmlReader(Game game, String filename) {
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
			SetNestedPropertiesRule snpr;
			
			digester = new Digester();
			digester.setValidating(false);
			digester.setRules(new RegexRules(new SimpleRegexMatcher()));
			digester.addObjectCreate("More/TurnInfo", TurnInfo.class);
			// create container for Non Hidden Artifactss
            digester.addObjectCreate("More/TurnInfo/ArtifactInfo/NonHiddenArtifacts", "org.joverseer.support.Container");
            // add container to turn info
            digester.addSetNext("More/TurnInfo/ArtifactInfo/NonHiddenArtifacts", "setArtifacts");
        	// create artifact wrapper
            digester.addObjectCreate("More/TurnInfo/ArtifactInfo/NonHiddenArtifacts/Artifact", "org.joverseer.support.readers.newXml.ArtifactWrapper");
            // set id
            digester.addSetProperties("More/TurnInfo/ArtifactInfo/NonHiddenArtifacts/Artifact", "ID", "id");
            // set nested properties
            digester.addRule("More/TurnInfo/ArtifactInfo/NonHiddenArtifacts/Artifact",
                    snpr = new SetNestedPropertiesRule(new String[]{"Name", "MageSkill", "CommandSkill", "EmmisarySkill", "AgentSkill", "StealthSkill", "CombatSkill", "Alignment", "Latent", "Item"},
                            new String[]{"name", "mage", "command", "emissary", "agent", "stealth", "combat", "alignment", "latent", "item"}));
            snpr.setAllowUnknownChildElements(true);
            // add to container
            digester.addSetNext("More/TurnInfo/ArtifactInfo/NonHiddenArtifacts/Artifact", "addItem", "org.joverseer.support.readers.newXml.ArtifactWrapper");
			// create container for Hidden Artifactss
            digester.addObjectCreate("More/TurnInfo/ArtifactInfo/HiddenArtifacts", "org.joverseer.support.Container");
            // add container to turn info
            digester.addSetNext("More/TurnInfo/ArtifactInfo/HiddenArtifacts", "setArtifacts");
        	// create artifact wrapper
            digester.addObjectCreate("More/TurnInfo/ArtifactInfo/HiddenArtifacts/Artifact", "org.joverseer.support.readers.newXml.ArtifactWrapper");
            // set id
            digester.addSetProperties("More/TurnInfo/ArtifactInfo/HiddenArtifacts/Artifact", "ID", "id");
            // set nested properties
            digester.addRule("More/TurnInfo/ArtifactInfo/HiddenArtifacts/Artifact",
                    snpr = new SetNestedPropertiesRule(new String[]{"Name", "MageSkill", "CommandSkill", "EmmisarySkill", "AgentSkill", "StealthSkill", "CombatSkill", "Alignment", "Latent", "Item"},
                            new String[]{"name", "mage", "command", "emissary", "agent", "stealth", "combat", "alignment", "latent", "item"}));
            snpr.setAllowUnknownChildElements(true);
            // add to container
            digester.addSetNext("More/TurnInfo/ArtifactInfo/HiddenArtifacts/Artifact", "addItem", "org.joverseer.support.readers.newXml.ArtifactWrapper");
            
            // create container for Pop Centers
            digester.addObjectCreate("More/TurnInfo/PopCentres", "org.joverseer.support.Container");
            // add container to turn info
            digester.addSetNext("More/TurnInfo/PopCentres", "setPopCentres");
            // create pop center wrapper
            digester.addObjectCreate("More/TurnInfo/PopCentres/PopCentre", "org.joverseer.support.readers.newXml.PopCenterWrapper");
            // set hex no
            digester.addSetProperties("More/TurnInfo/PopCentres/PopCentre", "HexID", "hexNo");
            // set nested properties
            digester.addRule("More/TurnInfo/PopCentres/PopCentre",
                    snpr = new SetNestedPropertiesRule(new String[]{"Sieged", "Terrain", "Climate"},
                            new String[]{"sieged", "terrain", "climate"}));
            snpr.setAllowUnknownChildElements(true);
            // add to container
            digester.addSetNext("More/TurnInfo/PopCentres/PopCentre", "addItem", "org.joverseer.support.readers.newXml.PopCenterWrapper");
            // create production wrapper
            digester.addObjectCreate("More/TurnInfo/PopCentres/PopCentre/Product", "org.joverseer.support.readers.newXml.ProductionWrapper");
            // set type
            digester.addSetProperties("More/TurnInfo/PopCentres/PopCentre/Product", "type", "type");
            // set nested properties
            digester.addRule("More/TurnInfo/PopCentres/PopCentre/Product",
                    snpr = new SetNestedPropertiesRule(new String[]{"CurrentStores", "ExpProduction"},
                            new String[]{"currentStores", "expProduction"}));
            snpr.setAllowUnknownChildElements(true);
            // add to container
            digester.addSetNext("More/TurnInfo/PopCentres/PopCentre/Product", "addProduct", "org.joverseer.support.readers.newXml.ProductWrapper");
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
			// updateGame(game);
			game.setCurrentTurn(game.getMaxTurn());
			Thread.sleep(100);
		} catch (Exception exc) {
			errorOccured = true;
		}
	}
}
