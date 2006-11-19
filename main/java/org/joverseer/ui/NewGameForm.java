package org.joverseer.ui;

import org.springframework.richclient.form.AbstractForm;
import org.springframework.richclient.form.builder.TableFormBuilder;
import org.springframework.richclient.layout.TableLayoutBuilder;
import org.springframework.binding.form.FormModel;
import org.joverseer.metadata.GameTypeEnum;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * User: mskounak
 * Date: 19 Íןו 2006
 * Time: 10:41:00 לל
 * To change this template use File | Settings | File Templates.
 */
public class NewGameForm extends AbstractForm {
     public static final String FORM_PAGE = "newGameForm";

    public NewGameForm(FormModel formModel) {
        super(formModel, FORM_PAGE);
    }

    protected JComponent createFormControl() {
        TableFormBuilder tlb = new TableFormBuilder(getBindingFactory());
        JComboBox cmb;
        tlb.add("gameType", cmb = new JComboBox(GameTypeEnum.values()));
        tlb.row();
        tlb.add("number");
        tlb.row();
        tlb.add("nationNo");
        return tlb.getForm();
    }
}
