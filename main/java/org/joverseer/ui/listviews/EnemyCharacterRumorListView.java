package org.joverseer.ui.listviews;

import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JTable;

import org.joverseer.support.Container;
import org.joverseer.ui.domain.EnemyCharacterRumorWrapper;
import org.joverseer.ui.listviews.commands.GenericCopyToClipboardCommand;
import org.joverseer.ui.listviews.commands.ListViewDescriptionPopupCommand;
import org.joverseer.ui.listviews.commands.PopupMenuCommand;

import com.jidesoft.grid.AbstractFilter;

/**
 * List view for Enemy Character Rumors
 * 
 * @author Marios Skounakis
 */
public class EnemyCharacterRumorListView extends BaseItemListView {


    public EnemyCharacterRumorListView() {
        super(EnemyCharacterRumorTableModel.class);
        
    }

    protected int[] columnWidths() {
        return new int[]{120, 64, 64, 64, 64, 64, 240, 120};
    }
    
    
    protected JComponent createControlImpl() {
        JComponent comp = super.createControlImpl();
        //table.setDefaultRenderer(Boolean.class, new JTable().getDefaultRenderer(Boolean.class));
        return comp;
    }

    protected void setItems() {
        Container thieves = EnemyCharacterRumorWrapper.getAgentWrappers(true);
        ArrayList filteredItems = new ArrayList();
        for (EnemyCharacterRumorWrapper w : (ArrayList<EnemyCharacterRumorWrapper>)thieves.getItems()) {
        	if (getActiveFilter().accept(w)) filteredItems.add(w);
        }
        tableModel.setRows(filteredItems);
        tableModel.fireTableDataChanged();
    }
    
    protected JComponent[] getButtons() {
    	return new JComponent[]{
    			new PopupMenuCommand().getButton(new Object[]{
    					new GenericCopyToClipboardCommand(table),
    					//new ListViewDescriptionPopupCommand("enemyCharacterRumorListView")
    					})};
	}

	@Override
	protected AbstractListViewFilter[][] getFilters() {
		return new AbstractListViewFilter[][]{
				new AbstractListViewFilter[]{
						new AbstractListViewFilter("") {
							public boolean accept(Object obj) {
								return true;
							}
						},
						new AbstractListViewFilter("Agents") {
							public boolean accept(Object obj) {
								EnemyCharacterRumorWrapper w = (EnemyCharacterRumorWrapper)obj;
								return (w.getCharType().equals("agent"));
							}
						},
						new AbstractListViewFilter("Emissaries") {
							public boolean accept(Object obj) {
								EnemyCharacterRumorWrapper w = (EnemyCharacterRumorWrapper)obj;
								return (w.getCharType().equals("emmisary"));
							}
						}
				}
		};
	
		 
	}
    
    
}
