package org.joverseer.ui.listviews;

import org.joverseer.game.TurnElementsEnum;


public class NationMessageListView extends ItemListView {
    public NationMessageListView() {
        super(TurnElementsEnum.NationMessage, NationMessageTableModel.class);
    }

    protected int[] columnWidths() {
        return new int[]{96, 400};
    }
}
