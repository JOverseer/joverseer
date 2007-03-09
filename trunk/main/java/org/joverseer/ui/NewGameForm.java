package org.joverseer.ui;

import org.springframework.richclient.form.AbstractForm;
import org.springframework.richclient.form.builder.TableFormBuilder;
import org.springframework.richclient.layout.TableLayoutBuilder;
import org.springframework.binding.form.FormModel;
import org.joverseer.metadata.GameTypeEnum;

import javax.swing.*;


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
