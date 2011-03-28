package org.joverseer.preferences;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

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
	String prefix;

	public ArrayList<Preference> getAllPreferences() {
		return allPreferences;
	}

	public void setAllPreferences(ArrayList<Preference> allPreferences) {
		this.allPreferences = allPreferences;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public String getPreferenceValue(String preferenceKey) {
		for (Preference p : allPreferences) {
			if (p.getKey().equals(preferenceKey)) {
				return p.getValue(getPrefix());
			}
		}
		return "";
	}

	public void setPreferenceValue(String preferenceKey, String value) {
		for (Preference p : allPreferences) {
			if (p.getKey().equals(preferenceKey)) {
				p.setValue(getPrefix(), value);
			}
		}
	}

	public void clearCaches() {
		for (Preference p : allPreferences) {
			p.clearCache();
		}
	}

	public static PreferenceRegistry instance() {
		return (PreferenceRegistry) Application.instance().getApplicationContext().getBean("preferenceRegistry");
	}

	public ArrayList<Preference> getPreferencesSortedByGroup() {
		ArrayList<Preference> ret = new ArrayList<Preference>();
		for (Preference p : allPreferences) {
			ret.add(p);
		}

		Collections.sort(ret, new Comparator<Preference>() {
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
