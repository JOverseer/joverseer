package org.joverseer.support.readers.pdf;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
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
import org.joverseer.domain.ArmyElementType;
import org.joverseer.domain.ArmySizeEnum;
import org.joverseer.domain.Artifact;
import org.joverseer.domain.Challenge;
import org.joverseer.domain.Character;
import org.joverseer.domain.CharacterDeathReasonEnum;
import org.joverseer.domain.ClimateEnum;
import org.joverseer.domain.Combat;
import org.joverseer.domain.Company;
import org.joverseer.domain.Encounter;
import org.joverseer.domain.HexInfo;
import org.joverseer.domain.InformationSourceEnum;
import org.joverseer.domain.NationRelations;
import org.joverseer.domain.NationRelationsEnum;
import org.joverseer.domain.PdfTurnText;
import org.joverseer.domain.PlayerInfo;
import org.joverseer.domain.PopulationCenter;
import org.joverseer.domain.SeasonEnum;
import org.joverseer.game.Game;
import org.joverseer.game.Turn;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.metadata.GameTypeEnum;
import org.joverseer.metadata.SNAEnum;
import org.joverseer.metadata.domain.ArtifactInfo;
import org.joverseer.metadata.domain.Nation;
import org.joverseer.metadata.domain.NationAllegianceEnum;
import org.joverseer.support.AsciiUtils;
import org.joverseer.support.Container;
import org.joverseer.support.infoSources.DoubleAgentInfoSource;
import org.joverseer.support.infoSources.HostageInfoSource;
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

/**
 * Class that reads pdf turn files, parses them and updates the game's turns.
 * 
 * The class assumes that the xml files for this turn have already been read.
 * 
 * Parsing goes as follows:
 * 1. the PDFBox library extracts the text from the pdf files
 * 2. the txt2xml package converts the text into xml
 * 3. the xml is read using the Apache Digester and stored into objects contained into a TurnInfo object
 * 4. the objects are read and used to update the current game
 * 
 * The Parser has two public static attributes:
 * 1. parseTimeoutInSecs, which is the amount of time to spend trying to parse a pdf file. Sometimes
 * pdf parsing hangs, and this timeout is used to interrupt the thread doing the parsing so
 * that the program doesn't hang.
 * 2. deleteFilesWhenFinished, which tells the parser whether to delete the intermediate
 * txt and xml files that it produces. Useful for debugging.
 * 
 * The class is set-up to cooperate with a ProgressMonitor so that the progress in
 * parsing the pdf files can be shown in the gui layer.
 *  
 * @author Marios Skounakis
 */
public class TurnPdfReader implements Runnable {
    public static final String DEFAULT_ENCODING = "UTF-8";
    public static int parseTimeoutInSecs = 10;
    public static boolean deleteFilesWhenFinished = true;
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
            this.document = PDDocument.load(this.filename);


            if (encoding != null) {
                output = new OutputStreamWriter(outs = new ByteArrayOutputStream(), encoding);
            }

            PDFTextStripper stripper = null;
            stripper = new PDFTextStripper();
            stripper.setStartPage(startPage);
            stripper.setEndPage(endPage);
            stripper.writeText(this.document, output);
            ret = new String(outs.toByteArray(), "UTF-8");
            this.contents = ret;
            
            //tempPdfTextFile = new File(pdfFile + ".txt");
            FileWriter out = new FileWriter(this.pdfTextFile.getCanonicalPath());
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
            if (this.document != null) {
                this.document.close();
            }
        }
        return ret;
    }
    
    private void cleanup() {
        if (!deleteFilesWhenFinished) return;
        this.pdfTextFile.delete();
        this.xmlFile.delete();
    }

    public void pdf2xml() throws Throwable {
        try {
            String pdfContents = parsePdf();
            Resource r = Application.instance().getApplicationContext().getResource("classpath:ctx/txt2xml.config.xml");
            //Processor processor = ProcessorFactory.getInstance().createProcessor(new FileReader("bin/ctx/txt2xml.config.xml"));
            Processor processor = ProcessorFactory.getInstance().createProcessor(new InputStreamReader(r.getInputStream()));
            FileOutputStream outStream = new FileOutputStream(this.xmlFile.getAbsolutePath());
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
                    snpr = new SetNestedPropertiesRule(new String[]{"Allegiance", "Nation", "TurnNumber", "Season", "Date", "NationName"},
                            new String[]{"allegiance", "nationNo", "turnNo", "season", "date", "nationName"}));
            snpr.setAllowUnknownChildElements(true);
            // create container for SNAs
            digester.addObjectCreate("txt2xml/Turn/General/SNAs", "org.joverseer.support.Container");
            // add container to turn info
            digester.addSetNext("txt2xml/Turn/General/SNAs", "setSnas");
            // create SNA wrapper
            digester.addObjectCreate("txt2xml/Turn/General/SNAs/SNA", "org.joverseer.support.readers.pdf.SNAWrapper");
            // set nested properties
            digester.addRule("txt2xml/Turn/General/SNAs/SNA",
                    snpr = new SetNestedPropertiesRule(new String[]{"Number"},
                            new String[]{"number"}));
            snpr.setAllowUnknownChildElements(true);
            // add to container
            digester.addSetNext("txt2xml/Turn/General/SNAs/SNA", "addItem", "org.joverseer.support.readers.pdf.SNAWrapper");
            // create container for nation relations
            digester.addObjectCreate("txt2xml/Turn/General/NationRelations", "org.joverseer.support.Container");
            // add container to turn info
            digester.addSetNext("txt2xml/Turn/General/NationRelations", "setNationRelations");
            // create nation relation wrapper
            digester.addObjectCreate("txt2xml/Turn/General/NationRelations/NationRelation", "org.joverseer.support.readers.pdf.NationRelationWrapper");
            // set nested properties
            digester.addRule("txt2xml/Turn/General/NationRelations/NationRelation",
                    snpr = new SetNestedPropertiesRule(new String[]{"Nation", "Relation", "NationNumber"},
                            new String[]{"nation", "relation", "nationNo"}));
            snpr.setAllowUnknownChildElements(true);
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
                    snpr = new SetNestedPropertiesRule(new String[]{"Name", "Hex", "Climate", "Docks"},
                            new String[]{"name", "hexNo", "climate", "docks"}));
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
                    snpr = new SetNestedPropertiesRule(new String[]{"Name", "HexID", "Artifacts", "CharacterOrders"},
                            new String[]{"name", "hexNo", "artifacts", "orders"}));
            snpr.setAllowUnknownChildElements(true);
            // create assassination result object
            digester.addObjectCreate("txt2xml/Turn/Orders/Character/Assassination", "org.joverseer.support.readers.pdf.AssassinationResultWrapper");
            // add to character
            digester.addSetNext("txt2xml/Turn/Orders/Character/Assassination", "addOrderResult", "org.joverseer.support.readers.pdf.AssassinationResultWrapper");
            // parse properties
            digester.addRule("txt2xml/Turn/Orders/Character/Assassination",
                    snpr = new SetNestedPropertiesRule(new String[]{"Character"},
                            new String[]{"character"}));
            // create execution result object
            digester.addObjectCreate("txt2xml/Turn/Orders/Character/Execution", "org.joverseer.support.readers.pdf.ExecutionResultWrapper");
            // add to character
            digester.addSetNext("txt2xml/Turn/Orders/Character/Execution", "addOrderResult", "org.joverseer.support.readers.pdf.ExecutionResultWrapper");
            // parse properties
            digester.addRule("txt2xml/Turn/Orders/Character/Execution",
                    snpr = new SetNestedPropertiesRule(new String[]{"Character"},
                            new String[]{"character"}));
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
            // create RCT result object
            digester.addObjectCreate("txt2xml/Turn/Orders/Character/RevealCharacterTrue", "org.joverseer.support.readers.pdf.RevealCharacterResultTrueWrapper");
            // add to character
            digester.addSetNext("txt2xml/Turn/Orders/Character/RevealCharacterTrue", "addOrderResult", "org.joverseer.support.readers.pdf.RevealCharacterResultTrueWrapper");
            // parse properties
            digester.addRule("txt2xml/Turn/Orders/Character/RevealCharacterTrue",
                    snpr = new SetNestedPropertiesRule(new String[]{"Character", "Hex"},
                            new String[]{"characterName", "hexNo"}));
            // create DivCharsWithForces result object
            digester.addObjectCreate("txt2xml/Turn/Orders/Character/DivCharsWithForces", "org.joverseer.support.readers.pdf.DivCharsWithForcesResultWrapper");
            // add to character
            digester.addSetNext("txt2xml/Turn/Orders/Character/DivCharsWithForces", "addOrderResult", "org.joverseer.support.readers.pdf.DivCharsWithForcesResultWrapper");
            // parse properties
            digester.addRule("txt2xml/Turn/Orders/Character/DivCharsWithForces",
                    snpr = new SetNestedPropertiesRule(new String[]{"Commander", "Characters"},
                            new String[]{"commander", "characters"}));
            // handle assassinated, cursed, executed chars
            digester.addCallMethod("txt2xml/Turn/Orders/Character/Assassinated", "setAssassinatedOn");
            digester.addCallMethod("txt2xml/Turn/Orders/Character/Cursed", "setCursedOn");
            digester.addCallMethod("txt2xml/Turn/Orders/Character/Executed", "setExecutedOn");
            // create InfOther result object
            digester.addObjectCreate("txt2xml/Turn/Orders/Character/InfOther", "org.joverseer.support.readers.pdf.InfluenceOtherResultWrapper");
            // add to character
            digester.addSetNext("txt2xml/Turn/Orders/Character/InfOther", "addOrderResult", "org.joverseer.support.readers.pdf.InfluenceOtherResultWrapper");
            // parse properties
            digester.addRule("txt2xml/Turn/Orders/Character/InfOther",
                    snpr = new SetNestedPropertiesRule(new String[]{"PopCenter", "Loyalty"},
                            new String[]{"popCenter", "loyalty"}));
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
            // parse properties
            digester.addRule("txt2xml/Turn/Combats/Combat/Armies/Army",
                    snpr = new SetNestedPropertiesRule(new String[]{"Commander", "Morale"},
                            new String[]{"commanderName", "morale"}));
            snpr.setAllowUnknownChildElements(true);
            // create regiment container
            digester.addObjectCreate("txt2xml/Turn/Combats/Combat/Armies/Army/Regiments", "org.joverseer.support.Container");
            // add container to army
            digester.addSetNext("txt2xml/Turn/Combats/Combat/Armies/Army/Regiments", "setRegiments");
            // create CombatArmyElement
            digester.addObjectCreate("txt2xml/Turn/Combats/Combat/Armies/Army/Regiments/Regiment", "org.joverseer.support.readers.pdf.CombatArmyElement");
            // add to container
            digester.addSetNext("txt2xml/Turn/Combats/Combat/Armies/Army/Regiments/Regiment", "addItem", "org.joverseer.support.readers.pdf.CombatArmyElement");
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
                    snpr = new SetNestedPropertiesRule(new String[]{"Commander", "Type", "Food", "Warships", "Transports", "WarMachines", "Climate", "Hex", "Morale"},
                            new String[]{"commander", "type", "food", "warships", "transports", "warMachines", "climate", "hexNo", "morale"}));
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
            // create container for double agents
            digester.addObjectCreate("txt2xml/Turn/DoubleAgents", "org.joverseer.support.Container");
            // add container to turn info
            digester.addSetNext("txt2xml/Turn/DoubleAgents", "setDoubleAgents");
            // create DoubleAgent wrapper
            digester.addObjectCreate("txt2xml/Turn/DoubleAgents/DoubleAgent", "org.joverseer.support.readers.pdf.DoubleAgentWrapper");
            // add DoubleAgent wrapper
            digester.addSetNext("txt2xml/Turn/DoubleAgents/DoubleAgent", "addItem", "org.joverseer.support.readers.pdf.DoubleAgentWrapper");
            // parse properties
            digester.addRule("txt2xml/Turn/DoubleAgents/DoubleAgent",
                    snpr = new SetNestedPropertiesRule(new String[]{"Character", "Hex", "Nation", "Orders"},
                            new String[]{"name", "hexNo", "nation", "orders"}));
            snpr.setAllowUnknownChildElements(true);
            
            // create container for hostages
            digester.addObjectCreate("txt2xml/Turn/Hostages", "org.joverseer.support.Container");
            // add container to turn info
            digester.addSetNext("txt2xml/Turn/Hostages", "setHostages");
            // create DoubleAgent wrapper
            digester.addObjectCreate("txt2xml/Turn/Hostages/Hostage", "org.joverseer.support.readers.pdf.HostageWrapper");
            // add DoubleAgent wrapper
            digester.addSetNext("txt2xml/Turn/Hostages/Hostage", "addItem", "org.joverseer.support.readers.pdf.HostageWrapper");
            // parse properties
            digester.addRule("txt2xml/Turn/Hostages/Hostage",
                    snpr = new SetNestedPropertiesRule(new String[]{"Name", "Nation", "Owner", "Hex"},
                            new String[]{"name", "nation", "owner", "hexNo"}));
            snpr.setAllowUnknownChildElements(true);

            // create container for artifacts
            digester.addObjectCreate("txt2xml/Turn/Artifacts", "org.joverseer.support.Container");
            // add container to turn info
            digester.addSetNext("txt2xml/Turn/Artifacts", "setArtifacts");
            // create Artifact wrapper
            digester.addObjectCreate("txt2xml/Turn/Artifacts/Artifact", "org.joverseer.support.readers.pdf.ArtifactWrapper");
            // add Artifact wrapper
            digester.addSetNext("txt2xml/Turn/Artifacts/Artifact", "addItem", "org.joverseer.support.readers.pdf.ArtifactWrapper");
            // parse properties
            digester.addRule("txt2xml/Turn/Artifacts/Artifact",
                    snpr = new SetNestedPropertiesRule(new String[]{"Name", "Hex", "Number", "Power"},
                            new String[]{"name", "hexNo", "number", "power"}));
            snpr.setAllowUnknownChildElements(true);

            // create container for anchored ships
            digester.addObjectCreate("txt2xml/Turn/AnchoredShips", "org.joverseer.support.Container");
            // add container to turn info
            digester.addSetNext("txt2xml/Turn/AnchoredShips", "setAnchoredShips");
            // create anchored ships wrapper
            digester.addObjectCreate("txt2xml/Turn/AnchoredShips/Ships", "org.joverseer.support.readers.pdf.AnchoredShipsWrapper");
            // add anchored ships wrapper
            digester.addSetNext("txt2xml/Turn/AnchoredShips/Ships", "addItem", "org.joverseer.support.readers.pdf.AnchoredShipsWrapper");
            // parse properties
            digester.addRule("txt2xml/Turn/AnchoredShips/Ships",
                    snpr = new SetNestedPropertiesRule(new String[]{"Number", "Hex", "Type"},
                            new String[]{"number", "hexNo", "type"}));
            snpr.setAllowUnknownChildElements(true);

            this.turnInfo = (TurnInfo)digester.parse("file:///" + this.xmlFile.getCanonicalPath());
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
    
    @Override
	public void run() {
        try {
            File f = new File(this.filename);
            this.pdfTextFile = File.createTempFile(f.getName(), ".pdf.txt");
            this.xmlFile = File.createTempFile(f.getName(), ".pdf.txt.xml");
            Runnable runnable = new Runnable() {
                @Override
				public void run() {
                    try {
                        pdf2xml();
                    }
                    catch (Throwable exc) {
                        if (getMonitor() != null) {
                            getMonitor().subTaskStarted(exc.getMessage());
                        }
                        logger.error(exc);
                        exc.printStackTrace();
                    }
                }
            };
            Thread t = new Thread(runnable);
            t.start();
            t.join(parseTimeoutInSecs * 1000);
            //t.join();
            if (t.getState() != Thread.State.TERMINATED) {
                //t.stop();
                // do not update game
                getMonitor().subTaskStarted("Error: Pdf parsing timer expired. Skipping...");
                getMonitor().worked(100);
            } else {
                if (getMonitor() != null) {
                    getMonitor().worked(50);
                    getMonitor().subTaskStarted("Parsing Pdf file...");
                }
                readFile();
                updateGame(this.game);
                this.game.setCurrentTurn(this.game.getMaxTurn());
            }
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
    
    public void updateGame(Game game1) throws Exception {
    	if (this.turnInfo == null) {
    		this.errorOccurred = true;
    		throw new Exception("Failed to parse pdf file.");
    	}
        if (this.turnInfo.getTurnNo() < game1.getMaxTurn()) {
            //todo fix
        	this.errorOccurred = true;
            throw new Exception("Can only import pdfs for last turn.");
        }
        
        
        try {
            this.turn = game1.getTurn(game1.getMaxTurn());
            // check to see if corresponding XML has been imported
            PlayerInfo pi = (PlayerInfo)this.turn.getContainer(TurnElementsEnum.PlayerInfo).findFirstByProperty("nationNo", this.turnInfo.getNationNo());
            if (pi == null) {
            	if (getMonitor() != null) {
                    getMonitor().worked(100);
                    getMonitor().subTaskStarted("Skipping file because XML has not been imported...");
                }	
            	return;
            }
            
            try {
                updateTurnData();
            }
            catch (Exception exc) {
                logger.error(exc);
            	this.errorOccurred = true;
                getMonitor().subTaskStarted("Error updating turn data : '" + exc.getMessage() + "'.");
            }
            
            this.infoSource = new PdfTurnInfoSource(this.turnInfo.getTurnNo(), this.turnInfo.getNationNo());
            
            // add text
            PdfTurnText ptt = (PdfTurnText)this.turn.getContainer(TurnElementsEnum.PdfText).findFirstByProperty("nationNo", this.turnInfo.getNationNo());
            if (ptt != null) {
                this.turn.getContainer(TurnElementsEnum.PdfText).removeItem(ptt);
            }
            ptt = new PdfTurnText();
            ptt.setNationNo(this.turnInfo.getNationNo());
            ptt.setText(this.contents);
            this.turn.getContainer(TurnElementsEnum.PdfText).addItem(ptt);
            
            if (getMonitor() != null) {
                getMonitor().worked(50);
                getMonitor().subTaskStarted("Updating nation relations...");
            }
            try {
                if (game1.getMetadata().getGameType() == GameTypeEnum.gameFA && this.turn.getTurnNo() == 0) {
                    updateNationMetadata(game1);
                }
                updateNationRelations(game1);
            }
            catch (Exception exc) {
                logger.error(exc);
            	this.errorOccurred = true;
                getMonitor().subTaskStarted("Error: " + exc.getMessage());
            }
            if (getMonitor() != null) {
                getMonitor().worked(60);
                getMonitor().subTaskStarted("Updating population centers...");
            }
            try {
                updatePcs(game1);
            }
            catch (Exception exc) {
            	this.errorOccurred = true;
                getMonitor().subTaskStarted("Error: " + exc.getMessage());
            }
            if (getMonitor() != null) {
                getMonitor().worked(70);
                getMonitor().subTaskStarted("Updating characters...");
            }
            try {
                updateCharacters(game1);
            }
            catch (Exception exc) {
                logger.error(exc);
            	this.errorOccurred = true;
                getMonitor().subTaskStarted("Error: " + exc.getMessage());
            }
            try {
                updateDoubleAgents(game1);
            }
            catch (Exception exc) {
                logger.error(exc);
            	this.errorOccurred = true;
                getMonitor().subTaskStarted("Error: " + exc.getMessage());
            }
            try {
                updateHostages(game1);
            }
            catch (Exception exc) {
                logger.error(exc);
                this.errorOccurred = true;
                getMonitor().subTaskStarted("Error: " + exc.getMessage());
            }
            if (getMonitor() != null) {
                getMonitor().worked(80);
                getMonitor().subTaskStarted("Updating armies...");
            }
            try {
                updateArmies(game1);
            }
            catch (Exception exc) {
                logger.error(exc);
            	this.errorOccurred = true;
                getMonitor().subTaskStarted("Error: " + exc.getMessage());
            }
            try {
                updateAnchoredShips(game1);
            }
            catch (Exception exc) {
                logger.error(exc);
                this.errorOccurred = true;
                getMonitor().subTaskStarted("Error: " + exc.getMessage());
            }
            if (getMonitor() != null) {
                getMonitor().worked(90);
                getMonitor().subTaskStarted("Updating companies...");
            }
            try {
                updateCompanies(game1);
            }
            catch (Exception exc) {
                logger.error(exc);
            	this.errorOccurred = true;
                getMonitor().subTaskStarted("Error: " + exc.getMessage());
            }
            if (getMonitor() != null) {
                getMonitor().worked(95);
                getMonitor().subTaskStarted("Updating artifacts...");
            }
            try {
                updateArtifacts(game1);
            }
            catch (Exception exc) {
                logger.error(exc);
                this.errorOccurred = true;
                getMonitor().subTaskStarted("Error: " + exc.getMessage());
            }
            if (getMonitor() != null) {
                getMonitor().worked(100);
                getMonitor().subTaskStarted("Updating combats, encounters, challenges...");
            }
            try {
                updateCombats(game1);
            }
            catch (Exception exc) {
                logger.error(exc);
            	this.errorOccurred = true;
                getMonitor().subTaskStarted("Error: " + exc.getMessage());
            }
            try {
                updateEncounters(game1);
            }
            catch (Exception exc) {
                logger.error(exc);
            	this.errorOccurred = true;
                getMonitor().subTaskStarted("Error: " + exc.getMessage());
            }
            try {
                updateClimates(game1);
            }
            catch (Exception exc) {
                logger.error(exc);
            	this.errorOccurred = true;
                getMonitor().subTaskStarted("Error: " + exc.getMessage());
            }
        }
        catch (Exception exc) {
            logger.error(exc);
            if (getMonitor() != null) {
                getMonitor().worked(100);
                getMonitor().subTaskStarted("Error : '" + exc.getMessage() + "'.");
            }
        	this.errorOccurred = true;
            throw new Exception("Error updating game from Pdf file.", exc);
        }
    }
    
    private void updateNationMetadata(Game game1) throws Exception {
        for (NationRelationWrapper nrw : (ArrayList<NationRelationWrapper>)this.turnInfo.getNationRelations().getItems()) {
            if (nrw.getNationNo() > 0) {
                Nation n = game1.getMetadata().getNationByNum(nrw.getNationNo());
                if (n != null) {
                    n.setName(nrw.getNation().trim());
                    String[] shortNames = createNationShortNames(n.getName().trim());
                    for (String s : shortNames) {
                        boolean duplicate = false;
                        for (Nation nn : (ArrayList<Nation>)game1.getMetadata().getNations()) {
                            if (nn == n) continue;
                            if (nn.getShortName().equals(s)) {
                                duplicate = true;
                            }
                        }
                        if (!duplicate) {
                            n.setShortName(s);
                            break;
                        }
                    }
                }
            }
        }
        // do current nation
        Nation n = game1.getMetadata().getNationByNum(this.turnInfo.getNationNo());
        if (n != null) {
            n.setName(this.turnInfo.getNationName().trim());
            String[] shortNames = createNationShortNames(this.turnInfo.getNationName().trim());
            for (String s : shortNames) {
                boolean duplicate = false;
                for (Nation nn : (ArrayList<Nation>)game1.getMetadata().getNations()) {
                    if (nn == n) continue;
                    if (nn.getShortName().equals(s)) {
                        duplicate = true;
                    }
                }
                if (!duplicate) {
                    n.setShortName(s);
                    break;
                }
            }
        }
        
        //update SNAs
        if (n != null) {
        	for (SNAWrapper nw : (ArrayList<SNAWrapper>)this.turnInfo.getSnas().getItems()) {
        		SNAEnum sna = SNAEnum.getSnaFromNumber(nw.getNumber());
        		if (sna != null) {
        			n.getSnas().add(sna);
        		}
        	}
        }
    }
    
    private String[] createNationShortNames(String name) {
        String[] parts = name.split(" ");
        if (parts.length == 1) {
            ArrayList<String> ret = new ArrayList<String>();
            for (int i=2; i<=Math.min(name.length(),3); i++) {
                ret.add(name.substring(0, i));
            }
            return ret.toArray(new String[]{});
        } else if (parts.length == 2) {
            return new String[]{
                    parts[0].substring(0, 1) + parts[1].substring(0, 1),
                    parts[0].substring(0, 2) + parts[1].substring(0, 1),
                    parts[0].substring(0, 1) + parts[1].substring(0, 2),
                    parts[0].substring(0, 2) + parts[1].substring(0, 2)};
        } else {
            return new String[]{
                    parts[0].substring(0, 1) + parts[1].substring(0, 1) + parts[2].substring(0, 1),
                    parts[0].substring(0, 2) + parts[1].substring(0, 1) + parts[2].substring(0, 1),
                    parts[0].substring(0, 1) + parts[1].substring(0, 2) + parts[2].substring(0, 1),
                    parts[0].substring(0, 1) + parts[1].substring(0, 1) + parts[2].substring(0, 2)
            };
        }
    }
    
    private void updateTurnData() throws Exception {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("M/d/yyyy");
            this.turn.setTurnDate(sdf.parse(this.turnInfo.getDate()));
            if (this.turnInfo.getSeason().equals("Spring")) {
                this.turn.setSeason(SeasonEnum.Spring);
            } else if (this.turnInfo.getSeason().equals("Summer")) {
                this.turn.setSeason(SeasonEnum.Summer);
            } else if (this.turnInfo.getSeason().equals("Fall")) {
                this.turn.setSeason(SeasonEnum.Fall);
            } else if (this.turnInfo.getSeason().equals("Winter")) {
                this.turn.setSeason(SeasonEnum.Winter);
            }
        }
        catch (Exception exc) {
            throw exc;
        }
    }
    
    private void updateEncounters(Game game1) {
        Container ews = this.turnInfo.getEncounters();
        if (ews == null) return;
        Container encounters = game1.getTurn().getContainer(TurnElementsEnum.Encounter);
        for (EncounterWrapper ew : (ArrayList<EncounterWrapper>)ews.getItems()) {
            Encounter e = (Encounter)encounters.findFirstByProperties(new String[]{"character", "hexNo"}, new Object[]{ew.getCharacter(), ew.getHexNo()});
            if (e != null) {
                encounters.removeItem(e);
            }
            encounters.addItem(ew.getEncounter());
        }
    }
    
    private void updateArmies(Game game1) {
        Container armies = game1.getTurn().getContainer(TurnElementsEnum.Army);
        Container aws = this.turnInfo.getArmies();
        if (aws == null) return;
        for (ArmyWrapper aw : (ArrayList<ArmyWrapper>)aws.getItems()) {
            Army a = (Army)armies.findFirstByProperty("commanderName", aw.getCommander());
            if (a != null) {
                aw.updateArmy(a);
            }
        }
    }
      
    private void updateAnchoredShips(Game game1) {
        String commanderName = "[Anchored Ships]";
        Container armies = game1.getTurn().getContainer(TurnElementsEnum.Army);
        Container asws = this.turnInfo.getAnchoredShips();
        if (asws == null) return;
        for (AnchoredShipsWrapper asw : (ArrayList<AnchoredShipsWrapper>)asws.getItems()) {
            String hexNo = String.valueOf(asw.getHexNo());
            Army a = (Army)armies.findFirstByProperties(new String[]{"commanderName", "hexNo", "nationNo"}, new Object[]{commanderName, hexNo, this.turnInfo.getNationNo()});
            if (a == null) {
                a = new Army();
                a.setNavy(true);
                a.setSize(ArmySizeEnum.unknown);
                a.setCommanderName(commanderName);
                a.setCommanderTitle("");
                a.setHexNo(hexNo);
                a.setNationNo(this.turnInfo.getNationNo());
                NationAllegianceEnum allegiance = NationAllegianceEnum.Neutral;
                NationRelations nr = (NationRelations)game1.getTurn().getContainer(TurnElementsEnum.NationRelation).findFirstByProperty("nationNo", this.turnInfo.getNationNo());
                if (nr != null) {
                    allegiance = nr.getAllegiance();
                }
                a.setNationAllegiance(allegiance);
                a.setInformationSource(InformationSourceEnum.exhaustive);
                a.setInfoSource(new PdfTurnInfoSource(this.turnInfo.getTurnNo(), this.turnInfo.getNationNo()));
                armies.addItem(a);
            }
            if (asw.getType().equalsIgnoreCase("warships")) {
                a.setElement(ArmyElementType.Warships, asw.getNumber());
            } else {
                a.setElement(ArmyElementType.Transports, asw.getNumber());
            }
        }
    }
    
    public void updateCombats(Game game1) throws Exception {
        Container combats = game1.getTurn().getContainer(TurnElementsEnum.Combat);
        Container challenges = game1.getTurn().getContainer(TurnElementsEnum.Challenge);
        Container cws = this.turnInfo.getCombats();
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
                    c.addNarration(this.turnInfo.getNationNo(), cw.getNarration());
                    combats.addItem(c);
                } else {
                    c.addNarration(this.turnInfo.getNationNo(), cw.getNarration());
                }
            }
            try {
                cw.updateGame(game1, this.turnInfo.getTurnNo(), this.turnInfo.getNationNo());
            }
            catch (Exception exc) {
                throw new Exception("Failed to parse combat at " + cw.getHexNo());
            }
        }
    }

    private void updateNationRelations(Game game1) throws Exception {
        Nation nation = game1.getMetadata().getNationByNum(this.turnInfo.getNationNo());
        if (nation == null) {
        	throw new Exception("Failed to find nation with number " + this.turnInfo.getNationNo());
        }
        Container nrs = this.turn.getContainer(TurnElementsEnum.NationRelation);
        NationRelations nr = (NationRelations)nrs.findFirstByProperty("nationNo", this.turnInfo.getNationNo());
        if (this.turnInfo.getAllegiance().equals("Free People")) {
            nation.setAllegiance(NationAllegianceEnum.FreePeople);
        } else if (this.turnInfo.getAllegiance().equals("Dark Servant")) {
            nation.setAllegiance(NationAllegianceEnum.DarkServants);
        } else if (this.turnInfo.getAllegiance().equals("Neutral")) {
            nation.setAllegiance(NationAllegianceEnum.Neutral);
        } 
        if (nr == null) {
        	throw new Exception("Failed to retrieve NationRelations object for nation " + this.turnInfo.getNationNo());
        }
        nr.setAllegiance(nation.getAllegiance());
        
        Container nrws = this.turnInfo.getNationRelations();
        String problematicNations = "";
        for (NationRelationWrapper nrw : (ArrayList<NationRelationWrapper>)nrws.getItems()) {
        	Nation n = game1.getMetadata().getNationByName(nrw.getNation());
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
    
    public void updatePcs(Game game1) throws Exception {
        Container pcws = this.turnInfo.getPopulationCenters();
        Container pcs = this.turn.getContainer(TurnElementsEnum.PopulationCenter);
        String pcsNotFound = "";
        for (PopCenterWrapper pcw : (ArrayList<PopCenterWrapper>)pcws.getItems()) {
            //PopulationCenter pc = (PopulationCenter)pcs.findFirstByProperty("name", pcw.getName());
            PopulationCenter pc = (PopulationCenter)pcs.findFirstByProperty("hexNo", pcw.getHexNo());
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
    
    public void updateClimates(Game game1) throws Exception {
        Container pcws = this.turnInfo.getPopulationCenters();
        Container his = this.turn.getContainer(TurnElementsEnum.HexInfo);
        for (PopCenterWrapper pcw : (ArrayList<PopCenterWrapper>)pcws.getItems()) {
            HexInfo hi = (HexInfo)his.findFirstByProperty("hexNo", pcw.getHexNo());
            ClimateEnum climate = translateClimate(pcw.getClimate());
            if (climate != null) {
                hi.setClimate(climate);
            }
        }
        
        Container aws = this.turnInfo.getArmies();
        for (ArmyWrapper aw : (ArrayList<ArmyWrapper>)aws.getItems()) {
            HexInfo hi = (HexInfo)his.findFirstByProperty("hexNo", aw.getHexNo());
            ClimateEnum climate = translateClimate(aw.getClimate());
            if (climate != null) {
                hi.setClimate(climate);
            }
        }
    }
    
    public void updateArtifacts(Game game1) throws Exception {
        Container aws = this.turnInfo.getArtifacts();
        for (ArtifactWrapper aw : (ArrayList<ArtifactWrapper>)aws.getItems()) {
            // for FA game, update artifact numbers
            if (game1.getMetadata().getGameType() == GameTypeEnum.gameFA) {
                String artiNameInAscii = AsciiUtils.convertNonAscii(aw.getName().trim());
                boolean found = false;
                for (ArtifactInfo ai : (ArrayList<ArtifactInfo>)game1.getMetadata().getArtifacts().getItems()) {
                    if (AsciiUtils.convertNonAscii(ai.getName()).equalsIgnoreCase(artiNameInAscii)) {
                        found = true;
                        ai.setNo(aw.getNumber());
                        break;
                    }
                }
                if (!found) {
                    // add artifact
                    ArtifactInfo ai = new ArtifactInfo();
                    ai.setName(aw.getName().trim());
                    ai.setNo(aw.getNumber());
                    game1.getMetadata().getArtifacts().addItem(ai);
                }
            };
            
            // for all games update powers
            ArtifactInfo artifactInfo = (ArtifactInfo)game1.getMetadata().getArtifacts().findFirstByProperty("no", aw.getNumber());
            if (artifactInfo != null && aw.getPower() != null && !aw.getPower().equals("")) {
                // parse power
                String power = aw.getPower();
                if (power.startsWith("Increases")) {
                    int idx = power.lastIndexOf(" ");
                    String value = power.substring(idx + 1);
                    if (power.indexOf("Agent") > -1) {
                        power = "Agent " + value;
                    } else if (power.indexOf("Command") > -1) {
                        power = "Command " + value;
                    } else if (power.indexOf("Mage") > -1) {
                        power = "Mage " + value;
                    } else if (power.indexOf("Emmisary") > -1) {
                        power = "Emmisary " + value;
                    } else if (power.indexOf("Stealth") > -1) {
                        power = "Stealth " + value;
                    }
                } else if (power.startsWith("COMBAT")) {
                    int i2 = power.lastIndexOf(" ");
                    int i1 = power.lastIndexOf(" ", i2-1);
                    power = "Combat " + power.substring(i1 + 1, i2).trim();
                } else if (power.indexOf("Open seas")> -1) {
                    power = "Open seas";
                } else if (power.indexOf("HIDING - one Pop") > -1) {
                    power = "Hide PC";
                } else if (power.indexOf("SCRYING - \"Scout Area\"") > -1) {
                    power = "Scry Area";
                }
                
                if (!power.equals(aw.getPower())) {
                    power += "*"; // mark power as updated for this game
                }
                
                artifactInfo.setPower(0, power);
            }
            
            // update hidden artifacts
            if (aw.getHexNo() > 0) {
                Artifact a = (Artifact)game1.getTurn().getContainer(TurnElementsEnum.Artifact).findFirstByProperty("number", aw.getNumber());
                if (a == null) {
                    a = new Artifact();
                    a.setNumber(aw.getNumber());
                    a.setName(aw.getName().trim());
                    a.setHexNo(aw.getHexNo());
                    a.setOwner(this.turnInfo.getNationName());
                    a.setInfoSource(new PdfTurnInfoSource(this.turnInfo.getTurnNo(), this.turnInfo.getNationNo()));
                    game1.getTurn().getContainer(TurnElementsEnum.Artifact).addItem(a);
                }
            }
        }
    }
    
    public void updateCharacters(Game game1) throws Exception {
        Container cws = this.turnInfo.getCharacters();
        Container cs = this.turn.getContainer(TurnElementsEnum.Character);
        for (CharacterWrapper cw : (ArrayList<CharacterWrapper>)cws.getItems()) {
        	try {
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
	                    Turn t = game1.getTurn(this.turnInfo.getTurnNo() - 1);
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
	                    c.setNationNo(this.turnInfo.getNationNo());
	                    c.setHealth(0);
	                    c.setDeathReason(deathReason);
	                    c.setHexNo(cw.getHexNo());
	                    c.setInfoSource(this.infoSource);
	                    c.setInformationSource(InformationSourceEnum.exhaustive);
	                    cs.addItem(c);
	                }
	            };
	            if (c != null) {
	                cw.updateCharacter(c);
	            }
	            for (OrderResult orderResult : cw.getOrderResults()) {
	                orderResult.updateGame(game1, this.turn, this.nationNo, cw.getName());
	            }
	            cw.parsePopCenter(game1, this.infoSource, c);
	            cw.parseScoHexOrScoPop(game1, this.infoSource, c);
	            cw.parseArmiesFromDivineNationForces(game1, this.infoSource, c);
	            cw.parseDivineCharsWithForces(game1, this.infoSource, c);
        	}
        	catch (Exception exc) {
        		logger.error("failed to parse character " + cw.getName());
        		logger.error(exc);
        	}
        }
    }
    
    public void updateDoubleAgents(Game game1) throws Exception {
        DoubleAgentInfoSource dais = new DoubleAgentInfoSource(this.turnInfo.getTurnNo(), this.turnInfo.getNationNo());
        Container daws = this.turnInfo.getDoubleAgents();
        Container cs = this.turn.getContainer(TurnElementsEnum.Character);
        for (DoubleAgentWrapper daw : (ArrayList<DoubleAgentWrapper>)daws.getItems()) {
            Character c = (Character)cs.findFirstByProperty("name", daw.getName());
            if (c == null) {
                // add character
                c = daw.getCharacter();
                c.setInfoSource(dais);
                cs.addItem(c);
            }
            // set nation if applicable
            Nation n = game1.getMetadata().getNationByName(daw.getNation());
            if (n != null) {
                c.setNationNo(n.getNumber());
            }
            // set order results if applicable
            if (c.getOrderResults() == null || c.getOrderResults().equals("")) {
            	c.setOrderResults(daw.getOrders());
            }
        }
                
    }
    
    public void updateHostages(Game game1) throws Exception {
        HostageInfoSource dais = new HostageInfoSource(this.turnInfo.getTurnNo(), this.turnInfo.getNationNo());
        Container hws = this.turnInfo.getHostages();
        Container cs = this.turn.getContainer(TurnElementsEnum.Character);
        for (HostageWrapper hw : (ArrayList<HostageWrapper>)hws.getItems()) {
            Character c = (Character)cs.findFirstByProperty("name", hw.getName());
            if (c == null) {
                // add character
                c = hw.getCharacter();
                c.setInfoSource(dais);
                cs.addItem(c);
            } 
            c.setHostage(true);
           
            // set nation if applicable
            if (hw.getNation() != null) {
                Nation n = game1.getMetadata().getNationByName(hw.getNation());
                if (n != null) {
                    c.setNationNo(n.getNumber());
                }
            }
            
            // add to owner
            c = (Character)cs.findFirstByProperty("name", hw.getOwner());
            if (c != null) {
                if (!c.getHostages().contains(hw.getName())) {
                    c.getHostages().add(hw.getName());
                }
            }
        }
                
    }
    
    public void updateCompanies(Game game1) throws Exception {
        Container cws = this.turnInfo.getCompanies();
        if (cws == null) return;
        Container cs = this.turn.getContainer(TurnElementsEnum.Company);
        for (CompanyWrapper cw : (ArrayList<CompanyWrapper>)cws.getItems()) {
            Company newC = cw.getCompany();
            newC.setInfoSource(this.infoSource);
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
        return this.monitor;
    }

    
    public void setMonitor(ProgressMonitor monitor) {
        this.monitor = monitor;
    }

	public boolean getErrorOccurred() {
		return this.errorOccurred;
	}

	public void setErrorOccurred(boolean errorOccurred) {
		this.errorOccurred = errorOccurred;
	}
    
    
}


