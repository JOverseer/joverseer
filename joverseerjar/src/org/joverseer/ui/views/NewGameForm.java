package org.joverseer.ui.views;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JTextField;

import org.joverseer.metadata.GameTypeEnum;
import org.springframework.binding.form.FormModel;
import org.springframework.richclient.form.AbstractForm;
import org.springframework.richclient.form.builder.TableFormBuilder;

/**
 * Form for creating a new game
 * 
 * @author Marios Skounakis
 */
public class NewGameForm extends AbstractForm {
     public static final String FORM_PAGE = "newGameForm";
     boolean edit = false;
     
     public NewGameForm(FormModel formModel) {
         super(formModel, FORM_PAGE);
     }
     
    public NewGameForm(FormModel formModel, boolean edit) {
        super(formModel, FORM_PAGE);
        this.edit = edit;
    }

    @Override
	protected JComponent createFormControl() {
        TableFormBuilder tlb = new TableFormBuilder(getBindingFactory());
        JComboBox cmb;
        tlb.add("gameType", cmb = new JComboBox(GameTypeEnum.values()));
        cmb.setEditable(false);
        cmb.setEnabled(!this.edit);
        tlb.row();
        JTextField txt;
        tlb.add("nationNo");
        tlb.row();
        tlb.add("number", txt = new JTextField());
        txt.setEnabled(!this.edit);
        tlb.row();
//        JCheckBox chk;
        //tlb.add("additionalNations");
//        tlb.add("newXmlFormat", chk = new JCheckBox());
//        chk.setEnabled(!this.edit);
        return tlb.getForm();
    }
    
    
}
