package org.joverseer.ui.support;

import org.springframework.richclient.form.binding.swing.SwingBinderSelectionStrategy;
import org.springframework.richclient.form.binding.Binder;

import java.util.Map;
import java.util.Iterator;

/**
 * User: mscoon
 * Date: 18 Σεπ 2006
 * Time: 10:26:56 πμ
 */
public class BinderSelectionStrategy extends SwingBinderSelectionStrategy {
    public void setBindersForPropertyTypes(Map binders) {
        for (Iterator i = binders.entrySet().iterator(); i.hasNext();) {
            Map.Entry entry = (Map.Entry)i.next();
            registerBinderForPropertyType((Class)entry.getKey(), (Binder)entry.getValue());
        }
    }
}
