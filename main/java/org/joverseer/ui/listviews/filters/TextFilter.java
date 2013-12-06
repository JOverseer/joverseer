package org.joverseer.ui.listviews.filters;

import org.joverseer.support.AsciiUtils;
import org.joverseer.tools.ordercheckerIntegration.ReflectionUtils;
import org.joverseer.ui.listviews.AbstractListViewFilter;

/**
 * Text filter
 * 
 * @author Marios Skounakis
 */
public class TextFilter extends AbstractListViewFilter {
    String field;
    String value;
    
    public TextFilter(String description, String field, String value) {
        super(description);
        this.field = field;
        this.value = value;
    }

    @Override
	public boolean accept(Object obj) {
        if (this.value == null) return true;
        try {
            Object val = ReflectionUtils.retrieveField(obj, this.field);
            if (val == null) val = "";
            String str = val.toString();
            if (this.value.equals("")) return str.equals("");
            str = AsciiUtils.convertNonAscii(str);
            if (str.toString().toUpperCase().indexOf(this.value.toUpperCase()) > -1) {
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
        return this.value;
    }

    
    public void setValue(String value) {
        this.value = value;
    }
    
    

}
