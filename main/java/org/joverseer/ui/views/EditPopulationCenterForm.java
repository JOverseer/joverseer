/*
 * EditPopulationCenter.java
 *
 * Created on September 9, 2006, 11:59 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package org.joverseer.ui.views;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.*;

import org.springframework.beans.support.PropertyComparator;
import org.springframework.binding.form.FormModel;
import org.springframework.binding.value.support.ValueHolder;
import org.springframework.binding.value.support.ValueModelWrapper;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.form.AbstractForm;
import org.springframework.richclient.form.binding.swing.ComboBoxBinder;
import org.springframework.richclient.form.binding.swing.ComboBoxBinding;
import org.springframework.richclient.form.binding.swing.SwingBindingFactory;
import org.springframework.richclient.form.builder.TableFormBuilder;
import org.springframework.richclient.layout.GridBagLayoutBuilder;
import org.springframework.richclient.list.BeanPropertyValueListRenderer;
import org.joverseer.domain.FortificationSizeEnum;
import org.joverseer.domain.PopulationCenterSizeEnum;
import org.joverseer.game.Game;
import org.joverseer.metadata.GameMetadata;
import org.joverseer.support.GameHolder;
import org.joverseer.ui.viewers.PopulationCenterViewer;


public class EditPopulationCenterForm extends AbstractForm {
    public static final String FORM_PAGE = "editPopulationCenterForm";

    public EditPopulationCenterForm(FormModel formModel) {
        super(formModel, FORM_PAGE);
    }

    protected JComponent createFormControl() {
        TableFormBuilder formBuilder = new TableFormBuilder(getBindingFactory());
        formBuilder.add("name");
        formBuilder.row();
        
        ArrayList nations = new ArrayList();
        Game g = ((GameHolder)Application.instance().getApplicationContext().getBean("gameHolder")).getGame();
        if (Game.isInitialized(g)) {
            GameMetadata gm = g.getMetadata();
            nations.addAll(gm.getNations());
        }
            
        SwingBindingFactory sbf = (SwingBindingFactory)getBindingFactory();
        ComboBoxBinding b = (ComboBoxBinding)sbf.createBoundComboBox("nation", new ValueHolder(nations), "name");
        b.setComparator(new PropertyComparator("number", true, true));
        formBuilder.add(b);
        formBuilder.row();
        JComboBox cmb = new JComboBox(PopulationCenterSizeEnum.values());
        formBuilder.add("size", cmb);
        formBuilder.row();
        formBuilder.add("fortification", cmb = new JComboBox(FortificationSizeEnum.values()));
        formBuilder.row();
        formBuilder.add("loyalty");
        formBuilder.row();
        return formBuilder.getForm();
    }
}