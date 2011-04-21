package org.joverseer.domain;

import org.joverseer.support.StringUtils;

/**
 * Stores the narration for a challenge from the pdf turn results.
 * 
 * @author Marios Skounakis
 * 
 */
public class Challenge extends Encounter {

	/**
     * 
     */
	private static final long serialVersionUID = -8479597823662327575L;
	boolean parsed = false;
	String victor;
	String loser;
	String victorWounds;

	protected void parse() {
		parsed = true;
		String d = getCleanDescription();
		int i = Math.max(d.lastIndexOf('.'), d.lastIndexOf('!'));
		String[] sentences = d.substring(0, i).split("[\\.\\!]");
		if (sentences.length > 4) {
			int si = 0;
			if (sentences[0].endsWith(String.valueOf(getHexNo()))) {
				si = 1;
			}
			// 3rd sentence lists the attacker
			String char1 = StringUtils.getUniquePart(sentences[si + 2], ", ", ",", false, false);
			// 4th sentence may list the defender or the attacker's artifact
			String char2 = StringUtils.getUniquePart(sentences[si + 3], "In answer, ", ",", false, false);
			if (char2 == null) {
				// 5th sentence will list the defender if 4th listed the
				// attacker's artifact
				char2 = StringUtils.getUniquePart(sentences[si + 4], "In answer, ", ",", false, false);
			}
			if (char1 != null && char2 != null) {
				// last sentence lists the victor's wounds
				String ls = sentences[sentences.length - 1].trim();
				victorWounds = StringUtils.getUniquePart(ls, " was noted to have suffered ", " wounds ", false, false);
				if (victorWounds == null) {
					victorWounds = StringUtils.getUniquePart(ls, "but suffered ", "wounds", false, false);
				}
				// start from the end and find a sentence that mentions both
				// chars
				for (int j = sentences.length - 1; j >= 0; j--) {
					int ci1 = sentences[j].indexOf(char1);
					int ci2 = sentences[j].indexOf(char2);
					// the one mentioned first is the victor, the other the
					// loser
					if (ci1 > -1 && ci2 > -1) {
						if (ci1 > ci2) {
							victor = char2;
							loser = char1;
						} else {
							victor = char1;
							loser = char2;
						}
						return;
					}
				}
			}
		}

	}

	public String getVictor() {
		parsed = false;
		if (!parsed)
			parse();
		return victor;
	}

	public String getLoser() {
		if (!parsed)
			parse();
		return loser;
	}

	public String getVictorWounds() {
		return victorWounds;
	}

}
