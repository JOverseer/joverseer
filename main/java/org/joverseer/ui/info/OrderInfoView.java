package org.joverseer.ui.info;

import javax.swing.JComponent;
import javax.swing.JScrollPane;

import org.joverseer.ui.support.controls.TableUtils;
import org.springframework.richclient.layout.TableLayoutBuilder;

/**
 * Order info view
 * Shows information about orders in tabular format
 * 
 * @author Marios Skounakis
 */
public class OrderInfoView extends InfoView {

    protected JComponent createControl() {
        TableLayoutBuilder lb = new TableLayoutBuilder();

        lb.separator("Orders");
        lb.relatedGapRow();
        lb.cell(createTableFromResource("classpath:metadata/orders.csv", 700, 2400), "align=left");
        lb.relatedGapRow();
        TableUtils.setTableColumnWidths(tables.get(0), new int[]{120, 64, 64, 120, 80, 120, 120, 80});
        
        JScrollPane scp = new JScrollPane(lb.getPanel());
        scp.getVerticalScrollBar().setUnitIncrement(40);
        return scp;
    }
    

}
