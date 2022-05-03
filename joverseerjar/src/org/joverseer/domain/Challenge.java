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
		this.parsed = true;
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
				this.victorWounds = StringUtils.getUniquePart(ls, " was noted to have suffered ", " wounds ", false, false);
				if (this.victorWounds == null) {
					this.victorWounds = StringUtils.getUniquePart(ls, "but suffered ", "wounds", false, false);
				}
				// start from the end and find a sentence that mentions both
				// chars
				for (int j = sentences.length - 1; j >= 0; j--) {
					int ci1 = sentences[j].indexOf(char1);
					int ci2 = sentences[j].indexOf(char2);
					// there are inconsistencies as to who is victor or loser:
					// in general the last mentioned character dies.
					// unless the text is 'loser fell'
					
					// Finally, Amroth fell to a savage barrage of spells by The Blue Wizard
					// Finally, Lorenet fell to a savage barrage of blows by Throkmaw. Throkmaw was noted to have suffered no wounds in the fight.
					// Suddenly, Treebeard slew Lucki with a swift feint and thrust, but suffered no wounds
					
					if ((ci1 > -1) && (ci2 > -1)) { // both mentioned
						int fell = sentences[j].indexOf(" fell ");
						if (fell > -1) {
							if (ci1 < fell) {
								this.victor = char2;
								this.loser = char1;
							} else {
								this.victor = char1;
								this.loser = char2;
							}
							return;
						}
						fell = sentences[j].indexOf(" felled ");
						if (fell > -1) {
							if (ci1 > fell) {
								this.victor = char2;
								this.loser = char1;
								return;
							}
						}
						this.victor = char1;
						this.loser = char2;
						return;
					}
				}
				// if we get here then try:
				//Ringlin relinquished his spirit and gasped his last.
				ls = sentences[sentences.length - 2].trim();
				this.loser = StringUtils.getUniquePart(ls, "Finally, ", " relinquished", false, false);
				if (this.loser != null) {
					this.victor = (this.loser.equalsIgnoreCase(char1)) ? char2 : char1;
					return;
				}
			}
		}

	}

	public String getVictor() {
		this.parsed = false;
		if (!this.parsed)
			parse();
		return this.victor;
	}

	public String getLoser() {
		if (!this.parsed)
			parse();
		return this.loser;
	}

	public String getVictorWounds() {
		return this.victorWounds;
	}

}
