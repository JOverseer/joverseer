package org.joverseer.engine;

public class Randomizer {
	static boolean autoSuccess = false;
	
	
	
	public static boolean isAutoSuccess() {
		return autoSuccess;
	}

	public static void setAutoSuccess(boolean autoSuccess) {
		Randomizer.autoSuccess = autoSuccess;
	}

	public static int roll() {
		if (autoSuccess) return 100;
		return (int)(Math.random() * 100) + 1;
	}
	
	public static boolean fumble(int roll) {
		return roll <= 5;
	}
	
	public static boolean luckyStrike(int roll) {
		return roll > 95;
	}
	
	public static int roll(int modifier) {
		int roll = roll();
		if (fumble(roll)) return 0 + Math.min(0, modifier);
		if (luckyStrike(roll)) return 100 + Math.max(0, modifier);
		return roll + modifier;
	}
	
	public static boolean success(int roll) {
		return roll > 100;
	}
	
	public static int roll(int min, int max) {
		return (int)(Math.random() * (max - min)) + min;
	}
	
	public static int skillIncrease(int currentSkill, int difficulty, int max) {
		int r = (int)(Math.random() * max) + 1;
		return r;
	}
	
	public static int getModifier(int skillRank, OrderDifficultyEnum difficulty) {
		if (difficulty.equals(OrderDifficultyEnum.Easy)) {
			if (skillRank < 10) return 5;
			return 30 + skillRank;
		} else if (difficulty.equals(OrderDifficultyEnum.Average)) {
			return skillRank;  
		} else if (difficulty.equals(OrderDifficultyEnum.Hard)) {
			return skillRank - 30;
		}
		return 0;
	}
	
	
}
