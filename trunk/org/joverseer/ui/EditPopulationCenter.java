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
import org.springframework.richclient.form.AbstractForm;
import org.springframework.richclient.form.builder.TableFormBuilder;
import org.springframework.richclient.layout.GridBagLayoutBuilder;
import org.joverseer.domain.PopulationCenterSizeEnum;
import org.joverseer.ui.viewers.PopulationCenterViewer;


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
        JComboBox cmb = new JComboBox(PopulationCenterSizeEnum.values());
        formBuilder.add("size", cmb);
        formBuilder.row();
        formBuilder.add("fortification", new JComboBox());
        formBuilder.row();
        formBuilder.add("loyalty");
        formBuilder.row();
        return formBuilder.getForm();
    }
}