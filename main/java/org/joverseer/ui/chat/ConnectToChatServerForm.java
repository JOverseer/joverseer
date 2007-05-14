package org.joverseer.ui.chat;

import javax.swing.JComponent;

import org.springframework.binding.form.FormModel;
import org.springframework.richclient.form.AbstractForm;
import org.springframework.richclient.form.builder.TableFormBuilder;


public class ConnectToChatServerForm extends AbstractForm {
    static String FORM_ID = "connectToChatServerForm";
    
    public ConnectToChatServerForm(FormModel m) {
        super(m, FORM_ID);
    }
    
    protected JComponent createFormControl() {
        TableFormBuilder tlb = new TableFormBuilder(getBindingFactory());
        
        tlb.add("server");
        tlb.row();

        tlb.add("port");
        tlb.row();

        tlb.add("username");
        tlb.row();
        
        return tlb.getForm();
    }

}
