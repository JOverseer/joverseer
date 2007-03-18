package org.joverseer.ui.views;

import javax.swing.JComboBox;
import javax.swing.JComponent;

import org.joverseer.metadata.GameTypeEnum;
import org.springframework.binding.form.FormModel;
import org.springframework.richclient.form.AbstractForm;
import org.springframework.richclient.form.builder.TableFormBuilder;


public class NewGameForm extends AbstractForm {
     public static final String FORM_PAGE = "newGameForm";

    public NewGameForm(FormModel formModel) {
        super(formModel, FORM_PAGE);
    }

    protected JComponent createFormControl() {
        TableFormBuilder tlb = new TableFormBuilder(getBindingFactory());
        JComboBox cmb;
        tlb.add("gameType", cmb = new JComboBox(GameTypeEnum.values()));
        cmb.setEditable(false);
        tlb.row();
        tlb.add("nationNo");
        tlb.row();
        tlb.add("number");
        return tlb.getForm();
    }
    
    
}
