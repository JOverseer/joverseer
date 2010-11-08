package org.joverseer.ui.info;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import org.joverseer.ui.support.UIUtils;
import org.springframework.core.io.Resource;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.application.support.AbstractView;
import org.springframework.richclient.layout.TableLayoutBuilder;
import org.springframework.richclient.table.BaseTableModel;
import org.springframework.richclient.table.TableUtils;

import com.jidesoft.swing.JideTabbedPane;

/**
 * The InfoView
 * 
 * Shows various background game info (such as movement costs, army element
 * strengths, maintenance costs etc) The info is retrieve from the info data
 * files
 * 
 * @author Marios Skounakis
 */
public class InfoView extends AbstractView {

	protected JideTabbedPane pane;
	protected ArrayList<JTable> tables = new ArrayList<JTable>();

	@Override
	protected JComponent createControl() {
		TableLayoutBuilder lb = new TableLayoutBuilder();

		lb.separator("Population Centers");
		lb.relatedGapRow();
		lb.cell(createTableFromResource("classpath:metadata/info/popCenters.csv", 700, 100), "align=left");
		lb.relatedGapRow();

		lb.separator("Fortifications");
		lb.relatedGapRow();
		lb.cell(createTableFromResource("classpath:metadata/info/fortifications.csv", 400, 100), "align=left");
		lb.relatedGapRow();

		lb.separator("General Costs");
		lb.relatedGapRow();
		lb.cell(createTableFromResource("classpath:metadata/info/generalCosts.csv", 600, 240), "align=left");
		lb.relatedGapRow();

		lb.separator("Maintenance Costs");
		lb.relatedGapRow();
		lb.cell(createTableFromResource("classpath:metadata/info/maintenanceCosts.csv", 400, 200), "align=left");
		lb.relatedGapRow();

		lb.separator("Character Titles");
		lb.relatedGapRow();
		lb.cell(createTableFromResource("classpath:metadata/info/characterTitles.csv", 400, 200), "align=left");
		lb.relatedGapRow();

		lb.separator("Movement");
		lb.relatedGapRow();
		lb.cell(createTableFromResource("classpath:metadata/info/movementCosts.csv", 600, 200), "align=left");
		lb.relatedGapRow();

		lb.separator("Tactic vs Tactic");
		lb.relatedGapRow();
		lb.cell(createTableFromResource("classpath:metadata/info/tacticVsTactic.csv", 400, 120), "align=left");
		lb.relatedGapRow();

		lb.separator("Troop Type - Tactics");
		lb.relatedGapRow();
		lb.cell(createTableFromResource("classpath:metadata/info/troopTactics.csv", 600, 120), "align=left");
		lb.relatedGapRow();

		lb.separator("Troop Combat Strength");
		lb.relatedGapRow();
		lb.cell(createTableFromResource("classpath:metadata/info/armyCombatValues.csv", 300, 120), "align=left");
		lb.relatedGapRow();

		lb.separator("Troop Terrain Modifiers");
		lb.relatedGapRow();
		lb.cell(createTableFromResource("classpath:metadata/info/troopTerrainPerformance.csv", 600, 120), "align=left");
		lb.relatedGapRow();

		lb.separator("Climate Production Modifiers");
		lb.relatedGapRow();
		lb.cell(createTableFromResource("classpath:metadata/info/climateProduction.csv", 600, 140), "align=left");
		lb.relatedGapRow();

		lb.separator("Dragons (player collected information - i.e. not official)");
		lb.relatedGapRow();
		lb.cell(createTableFromResource("classpath:metadata/info/dragons2.csv", 850, 1100), "align=left");
		lb.relatedGapRow();

		JScrollPane scp = new JScrollPane(lb.getPanel());
		UIUtils.fixScrollPaneMouseScroll(scp);

		return scp;
	}

	/**
	 * Creates a jtable for the given dimensions (w, h) using the file found in
	 * uri
	 */
	protected JComponent createTableFromResource(String uri, int w, int h) {
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

			JTable table = TableUtils.createStandardSortableTable(model);
			tables.add(table);
			table.setDefaultRenderer(String.class, new DefaultTableCellRenderer() {
				@Override
				public Component getTableCellRendererComponent(JTable arg0, Object arg1, boolean arg2, boolean arg3, int arg4, int arg5) {
					JLabel lbl = (JLabel) super.getTableCellRendererComponent(arg0, arg1, arg2, arg3, arg4, arg5);
					if (arg5 > 0) {
						lbl.setHorizontalAlignment(JLabel.CENTER);
					} else {
						lbl.setHorizontalAlignment(JLabel.LEFT);
					}
					return lbl;
				}

			});
			pnl.add(table.getTableHeader(), BorderLayout.PAGE_START);
			pnl.add(table, BorderLayout.CENTER);

			table.setPreferredSize(new Dimension(w, h));
			TableUtils.sizeColumnsToFitRowData(table);
			reader.close();
			return pnl;
		} catch (Exception exc) {
			exc.printStackTrace();
		}
		;
		return null;
	}

	public class InfoTableModel extends BaseTableModel {

		ArrayList<String> colNames = new ArrayList<String>();
		ArrayList<String[]> values = new ArrayList<String[]>();

		@Override
		public boolean isCellEditable(int arg0, int arg1) {
			return false;
		}

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

		@Override
		public int getColumnCount() {
			return colNames.size();
		}

		@Override
		public int getRowCount() {
			return values.size();
		}

		@Override
		public Object getValueAt(int arg0, int arg1) {
			return values.get(arg0)[arg1];
		}

		@Override
		protected Class[] createColumnClasses() {
			Class[] classes = new Class[colNames.size()];
			Arrays.fill(classes, String.class);
			return classes;
		}

		@Override
		protected String[] createColumnNames() {
			return colNames.toArray(new String[] {});
		}

		@Override
		protected Object getValueAtInternal(Object arg0, int arg1) {
			return getValueAt((Integer) arg0, arg1);
		}

		@Override
		public String getColumnName(int arg0) {
			return colNames.get(arg0);
		}

		@Override
		public Class getColumnClass(int arg0) {
			return String.class;
		}

	}

}
