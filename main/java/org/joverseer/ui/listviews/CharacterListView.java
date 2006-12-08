package org.joverseer.ui.listviews;

import org.joverseer.game.TurnElementsEnum;

/**
 * Created by IntelliJ IDEA.
 * User: mskounak
 * Date: 14 Οκτ 2006
 * Time: 4:10:19 μμ
 * To change this template use File | Settings | File Templates.
 */
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
