package org.joverseer.ui.domain;

import org.joverseer.domain.Company;
import org.joverseer.domain.Character;
import org.joverseer.domain.IBelongsToNation;
import org.joverseer.domain.IHasMapLocation;
import org.joverseer.game.Game;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.metadata.domain.Nation;
import org.joverseer.preferences.PreferenceRegistry;
import org.joverseer.support.GameHolder;

public class CompanyWrapper implements IHasMapLocation, IBelongsToNation {
	Company company;
	
	public CompanyWrapper(Company company) {
		super();
		this.company = company;
	}

	public Integer getNationNo() {
		Character leader = (Character)GameHolder.instance().getGame().getTurn().getContainer(TurnElementsEnum.Character).findFirstByProperty("name", company.getCommander());
		if (leader == null) return null;
		return leader.getNationNo();
	}

	public void setNationNo(Integer no) {
	}

	public int getX() {
		return company.getX();
	}

	public int getY() {
		return company.getY();	
	}
	
	public String getMemberStr() {
		String txt = "";
		Game g = GameHolder.instance().getGame();
		for (String ch : company.getMembers()) {
			ch = ch.replace("\n", "");
			String memberStr = ch;
			Character member = (Character)g.getTurn().getContainer(TurnElementsEnum.Character).findFirstByProperty("name", ch);
			if (member != null && member.getNationNo() > 0) {
                            String pval = PreferenceRegistry.instance().getPreferenceValue("listviews.showNationAs");
                            if (pval.equals("number")) {
                                memberStr += " (" + member.getNationNo() + ")";
                            } else {
                                Nation nat = g.getMetadata().getNationByNum(member.getNationNo());
                                if (nat != null) {
                                	memberStr += " (" + nat.getShortName() + ")";
                                }
                            }
			}
			txt += (txt.equals("") ? "" : ", ") + memberStr;
		}
		return txt;
	}
	
	public int getHexNo() {
		return company.getHexNo();
	}
	
	public String getCommander() {
		return company.getCommander();
	}
	

}
