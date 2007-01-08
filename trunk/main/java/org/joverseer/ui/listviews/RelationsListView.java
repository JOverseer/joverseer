package org.joverseer.ui.listviews;

import org.joverseer.game.TurnElementsEnum;


public class RelationsListView extends ItemListView {
    public RelationsListView() {
        super(TurnElementsEnum.NationRelation, RelationsTableModel.class);
    }

    protected int[] columnWidths() {
        return new int[]{64, 96, 
                        32, 32, 32, 32, 32, 
                        32, 32, 32, 32, 32,
                        32, 32, 32, 32, 32,
                        32, 32, 32, 32, 32,
                        32, 32, 32, 32, 32};
    }

    protected void setItems() {
        super.setItems();
        try {
            for (int i=1; i<26; i++) {
                table.getColumnModel().getColumn(i+1).setHeaderValue(tableModel.getColumnName(i+1));
            }
        } catch (Exception exc) {};
    }
    
    

}
