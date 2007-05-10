package org.joverseer.ui.views;

import java.awt.Dimension;
import java.awt.GridBagLayout;

import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.text.html.HTMLEditorKit;

import org.springframework.binding.form.FormModel;
import org.springframework.richclient.form.AbstractForm;
import org.springframework.richclient.layout.GridBagLayoutBuilder;

public class SubmitOrdersResultsForm extends AbstractForm {
	static String FORM_ID = "submitOrdersResultsForm"; 
	
	JEditorPane htmlResponse;
	
	
	public SubmitOrdersResultsForm(FormModel arg0) {
		super(arg0, FORM_ID);
	}

	protected JComponent createFormControl() {
		htmlResponse = new JEditorPane();
		htmlResponse.setContentType("text/html");
		htmlResponse.setEditorKit(new HTMLEditorKit());
		
		JScrollPane scp = new JScrollPane(htmlResponse);
		scp.setPreferredSize(new Dimension(600, 500));
		
		return scp;
	}
	
	public JEditorPane getJEditorPane() {
		return htmlResponse;
	}
	

}
