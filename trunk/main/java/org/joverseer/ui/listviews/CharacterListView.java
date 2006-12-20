package org.joverseer.ui.listviews;

import org.joverseer.game.TurnElementsEnum;


public class CharacterListView extends ItemListView {
    public CharacterListView() {
        super(TurnElementsEnum.Character, CharacterTableModel.class);
    }

    protected int[] columnWidths() {
        return new int[]{40, 120,
                        32, 32, 32, 32,
                        32, 32, 32, 32,
                        32, 32, 32, 32, 32};
    }

}
