package org.joverseer.support;

import java.util.Comparator;

import org.apache.commons.beanutils.BeanComparator;

public class PropertyComparator<X> implements Comparator<X> {
	BeanComparator peer;

	public PropertyComparator(String property) {
		this.peer = new BeanComparator(property);
	}

	@Override
	public int compare(X o1, X o2) {
		return this.peer.compare(o1, o2);
	}

}
