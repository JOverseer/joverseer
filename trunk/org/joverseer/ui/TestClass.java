/*
 * TestClass.java
 *
 * Created on September 9, 2006, 11:59 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package org.joverseer.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Color;

import org.springframework.binding.form.FormModel;
import org.springframework.richclient.form.AbstractForm;
import org.springframework.richclient.form.builder.TableFormBuilder;



public class TestClass extends AbstractForm {
    public static final String FORM_PAGE = "TestClass";
    
    public TestClass(FormModel formModel) {
        super(formModel, FORM_PAGE);
    }

    protected JComponent createFormControl() {
        TableFormBuilder formBuilder = new TableFormBuilder(getBindingFactory());
        formBuilder.add("firstName");
        formBuilder.row();
        formBuilder.add("lastName", "colSpec=2cm");
        return formBuilder.getForm();
    }
}