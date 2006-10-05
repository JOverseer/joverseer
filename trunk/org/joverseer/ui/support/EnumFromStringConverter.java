package org.joverseer.ui.support;

import org.springframework.binding.convert.support.AbstractFormattingConverter;
import org.springframework.binding.format.support.SimpleFormatterLocator;
import org.springframework.binding.format.FormatterLocator;

import java.lang.reflect.Method;

/**
 * User: mscoon
 * Date: 18 Σεπ 2006
 * Time: 10:48:07 πμ
 */
public class EnumFromStringConverter extends AbstractFormattingConverter {
    private final boolean allowEmpty;
    private Class clazz = null;

    /**
     * required a default ctor to be used in XML
     */
    public EnumFromStringConverter(Class c) {
        super(new SimpleFormatterLocator());
        this.allowEmpty = true;
        this.clazz = c;
    }

    protected EnumFromStringConverter(Class c, final FormatterLocator formatterLocator, final boolean allowEmpty) {
        super(formatterLocator);
        this.allowEmpty = allowEmpty;
        clazz = c;
    }

    public Class[] getTargetClasses() {
        return new Class[] {clazz};
    }

    public Class[] getSourceClasses() {
        return new Class[] {String.class};
    }

    protected Object doConvert(Object source, Class targetClass) throws Exception {
        Method mi = targetClass.getMethod("valueOf");
        return (!allowEmpty || source!=null) ? mi.invoke(targetClass, new Object[]{source}) : null;
    }
}
