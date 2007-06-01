package org.joverseer.ui.listviews;

import org.joverseer.game.TurnElementsEnum;
import org.joverseer.ui.listviews.NationEconomyTableModel;

/**
 * List view for NationEconomy objects
 * 
 * @author Marios Skounakis
 */
public class NationEconomyListView extends ItemListView {
    public NationEconomyListView() {
        super(TurnElementsEnum.NationEconomy, NationEconomyTableModel.class);
    }

    protected int[] columnWidths() {
        return new int[]{60, 128,
                        64, 64, 64, 64};
    }
}
