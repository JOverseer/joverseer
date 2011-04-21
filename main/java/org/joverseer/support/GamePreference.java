package org.joverseer.support;

import java.util.prefs.Preferences;

import org.joverseer.game.Game;

/**
 * Implements a preference that primarily depends on the Game In other words,
 * the system first checks the active game for a value for the preference and
 * then checks the system preferences.
 * 
 * @author Marios Skounakis
 * 
 */

public class GamePreference {
	public static String getValueForPreference(String key, Class<?> clazz) {
		// try to get from game
		if (GameHolder.hasInitializedGame()) {
			Game game = GameHolder.instance().getGame();
			if (game.containsParameter(key)) {
				return game.getParameter(key);
			}
		}
		// try to get from prefs
		Preferences prefs = Preferences.userNodeForPackage(clazz);
		return prefs.get(key, null);
	}

	public static void setValueForPreference(String key, String value, Class<?> clazz) {
		// try to save with game
		if (GameHolder.hasInitializedGame()) {
			Game game = GameHolder.instance().getGame();
			game.setParameter(key, value);
			return;
		}
		// try to save with prefs
		Preferences prefs = Preferences.userNodeForPackage(clazz);
		prefs.put(key, value);
	}
}
