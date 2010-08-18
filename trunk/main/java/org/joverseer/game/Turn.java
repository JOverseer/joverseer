package org.joverseer.game;

import org.joverseer.domain.Army;
import org.joverseer.domain.Challenge;
import org.joverseer.domain.Encounter;
import org.joverseer.domain.NationMessage;
import org.joverseer.domain.NationRelations;
import org.joverseer.domain.PopulationCenter;
import org.joverseer.domain.SeasonEnum;
import org.joverseer.support.Cloner;
import org.joverseer.support.Container;
import org.joverseer.domain.Character;

import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.io.Serializable;

/**
 * Basic Turn structure. It stores all the information that pertains to a specific turn.
 * 
 * Turn information is stored in a hashmap of containers, one for each TurnElementEnum item.
 * 
 * @author Marios Skounakis
 *
 */
public class Turn implements Serializable {
    private static final long serialVersionUID = 8759609718974408867L;
    int turnNo;
    Date turnDate;
    SeasonEnum season;
    
    Hashtable<TurnElementsEnum, Container> containers = new Hashtable<TurnElementsEnum, Container>();

    public int getTurnNo() {
        return turnNo;
    }

    public void setTurnNo(int turnNo) {
        this.turnNo = turnNo;
    }

    public Hashtable<TurnElementsEnum, Container> getContainers() {
        return containers;
    }

    public void setContainers(Hashtable<TurnElementsEnum, Container> containers) {
        this.containers = containers;
    }

    public Container getContainer(TurnElementsEnum turnElement) {
        Container c = getContainers().get(turnElement);
        if (c == null) {
            c = new Container();
            getContainers().put(turnElement, c);
        }
        return c;
    }

    
    public SeasonEnum getSeason() {
        return season;
    }

    
    public void setSeason(SeasonEnum season) {
        this.season = season;
    }

    
    public Date getTurnDate() {
        return turnDate;
    }

    
    public void setTurnDate(Date turnDate) {
        this.turnDate = turnDate;
    }
    

    public Turn clone() {
    	return (Turn)Cloner.clone(this);
    }
    
    public ArrayList<Character> getAllCharacters() {
    	return (ArrayList<Character>)getContainer(TurnElementsEnum.Character).getItems();
    }
    
    public Character getCharByName(String name) {
    	return (Character)getContainer(TurnElementsEnum.Character).findFirstByProperty("name", name);
    }
    
    public Character getCharById(String id) {
    	return (Character)getContainer(TurnElementsEnum.Character).findFirstByProperty("id", id);
    }
    
    public PopulationCenter getCapital(int nationNo) {
    	return (PopulationCenter)getContainer(TurnElementsEnum.PopulationCenter).findFirstByProperties(new String[]{"nationNo", "capital"}, new Object[]{nationNo, true});
    }
    
    public PopulationCenter getPopCenter(int hexNo) {
    	return (PopulationCenter)getContainer(TurnElementsEnum.PopulationCenter).findFirstByProperty("hexNo", hexNo);
    }

    public PopulationCenter getPopCenter(String name) {
    	return (PopulationCenter)getContainer(TurnElementsEnum.PopulationCenter).findFirstByProperty("name", name);
    }
    
    public ArrayList<Character> getCharacters(int nationNo) {
    	return (ArrayList<Character>)getContainer(TurnElementsEnum.Character).findAllByProperty("nationNo", nationNo);
    }
    
    public ArrayList<Character> getCharactersAtHex(int hexNo) {
    	return (ArrayList<Character>)getContainer(TurnElementsEnum.Character).findAllByProperty("hexNo", hexNo);
    }
    
    public ArrayList<PopulationCenter> getPopCenters(int nationNo) {
    	return (ArrayList<PopulationCenter>)getContainer(TurnElementsEnum.PopulationCenter).findAllByProperty("nationNo", nationNo);
    }
    
    public ArrayList<Army> getArmies(int hexNo) {
    	String hexNoStr = String.valueOf(hexNo);
    	if (hexNo < 1000) hexNoStr = "0" + hexNoStr;
    	return (ArrayList<Army>)getContainer(TurnElementsEnum.Army).findAllByProperty("hexNo", hexNoStr);
    }
    
    public Army getArmy(String commander) {
    	return (Army)getContainer(TurnElementsEnum.Army).findFirstByProperty("commanderName", commander);
    }
    
    public Encounter getEncounter(int hexNo, String character) {
    	return (Encounter)getContainer(TurnElementsEnum.Encounter).findFirstByProperties(new String[]{"hexNo", "character"}, new Object[]{hexNo, character});
    }
    
    public Challenge findChallenge(String character) {
    	for (Challenge c : (ArrayList<Challenge>)getContainer(TurnElementsEnum.Challenge).getItems()) {
    		if (character.equals(c.getVictor()) || character.equals(c.getLoser())) return c;
    	}
    	return null;
    }
    
    public ArrayList<NationMessage> getNationMessages() {
    	return (ArrayList<NationMessage>)getContainer(TurnElementsEnum.NationMessage).getItems();
    }
    
    public ArrayList<NationMessage> getNationMessages(int nationNo) {
    	return (ArrayList<NationMessage>)getContainer(TurnElementsEnum.NationMessage).findAllByProperty("nationNo", nationNo);
    }
    
    public NationRelations getNationRelations(int nationNo) {
    	return (NationRelations)getContainer(TurnElementsEnum.NationRelation).findFirstByProperty("nationNo", nationNo);
    }
}
