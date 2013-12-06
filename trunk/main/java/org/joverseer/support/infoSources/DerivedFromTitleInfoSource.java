package org.joverseer.support.infoSources;

/**
 * Information about character stats derived from the character's title.
 * 
 * @author Marios Skounakis
 *
 */
public class DerivedFromTitleInfoSource extends TurnInfoSource {
	private static final long serialVersionUID = -294276048631699957L;
	String title;
	
	public DerivedFromTitleInfoSource(String title) {
		super();
		this.title = title;
	}

	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	@Override
	public String toString() {
    	return "Title";
    }
}
