package org.joverseer.support;

import java.util.HashMap;
import java.util.ArrayList;

import org.apache.commons.beanutils.PropertyUtils;

/**
 * Created by IntelliJ IDEA.
 * User: mskounak
 * Date: 17 Дек 2006
 * Time: 11:36:22 рм
 * To change this template use File | Settings | File Templates.
 */
public class ContainerCache {
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

    public ArrayList retrieveItems(Object value) {
        ArrayList objects = cache.get(value);
        return objects;
    }

    public void clear() {
        cache.clear();
    }
}
