package org.joverseer.ui.listviews;

import org.joverseer.game.TurnElementsEnum;
import org.joverseer.ui.listviews.NationEconomyTableModel;

/**
 * Created by IntelliJ IDEA.
 * User: mskounak
 * Date: 14 Οκτ 2006
 * Time: 5:45:04 μμ
 * To change this template use File | Settings | File Templates.
 */
public class NationEconomyListView extends ItemListView {
    public NationEconomyListView() {
        super(TurnElementsEnum.NationEconomy, NationEconomyTableModel.class);
    }
}
