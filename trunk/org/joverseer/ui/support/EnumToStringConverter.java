package org.joverseer.ui.support;

import org.springframework.binding.convert.support.AbstractFormattingConverter;
import org.springframework.binding.format.support.SimpleFormatterLocator;
import org.springframework.binding.format.FormatterLocator;

import java.io.StringWriter;

/**
 * User: mscoon
 * Date: 18 Σεπ 2006
 * Time: 10:35:56 πμ
 */
public class EnumToStringConverter extends AbstractFormattingConverter {
    private final boolean allowEmpty;
    private Class clazz = null;

    /**
     * required a default ctor to be used in XML
     */
    public EnumToStringConverter(Class c) {
        super(new SimpleFormatterLocator());
        this.allowEmpty = true;
        this.clazz = c;
    }

    protected EnumToStringConverter(Class c, final FormatterLocator formatterLocator, final boolean allowEmpty) {
        super(formatterLocator);
        this.allowEmpty = allowEmpty;
        clazz = c;
    }

    public Class[] getSourceClasses() {
        return new Class[] {clazz};
    }

    public Class[] getTargetClasses() {
        return new Class[] {String.class};
    }

    protected Object doConvert(Object source, Class targetClass) throws Exception {
        return (!allowEmpty || source!=null) ? toString(source.toString()) : null;
    }

    private String toString(String v) {
        StringWriter w = new StringWriter();
        for (int i=0; i<v.length(); i++) {
            char c = v.charAt(i);
            if (i == 0) {
                c = Character.toUpperCase(c);
            } else if (Character.isUpperCase(c)) {
                w.write(' ');
            }
            w.write((int)c);

        }
        return w.toString();
    }
}