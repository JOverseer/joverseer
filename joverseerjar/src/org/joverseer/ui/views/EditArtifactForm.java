package org.joverseer.ui.views;

import javax.swing.JComponent;

import org.joverseer.ui.support.GraphicUtils;
import org.springframework.binding.form.FormModel;
import org.springframework.richclient.form.AbstractForm;
import org.springframework.richclient.form.builder.TableFormBuilder;


public class EditArtifactForm extends AbstractForm {
    public static final String FORM_PAGE = "editArtifactForm";

    public EditArtifactForm(FormModel formModel) {
        super(formModel, FORM_PAGE);
    }

    @Override
	protected JComponent createFormControl() {
        TableFormBuilder formBuilder = new TableFormBuilder(getBindingFactory());
        
        GraphicUtils.registerIntegerPropertyConverters(this, "hexNo");
        GraphicUtils.registerIntegerPropertyConverters(this, "number");
        
        formBuilder.add("number");
        formBuilder.row();
        
        formBuilder.add("name");
        formBuilder.row();
        
        formBuilder.add("hexNo");
        formBuilder.row();

        formBuilder.add("owner");
        formBuilder.row();
        
        return formBuilder.getForm();
    }

}
