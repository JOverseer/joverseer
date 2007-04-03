package org.joverseer.ui.listviews;

public class StartingCharacterListView extends ItemListView {

	public StartingCharacterListView() {
		super("characters", StartingCharacterTableModel.class);
	}

	protected int[] columnWidths() {
		return new int[]{120, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32};
	}
	

}
