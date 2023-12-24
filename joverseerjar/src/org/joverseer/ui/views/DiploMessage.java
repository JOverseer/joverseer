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
 * 
 */
public class DiploMessage extends BaseView implements ApplicationListener{
	JTextArea diplomaticMess;
	JPanel panel;
	JButton bt;
	JLabel lb;
	String inputDiplo;
	
	@Override
	protected JComponent createControl() {
		TableLayoutBuilder tlb = new TableLayoutBuilder();
		tlb.cell(this.diplomaticMess = new JTextArea("Type your diplomatic message to send here", 5, 50), "align=left");
		this.diplomaticMess.setWrapStyleWord(true);
		this.diplomaticMess.setLineWrap(true);
		this.diplomaticMess.getDocument().addDocumentListener(new MyDocumentListener());
		tlb.row();
		tlb.cell(this.bt = new JButton("Submit"), "align=left");
		tlb.gapCol();
		tlb.cell(this.lb = new JLabel(""), "align=left");

        this.bt.addActionListener(new ActionListener() {

            @Override
			public void actionPerformed(ActionEvent e) {
                saveMessage();
            }

        });
		//this.header = new JLabel();

//		JPanel p = new JPanel();
//		p.add(diplomaticMess);
//		
//		TableLayoutBuilder tlb = new TableLayoutBuilder();
//		tlb.cell(new JLabel("Here")); //$NON-NLS-1$
//		tlb.gapCol();
		
		this.panel = tlb.getPanel();
		this.panel.setPreferredSize(new Dimension(1200, 1200));
		return this.panel;
		
	}
	
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
		
		public void updateLabel(DocumentEvent e) {
			Document doc = e.getDocument();
			lb.setText(Integer.toString(doc.getLength()));
		}
		
	}

	@Override
	public void onApplicationEvent(ApplicationEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	private void saveMessage() {
		this.inputDiplo = this.diplomaticMess.getText();
		this.bt.setText(this.inputDiplo);
	}

}
