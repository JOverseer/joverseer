package org.joverseer.game;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;

import org.joverseer.domain.Army;
import org.joverseer.domain.Artifact;
import org.joverseer.domain.Challenge;
import org.joverseer.domain.Character;
import org.joverseer.domain.Combat;
import org.joverseer.domain.Company;
import org.joverseer.domain.Encounter;
import org.joverseer.domain.NationEconomy;
import org.joverseer.domain.NationMessage;
import org.joverseer.domain.NationRelations;
import org.joverseer.domain.Note;
import org.joverseer.domain.PlayerInfo;
import org.joverseer.domain.PopulationCenter;
import org.joverseer.domain.ProductEnum;
import org.joverseer.domain.ProductPrice;
import org.joverseer.domain.SeasonEnum;
import org.joverseer.support.Cloner;
import org.joverseer.support.Container;
import org.joverseer.ui.domain.mapItems.AbstractMapItem;

/**
 * Basic Turn structure. It stores all the information that pertains to a
 * specific turn.
 * 
 * Turn information is stored in a hashmap of containers, one for each
 * TurnElementEnum item.
 * 
 * @author Marios Skounakis
 * 
 */
public class Turn implements Serializable {
	private static final long serialVersionUID = 8759609718974408867L;
	int turnNo;
	Date turnDate;
	SeasonEnum season;

	Hashtable<TurnElementsEnum, Container<?>> containers = new Hashtable<TurnElementsEnum, Container<?>>();

	public int getTurnNo() {
		return turnNo;
	}

	public void setTurnNo(int turnNo) {
		this.turnNo = turnNo;
	}

	public Hashtable<TurnElementsEnum, Container<?>> getContainers() {
		return containers;
	}

	public void setContainers(Hashtable<TurnElementsEnum, Container<?>> containers) {
		this.containers = containers;
	}

	public <T> Container<T> getContainerGeneric(TurnElementsEnum turnElement) {
		Container<T> c = (Container<T>) getContainers().get(turnElement);
		if (c == null) {
			c = new Container<T>();
			getContainers().put(turnElement, c);
		}
		return c;
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

	@Override
	public Turn clone() {
		return (Turn) Cloner.clone(this);
	}

	public Container<Character> getCharacters() {
		return getContainerGeneric(TurnElementsEnum.Character);
	}

	public Container<Army> getArmies() {
		return getContainerGeneric(TurnElementsEnum.Army);
	}

	public Container<PopulationCenter> getPopulationCenters() {
		return getContainerGeneric(TurnElementsEnum.PopulationCenter);
	}

	public Container<NationRelations> getNationRelations() {
		return getContainerGeneric(TurnElementsEnum.NationRelation);
	}

	public Container<NationMessage> getNationMessages() {
		return getContainerGeneric(TurnElementsEnum.NationMessage);
	}

	public Container<Company> getCompanies() {
		return getContainerGeneric(TurnElementsEnum.Company);
	}

	public Container<Encounter> getEncounters() {
		return getContainerGeneric(TurnElementsEnum.Encounter);
	}

	public Container<Combat> getCombats() {
		return getContainerGeneric(TurnElementsEnum.Combat);
	}

	public Container<Artifact> getArtifacts() {
		return getContainerGeneric(TurnElementsEnum.Artifact);
	}

	public Container<Challenge> getChallenges() {
		return getContainerGeneric(TurnElementsEnum.Challenge);
	}

	public Container<Note> getNotes() {
		return getContainerGeneric(TurnElementsEnum.Notes);
	}

	public Container<NationEconomy> getNationEconomies() {
		return getContainerGeneric(TurnElementsEnum.NationEconomy);
	}

	public Container<AbstractMapItem> getMapItems() {
		return getContainerGeneric(TurnElementsEnum.MapItem);
	}

	public Container<ProductPrice> getProductPrices() {
		return getContainerGeneric(TurnElementsEnum.ProductPrice);
	}

	public Container<PlayerInfo> getPlayerInfo() {
		return getContainerGeneric(TurnElementsEnum.PlayerInfo);
	}

	public ArrayList<Character> getAllCharacters() {
		return getContainer(TurnElementsEnum.Character).getItems();
	}

	public Character getCharByName(String name) {
		return (Character) getContainer(TurnElementsEnum.Character).findFirstByProperty("name", name);
	}

	public Character getCharById(String id) {
		return (Character) getContainer(TurnElementsEnum.Character).findFirstByProperty("id", id);
	}

	public PopulationCenter getCapital(int nationNo) {
		return (PopulationCenter) getContainer(TurnElementsEnum.PopulationCenter).findFirstByProperties(new String[] { "nationNo", "capital" }, new Object[] { nationNo, true });
	}

	public PopulationCenter getPopCenter(int hexNo) {
		return (PopulationCenter) getContainer(TurnElementsEnum.PopulationCenter).findFirstByProperty("hexNo", hexNo);
	}

	public PopulationCenter getPopCenter(String name) {
		return (PopulationCenter) getContainer(TurnElementsEnum.PopulationCenter).findFirstByProperty("name", name);
	}

	public ArrayList<Character> getCharacters(int nationNo) {
		return getContainer(TurnElementsEnum.Character).findAllByProperty("nationNo", nationNo);
	}

	public ArrayList<Character> getCharactersAtHex(int hexNo) {
		return getContainer(TurnElementsEnum.Character).findAllByProperty("hexNo", hexNo);
	}

	public ArrayList<PopulationCenter> getPopCenters(int nationNo) {
		return getContainer(TurnElementsEnum.PopulationCenter).findAllByProperty("nationNo", nationNo);
	}

	public ArrayList<Army> getArmies(int hexNo) {
		String hexNoStr = String.valueOf(hexNo);
		if (hexNo < 1000)
			hexNoStr = "0" + hexNoStr;
		return getContainer(TurnElementsEnum.Army).findAllByProperty("hexNo", hexNoStr);
	}

	public Army getArmy(String commander) {
		return (Army) getContainer(TurnElementsEnum.Army).findFirstByProperty("commanderName", commander);
	}

	public Encounter getEncounter(int hexNo, String character) {
		return (Encounter) getContainer(TurnElementsEnum.Encounter).findFirstByProperties(new String[] { "hexNo", "character" }, new Object[] { hexNo, character });
	}

	public ArrayList<Encounter> getEncounters(String character) {
		return getEncounters().findAllByProperties(new String[] { "character" }, new Object[] { character });
	}

	public Challenge findChallenge(String character) {
		for (Challenge c : getChallenges()) {
			if (character.equals(c.getVictor()) || character.equals(c.getLoser()))
				return c;
		}
		return null;
	}
	
	public ProductPrice getProductPrice(ProductEnum product) {
		return getProductPrices().findFirstByProperty("product", product);
	}

	public ArrayList<NationMessage> getAllNationMessages() {
		return getContainer(TurnElementsEnum.NationMessage).getItems();
	}

	public ArrayList<NationMessage> getNationMessages(int nationNo) {
		return getContainer(TurnElementsEnum.NationMessage).findAllByProperty("nationNo", nationNo);
	}

	public NationRelations getNationRelations(int nationNo) {
		return (NationRelations) getContainer(TurnElementsEnum.NationRelation).findFirstByProperty("nationNo", nationNo);
	}
}
