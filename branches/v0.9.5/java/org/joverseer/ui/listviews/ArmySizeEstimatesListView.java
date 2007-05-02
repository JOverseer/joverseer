package org.joverseer.ui.listviews;

import java.awt.Component;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import org.joverseer.domain.ArmySizeEstimate;
import org.joverseer.tools.ArmySizeEstimator;


public class ArmySizeEstimatesListView extends BaseItemListView {
    public ArmySizeEstimatesListView() {
        super(ArmySizeEstimatesTableModel.class);
    }

    protected int[] columnWidths() {
        return new int[] {64, 64, 64, 64, 64, 64};
    }
    
    

    protected JComponent createControlImpl() {
        JComponent comp = super.createControlImpl();
        table.setDefaultRenderer(Integer.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel lbl = (JLabel)super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                try {
	                if (value == null || (Integer)value <= 0) {
	                    lbl.setText("");
	                }
	                lbl.setHorizontalAlignment(JLabel.RIGHT);
                }
                catch (Exception exc) {};
                return lbl;
            }
            
        });
        return comp;
    }

    protected void setItems() {
        ArmySizeEstimator ase = new ArmySizeEstimator();
        ArrayList items = ase.estimateArmySizes();
        // find navy index
        int idx = -1;
        for (int i=0; i<items.size(); i++) {
            ArmySizeEstimate aseo = (ArmySizeEstimate)items.get(i);
            if (aseo.getType().equals(ArmySizeEstimate.NAVY_TYPE)) {
                idx = i;
                break;
            }
        }
        if (idx > -1) {
            items.add(idx, new ArmySizeEstimate("", null)); 
        }
        tableModel.setRows(items);
        tableModel.fireTableDataChanged();
    }
}
