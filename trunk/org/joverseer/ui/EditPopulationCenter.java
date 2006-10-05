/*
 * EditPopulationCenter.java
 *
 * Created on September 9, 2006, 11:59 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package org.joverseer.ui;

import javax.swing.*;

import org.springframework.binding.form.FormModel;
import org.springframework.binding.value.ValueModel;
import org.springframework.binding.value.support.ValueHolder;
import org.springframework.richclient.form.AbstractForm;
import org.springframework.richclient.form.binding.swing.SwingBindingFactory;
import org.springframework.richclient.form.binding.swing.ComboBoxBinding;
import org.springframework.richclient.form.binding.BindingFactory;
import org.springframework.richclient.form.binding.Binding;
import org.springframework.richclient.form.builder.TableFormBuilder;
import org.springframework.richclient.factory.DefaultComponentFactory;
import org.joverseer.domain.PopulationCenterSizeEnum;


public class EditPopulationCenter extends AbstractForm {
    public static final String FORM_PAGE = "EditPopulationCenter";

    public EditPopulationCenter(FormModel formModel) {
        super(formModel, FORM_PAGE);
    }

    protected JComponent createFormControl() {
        TableFormBuilder formBuilder = new TableFormBuilder(getBindingFactory());
        formBuilder.add("name");
        formBuilder.row();
        formBuilder.add("nationNo");
        formBuilder.row();
        //SwingBindingFactory dbf = new SwingBindingFactory(getFormModel());
        //formBuilder.add(dbf.createBoundComboBox("size"));
//        ComboBoxBinding b = new ComboBoxBinding(getFormModel(), "size");
//        Object[] vals = PopulationCenterSizeEnum.values();
//        b.setSelectableItems(vals);
        JComboBox cmb = new JComboBox(PopulationCenterSizeEnum.values());
        formBuilder.add("size", cmb);
        formBuilder.row();
        //formBuilder.add("fortification", new JComboBox());
        formBuilder.row();
        formBuilder.add("loyalty");
        formBuilder.row();
        return formBuilder.getForm();
    }
}