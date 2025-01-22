package org.joverseer.ui.combatCalculator;

import java.awt.Dimension;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;

import org.joverseer.tools.combatCalc.Combat.combatLogClass;
import org.springframework.binding.form.FormModel;
import org.springframework.richclient.form.AbstractForm;

public class CombatLog extends AbstractForm {
	public static String FORM_ID = "CombatLog"; //$NON-NLS-1$
	private combatLogClass log;
	
	public CombatLog(FormModel arg0, combatLogClass cL) {
		super(arg0, FORM_ID);
		this.log = cL;
	}

	@Override
	protected JComponent createFormControl() {
		// TODO Auto-generated method stub
		JTabbedPane tabP = new JTabbedPane();
		for(int i = 0; i < this.log.round; i++) {
	        JTextArea textArea = new JTextArea(this.log.getRoundLogs().get(i));

	        textArea.setLineWrap(true); // Enable line wrapping
	        textArea.setWrapStyleWord(true); // Wrap at word boundaries
	        textArea.setEditable(false); // Make the text area non-editable

	        // Create a scroll pane and add the text area to it
	        JScrollPane scrollPane = new JScrollPane(textArea);
	        scrollPane.setPreferredSize(new Dimension(300,300));
	        tabP.add("Round " + i, scrollPane);
		}
		
		if(this.log.isPC()) {
	        JTextArea textArea = new JTextArea(this.log.getPCLogs());

	        textArea.setLineWrap(true); // Enable line wrapping
	        textArea.setWrapStyleWord(true); // Wrap at word boundaries
	        textArea.setEditable(false); // Make the text area non-editable

	        // Create a scroll pane and add the text area to it
	        JScrollPane scrollPane = new JScrollPane(textArea);
	        scrollPane.setPreferredSize(new Dimension(600,400));
	        tabP.add("PC Battle", scrollPane);
		}
		return tabP;
	}

}
