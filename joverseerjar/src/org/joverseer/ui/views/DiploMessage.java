/**
 * 
 */
package org.joverseer.ui.views;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.joverseer.ui.support.Messages;
import org.springframework.richclient.application.support.AbstractView;
import org.springframework.richclient.layout.TableLayoutBuilder;
import org.joverseer.ui.BaseView;

/**
 * 
 */
public class DiploMessage extends BaseView {
	JTextField DiplomaticMess;
	JPanel panel;
	
	@Override
	protected JComponent createControl() {
		this.DiplomaticMess = new JTextField();
		this.panel = new JPanel();
		this.panel.add(this.DiplomaticMess);
//		JPanel p = new JPanel();
//		p.add(DiplomaticMess);
//		
//		TableLayoutBuilder tlb = new TableLayoutBuilder();
//		tlb.cell(new JLabel("Here")); //$NON-NLS-1$
//		tlb.gapCol();


		return this.panel;
		
	}

}
