package org.joverseer.ui.info;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Locale;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;

import org.joverseer.JOApplication;
import org.joverseer.metadata.GameMetadata;
import org.joverseer.support.GameHolder;
import org.joverseer.support.info.Info;
import org.joverseer.ui.support.Messages;
import org.joverseer.ui.support.UIUtils;
import org.joverseer.ui.support.controls.JLabelButton;
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
 * TODO: review against use of InfoRegistry class. which is way forward?
 * @author Marios Skounakis
 */
public class InfoView extends AbstractView {

	protected JideTabbedPane pane;
	protected ArrayList<JTable> tables = new ArrayList<JTable>();

	@Override
	protected JComponent createControl() {
		TableLayoutBuilder lb = new TableLayoutBuilder();

		//TODO: fix the files so that we can have language bundles.
		lb.separator(Messages.getString("InfoView.PC"));
		lb.relatedGapRow();
		lb.cell(createTableFromResource("classpath:metadata/info/popCenters", 700), "align=left valign=top");
		lb.relatedGapRow();

		lb.separator(Messages.getString("InfoView.Fortifications"));
		lb.relatedGapRow();
		lb.cell(createTableFromResource("classpath:metadata/info/fortifications", 400), "align=left valign=top");
		lb.relatedGapRow();

		lb.separator(Messages.getString("InfoView.GeneralCosts"));
		lb.relatedGapRow();
		lb.cell(createTableFromResource("classpath:metadata/info/generalCosts", 600), "align=left valign=top");
		lb.relatedGapRow();

		lb.separator(Messages.getString("InfoView.MaintenanceCosts"));
		lb.relatedGapRow();
		lb.cell(createTableFromResource("classpath:metadata/info/maintenanceCosts", 400), "align=left valign=top");
		lb.relatedGapRow();

		lb.separator(Messages.getString("InfoView.CharacterTitles"));
		lb.relatedGapRow();
		lb.cell(createTableFromInfo("characterTitles", 400, null), "align=left valign=top");
		lb.relatedGapRow();

		lb.separator(Messages.getString("InfoView.Movement"));
		lb.relatedGapRow();
		lb.cell(createTableFromResource("classpath:metadata/info/movementCosts", 600), "align=left valign=top");
		lb.relatedGapRow();

		lb.separator(Messages.getString("InfoView.TacticvsTactic"));
		lb.relatedGapRow();
		lb.cell(createTableFromResource("classpath:metadata/info/tacticVsTactic", 400), "align=left valign=top");
		lb.relatedGapRow();

		lb.separator(Messages.getString("InfoView.TroopTypeTactics"));
		lb.relatedGapRow();
		lb.cell(createTableFromResource("classpath:metadata/info/troopTactics", 600), "align=left valign=top");
		lb.relatedGapRow();

		lb.separator(Messages.getString("InfoView.TroopCombatStrength"));
		lb.relatedGapRow();
		lb.cell(createTableFromResource("classpath:metadata/info/armyCombatValues", 300), "align=left valign=top");
		lb.relatedGapRow();

		lb.separator(Messages.getString("InfoView.TroopTerrainModifiers"));
		lb.relatedGapRow();
		lb.cell(createTableFromInfo("combat.troopTerrainModifiers", 600, null), "align=left valign=top");
		lb.relatedGapRow();

		lb.separator(Messages.getString("InfoView.Dragons"));
		lb.relatedGapRow();
		//lb.cell(createTableFromInfo("dragons",850, 1100,null), "align=left valign=top");
		lb.cell(new JLabel("Click on the link below to open it in your browser:"), "align=left valign=top");
		lb.relatedGapRow();
		JLabelButton hyperlinkDragon = new JLabelButton("<html><font color='#3a79d1'>https://wiki.mepbm.com/dragons</font><html>");
		hyperlinkDragon.addActionListener(new ActionListener() {
			
	          @Override
				public void actionPerformed(ActionEvent e) {
	              try {
	            	  //Upon click link will try and open in default browser
	                  if (Desktop.isDesktopSupported()) {
	                      Desktop desktop = Desktop.getDesktop();
	                      if (desktop.isSupported(Desktop.Action.BROWSE)) {
	                          desktop.browse(URI.create("https://wiki.mepbm.com/dragons"));
	                      }
	                  }
	              } catch (IOException | InternalError e1) {
	            	  	e1.printStackTrace();
	            	  	//Commented out code is for copying clicked link to clipboard
//						StringSelection stringSelection = new StringSelection("https://wiki.mepbm.com/dragons");
//						Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
//						clipboard.setContents(stringSelection, null);
	              }

	          }
			});

		lb.cell(hyperlinkDragon, "align=left valign=top");
		lb.relatedGapRow();

		lb.separator(Messages.getString("InfoView.CharactersAllowed"));
		lb.relatedGapRow();
		String selected  = null;
		
		//TODO: filtering by game type doesn't work as the view is cached by jide/spring
		if (GameHolder.hasInitializedGame()) {
			GameMetadata gm = JOApplication.getMetadata();
			selected = gm.getGameType().toMEString();
		}
		lb.cell(createTableFromInfo("charactersAllowed",500, selected), "align=left valign=top");
		lb.relatedGapRow();
		lb.separator(Messages.getString("InfoView.ClimateProductionModifiers"));
		lb.relatedGapRow();
		lb.cell(createTableFromInfo("climateProduction", 600, null), "align=left valign=top");
		lb.relatedGapRow();


		JScrollPane scp = new JScrollPane(lb.getPanel());
		UIUtils.fixScrollPaneMouseScroll(scp);

		return scp;
	}

	/**
	 * Creates a jtable for the given dimensions (w, h) using the file found in
	 * uri
	 */
	protected JComponent createTableFromResource(String uri, int w) {
		Resource res;
		Locale current =Locale.getDefault();
		final String localizedUri = uri + "_" + current.getLanguage() + ".csv";
		res = Application.instance().getApplicationContext().getResource(localizedUri);
		if (!res.exists()) {
			res = Application.instance().getApplicationContext().getResource(uri + ".csv");
		}
		try {
			BufferedReader reader = GameMetadata.getUTF8Resource(res,true);

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
			this.tables.add(table);
			table.setDefaultRenderer(String.class, new DefaultTableCellRenderer() {
				private static final long serialVersionUID = 1L;

				@Override
				public Component getTableCellRendererComponent(JTable arg0, Object arg1, boolean arg2, boolean arg3, int arg4, int arg5) {
					JLabel lbl = (JLabel) super.getTableCellRendererComponent(arg0, arg1, arg2, arg3, arg4, arg5);
					if(arg2) lbl.setForeground(Color.white);
					else lbl.setForeground(UIManager.getColor("Label.foreground"));
					if (arg5 > 0) {
						lbl.setHorizontalAlignment(SwingConstants.CENTER);
					} else {
						lbl.setHorizontalAlignment(SwingConstants.LEFT);
					}
					return lbl;
				}

			});
			pnl.add(table.getTableHeader(), BorderLayout.PAGE_START);
			pnl.add(table, BorderLayout.CENTER);

			table.setPreferredSize(new Dimension(w, table.getRowHeight()*table.getRowCount()));
			TableUtils.sizeColumnsToFitRowData(table);
			reader.close();
			return pnl;
		} catch (Exception exc) {
			exc.printStackTrace();
		}
	
		return null;
	}
	// creates a table from an Info table, already loaded from the InfoRegistry.
	// otherwise the same as createTableFromResource.
	// if only is not "" then only include those elements of column 0 
	protected JComponent createTableFromInfo(String key, int w,String only) {
		Info info = JOApplication.getInfoRegistry().getInfo(key);
		try {

			InfoTableModel model = new InfoTableModel();
			ArrayList<String> colNames = info.getColumnHeaders();
			if (only != null) {
				if (only.length() > 0) {
					colNames.remove(0);
				}
			}
			model.setColNames(colNames);
				
			Iterator<ArrayList<String>> iter = info.getRowValuesIterator();
			ArrayList<String> row;
			while (iter.hasNext()) {
				row = iter.next();
				if (only == null) {
					model.getValues().add(row.toArray(new String[0]));
				} else {
					if (row.get(0).compareToIgnoreCase(only) == 0) {
						row.subList(1, row.size()-1).toArray(new String[0]);
					}
				}
			}
			JPanel pnl = new JPanel(new BorderLayout());

			JTable table = TableUtils.createStandardSortableTable(model);
			this.tables.add(table);
			table.setDefaultRenderer(String.class, new DefaultTableCellRenderer() {
				private static final long serialVersionUID = 1L;

				@Override
				public Component getTableCellRendererComponent(JTable arg0, Object arg1, boolean arg2, boolean arg3, int arg4, int arg5) {
					JLabel lbl = (JLabel) super.getTableCellRendererComponent(arg0, arg1, arg2, arg3, arg4, arg5);
					if(arg2) lbl.setForeground(Color.white);
					else lbl.setForeground(UIManager.getColor("Label.foreground"));
					if (arg5 > 0) {
						lbl.setHorizontalAlignment(SwingConstants.CENTER);
					} else {
						lbl.setHorizontalAlignment(SwingConstants.LEFT);
					}
					return lbl;
				}

			});
			pnl.add(table.getTableHeader(), BorderLayout.PAGE_START);
			pnl.add(table, BorderLayout.CENTER);

			table.setPreferredSize(new Dimension(w, table.getRowHeight()*table.getRowCount()));
			TableUtils.sizeColumnsToFitRowData(table);
			return pnl;
		} catch (Exception exc) {
			exc.printStackTrace();
		}
	
		return null;
	}

	public class InfoTableModel extends BaseTableModel {

		/**
		 * 
		 */
		private static final long serialVersionUID = 139624667549196939L;
		ArrayList<String> colNames = new ArrayList<String>();
		ArrayList<String[]> values = new ArrayList<String[]>();

		@Override
		public boolean isCellEditable(int arg0, int arg1) {
			return false;
		}

		public ArrayList<String> getColNames() {
			return this.colNames;
		}

		public void setColNames(ArrayList<String> colNames) {
			this.colNames =colNames;
		}

		public ArrayList<String[]> getValues() {
			return this.values;
		}

		public void setValues(ArrayList<String[]> values) {
			this.values = values;
		}

		@Override
		public int getColumnCount() {
			return this.colNames.size();
		}

		@Override
		public int getRowCount() {
			return this.values.size();
		}

		@Override
		public Object getValueAt(int arg0, int arg1) {
			return this.values.get(arg0)[arg1];
		}

		@Override
		public void setValueAt(Object value, int rowIndex, int columnIndex) {
			String[] row = this.values.get(rowIndex);
			row[columnIndex] = (String) value;
	    }

		@Override
		protected Class<?>[] createColumnClasses() {
			Class<?>[] classes = new Class<?>[this.colNames.size()];
			Arrays.fill(classes, String.class);
			return classes;
		}

		@Override
		protected String[] createColumnNames() {
			return getColumnNames();
		}

		@Override
		protected Object getValueAtInternal(Object arg0, int arg1) {
			return getValueAt((Integer) arg0, arg1);
		}

		@Override
		public String getColumnName(int arg0) {
			return this.colNames.get(arg0);
		}

		@Override
		public Class<?> getColumnClass(int arg0) {
			return String.class;
		}

	    @Override
		protected String[] getColumnNames() {
			return this.colNames.toArray(new String[this.colNames.size()]);
	    }
	}

}
