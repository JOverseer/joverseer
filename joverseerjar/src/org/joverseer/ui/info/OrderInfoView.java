package org.joverseer.ui.info;

import java.util.HashMap;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.table.TableModel;

import org.joverseer.ui.support.Messages;
import org.joverseer.ui.support.controls.TableUtils;
import org.springframework.richclient.layout.TableLayoutBuilder;

/**
 * Order info view
 * Shows information about orders in tabular format
 * 
 * @author Marios Skounakis
 */
public class OrderInfoView extends InfoView {

    // hack for nicer skill names as the abbreviations are used elsewhere.
	private void hackDescriptions() {
		HashMap<String, String> friendly = new HashMap<String, String>();
		friendly.put("CS", Messages.getString("OrderRequirement.CS"));
		friendly.put("CM", Messages.getString("OrderRequirement.CM"));
		friendly.put("AS", Messages.getString("OrderRequirement.AS"));
		friendly.put("AM", Messages.getString("OrderRequirement.AM"));
		friendly.put("MS", Messages.getString("OrderRequirement.MS"));
		friendly.put("MM", Messages.getString("OrderRequirement.MM"));
		friendly.put("ES", Messages.getString("OrderRequirement.ES"));
		friendly.put("EM", Messages.getString("OrderRequirement.EM"));
		friendly.put("M", Messages.getString("OrderRequirement.M"));
		friendly.put("Move", Messages.getString("OrderRequirement.Move"));
        // hack for nicer skill names as the abbreviations are used elsewhere.
        TableModel model = this.tables.get(0).getModel();
        int col = 6; 
        String code;
        String nicer;
        for(int row=0;row < model.getRowCount();row++) {
        	code = (String)model.getValueAt(row, col);
        	nicer = friendly.get(code);
        	if (nicer != null) {
        		model.setValueAt(nicer, row, col);
        	}
        }
	}
    /**
     * @wbp.parser.entryPoint
     */
    @Override
	protected JComponent createControl() {
        TableLayoutBuilder lb = new TableLayoutBuilder();

        lb.separator(Messages.getString("OrderInfoView.Orders"));
        //lb.relatedGapRow();
        lb.row();

        lb.cell(createTableFromResource("classpath:metadata/orders", 700), "align=left valign=top");
        //lb.relatedGapRow();
        TableUtils.setTableColumnWidths(this.tables.get(0), new int[]{120, 64, 64, 120, 80, 120, 120, 80});
        
        hackDescriptions();
        
        JScrollPane scp = new JScrollPane(lb.getPanel());
        scp.getVerticalScrollBar().setUnitIncrement(40);
        return scp;
    }
    

}
