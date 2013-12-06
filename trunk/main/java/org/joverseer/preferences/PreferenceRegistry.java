package org.joverseer.preferences;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import org.springframework.richclient.application.Application;

import com.jidesoft.spring.richclient.docking.JideApplicationLifecycleAdvisor;

/**
 * The container for all preferences. It currently implements the singleton
 * pattern.
 * 
 * @author Marios Skounakis
 * 
 */
public class PreferenceRegistry {
	ArrayList<Preference> allPreferences = new ArrayList<Preference>();
	HashMap<String, Preference> preferenceMap = new HashMap<String, Preference>();
	String prefix;

	public ArrayList<Preference> getAllPreferences() {
		return this.allPreferences;
	}

	public void setAllPreferences(ArrayList<Preference> allPreferences) {
		this.allPreferences = allPreferences;
		this.preferenceMap.clear();
		for (Preference p : allPreferences) {
			this.preferenceMap.put(p.getKey(), p);
		}
	}

	public String getPrefix() {
		return this.prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public String getPreferenceValue(String preferenceKey) {
		if (this.preferenceMap.containsKey(preferenceKey)) {
			return this.preferenceMap.get(preferenceKey).getValue(getPrefix());
		}
		return "";
	}

	public void setPreferenceValue(String preferenceKey, String value) {
		for (Preference p : this.allPreferences) {
			if (p.getKey().equals(preferenceKey)) {
				p.setValue(getPrefix(), value);
			}
		}
	}

	public void clearCaches() {
		for (Preference p : this.allPreferences) {
			p.clearCache();
		}
	}

	public static PreferenceRegistry instance() {
		return (PreferenceRegistry) Application.instance().getApplicationContext().getBean("preferenceRegistry");
	}

	public ArrayList<Preference> getPreferencesSortedByGroup() {
		ArrayList<Preference> ret = new ArrayList<Preference>();
		for (Preference p : this.allPreferences) {
			ret.add(p);
		}

		Collections.sort(ret, new Comparator<Preference>() {
			@Override
			public int compare(Preference p1, Preference p2) {
				return (p1.getGroup() + "." + p1.getDescription()).compareTo(p2.getGroup() + "." + p2.getDescription());
			}
		});
		return ret;
	}

	public boolean advancedPreferencesOn() {
		return JideApplicationLifecycleAdvisor.devOption;
	}

}
