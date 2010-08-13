package org.joverseer.tools.turnReport;

public class TransportReport extends BaseReportObject {
	public String getHtmlString() {
		return 	
			//appendTd(getNationStr()) +
		//appendTd(getModification()) +
			appendTd(getNationsStr()) +
				appendTd(getHexNoStr()) +
				appendTd(getNotes()) +
				appendTd(getLinks());
		
	}
}
