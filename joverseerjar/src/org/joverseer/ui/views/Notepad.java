package org.joverseer.ui.views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import org.joverseer.domain.NotepadInfo;
import org.joverseer.game.Game;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.support.GameHolder;
import org.joverseer.ui.LifecycleEventsEnum;
import org.joverseer.ui.support.JOverseerEvent;
import org.joverseer.ui.support.Messages;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.richclient.application.support.AbstractView;

/**
 * Notepad panel
 * Allows different tabs of notes, like OneNote.
 * Different from notes, not attached to any particular objects, just a generic notepad, persistent between turns.
 *
 * @author Sam Terrett
 */
public class Notepad extends AbstractView implements ApplicationListener{
	boolean init = false;
	
	JTextPane textP;
	JTabbedPane tabPane;
	NotepadInfo nI;

	//injected dependencies
	GameHolder gameHolder;
	public GameHolder getGameHolder() {
		return this.gameHolder;
	}
	
	public void setGameHolder(GameHolder gameHolder) {
		this.gameHolder = gameHolder;
	}
	
	@Override
	public void onApplicationEvent(ApplicationEvent arg0) {
		if (arg0 instanceof JOverseerEvent) {
			JOverseerEvent e = (JOverseerEvent) arg0;
			if (e.isLifecycleEvent(LifecycleEventsEnum.GameChangedEvent)) {
				if(this.init == false) return;
				refresh(0);
			}
			if (e.isLifecycleEvent(LifecycleEventsEnum.SelectedTurnChangedEvent)) {
				if(this.init == false) return;
				refresh(this.tabPane.getSelectedIndex());
			}
		}
	}
	
	public JTextPane getTextPaneAt(int ind) {
		return (((JTextPane)((JScrollPane) this.tabPane.getComponentAt(ind)).getViewport().getView()));
	}
	
	public String getNoteAt(int ind) {
		return getTextPaneAt(ind).getText();
	}

	@Override
	protected JComponent createControl() {
		// TODO Auto-generated method stub
		JPanel p = new JPanel();
		p.setLayout(new BorderLayout());
		
		this.tabPane = new JTabbedPane();
		
		p.add(this.tabPane, BorderLayout.CENTER);
		
		//Small text pane at bottom to tell user how to use pane
		JEditorPane jp = new JEditorPane();
		jp.setContentType("text/html");
		jp.setEditable(false);
		jp.setCaretColor(Color.WHITE);
		jp.setText("<div style='font-family:MS Sans Serif; font-size:11pt'><i>" + Messages.getString("NotePad.instructions"));
		p.add(jp, BorderLayout.PAGE_END);
		
		this.init = true;
		refresh(0);
		return p;
	}
	
	/**
	 * Gets the users notes for the game.
	 * @return the notepadInfo stored in the turn, which contains all the users notes for the game.
	 */
	private NotepadInfo getNotepadInfo() {
		if(this.gameHolder == null) return null;
		NotepadInfo nI1 = null;
		Game g = this.getGameHolder().getGame();
		if(g == null) return null;
		if(g.getTurn().getNotepadInfo() == null) {
			for (int i = 0; i <= g.getMaxTurn(); i++) {
				if(g.getTurn(i).getNotepadInfo() == null) continue;
				nI1 = g.getTurn(i).getNotepadInfo();
				break;
			}
			if (nI1 == null) nI1 = new NotepadInfo();
			g.getTurn().getContainer(TurnElementsEnum.NotepadInfo).addItem(nI1);
		}
		else {
			nI1 = g.getTurn().getNotepadInfo();
		}
		this.nI = nI1;
		return nI1;
	}
	
	/**
	 * Adds a tab to the tabbedPane, populating it with the note and the notes title
	 * @param title: The title of the note, what the tab should be named.
	 * @param note: The content of the note itself, which will be loaded into the JEditorPane of the tab to be viewed and edited by user.
	 */
	public void addTab(String title, String note) {
		JTextPane tP = new JTextPane();
		int i = this.tabPane.getTabCount();
		
		tP.setText(note);
		
		JScrollPane scp = new JScrollPane(tP);
		this.tabPane.add(scp);
		this.tabPane.setTabComponentAt(i, new JLabel(title));	//Setting title like this allows us to attach a mouse listener to the JLabel, so we can add a right click menu

		tP.getDocument().addDocumentListener(new MyDocumentListener(i));	//Updates and saves the note as the user types.
		this.tabPane.getTabComponentAt(i).addMouseListener(new TabPaneMouseAdapter(i));	
	}
	
	/**
	 * Refresh the pane, used when a game is loaded, or when a tab is added or removed. 
	 * It collects the users notes from the turn, and adds all of them as tabs. 
	 * @param selInd: Which tab index to be selected after loading
	 */
	private void refresh(int selInd) {
		this.tabPane.removeAll();
		
		if (this.getNotepadInfo() == null) {
			JEditorPane jp = new JEditorPane();
			jp.setContentType("text/html");
			jp.setEditable(false);
			jp.setCaretColor(Color.WHITE);
			jp.setText("<div style='font-family:MS Sans Serif; font-size:13pt'><i>" + Messages.getString("NotePad.preLoadGame"));
			this.tabPane.add(jp);
			return;
		}
		
		ArrayList<String> titles = this.nI.getNoteTitles();
		ArrayList<String> notes = this.nI.getNotes();
		for(int i = 0; i < titles.size(); i++) {
			this.addTab(titles.get(i), notes.get(i));
		}
		
		this.addAddTab();
		
		this.tabPane.setSelectedIndex(selInd);
	}
	
	/**
	 * Weird name... adds a '+' tab which when clicked, allows the user to add a new note and therefore tab.
	 */
	public void addAddTab() {
		int i = this.tabPane.getTabCount();
		
		JButton addButton = new JButton(new AddNoteAction());
		addButton.setBorder(null);
		addButton.setFocusPainted(false);
		addButton.setContentAreaFilled(false);
		addButton.setPreferredSize(new Dimension(30, 30));
		
		this.tabPane.addTab("", null);;
		this.tabPane.setTabComponentAt(i, addButton);

		this.tabPane.getTabComponentAt(i).addMouseListener(new TabPaneMouseAdapter(i));
	}
	
	/**
	 * MouseAdaptor for the tab titles.
	 */
	class TabPaneMouseAdapter extends MouseAdapter {
		int ind;
		public TabPaneMouseAdapter(int ind) {
			this.ind = ind;
		}
		
	    @Override
	    public void mouseClicked(MouseEvent e) 
	    {
	    	//This stops the user from right clicking on the '+' tab
	    	if (e.getComponent() instanceof JButton) return;
	    	
	    	if(e.getButton() == MouseEvent.BUTTON1) {
	    		Notepad.this.tabPane.setSelectedIndex(this.ind);
	    	}
	    	//Generates menu on right click on tab.
	        if(e.getButton() == MouseEvent.BUTTON3) {
	            JPopupMenu menu = new JPopupMenu(); 
	            menu.add(new JMenuItem(new CloseAction(this.ind)));
	            menu.add(new JMenuItem(new EditTitleAction(this.ind)));
	            menu.show(Notepad.this.tabPane, e.getX(), e.getY());
	        }
	    }
	}
	
	/**
	 * The action added to the button to add a new tab
	 */
	class AddNoteAction extends AbstractAction{
		private static final long serialVersionUID = 1243168452698565591L;
		
		public AddNoteAction(){
			super("+");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			String newTitle;
			do {
				newTitle = (String)JOptionPane.showInputDialog("Input a new note title that doesn't currently exist:");
			} while(!Notepad.this.nI.newNote(newTitle, ""));	//Adds note to backend then refreshes pane to reload info.
			Notepad.this.refresh(Notepad.this.tabPane.getTabCount());
		}
	}
	
	/**
	 * The close action which is available upon right clicking a tab.
	 * It deletes said note from the backend then refreshes the pane.
	 */
	class CloseAction extends AbstractAction{
		int ind;
		
		public CloseAction(int ind){
			super("Delete");
			this.ind = ind;
		}

		private static final long serialVersionUID = 2123854697257380604L;

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			String title = ((JLabel) Notepad.this.tabPane.getTabComponentAt(this.ind)).getText();
			if(Notepad.this.nI.removeNote(title)) Notepad.this.refresh(0);
		}
		
	}
	
	/**
	 * The edit action which is available upon right clicking a tab.
	 * It edits the title of said note on the backend then refreshes the pane.
	 */
	class EditTitleAction extends AbstractAction{
		private static final long serialVersionUID = 2239120300087720316L;
		int ind;
		
		public EditTitleAction(int ind){
			super("Edit Title");
			this.ind = ind;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			String title = ((JLabel) Notepad.this.tabPane.getTabComponentAt(this.ind)).getText();
			String newTitle;
			do {
				newTitle = (String)JOptionPane.showInputDialog("Input a new note title that doesn't currently exist:", title);
			} while(!Notepad.this.nI.updateNoteTitle(title, newTitle));
			Notepad.this.refresh(this.ind);
		}
		
	}

	/**
	 * Document Listener detects if a note has been changed (more typed or deleted) and updates note contents on the backend
	 */
	class MyDocumentListener implements DocumentListener {
		int ind;
		public MyDocumentListener(int ind) {
			this.ind = ind;
		}

		@Override
		public void insertUpdate(DocumentEvent e) {
			// TODO Auto-generated method stub
			updateNote(e);
			
		}

		@Override
		public void removeUpdate(DocumentEvent e) {
			// TODO Auto-generated method stub
			updateNote(e);
		}

		@Override
		public void changedUpdate(DocumentEvent e) {

			
		}

		public void updateNote(DocumentEvent e) {
			Document doc = e.getDocument();
			try {
				Notepad.this.nI.updateNote(((JLabel) Notepad.this.tabPane.getTabComponentAt(this.ind)).getText(), doc.getText(0, doc.getLength()));
			} catch (BadLocationException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
		}
	}

}
