package org.joverseer.ui.listviews;

import java.util.ArrayList;

import org.joverseer.tools.ArmySizeEstimator;


public class ArmySizeEstimatesListView extends BaseItemListView {
    public ArmySizeEstimatesListView() {
        super(ArmySizeEstimatesTableModel.class);
    }

    protected int[] columnWidths() {
        return new int[] {64, 64, 64, 64, 64};
    }

    protected void setItems() {
        ArmySizeEstimator ase = new ArmySizeEstimator();
        ArrayList items = ase.estimateArmySizes();
        tableModel.setRows(items);
        tableModel.fireTableDataChanged();
    }
}
