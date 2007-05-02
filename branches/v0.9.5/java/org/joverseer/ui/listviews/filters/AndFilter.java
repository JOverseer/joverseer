package org.joverseer.ui.listviews.filters;

import java.util.ArrayList;

import org.joverseer.ui.listviews.AbstractListViewFilter;

public class AndFilter extends AbstractListViewFilter {
	ArrayList<AbstractListViewFilter> filters = new ArrayList<AbstractListViewFilter>();
	public AndFilter() {
		super("");
	}
	
	public void addFilter(AbstractListViewFilter filter) {
		filters.add(filter);
	}

	public boolean accept(Object obj) {
		for (AbstractListViewFilter filter : filters) {
			if (filter == null) continue;
			if (!filter.accept(obj)) return false;
		}
		return true;
	}
	
	

}
