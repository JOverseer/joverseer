package org.joverseer.support.infoSources;

/**
 * Information from a rumor action
 * 
 * TODO: Check if this is being used and refine comments
 * 
 * @author Marios Skounakis
 *
 */
public class RumorActionInfoSource extends InfoSource {
	String reports;
	
	public RumorActionInfoSource(String reports) {
		this.reports = reports;
	}

	public String getReports() {
		return this.reports;
	}

	public void setReports(String reports) {
		this.reports = reports;
	}
	
	@Override
	public String toString() {
    	return "Rumor";
    }
}
