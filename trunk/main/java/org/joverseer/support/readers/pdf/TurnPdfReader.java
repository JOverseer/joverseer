package org.joverseer.support.readers.pdf;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.digester.Digester;
import org.apache.commons.digester.RegexRules;
import org.apache.commons.digester.SetNestedPropertiesRule;
import org.apache.commons.digester.SimpleRegexMatcher;
import org.apache.log4j.Logger;
import org.joverseer.domain.Army;
import org.joverseer.domain.Character;
import org.joverseer.domain.Combat;
import org.joverseer.domain.Company;
import org.joverseer.domain.Encounter;
import org.joverseer.domain.NationRelations;
import org.joverseer.domain.NationRelationsEnum;
import org.joverseer.domain.PopulationCenter;
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
        PDDocument document = null;
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

    public void pdf2xml() throws Throwable {
        try {
            String pdfContents = parsePdf();
            Processor processor = ProcessorFactory.getInstance().createProcessor(new FileReader("bin/ctx/txt2xml.config.xml"));
            FileOutputStream outStream = new FileOutputStream(xmlFile.getAbsolutePath());
            StreamDriver driver = new StreamDriver(processor);
            driver.useDebugOutputProperties();
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
            digester.addRule("txt2xml/Turn/Diplomacy",
                    snpr = new SetNestedPropertiesRule(new String[]{"Allegiance"},
                            new String[]{"allegiance"}));
            snpr.setAllowUnknownChildElements(true);
            // create container for nation relations
            digester.addObjectCreate("txt2xml/Turn/Diplomacy/NationRelations", "org.joverseer.support.Container");
            // add container to turn info
            digester.addSetNext("txt2xml/Turn/Diplomacy/NationRelations", "setNationRelations");
        	// create nation relation wrapper
            digester.addObjectCreate("txt2xml/Turn/Diplomacy/NationRelations/NationRelation", "org.joverseer.support.readers.pdf.NationRelationWrapper");
        	// set nested properties
            digester.addRule("txt2xml/Turn/Diplomacy/NationRelations/NationRelation",
                    snpr = new SetNestedPropertiesRule(new String[]{"Nation", "Relation"},
                            new String[]{"nation", "relation"}));
            // add to container
            digester.addSetNext("txt2xml/Turn/Diplomacy/NationRelations/NationRelation", "addItem", "org.joverseer.support.readers.pdf.NationRelationWrapper");
            // create container for pcs
            digester.addObjectCreate("txt2xml/Turn/PopulationCentres", "org.joverseer.support.Container");
            // add container to turn info
            digester.addSetNext("txt2xml/Turn/PopulationCentres", "setPopulationCenters");
            // create pop center wrapper
            digester.addObjectCreate("txt2xml/Turn/PopulationCentres/PopCentre", "org.joverseer.support.readers.pdf.PopCenterWrapper");
            // set nested properties
            digester.addRule("txt2xml/Turn/PopulationCentres/PopCentre",
                    snpr = new SetNestedPropertiesRule(new String[]{"Name", "Hex"},
                            new String[]{"name", "hexNo"}));
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
                    snpr = new SetNestedPropertiesRule(new String[]{"Commander", "Hex"},
                            new String[]{"commanderName", "hexNo"}));
            snpr.setAllowUnknownChildElements(true);
            // parse company members
            digester.addCallMethod("txt2xml/Turn/Companies/Company/Member", "addMember", 0);
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
                    snpr = new SetNestedPropertiesRule(new String[]{"Commander", "Type", "Food", "Warships", "Transports", "WarMachines"},
                            new String[]{"commander", "type", "food", "warships", "transports", "warMachines"}));
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
            Pattern p = Pattern.compile(".*g\\d{3}n(\\d{2})t(\\d{3}).*");
            Matcher m = p.matcher(xmlFile.getCanonicalPath());
            m.matches();
            nationNo = Integer.parseInt(m.group(1));
            int turnNo = Integer.parseInt(m.group(2));
            turnInfo.setNationNo(nationNo);
            turnInfo.setTurnNo(turnNo);
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
            infoSource = new PdfTurnInfoSource(turnInfo.getTurnNo(), turnInfo.getNationNo());
            if (getMonitor() != null) {
                getMonitor().worked(50);
                getMonitor().subTaskStarted("Updating nation relations...");
            }
            updateNationRelations(game);
            if (getMonitor() != null) {
                getMonitor().worked(60);
                getMonitor().subTaskStarted("Updating population centers...");
            }
            try {
                updatePcs(game);
            }
            catch (Exception exc) {
                getMonitor().subTaskStarted("Error: " + exc.getMessage());
            }
            if (getMonitor() != null) {
                getMonitor().worked(70);
                getMonitor().subTaskStarted("Updating characters...");
            }
            updateCharacters(game);
            updateDoubleAgents(game);
            if (getMonitor() != null) {
                getMonitor().worked(80);
                getMonitor().subTaskStarted("Updating armies...");
            }
            updateArmies(game);
            if (getMonitor() != null) {
                getMonitor().worked(90);
                getMonitor().subTaskStarted("Updating companies...");
            }
            updateCompanies(game);
            if (getMonitor() != null) {
                getMonitor().worked(100);
                getMonitor().subTaskStarted("Updating combats, encounters, challenges...");
            }
            updateCombats(game);
            updateEncounters(game);
        }
        catch (Exception exc) {
            if (getMonitor() != null) {
                getMonitor().worked(100);
                getMonitor().subTaskStarted("Unexpected error : '" + exc.getMessage() + "'.");
            }
            throw new Exception("Error updating game from Xml file.", exc);
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
        Container cws = turnInfo.getCombats();
        if (cws == null) return;
        for (CombatWrapper cw : (ArrayList<CombatWrapper>)cws.getItems()) {
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

    private void updateNationRelations(Game game) {
        Nation nation = game.getMetadata().getNationByNum(turnInfo.getNationNo());
        Container nrs = turn.getContainer(TurnElementsEnum.NationRelation);
        NationRelations nr = (NationRelations)nrs.findFirstByProperty("nationNo", turnInfo.getNationNo());
        if (turnInfo.getAllegiance().equals("Free People")) {
            nation.setAllegiance(NationAllegianceEnum.FreePeople);
        } else if (turnInfo.getAllegiance().equals("Dark Servants")) {
            nation.setAllegiance(NationAllegianceEnum.DarkServants);
        } else if (turnInfo.getAllegiance().equals("Neutral")) {
            nation.setAllegiance(NationAllegianceEnum.Neutral);
        } 
        nr.setAllegiance(nation.getAllegiance());
        
        Container nrws = turnInfo.getNationRelations();
        for (NationRelationWrapper nrw : (ArrayList<NationRelationWrapper>)nrws.getItems()) {
            int natNo = game.getMetadata().getNationByName(nrw.getNation()).getNumber();
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
    
    public void updateCharacters(Game game) throws Exception {
        Container cws = turnInfo.getCharacters();
        Container cs = turn.getContainer(TurnElementsEnum.Character);
        for (CharacterWrapper cw : (ArrayList<CharacterWrapper>)cws.getItems()) {
            Character c = (Character)cs.findFirstByProperty("name", cw.getName());
            if (c == null) {
                // missing character
                // maybe assassinated?
                // TODO
            } else {
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
            Character c = (Character)cs.findFirstByProperty("name", daw.getCharacter());
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
    
    
}


