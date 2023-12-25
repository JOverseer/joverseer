/**
 * 
 */
package org.joverseer.ui.views;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;

import org.joverseer.ui.support.Messages;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.richclient.application.support.AbstractView;
import org.springframework.richclient.layout.TableLayoutBuilder;
import org.joverseer.ui.BaseView;

/**
 * View which allows players to write and submit their diplomatic message.
 * 
 * @author Sam Terrett
 */
public class DiploMessage extends BaseView implements ApplicationListener{
	JTextArea diplomaticMess;
	JPanel panel;
	JButton bt;
	JLabel lb;
	String inputDiplo;
	
	/**
	 * Creates layout of window in JOverseer
	 */
	@Override
	protected JComponent createControl() {
		TableLayoutBuilder tlb = new TableLayoutBuilder();
		
		tlb.cell(this.diplomaticMess = new JTextArea("Type your diplomatic message to send here, it can be up to 150 characters", 5, 50), "align=left");
		this.diplomaticMess.setWrapStyleWord(true);
		this.diplomaticMess.setLineWrap(true);
		this.diplomaticMess.getDocument().addDocumentListener(new MyDocumentListener());
		tlb.row();
		
		tlb.cell(this.lb = new JLabel(""), "align=left");
		tlb.row();
		
		tlb.cell(this.bt = new JButton("Submit"), "align=left");
		this.bt.setEnabled(false);
		

        this.bt.addActionListener(new ActionListener() {

            @Override
			public void actionPerformed(ActionEvent e) {
                saveMessage();
            }

        });
		
		this.panel = tlb.getPanel();
		this.panel.setPreferredSize(new Dimension(1200, 1200));
		
		return this.panel;
		
	}
	
	/**
	 * Document listener which does live character count, updating Jlabel + Jbutton accordingly
	 */
	class MyDocumentListener implements DocumentListener {

		@Override
		public void insertUpdate(DocumentEvent e) {
			// TODO Auto-generated method stub
			updateLabel(e);
			
		}

		@Override
		public void removeUpdate(DocumentEvent e) {
			// TODO Auto-generated method stub
			updateLabel(e);
		}

		@Override
		public void changedUpdate(DocumentEvent e) {
			// TODO Auto-generated method stub
			
		}
		
		/**
		 * Keeps track of how many characters the user has typed, updating the label to show the live count 
		 * and disabling/enabling the button to save accordingly
		 * 
		 * Doesn't use getters or setters, accesses them directly (not best coding practices :/)
		 * 
		 * @param e: The event that caused the method to be called
		 */
		public void updateLabel(DocumentEvent e) {
			Document doc = e.getDocument();
			lb.setText("Characters: " + Integer.toString(doc.getLength()) + "/150");
			if (doc.getLength() < 151) {
				bt.setEnabled(true);
			}
			else {
				bt.setEnabled(false);
			}
		}
		
	}

	@Override
	public void onApplicationEvent(ApplicationEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * After button press, saves diplomatic message which will be sent with orders.
	 * (NOT COMPLETE, DOESN'T SAVE YET)
	 * 
	 */
	private void saveMessage() {
		this.inputDiplo = this.diplomaticMess.getText();
		this.bt.setText(this.inputDiplo);
	}

}
