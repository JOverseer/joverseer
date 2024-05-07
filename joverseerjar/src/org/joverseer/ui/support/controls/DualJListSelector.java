package org.joverseer.ui.support.controls;

import java.awt.FlowLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 * A class which creates a JPanel containing 2 JLists.
 * They function by having one list (the right one) be the "unselected items" and the left list being the selected.
 * The user can double click on items to move them between lists.
 * 
 * POTENTIAL TODO: Due to struggles with setting size of JList and ScrollPanes, I couldn't both control the width of the JList 
 * and allow the horizontal scrollbar to function properly (allowing items which are too long to be fully visible)
 * 
 * @author Sam Terrett
 */
public class DualJListSelector extends JPanel implements MouseListener{
	JList selectedList;
	JList n_selectedList;
	
	DefaultListModel selLM;
	DefaultListModel n_selLM;
	
	boolean keepOrder = false;
	ArrayList<String> orderOfItems;
	
	JScrollPane sp1;
	JScrollPane sp2;
	
	boolean allowEmptySelection;
	
	private static final long serialVersionUID = 1L;
	
	public DualJListSelector(boolean emptySelOpt) {
		super();
		this.allowEmptySelection = emptySelOpt;
		this.setLayout(new FlowLayout(FlowLayout.LEFT));

		this.selLM = new DefaultListModel();
		this.n_selLM = new DefaultListModel();
		
		this.selectedList = new JList(this.selLM);
		this.selectedList.addMouseListener(this);
		this.sp1 = new JScrollPane(this.selectedList, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		this.add(this.sp1);

		this.n_selectedList = new JList(this.n_selLM);
		this.n_selectedList.addMouseListener(this);
		this.sp2 = new JScrollPane(this.n_selectedList, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		this.add(this.sp2);
	}
	
	/**
	 * Sets this instance of DualJListSelector to mantain a consistent order when adding and removing items from both lists,
	 * based on the order of the list passed in.
	 * 
	 * @param list: Order of the items to be mantained in the lists
	 */
	public void setListOrder(ArrayList<String> list) {
		this.keepOrder = true;
		this.orderOfItems = list;
	}
	
	/**
	 * This function, given that there is a given order the items should be kept in (set by setListOrder) will find the correct point to insert 
	 * the item into the model to keep it in the same order
	 * 
	 * @param item: item in the list to find where it should be inserted into the model
	 * @param mod: The model which the item needs to be correctly inserted into
	 * @return the index of insertion
	 */
	private int getInsertionIndex(Object item, DefaultListModel mod) {
		int max = mod.size();
		if(!this.keepOrder) return 0;
		else {
			int ind = this.orderOfItems.indexOf(item);
			for (int i = 0; i < max; i++) {
				Object modInd = mod.getElementAt(i);
				if(ind < this.orderOfItems.indexOf(modInd)) return i;
			}
			return max;
		}
	}
	
	/**
	 * Gets the selected items, filtering out the placeholder element
	 * 
	 * @param deslectedItems: The array of items to be added into deslected list
	 * @param selectedItems: Same but for selected list
	 */
	public void setSelectorItems(String[] deselectedItems, String[] selectedItems) {
		this.selLM = new DefaultListModel();
		this.n_selLM = new DefaultListModel();
		
		if(selectedItems.length == 0) this.selLM.addElement(" ");
		
		else {
			for (String itemS : selectedItems) {
				this.selLM.addElement(itemS);
			}	
		}

		if(deselectedItems.length != 0) {
			for (String itemD : deselectedItems) {
				this.n_selLM.addElement(itemD);
			}
		}
		this.selectedList.setModel(this.selLM);
		this.n_selectedList.setModel(this.n_selLM);
	}
	
	public JList getSelectedList() {
		return this.selectedList;
	}
	
	public JList getDeSelectedList() {
		return this.n_selectedList;
	}
	/**
	 * 
	 **/
	//TODO setting width means horizontal scrollbar doesn't dynamically grow to show entire item, hads lots of issues trying to get this to work
	public void setListSize(int noVisibleItems, int maxWidth) {
		this.selectedList.setVisibleRowCount(noVisibleItems);
		this.n_selectedList.setVisibleRowCount(noVisibleItems);
		this.selectedList.setFixedCellWidth(maxWidth);
		this.n_selectedList.setFixedCellWidth(maxWidth);
	}

	/**
	 * Handles what happens when a list is double clicked
	 */
	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		JList list = (JList)e.getSource();
		if(e.getClickCount() == 2) {
			int index = list.locationToIndex(e.getPoint());		//Gets index of double clicked item
			
			//Handles what should happen if the "selected" list is the one interacted with
			if(list.equals(this.selectedList)) {
				//Handles special cases, not allowing the list to be completely empty according to certain conditions
				if(this.selLM.size() == 1 && (this.selLM.firstElement().equals(" ") || !this.allowEmptySelection));
				
				else {
					DefaultListModel mod = (DefaultListModel) list.getModel();
					Object item = mod.getElementAt(index);
					this.n_selLM.add(this.getInsertionIndex(item, this.n_selLM), item);
					this.selLM.removeElementAt(index);
					if(this.selLM.size() == 0) this.selLM.addElement(" ");
				}
			}
			if(list.equals(this.n_selectedList)) {
				if(this.selLM.size() == 1 && this.selLM.firstElement().equals(" ")) this.selLM.remove(0);
				
				DefaultListModel mod = (DefaultListModel) list.getModel();
				Object item = mod.getElementAt(index);
				this.selLM.add(this.getInsertionIndex(item, this.selLM), item);
				this.n_selLM.removeElementAt(index);
			}
		}
	}
	
	/**
	 * A method which refreshes and repaints all the lists and scrollpanes
	 */
	public void refreshLists() {
		this.n_selectedList.repaint();
		this.selectedList.repaint();
		this.n_selectedList.revalidate();
		this.selectedList.revalidate();
		
		this.sp1.repaint();
		this.sp2.repaint();
		this.sp1.revalidate();
		this.sp2.revalidate();
	}

	/**
	 * Gets the selected items, filtering out the placeholder
	 * 
	 * @return: the Model containing the selected items
	 */
	public DefaultListModel getSelectedItems() {
		if (this.selLM.size() == 1 && this.selLM.firstElement().equals(" ")) return new DefaultListModel();
		return this.selLM;
	}

	/**
	 * Rest are bunch of inherited methods
	 */
	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

}
