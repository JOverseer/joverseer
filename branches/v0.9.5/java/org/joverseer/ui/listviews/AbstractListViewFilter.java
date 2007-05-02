package org.joverseer.ui.listviews;


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
}
