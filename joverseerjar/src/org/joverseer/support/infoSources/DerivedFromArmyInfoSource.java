package org.joverseer.support.infoSources;


/**
 * Represents items derived from the existence of an army (basically army commanders
 * that are stored as characters when derived from an army with a known army commander name).
 * 
 * @author Marios Skounakis
 *
 */
public class DerivedFromArmyInfoSource extends InfoSource {
	private static final long serialVersionUID = -5497810597101255426L;
	
    @Override
	public String toString() {
    	return "Army/navy commander";
    }

}
