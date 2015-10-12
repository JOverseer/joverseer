package org.joverseer.preferences;

/**
 * Value for preferences with a predefined domain (list of values)
 * Contains key-value pairs.
 * 
 * TODO: should refactor to use message source for user-friendly strings 
 * 
 * @author Marios Skounakis
 *
 */
public class PreferenceValue {
    String key;
    String description;
    
    public String getDescription() {
        return this.description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getKey() {
        return this.key;
    }
    
    public void setKey(String key) {
        this.key = key;
    }
    
    
}
