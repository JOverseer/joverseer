package org.joverseer.support;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import org.apache.commons.beanutils.BeanPropertyValueEqualsPredicate;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.functors.AllPredicate;

/**
 * Support container class. It holds items and provides search capabilities on the items.
 * 
 * Searching can be done using one or more properties of the items, and one value for each property
 * e.g. search for name='Marios' and nation=11
 * 
 * It also implements caches so that searching can operate more efficiently.
 * Care must be taken when using caches:
 * - assume item A has property P=V
 * - assume container C has a cache on P
 * - assume I add item A to C
 * - assume I update P to V1
 * - the item must be removed and re-added to the container for the cache to be updated
 * 
 * TODO an update mechanism for caches should be implemented, maybe with PropertyChangeListeners
 * 
 * @author Marios Skounakis
 */
public class Container implements Serializable {

    private static final long serialVersionUID = -3898746240033481558L;
    public ArrayList items = new ArrayList();
    public HashMap<String, ContainerCache> caches = new HashMap<String, ContainerCache>();

    public Container() {}

    public Container(String[] cacheProperties) {
        for (String cacheProperty : cacheProperties) {
            addCache(cacheProperty);
        }
    }

    public void addCache(String propertyName) {
        assert(items.size() == 0);
        caches.put(propertyName, new ContainerCache(propertyName));
    }

    public void addItem(Object obj) {
        items.add(obj);
        for (ContainerCache cc : caches.values()) {
            cc.addItem(obj);
        }
    }

    public void refreshItem(Object obj) {
        removeItem(obj);
        addItem(obj);
    }
    
    public void removeItem(Object obj) {
        if (items.contains(obj)) {
            items.remove(obj);
            for (ContainerCache cc : caches.values()) {
                cc.removeItem(obj);
            }
        }
    }

    public void removeAll(Collection col) {
        items.removeAll(col);
        for (ContainerCache cc : caches.values()) {
            cc.removeAll(col);
        }
    }
    
    public void clear() {
        removeAll(getItems());
    }

    public int size() {
        return items.size();
    }

    public boolean contains(Object obj) {
        return items.contains(obj);
    }

    public ArrayList getItems() {
        return items;
    }

    private ArrayList findByCache(String propertyName, Object value) {
        ContainerCache cache = caches.get(propertyName);
        if (cache == null) return null;
        ArrayList ret = cache.retrieveItems(value);
        if (ret == null) return new ArrayList();
        return ret;
    }

    public ArrayList findAllByProperties(String[] properties, Object[] values) {
        ArrayList ret = findByCache(properties[0], values[0]);
        if (ret != null && ret.size() == 0) {
        	return ret;
        }
        int si;
        if (ret == null) {
            si = 0;
            ret = items;
        } else {
            si = 1;
        }
        if (si == properties.length) return ret;
        BeanPropertyValueEqualsPredicate[] ps = new BeanPropertyValueEqualsPredicate[properties.length - si];
        for (int i=si; i<properties.length; i++) {
            ps[i-si] = new BeanPropertyValueEqualsPredicate(properties[i], values[i]);
        }
        AllPredicate p = new AllPredicate(ps);
        //ArrayList res = new ArrayList();
        //res.addAll(ret);
        ArrayList res = ret;
        if (ps.length > 0) {
            //CollectionUtils.filter(res, p);
        	return new ArrayList(CollectionUtils.select(res, p));
        }
        return res;
    }

    public Object findFirstByProperties(String[] properties, Object[] values) {
        ArrayList res = findAllByProperties(properties, values);
        if (res.size() > 0) {
            return res.get(0);
        }
        return null;
    }

    public Object findFirstByProperty(String property, Object value) {
        return findFirstByProperties(new String[]{property}, new Object[]{value});
    }

    public ArrayList findAllByProperty(String property, Object value) {
        return findAllByProperties(new String[]{property}, new Object[]{value});
    }

    public void removeAllByProperties(String[] property, Object[] values) {
        ArrayList objects = findAllByProperties(property, values);
        for (Object obj : objects) {
            removeItem(obj);
        }
    }

    public void removeAllByProperties(String property, Object value) {
        removeAllByProperties(new String[]{property}, new Object[]{value});
    }


}
