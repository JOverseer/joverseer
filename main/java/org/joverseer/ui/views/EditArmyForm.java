package org.joverseer.ui.views;

import javax.swing.JComponent;

import org.springframework.binding.form.FormModel;
import org.springframework.richclient.form.AbstractForm;
import org.springframework.richclient.form.builder.TableFormBuilder;


public class EditArmyForm extends AbstractForm {
    public static String FORM_ID = "editArmyForm";

    public EditArmyForm(FormModel arg0) {
        super(arg0, FORM_ID);
    }

    protected JComponent createFormControl() {
        TableFormBuilder tfb = new TableFormBuilder(getBindingFactory());
        
        tfb.add("commander");
        tfb.add("commandRank");
        
        tfb.row();
        tfb.add("morale");
        tfb.add("food");
        tfb.row();
        
        JComponent panel = tfb.getForm();
        return panel;
    }
    
    

}
