package org.joverseer.ui.views;

import java.awt.Dimension;

import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.text.html.HTMLEditorKit;

import org.springframework.binding.form.FormModel;
import org.springframework.richclient.form.AbstractForm;

/**
 * Form for showing the html response for submitting orders to meturn.com
 * 
 * @author Marios Skounakis
 */
public class SubmitOrdersResultsForm extends AbstractForm {

    static String FORM_ID = "submitOrdersResultsForm";

    JEditorPane htmlResponse;


    public SubmitOrdersResultsForm(FormModel arg0) {
        super(arg0, FORM_ID);
    }

    @Override
	protected JComponent createFormControl() {
        this.htmlResponse = new JEditorPane();
        this.htmlResponse.setContentType("text/html");
        this.htmlResponse.setEditorKit(new HTMLEditorKit());

        this.htmlResponse.setPreferredSize(new Dimension(600, 500));
        JScrollPane scp = new JScrollPane(this.htmlResponse);

        return scp;
    }

    public JEditorPane getJEditorPane() {
        return this.htmlResponse;
    }


}
