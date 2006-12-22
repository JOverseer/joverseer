package org.joverseer.ui.listviews;


public class SpellInfoListView extends ItemListView {
    public SpellInfoListView() {
        super("spells", SpellInfoTableModel.class);
    }

    protected int[] columnWidths() {
        return new int[]{32, 96, 16, 32, 96, 96, 150};
    }
}
