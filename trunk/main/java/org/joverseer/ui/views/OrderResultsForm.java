package org.joverseer.ui.views;

import java.awt.Dimension;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.joverseer.domain.Character;
import org.springframework.binding.form.FormModel;
import org.springframework.richclient.form.AbstractForm;
import org.springframework.richclient.layout.TableLayoutBuilder;

/**
 * Form for showing Character Order Results
 * 
 * @author Marios Skounakis
 */
public class OrderResultsForm extends AbstractForm {

	public static String FORM_PAGE = "orderResultsForm";

	JTextField name;
	JTextArea results;

	public OrderResultsForm(FormModel arg0) {
		super(arg0, FORM_PAGE);
	}

	@Override
	protected JComponent createFormControl() {
		TableLayoutBuilder tlb = new TableLayoutBuilder();
		JLabel lbl;
		tlb.cell(lbl = new JLabel("Character :"));
		lbl.setPreferredSize(new Dimension(70, 20));
		this.name = new JTextField();
		this.name.setEditable(false);
		tlb.gapCol();
		tlb.cell(this.name);
		tlb.relatedGapRow();
		tlb.row();
		tlb.cell(lbl = new JLabel("Results :"), "valign=top");
		lbl.setPreferredSize(new Dimension(70, 20));
		tlb.gapCol();
		this.results = new JTextArea();
		this.results.setWrapStyleWord(true);
		this.results.setLineWrap(true);
		this.results.setEditable(false);
		JScrollPane scp = new JScrollPane(this.results);
		scp.setPreferredSize(new Dimension(500, 200));
		tlb.cell(scp);
		return tlb.getPanel();
	}

	@Override
	public void setFormObject(Object arg0) {
		super.setFormObject(arg0);
		Character c = (Character) arg0;
		this.name.setText(c.getName());
		String result = c.getCleanOrderResults().replaceAll("\n", "");
		result = result.replaceAll(" He was ordered", "\n\nHe was ordered");
		result = result.replaceAll(" She was ordered", "\n\nShe was ordered");
		result = result.replaceAll(" He is travelling", "\n\nHe is travelling");
		result = result.replaceAll(" She is travelling", "\n\nShe is travelling");
		result = result.replaceAll(" He is currently", "\n\nHe is currently");
		result = result.replaceAll(" She is currently", "\n\nShe is currently");
		result = result.replaceAll(" He commands a", "\n\nHe commands a");
		result = result.replaceAll(" She commands a", "\n\nShe commands a");
		this.results.setText(result);
		this.results.setCaretPosition(0);
	}

}
