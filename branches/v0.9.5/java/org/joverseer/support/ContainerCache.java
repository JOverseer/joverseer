package org.joverseer.support;

import java.util.Collection;
import java.util.HashMap;
import java.util.ArrayList;
import java.io.Serializable;

import org.apache.commons.beanutils.PropertyUtils;


public class ContainerCache implements Serializable {
    String propertyName;
    HashMap<Object, ArrayList> cache;

    public ContainerCache(String propertyName) {
        this.propertyName = propertyName;
        cache = new HashMap();
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void addItem(Object obj) {
        Object value = null;
        try {
            value = PropertyUtils.getProperty(obj, getPropertyName());
        }
        catch (Exception exc) {
        }
        ArrayList objects = cache.get(value);
        if (objects == null) {
            objects = new ArrayList();
            cache.put(value, objects);
        }
        objects.add(obj);
    }

    public void removeItem(Object obj) {
        Object value = null;
        try {
            value = PropertyUtils.getProperty(obj, getPropertyName());
        }
        catch (Exception exc) {
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
}
