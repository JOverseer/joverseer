package org.joverseer.domain;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Stores the program's estimate about an army (as derived from sources such as combat narrations)
 * 
 * @author Marios Skounakis
 *
 */
public class ArmyEstimate implements Serializable, IHasMapLocation, IBelongsToNation {
	private static final long serialVersionUID = -3658619439763149556L;
	String commanderName;
	String commanderTitle;
	ArrayList<String> lossesDescriptions = new ArrayList<String>();
	ArrayList<String> lossesRanges = new ArrayList<String>();
	ArrayList<Integer> losses = new ArrayList<Integer>();
	Integer nationNo;
	
	String moraleDescription;
	String moraleRange;
	int morale;
	
	ArrayList<ArmyEstimateElement> regiments = new ArrayList<ArmyEstimateElement>();
	int hexNo;
	
	public String getCommanderName() {
		return commanderName;
	}
	public void setCommanderName(String commanderName) {
		this.commanderName = commanderName;
	}
	public ArrayList<String> getLossesDescriptions() {
		return lossesDescriptions;
	}
	public ArrayList<ArmyEstimateElement> getRegiments() {
		return regiments;
	}
	
	public ArmyEstimateElement getRegiment(ArmyElementType t) {
		for (ArmyEstimateElement aee : getRegiments()) {
			if (aee.getType().equals(t)) return aee;
		}
		return null;
	}
	
	public int getHexNo() {
		return hexNo;
	}
	public void setHexNo(int hexNo) {
		this.hexNo = hexNo;
	}
	public int getX() {
		return getHexNo() / 100;
	}
	public int getY() {
		return getHexNo() % 100;
	}
	public int getMorale() {
		return morale;
	}
	public void setMorale(int morale) {
		this.morale = morale;
	}
	public String getMoraleDescription() {
		return moraleDescription;
	}
	public void setMoraleDescription(String moraleDescription) {
		this.moraleDescription = moraleDescription;
	}
	public String getMoraleRange() {
		return moraleRange;
	}
	public void setMoraleRange(String moraleRange) {
		this.moraleRange = moraleRange;
	}
	public ArrayList<Integer> getLosses() {
		return losses;
	}
	public ArrayList<String> getLossesRanges() {
		return lossesRanges;
	}
	public String getCommanderTitle() {
		return commanderTitle;
	}
	public void setCommanderTitle(String commanderTitle) {
		this.commanderTitle = commanderTitle;
	}
	
	public int getEffectiveLosses() {
		double losses = 100;
		for (int li : getLosses()) {
			double l = li; 
			losses = losses * (100d - (double)l) / 100d;
		}
		return (int)losses;
	}
	public Integer getNationNo() {
		return nationNo;
	}
	public void setNationNo(Integer nationNo) {
		this.nationNo = nationNo;
	}
	
	
	
}
