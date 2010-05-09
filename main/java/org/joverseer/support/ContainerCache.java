package org.joverseer.support;

import java.util.Collection;
import java.util.HashMap;
import java.util.ArrayList;
import java.io.IOException;
import java.io.Serializable;

import org.apache.commons.beanutils.PropertyUtils;
import org.joverseer.metadata.GameTypeEnum;

/**
 * Implements cache functionality for the Container class
 * 
 * @author Marios Skounakis
 */
public class ContainerCache implements Serializable {
    private static final long serialVersionUID = -8253812529870087895L;
	String propertyName;
    HashMap<Object, ArrayList> cache;
    transient HashMap<Object, Object> reverseMap;
    
    public ContainerCache(String propertyName) {
        this.propertyName = propertyName;
        cache = new HashMap();
        reverseMap = new HashMap();
    }

    public String getPropertyName() {
        return propertyName;
    }
    
    private HashMap<Object, Object> getReverseMap() {
    	if (reverseMap == null) {
    		reconstructReverseMap();
    	}
    	return reverseMap;
    }

    public void addItem(Object obj) {
        Object value = getPropertyValue(obj);
        ArrayList objects = cache.get(value);
        if (objects == null) {
            objects = new ArrayList();
            cache.put(value, objects);
        }
        getReverseMap().put(obj, value);
        objects.add(obj);
    }
    
    private Object getPropertyValue(Object obj) {
    	try {
            return PropertyUtils.getProperty(obj, getPropertyName());
        }
        catch (Exception exc) {
        }
        return null;
    }

    public void removeItem(Object obj) {
    	Object value = getPropertyValue(obj);
        try {
        	value = getReverseMap().get(obj);
        }
        catch (Exception e) {
	        try {
	            value = PropertyUtils.getProperty(obj, getPropertyName());
	        }
	        catch (Exception exc) {
	        }
        }
        ArrayList objects = cache.get(value);
        if (objects == null) {
            return;
        }
        objects.remove(obj);
    }
    
    
    public void removeAll(Collection col) {
        for (Object obj : col) {
            removeItem(obj);
        }
    }

    public ArrayList retrieveItems(Object value) {
        ArrayList objects = cache.get(value);
        return objects;
    }

    public void clear() {
        cache.clear();
    }
    
    private void writeObject(java.io.ObjectOutputStream out) throws IOException {
    	out.defaultWriteObject();
     }

    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
    	// for backwards compatibility with older versions (before 1.0.7)
    	Object o1 = in.readObject();
    	Object o2 = in.readObject();
    	if (HashMap.class.isInstance(o1)) {
    		cache = (HashMap)o1;
    		propertyName = (String)o2;
    	} else {
    		propertyName = (String)o1;
    		cache = (HashMap)o2;
    	}
    	reconstructReverseMap();
    }
    
    private void reconstructReverseMap() {
    	reverseMap = new HashMap<Object, Object>();
    	for (ArrayList list : cache.values()) {
    		for (Object obj : list) {
    			Object value = getPropertyValue(obj);
    			reverseMap.put(obj, value);
    		}
    	}
    }
}
