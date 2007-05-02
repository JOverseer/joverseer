package org.joverseer.support.infoSources;

public class RumorActionInfoSource extends InfoSource {
	String reports;
	
	public RumorActionInfoSource(String reports) {
		this.reports = reports;
	}

	public String getReports() {
		return reports;
	}

	public void setReports(String reports) {
		this.reports = reports;
	}
	
	
}
