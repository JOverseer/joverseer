package org.joverseer.ui.listviews;

import javax.swing.JComponent;

import org.joverseer.game.TurnElementsEnum;


public class EncounterListView extends ItemListView {
    public EncounterListView() {
        super(TurnElementsEnum.Encounter, EncounterTableModel.class);
    }

    protected int[] columnWidths() {
        return new int[]{40, 96,
                        250};
    }


}
