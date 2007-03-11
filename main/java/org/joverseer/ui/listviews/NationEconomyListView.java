package org.joverseer.ui.listviews;

import org.joverseer.game.TurnElementsEnum;
import org.joverseer.ui.listviews.NationEconomyTableModel;


public class NationEconomyListView extends ItemListView {
    public NationEconomyListView() {
        super(TurnElementsEnum.NationEconomy, NationEconomyTableModel.class);
    }

    protected int[] columnWidths() {
        return new int[]{60, 128,
                        64, 64, 64, 64};
    }
}
