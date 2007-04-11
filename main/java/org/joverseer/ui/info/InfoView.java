package org.joverseer.ui.info;

import java.awt.BorderLayout;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import org.joverseer.ui.listviews.ArtifactInfoListView;
import org.joverseer.ui.listviews.SpellInfoListView;
import org.springframework.core.io.Resource;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.application.support.AbstractView;
import org.springframework.richclient.layout.TableLayoutBuilder;
import org.springframework.richclient.table.BaseTableModel;
import org.springframework.richclient.table.BeanTableModel;
import org.springframework.richclient.table.ListTableModel;
import org.springframework.richclient.table.TableUtils;

import com.jidesoft.swing.JideTabbedPane;

public class InfoView extends AbstractView {
	JideTabbedPane pane;
	
	protected JComponent createControl() {
        TableLayoutBuilder lb = new TableLayoutBuilder();
        
        lb.separator("Population Centers");
        lb.relatedGapRow();
        lb.cell(createTableFromResource("classpath:metadata/info/popCenters.csv"), "align=left");
        lb.relatedGapRow();

        lb.separator("Fortifications");
        lb.relatedGapRow();
        lb.cell(createTableFromResource("classpath:metadata/info/fortifications.csv"), "align=left");
        lb.relatedGapRow();

        lb.separator("General Costs");
        lb.relatedGapRow();
        lb.cell(createTableFromResource("classpath:metadata/info/generalCosts.csv"), "align=left");
        lb.relatedGapRow();
        
        lb.separator("Character Titles");
        lb.relatedGapRow();
        lb.cell(createTableFromResource("classpath:metadata/info/characterTitles.csv"), "align=left");
        lb.relatedGapRow();

        return new JScrollPane(lb.getPanel());
	}
	
	private JComponent createTableFromResource(String uri) {
		Resource res = Application.instance().getApplicationContext().getResource(uri);
        try {
	        BufferedReader reader = new BufferedReader(new InputStreamReader(res.getInputStream()));
	
	        InfoTableModel model = new InfoTableModel();
	        ArrayList<String> colNames = null;
	        String ln;
	        while ((ln = reader.readLine()) != null) {
	        	String[] parts = ln.split(";");
	        	if (colNames == null) {
	        		colNames = new ArrayList<String>();
	        		colNames.addAll(Arrays.asList(parts.clone()));
	        		model.setColNames(colNames);
	        	} else {
	        		model.getValues().add(parts.clone());
	        	}
	        }
	        JPanel pnl = new JPanel(new BorderLayout());
	        
	        JTable table = new JTable(model);
	        pnl.add(table.getTableHeader(), BorderLayout.PAGE_START);
	        pnl.add(table, BorderLayout.CENTER);
	        
	        TableUtils.setPreferredColumnWidths(table);
	        reader.close();
	        return pnl;
        }
        catch (Exception exc) {
        	exc.printStackTrace();
        };
        return null;
	}
	
	public class InfoTableModel extends BaseTableModel {
		
		ArrayList<String> colNames = new ArrayList<String>();
		ArrayList<String[]> values = new ArrayList<String[]>();
		
		
		
		public ArrayList<String> getColNames() {
			return colNames;
		}

		public void setColNames(ArrayList<String> colNames) {
			this.colNames = colNames;
		}

		public ArrayList<String[]> getValues() {
			return values;
		}

		public void setValues(ArrayList<String[]> values) {
			this.values = values;
		}

		public int getColumnCount() {
			return colNames.size();
		}

		public int getRowCount() {
			return values.size();
		}

		public Object getValueAt(int arg0, int arg1) {
			return values.get(arg0)[arg1];
		}

		protected Class[] createColumnClasses() {
			Class[] classes = new Class[colNames.size()];
			Arrays.fill(classes, String.class);
			return classes;
		}

		@Override
		protected String[] createColumnNames() {
			return colNames.toArray(new String[]{});
		}

		protected Object getValueAtInternal(Object arg0, int arg1) {
			return getValueAt((Integer)arg0, arg1);
		}

		@Override
		public String getColumnName(int arg0) {
			return colNames.get(arg0);
		}
		
		public Class getColumnClass(int arg0) {
			return String.class;
		}

	}
	
}
	
	


