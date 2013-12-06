package org.joverseer.ui.listviews;

/**
 * Base class for ItemListView filters that always accepts the given object
 * 
 * @author Marios Skounakis
 */
public class PositiveListViewFilter extends AbstractListViewFilter {
	
	public PositiveListViewFilter() {
		super(null);
	}

	@Override
	public boolean accept(Object obj) {
		return true;
	}

	
}
