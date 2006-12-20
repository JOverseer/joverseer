package org.joverseer.ui.listviews;

import org.joverseer.game.TurnElementsEnum;


public class PopulationCenterListView extends ItemListView {
    public PopulationCenterListView() {
        super(TurnElementsEnum.PopulationCenter, PopulationCenterTableModel.class);
    }

    protected int[] columnWidths() {
        return new int[]{40, 96,
                        64, 64, 64, 40};
    }

}
