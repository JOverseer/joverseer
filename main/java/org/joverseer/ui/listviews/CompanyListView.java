package org.joverseer.ui.listviews;

import org.joverseer.game.TurnElementsEnum;


public class CompanyListView extends ItemListView {
    public CompanyListView() {
        super(TurnElementsEnum.Company, CompanyTableModel.class);
    }

    protected int[] columnWidths() {
        return new int[]{40, 60, 220};
    }

}
