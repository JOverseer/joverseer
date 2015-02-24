package org.joverseer.ui.views;

import java.awt.Dimension;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.joverseer.domain.Character;
import org.joverseer.ui.ScalableAbstractForm;
import org.joverseer.ui.support.Messages;
import org.springframework.binding.form.FormModel;
import org.springframework.richclient.form.AbstractForm;
import org.springframework.richclient.layout.TableLayoutBuilder;

/**
 * Form for showing Character Order Results
 * 
 * @author Marios Skounakis
 */
public class OrderResultsForm extends ScalableAbstractForm {

	public static String FORM_PAGE = "orderResultsForm"; //$NON-NLS-1$

	JTextField name;
	JTextArea results;

	public OrderResultsForm(FormModel arg0) {
		super(arg0, FORM_PAGE);
	}

	@Override
	protected JComponent createFormControl() {
		TableLayoutBuilder tlb = new TableLayoutBuilder();
		JLabel lbl;
		tlb.cell(lbl = new JLabel(Messages.getString("OrderResultsForm.CharacterColon"))); //$NON-NLS-1$
		lbl.setPreferredSize(this.uiSizes.newDimension(70/20, this.uiSizes.getHeight5()));
		this.name = new JTextField();
		this.name.setEditable(false);
		tlb.gapCol();
		tlb.cell(this.name);
		tlb.relatedGapRow();
		tlb.row();
		tlb.cell(lbl = new JLabel(Messages.getString("OrderResultsForm.ResultsColon")), "valign=top"); //$NON-NLS-1$ //$NON-NLS-2$
		lbl.setPreferredSize(this.uiSizes.newDimension(70/20, this.uiSizes.getHeight5()));
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
		final String[] searchStrings = {
				"OrderResultsForm.HeWasOrdered",
				"OrderResultsForm.SheWasOrdered",
				"OrderResultsForm.HeIsTravelling",
				"OrderResultsForm.SheIsTravelling",
				"OrderResultsForm.HeIsCurrently",
				"OrderResultsForm.SheIsCurrently",
				"OrderResultsForm.HeCommandsA",
				"OrderResultsForm.SheCommandsA"
		};
		Character c = (Character) arg0;
		this.name.setText(c.getName());
		String result = c.getCleanOrderResults().replaceAll("\n", ""); //$NON-NLS-1$ //$NON-NLS-2$

		// replace " {string}" with "\n\n{string}"
		StringBuilder search = new StringBuilder();
		StringBuilder replacement = new StringBuilder();
		String match;
		//TODO: test this.
		for(String s: searchStrings){
			search.setLength(0);
			replacement.setLength(0);
			search.append(" ");
			replacement.append("\n\n");
			match = Messages.getString(s);
			search.append(match);
			replacement.append(match);
			result = result.replaceAll(search.toString(), replacement.toString());
		}
		this.results.setText(result);
		this.results.setCaretPosition(0);
	}

}
