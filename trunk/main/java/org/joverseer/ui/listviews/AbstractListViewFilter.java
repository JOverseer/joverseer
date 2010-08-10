package org.joverseer.ui.listviews;

/**
 * Base class for ItemListView filters
 * 
 * @author Marios Skounakis
 */
public abstract class AbstractListViewFilter {
    String description;
    
    
    
    public String getDescription() {
        return description;
    }


    
    public void setDescription(String description) {
        this.description = description;
    }


    public AbstractListViewFilter(String description) {
        super();
        this.description = description;
    }


    public abstract boolean accept(Object obj);
    
    public String toString() {
        return getDescription();
    }



	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (obj.getClass().equals(getClass()) && ((AbstractListViewFilter)obj).getDescription().equals(getDescription())) {
			return true;
		}
		return super.equals(obj);
	}
    
    
}
