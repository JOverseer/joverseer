package org.joverseer.support.readers.pdf;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import org.apache.commons.digester.Digester;
import org.apache.commons.digester.RegexRules;
import org.apache.commons.digester.SetNestedPropertiesRule;
import org.apache.commons.digester.SimpleRegexMatcher;
import org.apache.log4j.Logger;
import org.joverseer.domain.Army;
import org.joverseer.domain.Challenge;
import org.joverseer.domain.Character;
import org.joverseer.domain.CharacterDeathReasonEnum;
import org.joverseer.domain.ClimateEnum;
import org.joverseer.domain.Combat;
import org.joverseer.domain.Company;
import org.joverseer.domain.Encounter;
import org.joverseer.domain.HexInfo;
import org.joverseer.domain.NationRelations;
import org.joverseer.domain.NationRelationsEnum;
import org.joverseer.domain.PdfTurnText;
import org.joverseer.domain.PopulationCenter;
import org.joverseer.domain.SeasonEnum;
import org.joverseer.game.Game;
import org.joverseer.game.Turn;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.metadata.domain.Nation;
import org.joverseer.metadata.domain.NationAllegianceEnum;
import org.joverseer.support.Container;
import org.joverseer.support.infoSources.DoubleAgentInfoSource;
import org.joverseer.support.infoSources.InfoSource;
import org.joverseer.support.infoSources.PdfTurnInfoSource;
import org.pdfbox.pdmodel.PDDocument;
import org.pdfbox.util.PDFTextStripper;
import org.springframework.core.io.Resource;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.progress.ProgressMonitor;
import org.txt2xml.config.ProcessorFactory;
import org.txt2xml.core.Processor;
import org.txt2xml.driver.StreamDriver;


public class TurnPdfReader implements Runnable {
    public static final String DEFAULT_ENCODING = "UTF-8";
    static Logger logger = Logger.getLogger(TurnPdfReader.class);
    TurnInfo turnInfo;
    Turn turn;
    InfoSource infoSource;
    ProgressMonitor monitor;
    Game game;
    String filename;
    int nationNo;
    File pdfTextFile;
    File xmlFile;
    String contents;
    PDDocument document;
    boolean deleteFilesWhenFinished = true;
    boolean errorOccurred = false;
    
    public TurnPdfReader(Game game, String filename) {
        this.game = game;
        this.filename = filename;
    }
    
    public String parsePdf() throws Throwable {
        String encoding = DEFAULT_ENCODING;
        int startPage = 1;
        int endPage = Integer.MAX_VALUE;
        String ret = null;
        Writer output = null;
        ByteArrayOutputStream outs = null;
        try {
            document = PDDocument.load(filename);


            if (encoding != null) {
                output = new OutputStreamWriter(outs = new ByteArrayOutputStream(), encoding);
            }

            PDFTextStripper stripper = null;
            stripper = new PDFTextStripper();
            stripper.setStartPage(startPage);
            stripper.setEndPage(endPage);
            stripper.writeText(document, output);
            ret = new String(outs.toByteArray(), "UTF-8");
            contents = ret;
            
            //tempPdfTextFile = new File(pdfFile + ".txt");
            FileWriter out = new FileWriter(pdfTextFile.getCanonicalPath());
            out.write(ret);
            out.close();
        }
        catch (Throwable exc) {
                logger.error(exc);
                exc.printStackTrace();
        	throw exc;
        }
        finally {
            if (output != null) {
                output.close();
            }
            if (document != null) {
                document.close();
            }
        }
        return ret;
    }
    
    private void cleanup() {
        if (!deleteFilesWhenFinished) return;
        pdfTextFile.delete();
        xmlFile.delete();
    }

    public void pdf2xml() throws Throwable {
        try {
            String pdfContents = parsePdf();
            Resource r = Application.instance().getApplicationContext().getResource("classpath:ctx/txt2xml.config.xml");
            //Processor processor = ProcessorFactory.getInstance().createProcessor(new FileReader("bin/ctx/txt2xml.config.xml"));
            Processor processor = ProcessorFactory.getInstance().createProcessor(new InputStreamReader(r.getInputStream()));
            FileOutputStream outStream = new FileOutputStream(xmlFile.getAbsolutePath());
            StreamDriver driver = new StreamDriver(processor);
            driver.useDebugOutputProperties();
            
            // pdf document - fix hack characters
            pdfContents = pdfContents.replace("Î","ë").replace("˙", "ú");
            
            driver.generateXmlDocument(pdfContents, outStream);
            outStream.close();

        }
        catch (Throwable exc) {
            // TODO
            logger.error(exc.getCause());
            exc.printStackTrace();
            throw exc;
        }

    }
   
    
    public void readFile() throws Exception {
    	try {
    	    SetNestedPropertiesRule snpr;
            Digester digester = new Digester();
            digester.setValidating(false);
            digester.setRules(new RegexRules(new SimpleRegexMatcher()));
            // parse turn info
            digester.addObjectCreate("txt2xml/Turn", TurnInfo.class);
            digester.addRule("txt2xml/Turn/General",
                    snpr = new SetNestedPropertiesRule(new String[]{"Allegiance", "Nation", "TurnNumber", "Season", "Date"},
                            new String[]{"allegiance", "nationNo", "turnNo", "season", "date"}));
            snpr.setAllowUnknownChildElements(true);
            // create container for nation relations
            digester.addObjectCreate("txt2xml/Turn/General/NationRelations", "org.joverseer.support.Container");
            // add container to turn info
            digester.addSetNext("txt2xml/Turn/General/NationRelations", "setNationRelations");
        	// create nation relation wrapper
            digester.addObjectCreate("txt2xml/Turn/General/NationRelations/NationRelation", "org.joverseer.support.readers.pdf.NationRelationWrapper");
        	// set nested properties
            digester.addRule("txt2xml/Turn/General/NationRelations/NationRelation",
                    snpr = new SetNestedPropertiesRule(new String[]{"Nation", "Relation"},
                            new String[]{"nation", "relation"}));
            // add to container
            digester.addSetNext("txt2xml/Turn/General/NationRelations/NationRelation", "addItem", "org.joverseer.support.readers.pdf.NationRelationWrapper");
            // create container for pcs
            digester.addObjectCreate("txt2xml/Turn/PopulationCentres", "org.joverseer.support.Container");
            // add container to turn info
            digester.addSetNext("txt2xml/Turn/PopulationCentres", "setPopulationCenters");
            // create pop center wrapper
            digester.addObjectCreate("txt2xml/Turn/PopulationCentres/PopCentre", "org.joverseer.support.readers.pdf.PopCenterWrapper");
            // set nested properties
            digester.addRule("txt2xml/Turn/PopulationCentres/PopCentre",
                    snpr = new SetNestedPropertiesRule(new String[]{"Name", "Hex", "Climate"},
                            new String[]{"name", "hexNo", "climate"}));
            snpr.setAllowUnknownChildElements(true);
            // add pop center wrapper
            digester.addSetNext("txt2xml/Turn/PopulationCentres/PopCentre", "addItem", "org.joverseer.support.readers.pdf.PopCenterWrapper");
            // create production wrapper object
            digester.addObjectCreate("txt2xml/Turn/PopulationCentres/PopCentre/Production", "org.joverseer.support.readers.pdf.ProductAmountWrapper");
            // add to pop center
            digester.addSetNext("txt2xml/Turn/PopulationCentres/PopCentre/Production", "setProduction");
            // parse production nested properties
            digester.addRule("txt2xml/Turn/PopulationCentres/PopCentre/Production",
                    snpr = new SetNestedPropertiesRule(new String[]{"Leather", "Bronze", "Steel", "Mithril", "Food", "Timber", "Mounts", "Gold"},
                            new String[]{"leather", "bronze", "steel", "mithril", "food", "timber", "mounts", "gold"}));
            // create stores wrapper object
            digester.addObjectCreate("txt2xml/Turn/PopulationCentres/PopCentre/Stores", "org.joverseer.support.readers.pdf.ProductAmountWrapper");
            // add to pop center
            digester.addSetNext("txt2xml/Turn/PopulationCentres/PopCentre/Stores", "setStores");
            // parse stores nested properties
            digester.addRule("txt2xml/Turn/PopulationCentres/PopCentre/Stores",
                    snpr = new SetNestedPropertiesRule(new String[]{"Leather", "Bronze", "Steel", "Mithril", "Food", "Timber", "Mounts", "Gold"},
                            new String[]{"leather", "bronze", "steel", "mithril", "food", "timber", "mounts", "gold"}));
            // create container for chars
            digester.addObjectCreate("txt2xml/Turn/Orders", "org.joverseer.support.Container");
            // add container to turn info
            digester.addSetNext("txt2xml/Turn/Orders", "setCharacters");
        	// create character wrapper
            digester.addObjectCreate("txt2xml/Turn/Orders/Character", "org.joverseer.support.readers.pdf.CharacterWrapper");
        	// add character wrapper
            digester.addSetNext("txt2xml/Turn/Orders/Character", "addItem", "org.joverseer.support.readers.pdf.CharacterWrapper");
            // set nested properties
            digester.addRule("txt2xml/Turn/Orders/Character",
                    snpr = new SetNestedPropertiesRule(new String[]{"Name", "HexID", "CharacterOrders"},
                            new String[]{"name", "hexNo", "orders"}));
            snpr.setAllowUnknownChildElements(true);
            // create LA result object
            digester.addObjectCreate("txt2xml/Turn/Orders/Character/LocateArtifact", "org.joverseer.support.readers.pdf.LocateArtifactResultWrapper");
            // add to character
            digester.addSetNext("txt2xml/Turn/Orders/Character/LocateArtifact", "addOrderResult", "org.joverseer.support.readers.pdf.LocateArtifactResultWrapper");
            // parse properties
            digester.addRule("txt2xml/Turn/Orders/Character/LocateArtifact",
                    snpr = new SetNestedPropertiesRule(new String[]{"ArtifactName", "ArtifactId", "Hex", "Owner"},
                            new String[]{"artifactName", "artifactNo", "hexNo", "owner"}));
            // create LAT result object
            digester.addObjectCreate("txt2xml/Turn/Orders/Character/LocateArtifactTrue", "org.joverseer.support.readers.pdf.LocateArtifactTrueResultWrapper");
            // add to character
            digester.addSetNext("txt2xml/Turn/Orders/Character/LocateArtifactTrue", "addOrderResult", "org.joverseer.support.readers.pdf.LocateArtifactTrueResultWrapper");
            // parse properties
            digester.addRule("txt2xml/Turn/Orders/Character/LocateArtifactTrue",
                    snpr = new SetNestedPropertiesRule(new String[]{"ArtifactName", "ArtifactId", "Hex", "Owner"},
                            new String[]{"artifactName", "artifactNo", "hexNo", "owner"}));
            // create RC result object
            digester.addObjectCreate("txt2xml/Turn/Orders/Character/RevealCharacter", "org.joverseer.support.readers.pdf.RevealCharacterResultWrapper");
            // add to character
            digester.addSetNext("txt2xml/Turn/Orders/Character/RevealCharacter", "addOrderResult", "org.joverseer.support.readers.pdf.RevealCharacterResultWrapper");
            // parse properties
            digester.addRule("txt2xml/Turn/Orders/Character/RevealCharacter",
                    snpr = new SetNestedPropertiesRule(new String[]{"Character", "Hex"},
                            new String[]{"characterName", "hexNo"}));
            // handle assassinated, cursed, executed chars
            digester.addCallMethod("txt2xml/Turn/Orders/Character/Assassinated", "setAssassinatedOn");
            digester.addCallMethod("txt2xml/Turn/Orders/Character/Cursed", "setCursedOn");
            digester.addCallMethod("txt2xml/Turn/Orders/Character/Executed", "setExecutedOn");
            
            // create container for companies
            digester.addObjectCreate("txt2xml/Turn/Companies", "org.joverseer.support.Container");
            // add container to turn info
            digester.addSetNext("txt2xml/Turn/Companies", "setCompanies");
            // create Company wrapper
            digester.addObjectCreate("txt2xml/Turn/Companies/Company", "org.joverseer.support.readers.pdf.CompanyWrapper");
            // add company wrapper
            digester.addSetNext("txt2xml/Turn/Companies/Company", "addItem", "org.joverseer.support.readers.pdf.CompanyWrapper");
            // parse properties
            digester.addRule("txt2xml/Turn/Companies/Company",
                    snpr = new SetNestedPropertiesRule(new String[]{"Commander", "Hex", "Members"},
                            new String[]{"commanderName", "hexNo", "members"}));
            snpr.setAllowUnknownChildElements(true);
            // create container for combats
            digester.addObjectCreate("txt2xml/Turn/Combats", "org.joverseer.support.Container");
            // add container to turn info
            digester.addSetNext("txt2xml/Turn/Combats", "setCombats");
            // create Combat wrapper
            digester.addObjectCreate("txt2xml/Turn/Combats/Combat", "org.joverseer.support.readers.pdf.CombatWrapper");
            // add combat wrapper
            digester.addSetNext("txt2xml/Turn/Combats/Combat", "addItem", "org.joverseer.support.readers.pdf.CombatWrapper");
            // parse properties
            digester.addRule("txt2xml/Turn/Combats/Combat",
                    snpr = new SetNestedPropertiesRule(new String[]{"HexNo", "Narration"},
                            new String[]{"hexNo", "narration"}));
            snpr.setAllowUnknownChildElements(true);
            // create army container
            digester.addObjectCreate("txt2xml/Turn/Combats/Combat/Armies", "org.joverseer.support.Container");
            // add container to combat
            digester.addSetNext("txt2xml/Turn/Combats/Combat/Armies", "setArmies");
            // create CombatArmy
            digester.addObjectCreate("txt2xml/Turn/Combats/Combat/Armies/Army", "org.joverseer.support.readers.pdf.CombatArmy");
            // add to container
            digester.addSetNext("txt2xml/Turn/Combats/Combat/Armies/Army", "addItem", "org.joverseer.support.readers.pdf.CombatArmy");
            // create regiment container
            digester.addObjectCreate("txt2xml/Turn/Combats/Combat/Armies/Army/Regiments", "org.joverseer.support.Container");
            // add container to army
            digester.addSetNext("txt2xml/Turn/Combats/Combat/Armies/Army/Regiments", "setRegiments");
            // create CombatArmyRegiment
            digester.addObjectCreate("txt2xml/Turn/Combats/Combat/Armies/Army/Regiments/Regiment", "org.joverseer.support.readers.pdf.CombatArmyRegiment");
            // add to container
            digester.addSetNext("txt2xml/Turn/Combats/Combat/Armies/Army/Regiments/Regiment", "addItem", "org.joverseer.support.readers.pdf.CombatArmyRegiment");
            // parse properties
            digester.addRule("txt2xml/Turn/Combats/Combat/Armies/Army/Regiments/Regiment",
                    snpr = new SetNestedPropertiesRule(new String[]{"Description"},
                            new String[]{"description"}));
            snpr.setAllowUnknownChildElements(true);
            // parse Army properties
            digester.addRule("txt2xml/Turn/Combats/Armies/Army",
                    snpr = new SetNestedPropertiesRule(new String[]{"Commander"},
                            new String[]{"commanderName"}));
            snpr.setAllowUnknownChildElements(true);
            
            // create challenge wrapper
            digester.addObjectCreate("txt2xml/Turn/Combats/Challenge", "org.joverseer.support.readers.pdf.ChallengeWrapper");
            // add challenge wrapper
            digester.addSetNext("txt2xml/Turn/Combats/Challenge", "addItem", "org.joverseer.support.readers.pdf.ChallengeWrapper");
            // parse properties
            digester.addRule("txt2xml/Turn/Combats/Challenge",
                    snpr = new SetNestedPropertiesRule(new String[]{"HexNo", "Text", "Character"},
                            new String[]{"hexNo", "narration", "character"}));
            snpr.setAllowUnknownChildElements(true);
            // create container for armies
            digester.addObjectCreate("txt2xml/Turn/Armies", "org.joverseer.support.Container");
            // add container to turn info
            digester.addSetNext("txt2xml/Turn/Armies", "setArmies");
            // create army wrapper
            digester.addObjectCreate("txt2xml/Turn/Armies/Army", "org.joverseer.support.readers.pdf.ArmyWrapper");
            // add army wrapper
            digester.addSetNext("txt2xml/Turn/Armies/Army", "addItem", "org.joverseer.support.readers.pdf.ArmyWrapper");
            // parse properties
            digester.addRule("txt2xml/Turn/Armies/Army",
                    snpr = new SetNestedPropertiesRule(new String[]{"Commander", "Type", "Food", "Warships", "Transports", "WarMachines", "Climate", "Hex"},
                            new String[]{"commander", "type", "food", "warships", "transports", "warMachines", "climate", "hexNo"}));
            snpr.setAllowUnknownChildElements(true);
            // create container for army elements
            digester.addObjectCreate("txt2xml/Turn/Armies/Army/Elements", "org.joverseer.support.Container");
            // add container to army
            digester.addSetNext("txt2xml/Turn/Armies/Army/Elements", "setArmyElements");
            // create element wrapper
            digester.addObjectCreate("txt2xml/Turn/Armies/Army/Elements/Element", "org.joverseer.support.readers.pdf.ArmyElementWrapper");
            // add element to container
            digester.addSetNext("txt2xml/Turn/Armies/Army/Elements/Element", "addItem", "org.joverseer.support.readers.pdf.ArmyElementWrapper");
            // parse properties
            digester.addRule("txt2xml/Turn/Armies/Army/Elements/Element",
                    snpr = new SetNestedPropertiesRule(new String[]{"Type", "Number", "Weapons", "Armor", "Training"},
                            new String[]{"type", "number", "weapons", "armor", "training"}));
            snpr.setAllowUnknownChildElements(true);
            
            // create container for encounters
            digester.addObjectCreate("txt2xml/Turn/Encounters", "org.joverseer.support.Container");
            // add container to turn info
            digester.addSetNext("txt2xml/Turn/Encounters", "setEncounters");
            // create encounter wrapper
            digester.addObjectCreate("txt2xml/Turn/Encounters/Encounter", "org.joverseer.support.readers.pdf.EncounterWrapper");
            // add army wrapper
            digester.addSetNext("txt2xml/Turn/Encounters/Encounter", "addItem", "org.joverseer.support.readers.pdf.EncounterWrapper");
            // parse properties
            digester.addRule("txt2xml/Turn/Encounters/Encounter",
                    snpr = new SetNestedPropertiesRule(new String[]{"Character", "Hex", "Text"},
                            new String[]{"character", "hexNo", "description"}));
            snpr.setAllowUnknownChildElements(true);
//          create container for double agents
            digester.addObjectCreate("txt2xml/Turn/DoubleAgents", "org.joverseer.support.Container");
            // add container to turn info
            digester.addSetNext("txt2xml/Turn/DoubleAgents", "setDoubleAgents");
            // create DoubleAgent wrapper
            digester.addObjectCreate("txt2xml/Turn/DoubleAgents/DoubleAgent", "org.joverseer.support.readers.pdf.DoubleAgentWrapper");
            // add army wrapper
            digester.addSetNext("txt2xml/Turn/DoubleAgents/DoubleAgent", "addItem", "org.joverseer.support.readers.pdf.DoubleAgentWrapper");
            // parse properties
            digester.addRule("txt2xml/Turn/DoubleAgents/DoubleAgent",
                    snpr = new SetNestedPropertiesRule(new String[]{"Character", "Hex"},
                            new String[]{"name", "hexNo"}));
            snpr.setAllowUnknownChildElements(true);
            turnInfo = (TurnInfo)digester.parse("file:///" + xmlFile.getCanonicalPath());
//            Pattern p = Pattern.compile(".*g\\d{3}n(\\d{2})t(\\d{3}).*");
//            Matcher m = p.matcher(xmlFile.getCanonicalPath());
//            m.matches();
//            nationNo = Integer.parseInt(m.group(1));
//            int turnNo = Integer.parseInt(m.group(2));
//            turnInfo.setNationNo(nationNo);
//            turnInfo.setTurnNo(turnNo);
            cleanup();
    	}
    	catch (Exception exc) {
			//todo fix
			throw new Exception("Error parsing Pdf Turn file.", exc);
    	}
    }
    
    public void run() {
        try {
            File f = new File(filename);
            pdfTextFile = File.createTempFile(f.getName(), ".pdf.txt");
            xmlFile = File.createTempFile(f.getName(), ".pdf.txt.xml");
            pdf2xml();
            if (getMonitor() != null) {
                getMonitor().worked(50);
                getMonitor().subTaskStarted("Parsing Pdf file...");
            }
            readFile();
            updateGame(game);
            game.setCurrentTurn(game.getMaxTurn());
            Thread.sleep(100);
        }
        catch (Throwable exc) {
            if (getMonitor() != null) {
                getMonitor().subTaskStarted(exc.getMessage());
            }
            logger.error(exc);
            exc.printStackTrace();
            // do nothing
        }
    }
    
    public void updateGame(Game game) throws Exception {
        if (turnInfo.getTurnNo() != game.getMaxTurn()) {
            //todo fix
            throw new Exception("Can only import pdfs for last turn.");
        }
        try {
            turn = game.getTurn(game.getMaxTurn());
            
            try {
                updateTurnData();
            }
            catch (Exception exc) {
            	errorOccurred = true;
                getMonitor().subTaskStarted("Error updating turn data : '" + exc.getMessage() + "'.");
            }
            
            infoSource = new PdfTurnInfoSource(turnInfo.getTurnNo(), turnInfo.getNationNo());
            
            // add text
            PdfTurnText ptt = (PdfTurnText)turn.getContainer(TurnElementsEnum.PdfText).findFirstByProperty("nationNo", turnInfo.getNationNo());
            if (ptt != null) {
                turn.getContainer(TurnElementsEnum.PdfText).removeItem(ptt);
            }
            ptt = new PdfTurnText();
            ptt.setNationNo(turnInfo.getNationNo());
            ptt.setText(contents);
            turn.getContainer(TurnElementsEnum.PdfText).addItem(ptt);
            
            if (getMonitor() != null) {
                getMonitor().worked(50);
                getMonitor().subTaskStarted("Updating nation relations...");
            }
            try {
                updateNationRelations(game);
            }
            catch (Exception exc) {
            	errorOccurred = true;
                getMonitor().subTaskStarted("Error: " + exc.getMessage());
            }
            if (getMonitor() != null) {
                getMonitor().worked(60);
                getMonitor().subTaskStarted("Updating population centers...");
            }
            try {
                updatePcs(game);
            }
            catch (Exception exc) {
            	errorOccurred = true;
                getMonitor().subTaskStarted("Error: " + exc.getMessage());
            }
            if (getMonitor() != null) {
                getMonitor().worked(70);
                getMonitor().subTaskStarted("Updating characters...");
            }
            try {
                updateCharacters(game);
            }
            catch (Exception exc) {
            	errorOccurred = true;
                getMonitor().subTaskStarted("Error: " + exc.getMessage());
            }
            try {
                updateDoubleAgents(game);
            }
            catch (Exception exc) {
            	errorOccurred = true;
                getMonitor().subTaskStarted("Error: " + exc.getMessage());
            }
            if (getMonitor() != null) {
                getMonitor().worked(80);
                getMonitor().subTaskStarted("Updating armies...");
            }
            try {
                updateArmies(game);
            }
            catch (Exception exc) {
            	errorOccurred = true;
                getMonitor().subTaskStarted("Error: " + exc.getMessage());
            }
            if (getMonitor() != null) {
                getMonitor().worked(90);
                getMonitor().subTaskStarted("Updating companies...");
            }
            try {
                updateCompanies(game);
            }
            catch (Exception exc) {
            	errorOccurred = true;
                getMonitor().subTaskStarted("Error: " + exc.getMessage());
            }
            if (getMonitor() != null) {
                getMonitor().worked(100);
                getMonitor().subTaskStarted("Updating combats, encounters, challenges...");
            }
            try {
                updateCombats(game);
            }
            catch (Exception exc) {
            	errorOccurred = true;
                getMonitor().subTaskStarted("Error: " + exc.getMessage());
            }
            try {
                updateEncounters(game);
            }
            catch (Exception exc) {
            	errorOccurred = true;
                getMonitor().subTaskStarted("Error: " + exc.getMessage());
            }
            try {
                updateClimates(game);
            }
            catch (Exception exc) {
            	errorOccurred = true;
                getMonitor().subTaskStarted("Error: " + exc.getMessage());
            }
        }
        catch (Exception exc) {
            if (getMonitor() != null) {
                getMonitor().worked(100);
                getMonitor().subTaskStarted("Error : '" + exc.getMessage() + "'.");
            }
        	errorOccurred = true;
            throw new Exception("Error updating game from Pdf file.", exc);
        }
    }
    
    private void updateTurnData() throws Exception {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("m/d/yyyy");
            turn.setTurnDate(sdf.parse(turnInfo.getDate()));
            if (turnInfo.getSeason().equals("Spring")) {
                turn.setSeason(SeasonEnum.Spring);
            } else if (turnInfo.getSeason().equals("Summer")) {
                turn.setSeason(SeasonEnum.Summer);
            } else if (turnInfo.getSeason().equals("Fall")) {
                turn.setSeason(SeasonEnum.Fall);
            } else if (turnInfo.getSeason().equals("Winter")) {
                turn.setSeason(SeasonEnum.Winter);
            }
        }
        catch (Exception exc) {
            throw exc;
        }
    }
    
    private void updateEncounters(Game game) {
        Container ews = turnInfo.getEncounters();
        if (ews == null) return;
        Container encounters = game.getTurn().getContainer(TurnElementsEnum.Encounter);
        for (EncounterWrapper ew : (ArrayList<EncounterWrapper>)ews.getItems()) {
            Encounter e = (Encounter)encounters.findFirstByProperties(new String[]{"character", "hexNo"}, new Object[]{ew.getCharacter(), ew.getHexNo()});
            if (e != null) {
                encounters.removeItem(e);
            }
            encounters.addItem(ew.getEncounter());
        }
    }
    
      private void updateArmies(Game game) {
        Container armies = game.getTurn().getContainer(TurnElementsEnum.Army);
        Container aws = turnInfo.getArmies();
        if (aws == null) return;
        for (ArmyWrapper aw : (ArrayList<ArmyWrapper>)aws.getItems()) {
            Army a = (Army)armies.findFirstByProperty("commanderName", aw.getCommander());
            if (a != null) {
                aw.updateArmy(a);
            }
        }
    }
    
    public void updateCombats(Game game) {
        Container combats = game.getTurn().getContainer(TurnElementsEnum.Combat);
        Container challenges = game.getTurn().getContainer(TurnElementsEnum.Challenge);
        Container cws = turnInfo.getCombats();
        if (cws == null) return;
        for (CombatWrapper cw : (ArrayList<CombatWrapper>)cws.getItems()) {
            if (ChallengeWrapper.class.isInstance(cw)) {
                Challenge c = (Challenge)challenges.findFirstByProperties(new String[]{"character", "hexNo"}, new Object[]{((ChallengeWrapper)cw).getCharacter(), cw.getHexNo()});
                if (c != null) {
                    challenges.removeItem(c);
                }
                challenges.addItem(((ChallengeWrapper)cw).getChallenge());
            } else {
                cw.parse();
                Combat c = (Combat)combats.findFirstByProperty("hexNo", cw.getHexNo());
                if (c == null) {
                    c = new Combat();
                    c.setHexNo(cw.getHexNo());
                    c.addNarration(nationNo, cw.getNarration());
                    combats.addItem(c);
                } else {
                    c.addNarration(nationNo, cw.getNarration());
                }
            }
        }
    }

    private void updateNationRelations(Game game) throws Exception {
        Nation nation = game.getMetadata().getNationByNum(turnInfo.getNationNo());
        if (nation == null) {
        	throw new Exception("Failed to find nation with number " + turnInfo.getNationNo());
        }
        Container nrs = turn.getContainer(TurnElementsEnum.NationRelation);
        NationRelations nr = (NationRelations)nrs.findFirstByProperty("nationNo", turnInfo.getNationNo());
        if (turnInfo.getAllegiance().equals("Free People")) {
            nation.setAllegiance(NationAllegianceEnum.FreePeople);
        } else if (turnInfo.getAllegiance().equals("Dark Servants")) {
            nation.setAllegiance(NationAllegianceEnum.DarkServants);
        } else if (turnInfo.getAllegiance().equals("Neutral")) {
            nation.setAllegiance(NationAllegianceEnum.Neutral);
        } 
        if (nr == null) {
        	throw new Exception("Failed to retrieve NationRelations object for nation " + turnInfo.getNationNo());
        }
        nr.setAllegiance(nation.getAllegiance());
        
        Container nrws = turnInfo.getNationRelations();
        String problematicNations = "";
        for (NationRelationWrapper nrw : (ArrayList<NationRelationWrapper>)nrws.getItems()) {
        	Nation n = game.getMetadata().getNationByName(nrw.getNation());
        	if (n == null) {
        		problematicNations += (problematicNations.equals("") ? "" : ", ") + nrw.getNation();
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
    
    public void updatePcs(Game game) throws Exception {
        Container pcws = turnInfo.getPopulationCenters();
        Container pcs = turn.getContainer(TurnElementsEnum.PopulationCenter);
        String pcsNotFound = "";
        for (PopCenterWrapper pcw : (ArrayList<PopCenterWrapper>)pcws.getItems()) {
            PopulationCenter pc = (PopulationCenter)pcs.findFirstByProperty("name", pcw.getName());
            if (pc == null) {
                pcsNotFound += (pcsNotFound.equals("") ? "" : ",") + pcw.getName();
            } else {
                pcw.updatePopCenter(pc);
            }
        }
        if (!pcsNotFound.equals("")) {
            throw new Exception("Population centers " + pcsNotFound + " not found in turn.");
        }
    }
    
    private ClimateEnum translateClimate(String climate) {
        if (climate == null) return null;
        if (climate.equals("Polar")) return ClimateEnum.Polar;
        if (climate.equals("Severe")) return ClimateEnum.Severe;
        if (climate.equals("Cold")) return ClimateEnum.Cold;
        if (climate.equals("Cool")) return ClimateEnum.Cool;
        if (climate.equals("Mild")) return ClimateEnum.Mild;
        if (climate.equals("Warm")) return ClimateEnum.Warm;
        if (climate.equals("Hot")) return ClimateEnum.Hot;
        return null;
    }
    
    public void updateClimates(Game game) throws Exception {
        Container pcws = turnInfo.getPopulationCenters();
        Container his = turn.getContainer(TurnElementsEnum.HexInfo);
        for (PopCenterWrapper pcw : (ArrayList<PopCenterWrapper>)pcws.getItems()) {
            HexInfo hi = (HexInfo)his.findFirstByProperty("hexNo", pcw.getHexNo());
            ClimateEnum climate = translateClimate(pcw.getClimate());
            if (climate != null) {
                hi.setClimate(climate);
            }
        }
        
        Container aws = turnInfo.getArmies();
        for (ArmyWrapper aw : (ArrayList<ArmyWrapper>)aws.getItems()) {
            HexInfo hi = (HexInfo)his.findFirstByProperty("hexNo", aw.getHexNo());
            ClimateEnum climate = translateClimate(aw.getClimate());
            if (climate != null) {
                hi.setClimate(climate);
            }
        }
    }
    
    public void updateCharacters(Game game) throws Exception {
        Container cws = turnInfo.getCharacters();
        Container cs = turn.getContainer(TurnElementsEnum.Character);
        for (CharacterWrapper cw : (ArrayList<CharacterWrapper>)cws.getItems()) {
            Character c = (Character)cs.findFirstByProperty("name", cw.getName());
            if (c == null) {
                CharacterDeathReasonEnum deathReason = null;
                
                if (cw.getAssassinated()) {
                    deathReason = CharacterDeathReasonEnum.Assassinated; 
                } else if (cw.getCursed()) {
                    deathReason = CharacterDeathReasonEnum.Cursed; 
                    
                } else if (cw.getExecuted()) {
                    deathReason = CharacterDeathReasonEnum.Executed; 
                }
                
                if (deathReason == null) {
                    // check last turn
                    // if charname existed, we can add him as dead
                    Turn t = game.getTurn(turnInfo.getTurnNo() - 1);
                    if (t != null) {
                        c = (Character)t.getContainer(TurnElementsEnum.Character).findFirstByProperty("name", cw.getName());
                        if (c != null) {
                            deathReason = CharacterDeathReasonEnum.Dead;
                        }
                    }
                }
                
                if (deathReason != null) {
                    // assassinated
                    // add char
                    c = new Character();
                    c.setName(cw.getName());
                    c.setId(Character.getIdFromName(cw.getName()));
                    c.setNationNo(turnInfo.getNationNo());
                    c.setHealth(0);
                    c.setDeathReason(deathReason);
                    c.setHexNo(cw.getHexNo());
                    c.setInfoSource(infoSource);
                    cs.addItem(c);
                }
            };
            if (c != null) {
                cw.updateCharacter(c);
            }
            for (OrderResult orderResult : cw.getOrderResults()) {
                orderResult.updateGame(turn, nationNo, cw.getName());
            }
        }
    }
    
    public void updateDoubleAgents(Game game) throws Exception {
        DoubleAgentInfoSource dais = new DoubleAgentInfoSource(turnInfo.getTurnNo(), turnInfo.getNationNo());
        Container daws = turnInfo.getDoubleAgents();
        Container cs = turn.getContainer(TurnElementsEnum.Character);
        for (DoubleAgentWrapper daw : (ArrayList<DoubleAgentWrapper>)daws.getItems()) {
            Character c = (Character)cs.findFirstByProperty("name", daw.getName());
            if (c == null) {
                // add character
                c = daw.getCharacter();
                c.setInfoSource(dais);
                cs.addItem(c);
            }
        }
                
    }
    
    public void updateCompanies(Game game) throws Exception {
        Container cws = turnInfo.getCompanies();
        if (cws == null) return;
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
    
    public static void main(String[] args) throws Throwable {
        TurnPdfReader r = new TurnPdfReader(null, args[0]);
        r.parsePdf();
    }

    
    public ProgressMonitor getMonitor() {
        return monitor;
    }

    
    public void setMonitor(ProgressMonitor monitor) {
        this.monitor = monitor;
    }

	public boolean getErrorOccurred() {
		return errorOccurred;
	}

	public void setErrorOccurred(boolean errorOccurred) {
		this.errorOccurred = errorOccurred;
	}
    
    
}


