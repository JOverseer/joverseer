package org.joverseer.support;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.commons.beanutils.BeanPropertyValueEqualsPredicate;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.functors.AllPredicate;

/**
 * Support container class. It holds items and provides search capabilities on
 * the items.
 * 
 * Searching can be done using one or more properties of the items, and one
 * value for each property e.g. search for name='Marios' and nation=11
 * 
 * It also implements caches so that searching can operate more efficiently.
 * Care must be taken when using caches: - assume item A has property P=V -
 * assume container C has a cache on P - assume I add item A to C - assume I
 * update P to V1 - the item must be removed and re-added to the container for
 * the cache to be updated
 * 
 * @author Marios Skounakis
 */
public class Container<X> implements Serializable, Iterable<X> {

	private static final long serialVersionUID = -3898746240033481558L;
	public ArrayList<X> items = new ArrayList<X>();
	public HashMap<String, ContainerCache<X>> caches = new HashMap<String, ContainerCache<X>>();

	public Container() {
	}

	public Container(String[] cacheProperties) {
		for (String cacheProperty : cacheProperties) {
			addCache(cacheProperty);
		}
	}

	@Override
	public Iterator<X> iterator() {
		return this.items.iterator();
	}

	public void addCache(String propertyName) {
		assert (this.items.size() == 0);
		this.caches.put(propertyName, new ContainerCache<X>(propertyName));
	}

	public void addItem(X obj) {
		this.items.add(obj);
		for (ContainerCache<X> cc : this.caches.values()) {
			cc.addItem(obj);
		}
	}

	public void clear() {
		removeAll(getItems());
	}

	public boolean contains(X obj) {
		return this.items.contains(obj);
	}

	public ArrayList<X> findAllByProperties(String[] properties, Object[] values) {
		ArrayList<X> ret = findByCache(properties[0], values[0]);
		if (ret != null && ret.size() == 0) {
			return ret;
		}
		int si;
		if (ret == null) {
			si = 0;
			ret = this.items;
		} else {
			si = 1;
		}
		if (si == properties.length)
			return ret;
		BeanPropertyValueEqualsPredicate[] ps = new BeanPropertyValueEqualsPredicate[properties.length - si];
		for (int i = si; i < properties.length; i++) {
			ps[i - si] = new BeanPropertyValueEqualsPredicate(properties[i], values[i]);
		}
		AllPredicate p = new AllPredicate(ps);
		// ArrayList res = new ArrayList();
		// res.addAll(ret);
		ArrayList<X> res = ret;
		if (ps.length > 0) {
			// CollectionUtils.filter(res, p);
			ArrayList<X> r = new ArrayList<X>();
			CollectionUtils.select(res, p, r);
			return r;
		}
		return res;
	}

	public ArrayList<X> findAllByProperty(String property, Object value) {
		return findAllByProperties(new String[] { property }, new Object[] { value });
	}

	private ArrayList<X> findByCache(String propertyName, Object value) {
		ContainerCache<X> cache = this.caches.get(propertyName);
		if (cache == null)
			return null;
		ArrayList<X> ret = cache.retrieveItems(value);
		if (ret == null)
			return new ArrayList<X>();
		return ret;
	}

	public X findFirstByProperties(String[] properties, Object[] values) {
		ArrayList<X> res = findAllByProperties(properties, values);
		if (res.size() > 0) {
			return res.get(0);
		}
		return null;
	}

	public X findFirstByProperty(String property, Object value) {
		return findFirstByProperties(new String[] { property }, new Object[] { value });
	}

	public ArrayList<X> getItems() {
		return this.items;
	}

	public void refreshItem(X obj) {
		removeItem(obj);
		addItem(obj);
	}

	public void removeAll(Collection<X> col) {
		this.items.removeAll(col);
		for (ContainerCache<X> cc : this.caches.values()) {
			cc.removeAll(col);
		}
	}

	public void removeAllByProperties(String property, Object value) {
		removeAllByProperties(new String[] { property }, new Object[] { value });
	}

	public void removeAllByProperties(String[] property, Object[] values) {
		ArrayList<X> objects = findAllByProperties(property, values);
		for (X obj : objects) {
			removeItem(obj);
		}
	}

	public void removeItem(X obj) {
		if (this.items.contains(obj)) {
			this.items.remove(obj);
			for (ContainerCache<X> cc : this.caches.values()) {
				cc.removeItem(obj);
			}
		}
	}

	public int size() {
		return this.items.size();
	}

}
