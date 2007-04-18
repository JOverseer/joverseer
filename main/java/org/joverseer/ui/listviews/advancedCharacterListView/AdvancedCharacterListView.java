package org.joverseer.ui.listviews.advancedCharacterListView;

import javax.swing.JComponent;

import org.joverseer.ui.listviews.BaseItemListView;

public class AdvancedCharacterListView extends BaseItemListView {

	public AdvancedCharacterListView() {
		super(AdvancedCharacterTableModel.class);
	}

	protected int[] columnWidths() {
		return new int[]{96, 48, 48, 48, 48, 48, 48, 96, 48};
	}

	protected void setItems() {
		tableModel.setRows(AdvancedCharacterWrapper.getWrappers());
	}

	protected JComponent createControlImpl() {
		JComponent c = super.createControlImpl();
		table.setDefaultRenderer(CharacterAttributeWrapper.class, new CharacterAttributeWrapperTableCellRenderer(tableModel));
		return c;
	}
	
	
	

}
