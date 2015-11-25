package org.joverseer.preferences;

import java.util.prefs.Preferences;

import org.joverseer.support.GameHolder;
import org.joverseer.ui.JOverseerJIDEClient;
import org.joverseer.ui.support.JOverseerEvent;
import org.springframework.richclient.application.Application;

/**
 * Basic class for user preferences.
 * 
 * It contains:
 * - the preference key with which the preference is accessed from the code
 * - the preference type, which defines how the preference is assigned it's value (serves as a guide for the gui layer)
 * - the preference user-friendly description TODO: should move to message source
 * - the preference's domain (if applicable), array of PreferenceValue objects
 * - the preferences cached value, so that we don't always have to go to the system preferences to retrieve its value
 * - the lifecycle event that is thrown when the preference's value is changed TODO: Maybe should be changed to a list
 * - the preference's group for grouping preferences, currently also serves as user-friendly description of the group for the gui layer TODO: make this a key and move user-friendly description to message source
 * - the preference's default value
 * 
 * @author Marios Skounakis
 *
 */
public class Preference {
	public static String TYPE_DROPDOWN = "dropDown";
	public static String TYPE_TEXT = "text";

    String key;
    String type = TYPE_DROPDOWN;
    String description;
    PreferenceValue[] domain;
    String valueCache = null;
    String lifecycleEvent;
    String group;
    String defaultValue;
    
    public String getGroup() {
        return this.group;
    }
    
    public void setGroup(String group) {
        this.group = group;
    }

    public String getDescription() {
        return this.description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public PreferenceValue[] getDomain() {
        return this.domain;
    }
    
    public void setDomain(PreferenceValue[] domain) {
        this.domain = domain;
    }
    
    public String getKey() {
        return this.key;
    }
    
    public void setKey(String key) {
        this.key = key;
    }
    
    public String getLifecycleEvent() {
        return this.lifecycleEvent;
    }

    
    public void setLifecycleEvent(String lifecycleEvent) {
        this.lifecycleEvent = lifecycleEvent;
    }

    public String getValue(String prefix) {
        if (this.valueCache != null) {
            return this.valueCache;
        }
        Preferences prefs = Preferences.userNodeForPackage(JOverseerJIDEClient.class);
        String value = prefs.get(prefix + "." + this.key, null);
        if (value != null) {
        	this.valueCache = value;
        	
        	return value;
        } else {
        	this.valueCache = getDefaultValue();
        	setValue(prefix, this.valueCache);
            return getDefaultValue();
        }
    }
    
    public void setValue(String prefix, String value) {
        Preferences prefs = Preferences.userNodeForPackage(JOverseerJIDEClient.class);
        prefs.put(prefix + "." + this.key, value);
        clearCache();
        if (getLifecycleEvent() != null && GameHolder.hasInitializedGame()) {
            Application.instance().getApplicationContext().publishEvent(
                    new JOverseerEvent(getLifecycleEvent(), this, this));
        }
    }
    
    public void clearCache() {
        this.valueCache = null;
    }

    
    public String getDefaultValue() {
        return this.defaultValue;
    }

    
    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}
    
    
    
}
