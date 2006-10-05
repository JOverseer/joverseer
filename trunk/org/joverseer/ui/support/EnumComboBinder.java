package org.joverseer.ui.support;

import javax.swing.JComboBox;
import javax.swing.JComponent;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.MethodInvocationException;
import org.springframework.binding.form.FormModel;
import org.springframework.binding.value.support.ValueHolder;
import org.springframework.richclient.form.binding.Binding;
import org.springframework.richclient.form.binding.support.AbstractBinder;
import org.springframework.richclient.form.binding.swing.ComboBoxBinding;
import org.springframework.util.Assert;

import java.util.Map;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

public class EnumComboBinder<E extends Enum<E>> extends AbstractBinder implements InitializingBean {
    private Class<E> clazz = null;
    private E[] theEnumValues;

    public EnumComboBinder(final Class<E> c, final E[] e) {
        super(c, new String[] {});
        clazz = c;
        theEnumValues = e;
    }

    public EnumComboBinder(final Class<E> c) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        super(c, new String[]{});
        Method mi = c.getMethod("values", null);
        theEnumValues = (E[])mi.invoke(c, null);
    }

    public void afterPropertiesSet() throws Exception {}

    protected JComponent createControl(Map context) {
        return getComponentFactory().createComboBox();
    }

    protected Binding doBind(JComponent control, FormModel formModel, String formPropertyPath, Map context) {
        Assert.isTrue(control instanceof JComboBox, formPropertyPath);
        ComboBoxBinding binding = new ComboBoxBinding((JComboBox)control, formModel, formPropertyPath);
        binding.setSelectableItemsHolder(new ValueHolder(theEnumValues));
        return binding;
    }
}