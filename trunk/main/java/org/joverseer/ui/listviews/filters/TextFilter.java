package org.joverseer.ui.listviews.filters;

import org.joverseer.tools.ordercheckerIntegration.ReflectionUtils;
import org.joverseer.ui.listviews.AbstractListViewFilter;


public class TextFilter extends AbstractListViewFilter {
    String property;
    String value;
    
    public TextFilter(String description, String property) {
        super(description);
        this.property = property;
    }

    public boolean accept(Object obj) {
        try {
            if (ReflectionUtils.retrieveField(obj, property).toString().indexOf(value) > -1) {
                return true;
            }
            return false;
        }
        catch (Exception exc) {
            return true;
        }
    }

    
    public String getValue() {
        return value;
    }

    
    public void setValue(String value) {
        this.value = value;
    }
    
    

}
