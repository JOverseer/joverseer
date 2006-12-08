package org.joverseer.ui.listviews;

import org.joverseer.game.TurnElementsEnum;

/**
 * Created by IntelliJ IDEA.
 * User: mskounak
 * Date: 29 Οκτ 2006
 * Time: 7:27:23 μμ
 * To change this template use File | Settings | File Templates.
 */
public class NationMessageListView extends ItemListView {
    public NationMessageListView() {
        super(TurnElementsEnum.NationMessage, NationMessageTableModel.class);
    }

    protected int[] columnWidths() {
        return new int[]{96, 400};
    }
}
