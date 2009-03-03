package org.joverseer.ui.chat;

import javax.swing.JComponent;
import javax.swing.JTextField;

import org.joverseer.ui.support.GraphicUtils;
import org.springframework.binding.form.FormModel;
import org.springframework.richclient.form.AbstractForm;
import org.springframework.richclient.form.binding.swing.SwingBindingFactory;
import org.springframework.richclient.form.builder.TableFormBuilder;


public class ConnectToChatServerForm extends AbstractForm {
    static String FORM_ID = "connectToChatServerForm";
    
    public ConnectToChatServerForm(FormModel m) {
        super(m, FORM_ID);
    }
    
    protected JComponent createFormControl() {
        TableFormBuilder tlb = new TableFormBuilder(getBindingFactory());
        
        //GraphicUtils.registerIntegerPropertyConverters(this, "port");
        
        tlb.add("username");
        tlb.row();

        tlb.add("password");
        tlb.row();

        return tlb.getForm();
    }
    
    

}
