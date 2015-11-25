package org.joverseer.ui.listviews.filters;

import java.util.ArrayList;

import org.joverseer.ui.listviews.AbstractListViewFilter;

/**
 * Filter that combines filters
 * 
 * @author Marios Skounakis
 */
public class AndFilter extends AbstractListViewFilter {
	ArrayList<AbstractListViewFilter> filters = new ArrayList<AbstractListViewFilter>();
	public AndFilter() {
		super("");
	}
	
	public void addFilter(AbstractListViewFilter filter) {
		this.filters.add(filter);
	}

	@Override
	public boolean accept(Object obj) {
		for (AbstractListViewFilter filter : this.filters) {
			if (filter == null) continue;
			if (!filter.accept(obj)) return false;
		}
		return true;
	}
	
	

}
