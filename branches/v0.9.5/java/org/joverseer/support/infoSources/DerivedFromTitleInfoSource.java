package org.joverseer.support.infoSources;

public class DerivedFromTitleInfoSource extends TurnInfoSource {
	String title;
	
	public DerivedFromTitleInfoSource(String title) {
		super();
		this.title = title;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	
}
