package org.joverseer.support;


import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.PredicateUtils;
import org.apache.commons.collections.functors.EqualPredicate;
import org.apache.commons.collections.functors.AndPredicate;
import org.apache.commons.collections.functors.AllPredicate;
import org.apache.commons.beanutils.BeanPropertyValueEqualsPredicate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: mskounak
 * Date: Sep 16, 2006
 * Time: 1:35:30 PM
 * To change this template use File | Settings | File Templates.
 */
public class Container implements Serializable {
    public ArrayList items = new ArrayList();

    public void addItem(Object obj) {
        items.add(obj);
    }

    public void removeItem(Object obj) {
        if (items.contains(obj)) {
            items.remove(obj);
        }
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

    public ArrayList findAllByProperties(String[] properties, Object[] values) {
        BeanPropertyValueEqualsPredicate[] ps = new BeanPropertyValueEqualsPredicate[properties.length];
        for (int i=0; i<ps.length; i++) {
            ps[i] = new BeanPropertyValueEqualsPredicate(properties[i], values[i]);
        }
        AllPredicate p = new AllPredicate(ps);
        ArrayList res = new ArrayList();
        res.addAll(items);
        CollectionUtils.filter(res, p);
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
