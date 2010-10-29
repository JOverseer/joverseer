package org.joverseer.tools.turnReport;

import org.joverseer.domain.Challenge;
import org.joverseer.domain.Character;

public class ChallengeReport extends BaseReportObject {
	Challenge challenge;

	public Challenge getChallenge() {
		return challenge;
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
			str += " <a href='http://event?challenge=" + Character.getIdFromName(challenge.getVictor()).replace(" ", "_") + "," + Character.getIdFromName(challenge.getLoser()).replace(" ", "_") + "'>Report</a>";
		} catch (Exception e) {

		}
		return str;
	}

	@Override
	public String getExtraInfo() {
		if (challenge.getVictorWounds() != null) {
			return "Victor's wounds: " + challenge.getVictorWounds();
		}
		return super.getExtraInfo();
	}

}
