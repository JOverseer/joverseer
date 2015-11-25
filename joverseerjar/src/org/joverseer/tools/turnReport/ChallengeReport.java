package org.joverseer.tools.turnReport;

import org.joverseer.domain.Challenge;
import org.joverseer.domain.Character;

public class ChallengeReport extends BaseReportObject {
	Challenge challenge;

	public Challenge getChallenge() {
		return this.challenge;
	}

	public void setChallenge(Challenge challenge) {
		this.challenge = challenge;
	}

	public ChallengeReport(Challenge challenge) {
		super();
		this.challenge = challenge;
		setHexNo(challenge.getHexNo());
	}

	@Override
	public String getLinks() {
		String str = super.getLinks();
		try {
			str += " <a href='http://event?challenge=" + Character.getIdFromName(this.challenge.getVictor()).replace(" ", "_") + "," + Character.getIdFromName(this.challenge.getLoser()).replace(" ", "_") + "'>Report</a>";
		} catch (Exception e) {

		}
		return str;
	}

	@Override
	public String getExtraInfo() {
		if (this.challenge.getVictorWounds() != null) {
			return "Victor's wounds: " + this.challenge.getVictorWounds();
		}
		return super.getExtraInfo();
	}

}
