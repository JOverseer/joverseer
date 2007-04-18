package org.joverseer.support.infoSources;

public class AgentActionInfoSource extends InfoSource {
	String reports;
	
	public AgentActionInfoSource(String reports) {
		this.reports = reports;
	}

	public String getReports() {
		return reports;
	}

	public void setReports(String reports) {
		this.reports = reports;
	}
	
	
}
