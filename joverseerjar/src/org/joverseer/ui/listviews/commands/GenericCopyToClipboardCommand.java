package org.joverseer.ui.listviews.commands;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;

import javax.swing.JTable;
import javax.swing.table.TableModel;

import org.joverseer.game.Game;
import org.joverseer.support.GameHolder;
import org.springframework.richclient.command.ActionCommand;

public class GenericCopyToClipboardCommand extends ActionCommand implements ClipboardOwner {
	JTable table;
    String DELIM = "\t";
    String NL = "\n";
    Game game;

    
    public GenericCopyToClipboardCommand(JTable table) {
		super("genericCopyToClipboardCommand");
		this.table = table;
	}

	@Override
	protected void doExecuteCommand() {
        this.game = GameHolder.instance().getGame();
        String txt = "";
        TableModel tableModel = this.table.getModel();
        for (int j=0; j<tableModel.getColumnCount(); j++) {
        	txt += (txt.equals("") ? "" : this.DELIM) + tableModel.getColumnName(j);
        }
        
        for (int i = 0; i < tableModel.getRowCount(); i++) {
        	String row = "";
        	for (int j=0; j<tableModel.getColumnCount(); j++) {
        		Object v = tableModel.getValueAt(i, j);
        		if (v == null) v = "";
        		row += (row.equals("") ? "" : this.DELIM) + v.toString();
        	}
        	txt += (txt.equals("") ? "" : this.NL) + row;
        }
        StringSelection stringSelection = new StringSelection(txt);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, this);
    }

	@Override
	public void lostOwnership(Clipboard arg0, Transferable arg1) {
		
		
	}
}
