package org.joverseer.preferences;

import java.util.prefs.Preferences;

import org.joverseer.ui.JOverseerClient;
import org.joverseer.ui.LifecycleEventsEnum;
import org.joverseer.ui.support.JOverseerEvent;
import org.springframework.richclient.application.Application;


public class Preference {
    String key;
    String description;
    PreferenceValue[] domain;
    String valueCache = null;
    String lifecycleEvent;
    String group;
    String defaultValue;
    
    public String getGroup() {
        return group;
    }
    
    public void setGroup(String group) {
        this.group = group;
    }

    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public PreferenceValue[] getDomain() {
        return domain;
    }
    
    public void setDomain(PreferenceValue[] domain) {
        this.domain = domain;
    }
    
    public String getKey() {
        return key;
    }
    
    public void setKey(String key) {
        this.key = key;
    }
    
    public String getLifecycleEvent() {
        return lifecycleEvent;
    }

    
    public void setLifecycleEvent(String lifecycleEvent) {
        this.lifecycleEvent = lifecycleEvent;
    }

    public String getValue(String prefix) {
        if (valueCache != null) {
            return valueCache;
        }
        Preferences prefs = Preferences.userNodeForPackage(JOverseerClient.class);
        String value = prefs.get(prefix + "." + key, null);
        if (value != null) {
        	valueCache = value;
        	
        	return value;
        } else {
        	valueCache = getDefaultValue();
        	setValue(prefix, valueCache);
            return getDefaultValue();
        }
    }
    
    public void setValue(String prefix, String value) {
        Preferences prefs = Preferences.userNodeForPackage(JOverseerClient.class);
        prefs.put(prefix + "." + key, value);
        clearCache();
        if (getLifecycleEvent() != null) {
            Application.instance().getApplicationContext().publishEvent(
                    new JOverseerEvent(getLifecycleEvent(), this, this));
        }
    }
    
    public void clearCache() {
        valueCache = null;
    }

    
    public String getDefaultValue() {
        return defaultValue;
    }

    
    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }
    
    
}
