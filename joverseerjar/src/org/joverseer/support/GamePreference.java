package org.joverseer.support;

import java.util.prefs.Preferences;

import org.joverseer.game.Game;
import org.joverseer.ui.command.OpenGameDirTree;
import org.joverseer.ui.views.ExportOrdersForm;

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

	public static class LastDirTuple {
		public String lastDir;
		public boolean usesImportDir;
		public LastDirTuple() {
			this.lastDir = "";
			this.usesImportDir = false;
		}
	}
	/**
	 * @param orderPathPref
	 * @return
	 */
	public static LastDirTuple determineLastDir(String orderPathPref) {
		final String DEFAULT_PROPERTY = "importDir"; 
		LastDirTuple result= new LastDirTuple(); 
		if (DEFAULT_PROPERTY.equals(orderPathPref)) {
			result.lastDir =GamePreference.getValueForPreference(DEFAULT_PROPERTY, OpenGameDirTree.class);
			result.usesImportDir = true;
		} else {
			result.lastDir = GamePreference.getValueForPreference("orderDir", ExportOrdersForm.class);
		}
		if (result.lastDir == null) {
			result.lastDir = GamePreference.getValueForPreference(DEFAULT_PROPERTY, OpenGameDirTree.class);
		}
		return result;
	}

}
