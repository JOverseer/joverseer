package org.joverseer.ui.support.controls;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

public class CheckBoxList extends JPanel {

	private static final long serialVersionUID = 1L;
	
	private ArrayList<JCheckBox> list;
	public ItemListener iL;
	
	public CheckBoxList(ItemListener itemListener) {
		super();
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.list = new ArrayList<JCheckBox>();
		this.iL = itemListener;
	}

	public void setList(ArrayList<String> newList) {
		this.removeAll();
		this.list.clear();
		
		for(int i = 0; i < newList.size(); i++) {
			this.addItem(newList.get(i));
		}
	}
	
	public void addItem(String item) {
		JCheckBox ch = new JCheckBox(item);
		ch.addItemListener(this.iL);
		ch.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				JCheckBox ch = (JCheckBox) e.getSource();
				if (ch.isSelected()) ch.setFont(new Font(ch.getFont().getName(), Font.BOLD, ch.getFont().getSize()));
				else ch.setFont(new Font(ch.getFont().getName(), Font.PLAIN, ch.getFont().getSize()));
			}
			
		});
		this.list.add(ch);
		this.add(ch);
		
	}
	
	public ArrayList<String> getSelected(){
		ArrayList<String> sel = new ArrayList<String>();
		for (JCheckBox ch : this.list) {
			if (ch.isSelected()) System.out.println(ch.getText());
			if (ch.isSelected()) sel.add(ch.getText());
		}
		System.out.println(sel.size());
		return sel;
	}
	
	public void setSelected(ArrayList<String> selItems) {
		for (JCheckBox ch : this.list) {
			if(selItems.contains(ch.getText())) {
				ch.setSelected(true);
				ch.setFont(new Font(ch.getFont().getName(), Font.BOLD, ch.getFont().getSize()));
			}
			else {
				ch.setSelected(false);
				ch.setFont(new Font(ch.getFont().getName(), Font.PLAIN, ch.getFont().getSize()));
			}
		}
	}
	

}
