package org.joverseer.ui.views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
		
		JEditorPane jp = new JEditorPane();
		jp.setContentType("text/html");
		jp.setEditable(false);
		jp.setCaretColor(Color.WHITE);
		jp.setText("<div style='font-family:MS Sans Serif; font-size:11pt'><i>" + "Right click on a tab to delete it or edit the title. When adding a new tab or changing a title you cannot name it the same as one that already exists.");
		p.add(jp, BorderLayout.PAGE_END);
		
		this.init = true;
		refresh(0);
		return p;
	}
	
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
	
	public void addTab(String title, String note) {
		JTextPane tP = new JTextPane();
		int i = this.tabPane.getTabCount();
		
		tP.setText(note);
		
		JScrollPane scp = new JScrollPane(tP);
		this.tabPane.add(scp);
		this.tabPane.setTabComponentAt(i, new JLabel(title));

		tP.getDocument().addDocumentListener(new MyDocumentListener(i));
		this.tabPane.getTabComponentAt(i).addMouseListener(new TabPaneMouseAdapter(i));
	}
	
	private void refresh(int selInd) {
		this.tabPane.removeAll();
		
		if (this.getNotepadInfo() == null) return;
		
		ArrayList<String> titles = this.nI.getNoteTitles();
		ArrayList<String> notes = this.nI.getNotes();
		for(int i = 0; i < titles.size(); i++) {
			this.addTab(titles.get(i), notes.get(i));
		}
		
		this.addAddTab();
		
		this.tabPane.setSelectedIndex(selInd);
	}
	
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
	
	class TabPaneMouseAdapter extends MouseAdapter {
		int ind;
		public TabPaneMouseAdapter(int ind) {
			this.ind = ind;
		}
		
	    @Override
	    public void mouseClicked(MouseEvent e) 
	    {
	    	if (e.getComponent() instanceof JButton) return;
	    	if(e.getButton() == MouseEvent.BUTTON1) {
	    		Notepad.this.tabPane.setSelectedIndex(this.ind);
	    	}
	        if(e.getButton() == MouseEvent.BUTTON3) {
	            JPopupMenu menu = new JPopupMenu(); 
	            menu.add(new JMenuItem(new CloseAction(this.ind)));
	            menu.add(new JMenuItem(new EditTitleAction(this.ind)));
	            menu.show(Notepad.this.tabPane, e.getX(), e.getY());
	        }
	    }
	}
	
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
			} while(!Notepad.this.nI.newNote(newTitle, ""));
			Notepad.this.refresh(Notepad.this.tabPane.getTabCount());
		}
	}
	
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
