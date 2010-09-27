package org.joverseer.support;

import java.util.Comparator;

import org.apache.commons.beanutils.BeanComparator;

public class PropertyComparator<X> implements Comparator<X> {
	BeanComparator peer;

	public PropertyComparator(String property) {
		peer = new BeanComparator(property);
	}

	public int compare(X o1, X o2) {
		return peer.compare(o1, o2);
	}

}
