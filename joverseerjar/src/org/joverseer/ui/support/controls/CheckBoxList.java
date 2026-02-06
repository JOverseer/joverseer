package org.joverseer.ui.support.controls;

import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class CheckBoxList extends JPanel {

	private static final long serialVersionUID = 1L;
	
	private ArrayList<JCheckBox> list;
	private ArrayList<JLabel> iconList;
	public ItemListener iL;
	
	public CheckBoxList(ItemListener itemListener) {
		super();
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.setAlignmentX(Component.LEFT_ALIGNMENT);
		this.list = new ArrayList<JCheckBox>();
		this.iconList = new ArrayList<JLabel>();
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
		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
		p.setAlignmentX(Component.LEFT_ALIGNMENT);
		
		JCheckBox ch = new JCheckBox(item);
		ch.addItemListener(this.iL);
		ch.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				JCheckBox ch1 = (JCheckBox) e.getSource();
				JLabel lb1 = CheckBoxList.this.iconList.get(CheckBoxList.this.list.indexOf(ch1));
				
				if (CheckBoxList.this.getSelected().size() == 0) ch1.setSelected(true);
				if (ch1.isSelected()) {
					ch1.setFont(new Font(ch1.getFont().getName(), Font.BOLD, ch1.getFont().getSize()));
					lb1.setVisible(true);
				}
				else {
					ch1.setFont(new Font(ch1.getFont().getName(), Font.PLAIN, ch1.getFont().getSize()));
					lb1.setVisible(false);
				}
			}
			
		});
		this.list.add(ch);
		p.add(ch);
		
		JLabel lb = new JLabel();
		this.iconList.add(lb);
		p.add(lb);
		
		this.add(p);
	}
	
	public ArrayList<String> getSelected(){
		ArrayList<String> sel = new ArrayList<String>();
		for (JCheckBox ch : this.list) {
			if (ch.isSelected()) sel.add(ch.getText());
		}

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

	public void setIconForItem(String itemToAddIconTo, Icon i) {
		this.setIconForItem(itemToAddIconTo, i, null);
	}
	
	public void setIconForItem(String itemToAddIconTo, Icon i, String toolTip) {
		for(JCheckBox ch : this.list) {
			if(itemToAddIconTo.equals(ch.getText())) {
				int ind = this.list.indexOf(ch);
				this.iconList.get(ind).setIcon(i);
				this.iconList.get(ind).setToolTipText(toolTip);
			}
		}
	}
}
