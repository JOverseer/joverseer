package org.joverseer.tools.turnReport;

import org.joverseer.domain.Character;
import org.joverseer.domain.Encounter;

public class EncounterReport extends BaseReportObject {
	Encounter encounter;
	
	
	
	public Encounter getEncounter() {
		return this.encounter;
	}



	public void setEncounter(Encounter encounter) {
		this.encounter = encounter;
	}



	@Override
	public String getLinks() {
		String ret = super.getLinks();
		if (getEncounter() != null && getEncounter().getCharacter() != null) ret+= " <a href='http://event?enc=" + getHexNo() + "," + Character.getIdFromName(getEncounter().getCharacter()).replace(" ", "_") + "'>Report</a>";
		return ret;
	}

	@Override
	public String getHtmlString() {
		return 	
			//appendTd(getNationStr()) +
		//appendTd(getModification()) +
			appendTd(getNationsStr()) +
				appendTd(getName()) + 
				appendTd(getHexNoStr()) +
				appendTd(getNotes()) +
				appendTd(getLinks());
		
	}
}
