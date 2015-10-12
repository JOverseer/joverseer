package org.joverseer.tools.turnReport;

public class NationReport extends BaseReportObject {
	@Override
	public String getHtmlString() {
		return 	
			//appendTd(getNationStr()) +
		//appendTd(getModification()) +
			appendTd(getNationsStr()) +
				appendTd(getNotes());
		
	}
}
