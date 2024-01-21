/**
 * 
 */
package org.joverseer.ui.views;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import org.joverseer.ui.support.JOverseerEvent;
import org.joverseer.ui.support.Messages;
import org.joverseer.ui.support.controls.NationComboBox;
import org.joverseer.ui.support.controls.NationJList;
import org.joverseer.ui.support.dialogs.ErrorDialog;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.application.support.AbstractView;
import org.springframework.richclient.layout.TableLayoutBuilder;
import org.springframework.richclient.progress.BusyIndicator;
import org.joverseer.domain.Diplo;
import org.joverseer.game.Game;
import org.joverseer.game.Turn;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.metadata.domain.Nation;
import org.joverseer.support.GameHolder;
import org.joverseer.ui.BaseView;
import org.joverseer.ui.command.AddEditNoteCommand;
import org.joverseer.ui.economyCalculator.EconomyCalculator;
import org.joverseer.ui.economyCalculator.EconomyTotalsTableModel;
import org.joverseer.ui.economyCalculator.MarketTableModel;
import java.awt.GridLayout;
import javax.swing.BoxLayout;

import static org.assertj.core.api.Assertions.assertThatIllegalStateException;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagLayout;
import javax.swing.SpringLayout;
import java.awt.List;
import java.awt.FlowLayout;
import javax.swing.SwingConstants;
import javax.swing.JList;
import javax.swing.JSeparator;
import javax.swing.JScrollPane;
import javax.swing.Box;

/**
 * View which allows players to write and submit their diplomatic message.
 * 
 * @author Sam Terrett
 */
public class DiploMessageForm extends BaseView implements ApplicationListener{
	JPanel panel;
	JLabel lbLiveCount;
	JLabel lbCurrentDiplo;
	JButton btSave;
	JButton btNationSave;
	JTextArea diplomaticMess;
	Diplo inputDiplo = null;
	NationJList nationList;
	JScrollPane listScroll;
	
	int nationsCount = 1;
	String[] nationsSelection = null;

	
	/**
	 * Creates layout of window in JOverseer
	 * @wbp.parser.entryPoint
	 */
	@Override
	protected JComponent createControl() {
		this.panel = new JPanel();
		this.panel.setLayout(new BorderLayout());
		this.panel.setAlignmentX(Component.LEFT_ALIGNMENT);
		
		JPanel diploPanel = new JPanel();
		diploPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		
		this.panel.add(diploPanel, BorderLayout.PAGE_START);		

		this.diplomaticMess = new JTextArea("Load a game to begin writing a diplomatic message.", 10, 50);
		this.diplomaticMess.setAlignmentX(Component.LEFT_ALIGNMENT);
		this.diplomaticMess.setWrapStyleWord(true);
		this.diplomaticMess.setLineWrap(true);
		this.diplomaticMess.getDocument().addDocumentListener(new MyDocumentListener());
		this.diplomaticMess.setEditable(false);
		
		JScrollPane txtAreaScroll = new JScrollPane(this.diplomaticMess);
		diploPanel.add(txtAreaScroll);
		
		Component box = Box.createRigidArea(new Dimension(20, 1));
		diploPanel.add(box);
		
		JPanel nationPanel = new JPanel();
		diploPanel.add(nationPanel);
		nationPanel.setLayout(new BoxLayout(nationPanel, BoxLayout.Y_AXIS));
		nationPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		
		JPanel nationPanelInstr = new JPanel();
		nationPanelInstr.setLayout(new FlowLayout(FlowLayout.LEFT));
		nationPanel.add(nationPanelInstr);
		JLabel nationInstr = new JLabel("<html>Select which nations you control here<br/>To select multiple nations, hold 'ctrl' ('cmd' on mac) and click<html>", SwingConstants.LEFT);
		nationPanelInstr.add(nationInstr);
		
		JPanel listPanel = new JPanel();
		listPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		nationPanel.add(listPanel);
		
		
		
		this.nationList = new NationJList(this.gameHolder);
		
		this.nationList.setPreferredSize(new Dimension(100,150));
		this.nationList.setVisibleRowCount(6);
		this.listScroll = new JScrollPane(this.nationList, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		listPanel.add(this.listScroll);	
		
		JPanel nationPanelButton = new JPanel();
		nationPanelButton.setLayout(new FlowLayout(FlowLayout.LEFT));
		nationPanel.add(nationPanelButton);
		
		this.btNationSave = new JButton("Save Selection");
		this.btNationSave.setEnabled(false);
		this.btNationSave.addActionListener(new ActionListener() {
			
          @Override
			public void actionPerformed(ActionEvent e) {
        	  saveNationSelection();
          }
		});
		nationPanelButton.add(this.btNationSave);
		
		JPanel buttonPanel = new JPanel();
		this.panel.add(buttonPanel, BorderLayout.CENTER);
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
		buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		
		this.lbLiveCount = new JLabel("");
		this.lbLiveCount.setAlignmentX(Component.LEFT_ALIGNMENT);
		buttonPanel.add(this.lbLiveCount);
		
		this.btSave = new JButton("Save Message");
		this.btSave.setHorizontalAlignment(SwingConstants.LEFT);
		this.btSave.setAlignmentX(Component.LEFT_ALIGNMENT);
		this.btSave.setEnabled(false);
		this.btSave.addActionListener(new ActionListener() {
			
	          @Override
				public void actionPerformed(ActionEvent e) {
	        	  saveMessage();
	          }
			});
		buttonPanel.add(this.btSave);
		
		this.lbCurrentDiplo = new JLabel("");
		this.lbCurrentDiplo.setHorizontalAlignment(SwingConstants.LEFT);
		this.lbCurrentDiplo.setAlignmentX(Component.LEFT_ALIGNMENT);
		buttonPanel.add(this.lbCurrentDiplo);
		
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

			try {
				updateLiveLabel(countDiploLength(doc.getText(0, doc.getLength())));
			} catch (BadLocationException e1) {
				// TODO Auto-generated catch block
				updateLiveLabel(doc.getLength());
				e1.printStackTrace();
			}
			
		}
	}
	
	/**
	 * Gets the character count of the diplo message without including hte new line characters
	 * @param txt: The diplomatic message
	 * @return: returns the lenght of the filtered message
	 */
	private int countDiploLength(String txt) {
		String temp = txt.replace("\n", "");
		
		return temp.length();
	}
	
	/**
	 * Keeps live character count updated, updating the UI as needed.
	 * 
	 * @param len: Current character count 
	 */
	private void updateLiveLabel(int len) {
		String colour;
		if (len < (Diplo.charPerNation * this.nationsCount) + 1) {
			this.btSave.setEnabled(true);
			colour = "black";
		}
		else {
			this.btSave.setEnabled(false);
			colour = "red";
		}	
		String txt = String.format("<html>Characters: <font color='%s'>%d</font>/%d<br/>Nations you are submitting a diplo for: %s<br/>Note: You don't have to include your own nation names!<html>", colour, len, (Diplo.charPerNation * this.nationsCount), String.join(", ", this.inputDiplo.getNations()));
		this.lbLiveCount.setText(txt);
	}
	
	/**
	 * Updates which nations the diplo is for, updating saved info and UI as needed.
	 */
	private void saveNationSelection() {
		String[] nations = this.nationList.getSelectedNations();
		if (nations == null) {
			ErrorDialog.showErrorDialog("No nation selected");
			return;
		}
		this.nationsCount = nations.length;
		this.nationsSelection = nations;
		this.inputDiplo.setNations(nations);
		//updateLiveLabel(this.diplomaticMess.getText().length());	
		updateLiveLabel(countDiploLength(this.diplomaticMess.getText()));		//Called to adjust the maximum character count as needed
	}
	
    @Override
	public void onApplicationEvent(ApplicationEvent applicationEvent) {
        if (applicationEvent instanceof JOverseerEvent) {
            this.onJOEvent((JOverseerEvent) applicationEvent);
        }
    }
	
    public void onJOEvent(JOverseerEvent e) {
    	switch(e.getType()) {
    	case GameChangedEvent:
        	super.resetGame();
            refreshList();
            this.loadDiplo();
            this.btNationSave.setEnabled(true);
            this.diplomaticMess.setEditable(true);
            if (this.inputDiplo.getMessage() == null) {
            	this.diplomaticMess.setText("");
            }
            else {
            	this.diplomaticMess.setText(this.inputDiplo.getMessage());

            }
            this.updateLiveLabel(this.diplomaticMess.getText().length());
            this.btSave.setEnabled(true);
            break;
    	case SelectedTurnChangedEvent:    
        	//super.resetGame();
    		System.out.println("Turn change");
    		
            refreshList();
            this.loadDiplo();
            this.btNationSave.setEnabled(true);
            this.diplomaticMess.setEditable(true);
            if (this.inputDiplo.getMessage() == null) {
            	this.diplomaticMess.setText("");
            }
            else {
            	this.diplomaticMess.setText(this.inputDiplo.getMessage());

            }
            this.updateLiveLabel(this.diplomaticMess.getText().length());
            this.btSave.setEnabled(true);
            
            
            break;
    	}
    }
    
    /**
     * Handles the loading and creation of diplos between turns and when loading games, 
     * making sure all the necessary data is instantiated
     */
    private void loadDiplo() {
    	Turn t = this.gameHolder.getGame().getTurn();
    	
    	if (t.getContainer(TurnElementsEnum.Diplo).size() != 0) {	//If diplo exists in save file, then get it
    		this.inputDiplo = t.getNationDiplo(this.gameHolder.getGame().getMetadata().getNationNo());
    		this.diplomaticMess.setText(this.inputDiplo.getMessage());
    	}
    	else {	
    		this.inputDiplo = new Diplo();
    		this.inputDiplo.setNationNo(this.gameHolder.getGame().getMetadata().getNationNo());
    	}
    	
    	if(this.nationsSelection == null) {		//Makes sure that either the previous selection of nations is re-applied or get default set nation
    		if (this.inputDiplo.getNations() == null) {
        		this.nationsSelection = (new String[] {this.gameHolder.getGame().getMetadata().getNationByNum(this.gameHolder.getGame().getMetadata().getNationNo()).getName()});
        		this.inputDiplo.setNations(this.nationsSelection);
    		}
    		else {
    			this.nationsSelection = this.inputDiplo.getNations();
    			this.nationsCount = this.nationsSelection.length;
    		}

    	}
    	else {
    		this.inputDiplo.setNations(this.nationsSelection);
    		this.nationsCount = this.nationsSelection.length;
    	}
    	
        if (this.inputDiplo.getMessage() == null) {		//Update UI based on if message is saved or not
        	this.lbCurrentDiplo.setText("No saved Diplomatic Message in game currently.");
        }
        else {
        	this.lbCurrentDiplo.setText("<html>Current saved diplomatic message: <br/>" + this.inputDiplo.getMessage() + "<html>");
        }
    }
    
    /**
     * Refreshes nation lists content. 
     */
    private void refreshList() {
    	this.nationList.load(true, true);
    	this.listScroll.repaint();
    }

    /**
     * Saves diplo in the turns container.
     */
    private void saveMessage() {
    	Turn t = this.gameHolder.getGame().getTurn();
    	this.inputDiplo.setMessage(this.diplomaticMess.getText());

        if (!t.getContainer(TurnElementsEnum.Diplo).contains(this.inputDiplo)) {
        	t.getContainer(TurnElementsEnum.Diplo).clear();
        	t.getContainer(TurnElementsEnum.Diplo).addItem(this.inputDiplo);
        }
        this.lbCurrentDiplo.setText("<html>Current saved diplomatic message: <br/>" + t.getNationDiplo(this.gameHolder.getGame().getMetadata().getNationNo()).getMessage() + "<html>");
    	
    }

}
