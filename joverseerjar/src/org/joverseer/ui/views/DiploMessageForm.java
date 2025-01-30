/**
 * 
 */
package org.joverseer.ui.views;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import org.joverseer.ui.support.JOverseerEvent;
import org.joverseer.ui.support.Messages;
import org.joverseer.ui.support.controls.DualJListSelector;
import org.joverseer.ui.support.controls.NationDualListSelector;
import org.joverseer.ui.support.controls.NationJList;
import org.joverseer.ui.support.dialogs.ErrorDialog;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.joverseer.domain.Diplo;
import org.joverseer.game.Turn;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.ui.BaseView;
import org.joverseer.ui.command.ExportDiploCommand;

import javax.swing.BoxLayout;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import javax.swing.SwingConstants;
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
	JLabel lbCurrentNations;
	JLabel lbCurrentDiplo;
	JLabel lbDiploReminder;
	JButton btSave;
	JButton btNationSave;
	JButton btReload;
	JButton btExportDiplo;
	JTextArea diplomaticMess;
	Diplo inputDiplo = null;
	NationJList nationList;
	JScrollPane listScroll;
	
	DualJListSelector testList;
	NationDualListSelector nationDList;
	
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
		//this.panel.setAlignmentX(Component.LEFT_ALIGNMENT);
		
		JPanel diploPanel = new JPanel();
		diploPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		
		this.panel.add(diploPanel, BorderLayout.CENTER);		

		JPanel nationPanel = new JPanel();
		diploPanel.add(nationPanel);
		nationPanel.setLayout(new BoxLayout(nationPanel, BoxLayout.Y_AXIS));
		nationPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		
		JPanel nationPanelInstr = new JPanel();
		nationPanelInstr.setLayout(new FlowLayout(FlowLayout.LEFT));
		nationPanel.add(nationPanelInstr);
		JLabel nationInstr = new JLabel("<html>Select which nations you control here<br/><font size=2>Double click on a nation to move it between lists.</font><html>", SwingConstants.LEFT);
		nationPanelInstr.add(nationInstr);
		
		JPanel listLabelPanel = new JPanel();
		listLabelPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
		nationPanel.add(listLabelPanel);
		
		Component horizontalStrut_2_1 = Box.createHorizontalStrut(5);
		listLabelPanel.add(horizontalStrut_2_1);
		
		JLabel lbSel = new JLabel("<html><font size=2><em>Selected:</em></font><html>");
		listLabelPanel.add(lbSel);
		
		Component horizontalStrut_2_2 = Box.createHorizontalStrut(75);
		listLabelPanel.add(horizontalStrut_2_2);
		
		JLabel lbUSel = new JLabel("<html><font size=2><em>Unselected:</em></font><html>");
		listLabelPanel.add(lbUSel);
		
		JPanel listPanel = new JPanel();
		listPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
		nationPanel.add(listPanel);

		this.nationDList = new NationDualListSelector(this.gameHolder);
		this.nationDList.setListSize(5, 90);
		setupListListeners();

		listPanel.add(this.nationDList);
		
		JPanel nationPanelButton = new JPanel();
		nationPanelButton.setLayout(new BoxLayout(nationPanelButton, BoxLayout.Y_AXIS));
		nationPanelButton.setAlignmentX(Component.LEFT_ALIGNMENT);

		listPanel.add(nationPanelButton);
		
		JPanel dipMessPanel = new JPanel();
		dipMessPanel.setLayout(new BoxLayout(dipMessPanel, BoxLayout.Y_AXIS));
		dipMessPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		diploPanel.add(dipMessPanel);
		
		Component box = Box.createRigidArea(new Dimension(1, 15));
		dipMessPanel.add(box);
		
		this.diplomaticMess = new JTextArea(Messages.getString("DiploMessageForm.DefaultTextArea"), 8, 40);
		this.diplomaticMess.setWrapStyleWord(true);
		this.diplomaticMess.setLineWrap(true);
		this.diplomaticMess.getDocument().addDocumentListener(new MyDocumentListener());
		this.diplomaticMess.setEditable(false);
		
		JScrollPane txtAreaScroll = new JScrollPane(this.diplomaticMess);
		dipMessPanel.add(txtAreaScroll);
		
		JPanel buttonPanel = new JPanel();
		diploPanel.add(buttonPanel);
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
		buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		
		this.lbLiveCount = new JLabel("");
		this.lbLiveCount.setAlignmentX(Component.LEFT_ALIGNMENT);
		buttonPanel.add(this.lbLiveCount);
		
		this.lbDiploReminder = new JLabel("");
		this.lbDiploReminder.setAlignmentX(Component.LEFT_ALIGNMENT);
		buttonPanel.add(this.lbDiploReminder);	
		
		JPanel pnl = new JPanel();
		pnl.setLayout(new BoxLayout(pnl, BoxLayout.X_AXIS));
		pnl.setAlignmentX(Component.LEFT_ALIGNMENT);
		
		this.btSave = new JButton(Messages.getString("DiploMessageForm.SaveMessageButton"));
		this.btSave.setHorizontalAlignment(SwingConstants.LEFT);
		this.btSave.setAlignmentX(Component.LEFT_ALIGNMENT);
		this.btSave.setEnabled(false);
		this.btSave.addActionListener(new ActionListener() {
			
	          @Override
				public void actionPerformed(ActionEvent e) {
	        	  saveMessage();
	          }
			});
		this.btExportDiplo = new JButton("Submit Diplo");
		ExportDiploCommand ep = new ExportDiploCommand(this.gameHolder);
		this.btExportDiplo.setEnabled(false);
		this.btExportDiplo.addActionListener(new ActionListener() {
			
	          @Override
				public void actionPerformed(ActionEvent e) {
	        	  ep.execute();
	          }
	    });
		
		pnl.add(this.btSave);
		pnl.add(this.btExportDiplo);
		buttonPanel.add(pnl);
		
		this.btReload = new JButton(Messages.getString("DiploMessageForm.ReloadButton"));
		this.btReload.setHorizontalAlignment(SwingConstants.LEFT);
		this.btReload.setAlignmentX(Component.LEFT_ALIGNMENT);
		this.btReload.setEnabled(false);
		this.btReload.addActionListener(new ActionListener() {
			
	          @Override
				public void actionPerformed(ActionEvent e) {
	        	  if(DiploMessageForm.this.inputDiplo.getMessage() == null) {
	      			ErrorDialog.showErrorDialog("No saved message");
	    			return;
	        	  }
	        	  DiploMessageForm.this.diplomaticMess.setText(DiploMessageForm.this.inputDiplo.getMessage());
	        	  DiploMessageForm.this.btReload.setEnabled(false);
	          }
			});
		buttonPanel.add(this.btReload);

		JScrollPane sp = new JScrollPane(this.panel);
		sp.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
		sp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		return sp;
	}
	
	/**
	 * Document listener which detects changes in JTextArea to update the rest of the panel accordingly.
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
	 * Attaches listeners to both the lists, so if any changes are made to either the saved nations gets updated.
	 */
	private void setupListListeners() {
        this.nationDList.getSelectedList().addListSelectionListener(new ListSelectionListener() {
        	boolean busy = false;
            @Override
            public void valueChanged(ListSelectionEvent arg0) {
            	if(!this.busy) {
            		this.busy = true;
            		saveNationSelection();
            		this.busy = false;
            	}
            }
        });	
        
        this.nationDList.getDeSelectedList().addListSelectionListener(new ListSelectionListener() {
        	boolean busy = false;
            @Override
            public void valueChanged(ListSelectionEvent arg0) {
            	if(!this.busy) {
            		this.busy = true;
            		saveNationSelection();
            		this.busy = false;
            	}
            }
        });	
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
	 * Keeps live character count updated, and other parts of the UI up to date as needed.
	 * 
	 * @param len: Current character count 
	 */
	private void updateLiveLabel(int len) {
		String colour = "black";
		String charCount = len + "/" + Integer.toString(Diplo.charPerNation * this.nationsCount);
		
		//This if statement changes label red or black depending on if user is over char limit, as well as uncapping the limit if indicated in xml file by '-1'
		if (Diplo.charPerNation == -1) {
			charCount = "Unlimited";
			this.btSave.setEnabled(true);
		}
		else if (len < (Diplo.charPerNation * this.nationsCount) + 1) {
			this.btSave.setEnabled(true);
		}
		else {
			this.btSave.setEnabled(false);
			colour = "red";
		}	
		
		String txt = String.format("<html>Character count: <font color='%s'>%s</font><br/>Note: You don't have to include your own nation names!<html>", colour, charCount);
		this.lbLiveCount.setText(txt);
				
		if (this.inputDiplo.getMessage() != null) {
			this.btReload.setEnabled(true);
			this.btReload.setText(Messages.getString("DiploMessageForm.ReloadButton"));
		}
		
	}
	
	/**
	 * Updates which nations the diplo is for, updating saved info and UI as needed.
	 */
	private void saveNationSelection() {
		String[] nations = this.nationDList.getSelectedNations();

		this.nationsCount = nations.length;
		this.nationsSelection = nations;
		this.inputDiplo.setNations(nations);
	
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
            this.loadDiplo(true);

            break;
    	case SelectedTurnChangedEvent:    
    		this.loadDiplo(false);
              
            break;
    	}
    }
    
    /**
     * Handles the loading and creation of diplos between turns and when loading games, 
     * making sure all the necessary data is instantiated
     */
    private void loadDiplo(boolean gameChange) {
    	Turn t = this.gameHolder.getGame().getTurn();
    	this.btExportDiplo.setEnabled(true);

    	if (t.getNationDiplo(this.getGame().getMetadata().getNationNo()) != null) {	//If diplo exists in save file, then get it
    		this.inputDiplo = t.getNationDiplo(this.gameHolder.getGame().getMetadata().getNationNo());
    		this.diplomaticMess.setText(this.inputDiplo.getMessage());
    	}
    	else {	
    		this.inputDiplo = new Diplo();
    		this.inputDiplo.setNationNo(this.gameHolder.getGame().getMetadata().getNationNo());
    	}
    	
        if (this.inputDiplo.getMessage() == null) {		//Update UI based on if message is saved or not
        	this.btReload.setText(Messages.getString("DiploMessageForm.ReloadButtonNone"));
        	this.btReload.setEnabled(false);
        }
        else {
        	this.btReload.setText(Messages.getString("DiploMessageForm.ReloadButton"));
        }
        
    	if(this.nationsSelection == null || gameChange) {		//Makes sure that either the previous selection of nations is re-applied or get default set nation
    		if (this.inputDiplo.getNations() == null) {
        		this.nationsSelection = (new String[] {this.gameHolder.getGame().getMetadata().getNationByNum(this.gameHolder.getGame().getMetadata().getNationNo()).getName()});
        		this.inputDiplo.setNations(this.nationsSelection);
        		this.refreshList(true, null);
    		}
    		else {
    			this.nationsSelection = this.inputDiplo.getNations();
    			this.refreshList(false, this.getNationNos());
    		}

    	}
    	else {
    		this.inputDiplo.setNations(this.nationsSelection);
    		this.refreshList(false, this.getNationNos());
    	}
    	this.nationsCount = this.nationsSelection.length;
        
        //Set label reminding users a diplo isn't due on a turn
        if (t.getPlayerInfo().size() != 0) {
        	if (!t.getPlayerInfo(this.gameHolder.getGame().getMetadata().getNationNo()).isDiploDue()) {
        		this.lbDiploReminder.setText(Messages.getString("DiploMessageForm.DiploReminderLabel"));
        		this.btExportDiplo.setEnabled(false);
      		}
        	else this.lbDiploReminder.setText("");
        }
        
        else {
        	this.lbDiploReminder.setText("");
        }
        
        //this.btNationSave.setEnabled(true);
        this.diplomaticMess.setEditable(true);
        if (this.inputDiplo.getMessage() == null) {		//Load message into text area
        	this.diplomaticMess.setText(Messages.getString("DiploMessageForm.DefaultTextAreaNewGame"));
        }
        else {
        	this.diplomaticMess.setText(this.inputDiplo.getMessage());
        }
        
        this.updateLiveLabel(this.diplomaticMess.getText().length());	//Call to refresh alot of the other components in UI
        this.btSave.setEnabled(true);
    }
    
    /**
     * Refreshes nation lists content. 
     */
    private void refreshList(boolean autoFocus, int[] inputNationNos) {
    	this.nationDList.load(autoFocus, false, inputNationNos);
    	this.nationDList.refreshLists();
    }
    
    /**
     * 
     * @return int[] of the saved nation numbers
     */
    private int[] getNationNos() {
    	int[] nos = new int[this.nationsSelection.length];
    	for (int i = 0; i < nos.length; i++) {
    		nos[i] = this.gameHolder.getGame().getMetadata().getNationByName(this.nationsSelection[i]).getNumber();
    	}
    	
    	return nos;
    }

    /**
     * Saves diplo in the turns container.
     */
    private void saveMessage() {
    	Turn t = this.gameHolder.getGame().getTurn();
    	saveNationSelection();
    	
    	this.inputDiplo.setMessage(this.diplomaticMess.getText());
    	this.btReload.setEnabled(false);
    	this.btReload.setText(Messages.getString("DiploMessageForm.ReloadButton"));

        if (!t.getContainer(TurnElementsEnum.Diplo).contains(this.inputDiplo)) {
        	t.getContainer(TurnElementsEnum.Diplo).clear();
        	t.getContainer(TurnElementsEnum.Diplo).addItem(this.inputDiplo);
        }
        //this.lbCurrentDiplo.setText("<html>Current saved diplomatic message: <br/>" + t.getNationDiplo(this.gameHolder.getGame().getMetadata().getNationNo()).getMessage() + "<html>");
    	
    }

}
