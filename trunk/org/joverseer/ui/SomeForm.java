/*
 * SomeForm.java
 *
 * Created on August 21, 2006, 11:04 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.joverseer.ui;

import javax.swing.JComponent;

import org.springframework.binding.form.FormModel;
import org.springframework.richclient.form.AbstractForm;
import org.springframework.richclient.form.builder.TableFormBuilder;


/**
 *
 * @author mskounak
 */
public class SomeForm extends AbstractForm {
    private JComponent address;

    /** Creates a new instance of SomeForm */
    public SomeForm(FormModel model) {
        super(model, "customerForm");
    }
    
      public boolean requestFocusInWindow() {
        return address.requestFocusInWindow();
    }

    protected JComponent createFormControl() {
        TableFormBuilder formBuilder = new TableFormBuilder(getBindingFactory());
        this.address = formBuilder.add("address")[1];
        formBuilder.row();
        formBuilder.add("city");        
        return formBuilder.getForm();
    }
    
}
