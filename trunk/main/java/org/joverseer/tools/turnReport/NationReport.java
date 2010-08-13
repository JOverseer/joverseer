package org.joverseer.tools.turnReport;

public class NationReport extends BaseReportObject {
	public String getHtmlString() {
		return 	
			//appendTd(getNationStr()) +
		//appendTd(getModification()) +
			appendTd(getNationsStr()) +
				appendTd(getNotes());
		
	}
}
