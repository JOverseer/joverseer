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
	public ArrayList<Integer> getLosses(int lossOptimismFactor) {
		ArrayList<Integer> ret = new ArrayList<Integer>();
		for (String lossesRange : getLossesRanges()) {
			int l = getRangeAverage(lossesRange, lossOptimismFactor);
			ret.add(l);
		}
		return ret;
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
	
	public int getEffectiveLosses(int lossOptimismFactor) {
		double losses = 100;
		for (int li : getLosses(lossOptimismFactor)) {
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
	
	
	protected int getRangeAverage(String rangeString, int max, int lossOptimismFactor) {
    	if (rangeString.indexOf("-") > -1) {
	    	String[] parts = rangeString.split("-");
	    	try {
	    		int min = Integer.parseInt(parts[0]);
	    		max = Integer.parseInt(parts[1]);
	    		if (lossOptimismFactor == 0) {
	    			return (int)Math.round(((double)min + (double)max) / 2d);
	    		} else if (lossOptimismFactor == -1) {
	    			return min;
	    		} else if (lossOptimismFactor == 1) {
	    			return max;
	    		}
	    		return 0;
	    		
	    	}
	    	catch (Exception exc) {
	    		exc.printStackTrace();
	    	}
	    	return 0;
    	}
    	if (rangeString.endsWith("+")) {
    		String[] parts = rangeString.split("+");
	    	try {
	    		int min =Integer.parseInt(parts[0]);
	    		if (lossOptimismFactor == 0) {
	    			return (int)Math.round(((double)min + (double)max) / 2d);
	    		} else if (lossOptimismFactor == -1) {
	    			return min;
	    		} else if (lossOptimismFactor == 1) {
	    			return max;
	    		}
	    		return 0;
	    	}
	    	catch (Exception exc) {
	    		exc.printStackTrace();
	    	}
	    	return 0;
    	}
    	try {
    		return Integer.parseInt(rangeString);
    	}
    	catch (Exception exc) {
    		exc.printStackTrace();
    	}
    	return 0;
    }
    protected int getRangeAverage(String rangeString, int lossOptimismFactor) {
    	return getRangeAverage(rangeString, 100, lossOptimismFactor);
    }	
}
