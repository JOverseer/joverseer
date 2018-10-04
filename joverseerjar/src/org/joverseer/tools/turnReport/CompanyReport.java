package org.joverseer.tools.turnReport;

import org.joverseer.domain.Character;
import org.joverseer.domain.Company;
import org.joverseer.game.Turn;
import org.joverseer.game.TurnElementsEnum;

public class CompanyReport extends BaseReportObject {
	public CompanyReport(ObjectModificationType modification,Company c)
	{
		super();
		this.setModification(modification);
		this.setHexNo(c.getHexNo());
		this.setName(c.getCommander());
	}
	public void noteMembers(Company c,Turn t) {
		this.appendNote("Members: " + c.getMemberStr());
		addNation(c.getCommander(), t);
		for (String m : c.getMembers())
			addNation(m, t);
	}
	protected void addNation(String characterName, Turn t) {
		Character c = (Character) t.getContainer(TurnElementsEnum.Character).findFirstByProperty("name", characterName);
		if (c != null)
			this.addNation(c.getNationNo());
	}

}
