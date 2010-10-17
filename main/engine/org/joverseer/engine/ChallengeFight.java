package org.joverseer.engine;

import java.util.ArrayList;

import org.joverseer.domain.Character;
import org.springframework.util.StringUtils;

public class ChallengeFight {
	Character c1;
	Character c2;
	int hexNo;
	ArrayList<String> lines = new ArrayList<String>();
	public ChallengeFight(Character c1, Character c2, int hexNo) {
		super();
		this.c1 = c1;
		this.c2 = c2;
		this.hexNo = hexNo;
	}
	
	
	
	public int getHexNo() {
		return hexNo;
	}



	public void setHexNo(int hexNo) {
		this.hexNo = hexNo;
	}



	public Character getC1() {
		return c1;
	}



	public Character getC2() {
		return c2;
	}



	public void run() {
		if (c1.getHealth() == null) {
			c1.setHealth(0);
		}
		if (c2.getHealth() == null) {
			c2.setHealth(0);
		}
		do {
			runRound();
		} while (c1.getHealth() > 0 && c2.getHealth() > 0);
	}
	
	public void runRound() {
		int a1 = Randomizer.roll(1, 100);
		int a2 = Randomizer.roll(1, 100);
		int b1 = 0;
		int b2 = 0;
		if (Randomizer.fumble(a1)) {
			b2 += Randomizer.roll(1, 100);
		} else if (Randomizer.luckyStrike(a1)) {
			b1 += Randomizer.roll(1, 100);
		}
		if (Randomizer.fumble(a2)) {
			b1 += Randomizer.roll(1, 100);
		} else if (Randomizer.luckyStrike(a2)) {
			b2 += Randomizer.roll(1, 100);
		}
		lines.add(c1.getName() + " rolled " + a1 + " " + b1);
		lines.add(c2.getName() + " rolled " + a2 + " " + b2);
		int t1 = a1 + b1 + c1.getChallenge();
		int t2 = a2 + b2 + c2.getChallenge();
		
		int damage = Math.abs(t1 - t2);
		int h1 = Math.max(t2 - t1, 0);
		int h2 = Math.max(t1 - t2, 0);
		
		int nh1 = Math.max(c1.getHealth() - h1, 0);
		int nh2 = Math.max(c2.getHealth() - h2, 0);
		int damage1 = c2.getHealth() - nh2;
		int damage2 = c1.getHealth() - nh1;
		
		c1.setHealth(nh1);
		c2.setHealth(nh2);
		
		if (t1 > t2) {
			lines.add(c1.getName() + " inflicts " + damage1 + " damage to " + c2.getName() + ".");
			if (c2.getHealth() == 0) {
				lines.add(c2.getName() + " died.");
			}
		} else if (t1 < t2) {
			lines.add(c2.getName() + " inflicts " + damage2 + " damage to " + c1.getName() + ".");
			if (c1.getHealth() == 0) {
				lines.add(c1.getName() + " died.");
			}
		} else {
			lines.add(c1.getName() + " and " + c2.getName() + " attack each other without success.");
		}
		
	}
	
	public String getDescription() {
		return StringUtils.collectionToDelimitedString(lines, "\n");
	}
}
