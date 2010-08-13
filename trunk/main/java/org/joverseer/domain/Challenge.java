package org.joverseer.domain;

import org.joverseer.support.StringUtils;


/**
 * Stores the narration for a challenge from the pdf turn results.
 * 
 * @author Marios Skounakis
 *
 */
public class Challenge extends Encounter {

    /**
     * 
     */
    private static final long serialVersionUID = -8479597823662327575L;
    boolean parsed = false;
    String victor;
    String loser;
    String victorWounds;
    
    
    protected void parse() {
    	parsed = true;
    	String d = getCleanDescription(); 
	    int i = d.lastIndexOf('.');
		String[] sentences = d.substring(0, i).split("\\.");
		String ls = sentences[sentences.length-1].trim();
		String pls = sentences[sentences.length-2].trim();
		victor = StringUtils.getUniquePart(ls, "^", " was noted", false, false);
		if (victor == null) {
			if (d.contains(", but suffered no wounds")) {
				victor = StringUtils.getUniquePart(d, "Suddenly, ", " slew ", false, false);
				victorWounds = "none";
				loser = StringUtils.getUniquePart(d, " slew ", " with a ", false, false);
				return;
			}
		}
		victorWounds = StringUtils.getUniquePart(ls, " was noted to have suffered ", " wounds ", false, false);
		loser = StringUtils.getUniquePart(pls, "Finally, ", " fell", false, false);
		if (loser == null) {
			loser = StringUtils.getUniquePart(pls, "essence from ", "'s body", false, false);
		} 
		if (loser == null) {
			loser = StringUtils.getUniquePart(pls, "weapon into ", "'s body", false, false);
		}
    }

	public String getVictor() {
		parsed = false;
		if (!parsed) parse();
		return victor;
	}

	public String getLoser() {
		if (!parsed) parse();
		return loser;
	}

	public String getVictorWounds() {
		return victorWounds;
	}

	

    
    
}
