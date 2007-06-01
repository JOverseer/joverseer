package org.joverseer.ui.listviews.filters;

import org.joverseer.tools.ordercheckerIntegration.ReflectionUtils;
import org.joverseer.ui.listviews.AbstractListViewFilter;

/**
 * Text filter
 * 
 * Unfinished and not used
 * 
 * @author Marios Skounakis
 */
public class TextFilter extends AbstractListViewFilter {
    String property;
    String value;
    
    public TextFilter(String description, String property, String value) {
        super(description);
        this.property = property;
        this.value = value;
    }

    public boolean accept(Object obj) {
        if (value == null) return true;
        try {
            Object val = ReflectionUtils.retrieveField(obj, property);
            if (val == null) val = "";
            if (value.equals("")) return val.toString().equals("");
            if (val.toString().indexOf(value) > -1) {
                return true;
            }
            return false;
        }
        catch (Exception exc) {
            exc.printStackTrace();
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
