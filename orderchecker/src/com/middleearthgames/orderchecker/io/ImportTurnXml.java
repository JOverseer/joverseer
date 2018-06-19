// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   ImportTurnXml.java

package com.middleearthgames.orderchecker.io;

import java.io.File;

import javax.swing.filechooser.FileSystemView;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.middleearthgames.orderchecker.Army;
import com.middleearthgames.orderchecker.Character;
import com.middleearthgames.orderchecker.Nation;
import com.middleearthgames.orderchecker.PopCenter;

// Referenced classes of package com.middleearthgames.orderchecker.io:
//            AdapterNode

public class ImportTurnXml
{

    public ImportTurnXml(String sFileName)
    {
        this.nation = null;
        this.primaryParse = true;
        this.secondaryNation = -1;
        this.filename = sFileName;
    }

    public boolean getTurnData()
    {
        this.primaryParse = true;
        return getTurnData(this.filename);
    }

    private boolean getTurnData(String filePath)
    {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            builder.setErrorHandler(new ErrorHandler() {
    
                @Override
				public void fatalError(SAXParseException saxparseexception)
                    throws SAXException
                {
                }
    
                @Override
				public void error(SAXParseException e)
                    throws SAXParseException
                {
                    throw e;
                }
    
                @Override
				public void warning(SAXParseException err)
                    throws SAXParseException
                {
                    System.out.println("** Warning, line " + err.getLineNumber() + ", uri " + err.getSystemId());
                    System.out.println("   " + err.getMessage());
                }
    
            }
    );
            File inputFile = new File(filePath);
            this.document = builder.parse(inputFile);
            return true;
        }
        catch (Exception ex) {
            return false;
        }
    }

    public Nation parseTurnData()
    {
        if(this.primaryParse)
            this.nation = new Nation();
        parseNationData();
        parseSecondaryFiles();
        return this.nation;
    }

    private boolean parseNationData()
    {
        AdapterNode root = new AdapterNode(this.document);
        AdapterNode node = root.child(0);
        if(!node.isNodeAnElement() || !node.getNodeName().equals("METurn"))
            return false;
        int children = node.childCount();
        for(int i = 0; i < children; i++)
        {
            AdapterNode childNode = node.child(i);
            String name = childNode.getNodeName();
            if(name.equals("TurnInfo"))
            {
                if(this.primaryParse)
                {
                    parseNationData(childNode);
                    continue;
                }
                if(!parseSecondaryNationData(childNode))
                    return false;
                continue;
            }
            if(name.equals("Nations"))
            {
                parseNationNames(childNode);
                continue;
            }
            if(name.equals("Characters"))
            {
                parseCharacters(childNode);
                continue;
            }
            if(name.equals("PopCentres"))
            {
                parsePopulationCenters(childNode);
                continue;
            }
            if(name.equals("Armies"))
                parseArmies(childNode);
        }

        return true;
    }

    private void parseSecondaryFiles()
    {
        if(this.nation == null || !this.nation.isNationComplete())
            return;
        File file = new File(this.filename);
        String directory = file.getParent();
        FileSystemView view = FileSystemView.getFileSystemView();
        File fileList[] = view.getFiles(new File(directory), false);
        this.primaryParse = false;
        for(int i = 0; i < fileList.length; i++)
        {
            boolean bXmlFile = false;
            String name = fileList[i].getName();
            int index = name.lastIndexOf('.');
            if(index != -1)
            {
                String extension = name.substring(index + 1, name.length());
                if(extension.equalsIgnoreCase("xml"))
                    bXmlFile = true;
            }
            if(bXmlFile && fileList[i].exists() && !fileList[i].isDirectory() && getTurnData(fileList[i].getPath()) && parseNationData())
                this.nation.addNationParsed(this.secondaryNation);
        }

    }

    private void parseNationData(AdapterNode node)
    {
        int children = node.childCount();
        for(int i = 0; i < children; i++)
        {
            AdapterNode childNode = node.child(i);
            String name = childNode.getNodeName();
            if(name.equals("NationNo"))
            {
                this.nation.SetNation(childNode.extractNodeNumber());
                continue;
            }
            if(name.equals("NationCapitalHex"))
            {
                this.nation.setCapital(childNode.extractNodeNumber());
                continue;
            }
            if(name.equals("GameNo"))
            {
                this.nation.setGame(childNode.extractNodeNumber());
                continue;
            }
            if(name.equals("TurnNo"))
            {
                this.nation.setTurn(childNode.extractNodeNumber());
                continue;
            }
            if(name.equals("Secret"))
            {
                this.nation.setSecret(childNode.extractNodeNumber());
                continue;
            }
            if(name.equals("GameType"))
            {
                this.nation.setGameType(childNode.extractNodeString());
                continue;
            }
            if(name.equals("DueDate"))
            {
                this.nation.setDueDate(childNode.extractNodeString());
                continue;
            }
            if(name.equals("Player"))
                this.nation.setPlayer(childNode.extractNodeString());
        }

    }

    private boolean parseSecondaryNationData(AdapterNode node)
    {
        boolean nationPassed = false;
        boolean gamePassed = false;
        boolean turnPassed = false;
        int children = node.childCount();
        for(int i = 0; i < children; i++)
        {
            AdapterNode childNode = node.child(i);
            String name = childNode.getNodeName();
            if(name.equals("NationNo"))
            {
                this.secondaryNation = childNode.extractNodeNumber();
                if(this.secondaryNation != this.nation.getNation())
                    nationPassed = true;
                continue;
            }
            if(name.equals("GameNo"))
            {
                int gameNumber = childNode.extractNodeNumber();
                if(gameNumber == this.nation.getGame())
                    gamePassed = true;
                continue;
            }
            if(!name.equals("TurnNo"))
                continue;
            int turnNumber = childNode.extractNodeNumber();
            if(turnNumber == this.nation.getTurn())
                turnPassed = true;
        }

        return nationPassed && gamePassed && turnPassed;
    }

    private void parseNationNames(AdapterNode node)
    {
        if(!this.primaryParse)
            return;
        int children = node.childCount();
        for(int i = 0; i < children; i++)
        {
            AdapterNode childNode = node.child(i);
            String name = childNode.getNodeName();
            if(name.equals("Nation"))
                this.nation.addNation(childNode.extractNodeString());
        }

    }

    private void parseCharacters(AdapterNode node)
    {
        int characters = node.childCount();
        for(int i = 0; i < characters; i++)
        {
            AdapterNode childNode = node.child(i);
            if(!childNode.getNodeName().equals("Character"))
                continue;
            Character character = new Character(childNode.extractAttributeString());
            parseCharacter(childNode, character);
            if(this.primaryParse)
            {
                this.nation.addCharacter(character);
                continue;
            }
            Character existingChar = this.nation.findCharacterById(character.getId());
            if(existingChar != null && character.getNation() != this.secondaryNation)
                continue;
            if(existingChar != null)
                this.nation.removeCharacter(existingChar);
            this.nation.addCharacter(character);
        }

    }

    private void parseCharacter(AdapterNode node, Character character)
    {
        int children = node.childCount();
        for(int i = 0; i < children; i++)
        {
            AdapterNode childNode = node.child(i);
            String name = childNode.getNodeName();
            if(name.equals("Name"))
            {
                character.setName(childNode.extractNodeString());
                continue;
            }
            if(name.equals("Location"))
            {
                String hostCheck = childNode.extractNodeString();
                if(hostCheck.equalsIgnoreCase("HOST"))
                    character.setLocation(0);
                else
                    character.setLocation(childNode.extractNodeNumber());
                continue;
            }
            if(name.equals("Nation"))
            {
                character.setNation(childNode.extractNodeNumber());
                continue;
            }
            if(name.equals("Command"))
            {
                character.setCommandRank(childNode.extractNodeNumber());
                continue;
            }
            if(name.equals("TotalCommand"))
            {
                character.setTotalCommandRank(childNode.extractNodeNumber());
                continue;
            }
            if(name.equals("Agent"))
            {
                character.setAgentRank(childNode.extractNodeNumber());
                continue;
            }
            if(name.equals("TotalAgent"))
            {
                character.setTotalAgentRank(childNode.extractNodeNumber());
                continue;
            }
            if(name.equals("Emmisary"))
            {
                character.setEmissaryRank(childNode.extractNodeNumber());
                continue;
            }
            if(name.equals("TotalEmmisary"))
            {
                character.setTotalEmissaryRank(childNode.extractNodeNumber());
                continue;
            }
            if(name.equals("Mage"))
            {
                character.setMageRank(childNode.extractNodeNumber());
                continue;
            }
            if(name.equals("TotalMage"))
            {
                character.setTotalMageRank(childNode.extractNodeNumber());
                continue;
            }
            if(name.equals("Stealth"))
            {
                character.setStealth(childNode.extractNodeNumber());
                continue;
            }
            if(name.equals("TotalStealth"))
            {
                character.setTotalStealth(childNode.extractNodeNumber());
                continue;
            }
            if(name.equals("Challenge"))
            {
                character.setChallenge(childNode.extractNodeNumber());
                continue;
            }
            if(name.equals("Health"))
            {
                character.setHealth(childNode.extractNodeNumber());
                continue;
            }
            if(name.equals("Artifacts"))
            {
                parseCharacterArtifacts(childNode, character);
                continue;
            }
            if(name.equals("Spells"))
                parseCharacterSpells(childNode, character);
        }

    }

    private void parseCharacterArtifacts(AdapterNode node, Character character)
    {
        int artifacts = node.childCount();
        for(int i = 0; i < artifacts; i++)
        {
            AdapterNode childNode = node.child(i);
            if(childNode.getNodeName().equals("Artifact"))
            {
                String name = childNode.extractNodeString();
                character.addArtifact(extractItemNumber(name), name);
            }
        }

    }

    private void parseCharacterSpells(AdapterNode node, Character character)
    {
        int spells = node.childCount();
        for(int i = 0; i < spells; i++)
        {
            AdapterNode childNode = node.child(i);
            if(childNode.getNodeName().equals("Spell"))
            {
                String name = childNode.extractNodeString();
                character.addSpell(extractItemNumber(name), name);
            }
        }

    }

    private void parsePopulationCenters(AdapterNode node)
    {
        int popcenters = node.childCount();
        for(int i = 0; i < popcenters; i++)
        {
            AdapterNode childNode = node.child(i);
            if(!childNode.getNodeName().equals("PopCentre"))
                continue;
            PopCenter pc = new PopCenter(childNode.extractAttributeNumber());
            parsePopulationCenter(childNode, pc);
            if(pc.getName() == null)
                continue;
            if(this.primaryParse)
            {
                this.nation.addPopulationCenter(pc);
                continue;
            }
            PopCenter existingPc = this.nation.findPopulationCenter(pc.getLocation());
            if(existingPc != null)
                existingPc.mergePopulationCenter(pc);
            else
                this.nation.addPopulationCenter(pc);
        }

    }

    private void parsePopulationCenter(AdapterNode node, PopCenter pc)
    {
        int children = node.childCount();
        for(int i = 0; i < children; i++)
        {
            AdapterNode childNode = node.child(i);
            String name = childNode.getNodeName();
            if(name.equals("Name"))
            {
                pc.setName(childNode.extractNodeString());
                continue;
            }
            if(name.equals("Nation"))
            {
                pc.setNation(childNode.extractNodeNumber());
                continue;
            }
            if(name.equals("FortificationLevel"))
            {
                pc.setFortification(childNode.extractNodeNumber());
                continue;
            }
            if(name.equals("Size"))
            {
                pc.setSize(childNode.extractNodeNumber());
                continue;
            }
            if(name.equals("Dock"))
            {
                pc.setDock(childNode.extractNodeNumber());
                continue;
            }
            if(name.equals("Capital"))
            {
                pc.setCapital(childNode.extractNodeNumber());
                continue;
            }
            if(name.equals("Loyalty"))
            {
                pc.setLoyalty(childNode.extractNodeNumber());
                continue;
            }
            if(name.equals("Hidden"))
                pc.setHidden(childNode.extractNodeNumber());
        }

    }

    private void parseArmies(AdapterNode node)
    {
        int armies = node.childCount();
        for(int i = 0; i < armies; i++)
        {
            AdapterNode childNode = node.child(i);
            if(!childNode.getNodeName().equals("Army"))
                continue;
            Army army = new Army(childNode.extractAttributeNumber());
            parseArmy(childNode, army);
            if(army.getNation() <= 0)
                continue;
            if(this.primaryParse)
            {
                this.nation.addArmy(army);
                continue;
            }
            Army existingArmy = this.nation.findArmyByCommander(army.getCommander());
            if(existingArmy != null && army.getNation() != this.secondaryNation)
                continue;
            if(existingArmy != null)
                this.nation.removeArmy(existingArmy);
            this.nation.addArmy(army);
        }

    }

    private void parseArmy(AdapterNode node, Army army)
    {
        int children = node.childCount();
        for(int i = 0; i < children; i++)
        {
            AdapterNode childNode = node.child(i);
            String name = childNode.getNodeName();
            if(name.equals("Nation"))
            {
                army.setNation(childNode.extractNodeNumber());
                continue;
            }
            if(name.equals("TroopCount"))
            {
                army.setTroopAmount(childNode.extractNodeNumber());
                continue;
            }
            if(name.equals("Commander"))
            {
                army.setCommander(childNode.extractNodeString());
                continue;
            }
            if(name.equals("ExtraInfo"))
            {
                String info = childNode.extractNodeString();
                if(info != null)
                    army.setExtraInfo(info);
                continue;
            }
            if(name.equals("Navy"))
            {
                army.setNavy(childNode.extractNodeNumber());
                continue;
            }
            if(name.equals("CharsTravellingWith"))
                army.setCharactersWith(childNode.extractNodeString());
        }

    }

    private Integer extractItemNumber(String name)
    {
        try {
            Integer number;
            int spaceIndex = name.indexOf(' ');
            number = new Integer(name.substring(1, spaceIndex));
            return number;
        }
        catch (Exception ex) {
            return new Integer(-1);
        }
    }

    private String filename;
    private Document document;
    private Nation nation;
    private boolean primaryParse;
    private int secondaryNation;
}
