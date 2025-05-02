package org.joverseer.ui.economyCalculator;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;

import org.joverseer.domain.EconomyCalculatorData;
import org.joverseer.domain.NationEconomy;
import org.joverseer.game.Turn;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.tools.orderCostCalculator.OrderCostCalculator;
import org.joverseer.ui.BaseView;
import org.joverseer.ui.listviews.NationEconomyListView;
import org.joverseer.ui.listviews.NationProductionListView;
import org.joverseer.ui.listviews.NationStatisticsListView;
import org.joverseer.ui.support.JOverseerEvent;
import org.joverseer.ui.support.Messages;
import org.joverseer.ui.support.UIUtils;
import org.joverseer.ui.support.controls.JOverseerTable;
import org.joverseer.ui.support.drawing.ColorPicker;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.richclient.layout.TableLayoutBuilder;

/**
 * The team economy view
 *
 * It create a new table called team economy and it also uses a number of list
 * views: - the nation economy list view - the nation production list view - the
 * nation statistics list view
 *
 * @author Marios Skounakis
 */
public class TeamEconomyView extends BaseView implements ApplicationListener {
	JTable teamEconomyTable;
	TeamEconomyTableModel teamEconomyTableModel;
//	JComboBox showProductAsCombo;
	NationProductionListView nationProductionListView;
	NationStatisticsListView nationStatisticsListView;
	NationEconomyListView nationEconomyListView;

	@Override
	protected JComponent createControl() {
		TableLayoutBuilder lb = new TableLayoutBuilder();

		lb.relatedGapRow();

		lb.separator(Messages.getString("TeamEconomyView.label")); //$NON-NLS-1$
		lb.row();
		lb.relatedGapRow();

		this.teamEconomyTableModel = new TeamEconomyTableModel(this.gameHolder);
		this.teamEconomyTable = new JOverseerTable(new com.jidesoft.grid.SortableTableModel(this.teamEconomyTableModel));
		this.teamEconomyTable.getTableHeader().setPreferredSize(new Dimension(400, 16));
		this.teamEconomyTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		for (int i = 0; i < this.teamEconomyTableModel.getColumnCount(); i++) {
			this.teamEconomyTable.getColumnModel().getColumn(i).setPreferredWidth(this.teamEconomyTableModel.getColumnWidth(i));
		}
		CellRendererStyle style = new CellRendererStyle();
		this.teamEconomyTable.setDefaultRenderer(Integer.class, new IntegerTeamEconomyTableRenderer(style));
		this.teamEconomyTable.setDefaultRenderer(String.class, new StringTeamEconomyTableRenderer(style));
//		this.teamEconomyTable.setBackground(Color.white);
		// we set up the reference to the NationStatisticsModel once we've created it in the view.

		JScrollPane scp = new JScrollPane(this.teamEconomyTable);
		scp.setPreferredSize(new Dimension(600, 250));
//		scp.getViewport().setBackground(Color.white);
//		scp.getViewport().setOpaque(true);
		lb.cell(scp);

		lb.row();
		lb.relatedGapRow();

		this.teamEconomyTableModel.setShowProductsAs(SummaryTypeEnum.Total);
/*		this.showProductAsCombo = new JComboBox(TeamEconomyTableModel.getSummaryOptions());
		this.showProductAsCombo.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				TeamEconomyView.this.teamEconomyTableModel.setShowProductsAs(SummaryTypeEnum.values()[TeamEconomyView.this.showProductAsCombo.getSelectedIndex()]);
				TeamEconomyView.this.teamEconomyTableModel.fireTableDataChanged();
			}
		});
		this.showProductAsCombo.setPreferredSize(new Dimension(230, 20));

		TableLayoutBuilder tlb = new TableLayoutBuilder();

		tlb.cell(new JLabel(Messages.getString("TeamEconomyView.ProductsColon"))); //$NON-NLS-1$
		tlb.gapCol();
		tlb.cell(this.showProductAsCombo, "colspec=left:230px"); //$NON-NLS-1$
		tlb.gapCol();

		lb.cell(tlb.getPanel(), "align=left"); //$NON-NLS-1$
		lb.gapCol();
*/

//		JButton btn = new JButton(Messages.getString("TeamEconomyView.UpdateMarket")); //$NON-NLS-1$
//		btn.setToolTipText(Messages.getString("TeamEconomyView.UpdateAll")); //$NON-NLS-1$
//		btn.addActionListener(new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				updateMarketAndOrderCosts();
//			}
//		});
//		btn.setPreferredSize(new Dimension(200, 20));
//		lb.cell(btn, "colspec=right:200px valign=top"); //$NON-NLS-1$
		//tlb.cell(this.showProductAsCombo, "colspec=left:230px"); //$NON-NLS-1$

		lb.relatedGapRow();

		lb.separator(Messages.getString("TeamEconomyView.ProductsLabel")); //$NON-NLS-1$
		lb.relatedGapRow();

		this.nationProductionListView = new NationProductionListView();
		try {
			this.nationProductionListView.afterPropertiesSet();
			JPanel pnl = (JPanel) this.nationProductionListView.getControl();
			pnl.setPreferredSize(new Dimension(300, 300));
			lb.cell(pnl);

			lb.relatedGapRow();

			lb.separator(Messages.getString("TeamEconomyView.TE")); //$NON-NLS-1$
			lb.relatedGapRow();

			this.nationEconomyListView = new NationEconomyListView();
			this.nationEconomyListView.afterPropertiesSet();
			pnl = (JPanel) this.nationEconomyListView.getControl();
			pnl.setPreferredSize(new Dimension(300, 270));
			lb.cell(pnl);

			lb.relatedGapRow();

			lb.separator(Messages.getString("TeamEconomyView.Tstats")); //$NON-NLS-1$
			lb.relatedGapRow();

			this.nationStatisticsListView = new NationStatisticsListView();
			this.nationStatisticsListView.afterPropertiesSet();
			pnl = (JPanel) this.nationStatisticsListView.getControl();
			pnl.setPreferredSize(new Dimension(300, 270));
			lb.cell(pnl);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		this.teamEconomyTableModel.nswm = this.nationStatisticsListView.getTableModel();
		// without this we, never get the initialised tax base.
		this.teamEconomyTableModel.nswm.addTableModelListener(new TableModelListener() {
			@Override
			public void tableChanged(TableModelEvent e) {
				TeamEconomyView.this.teamEconomyTableModel.fireTableDataChanged();
			}
		});

		lb.relatedGapRow();

		scp = new JScrollPane(lb.getPanel());
		UIUtils.fixScrollPaneMouseScroll(scp);
		return scp;
	}

	private void updateMarketAndOrderCosts() {
		Turn t = this.getTurn();
		if (t != null ) {
			OrderCostCalculator occ = new OrderCostCalculator();
			for (EconomyCalculatorData ecd : t.getEconomyCalculatorData().getItems()) {
				ecd.setOrdersCost(occ.getTotalOrderCostForNation(t, ecd.getNationNo()));
				ecd.updateMarketFromOrders();
			}

			TeamEconomyView.this.teamEconomyTableModel.fireTableDataChanged();
		}
	}
	public void refreshTableItems() {
		ArrayList<EconomyCalculatorData> ecds = new ArrayList<EconomyCalculatorData>();
		Turn t = this.getTurn();
		if (t != null) {
			updateMarketAndOrderCosts();
			for (NationEconomy ne : t.getNationEconomies().getItems()) {
				EconomyCalculatorData ecd = (EconomyCalculatorData) t.getContainer(TurnElementsEnum.EconomyCalucatorData).findFirstByProperty("nationNo", ne.getNationNo()); //$NON-NLS-1$
				if (ecd == null) {
					ecd = new EconomyCalculatorData();
					ecd.setNationNo(ne.getNationNo());
					t.getEconomyCalculatorData().addItem(ecd);
				}
				ecds.add(ecd);
			}
		}
		ecds.add(null);
		this.teamEconomyTableModel.setRows(ecds);
	}

	@Override
	public void onApplicationEvent(ApplicationEvent applicationEvent) {
		this.nationProductionListView.onApplicationEvent(applicationEvent);
		this.nationEconomyListView.onApplicationEvent(applicationEvent);
		this.nationStatisticsListView.onApplicationEvent(applicationEvent);
		if (applicationEvent instanceof JOverseerEvent) {
			this.onJOEvent((JOverseerEvent) applicationEvent);
		}
	}
	public void onJOEvent(JOverseerEvent e) {
		switch (e.getType()) {
		case EconomyCalculatorUpdate:
			this.teamEconomyTableModel.fireTableDataChanged();
			break;
		case SelectedTurnChangedEvent:
			try {
				refreshTableItems();
				this.teamEconomyTableModel.fireTableDataChanged();
			} catch (Exception exc) {
				exc.printStackTrace();
			}
			break;
		case GameChangedEvent:
			super.resetGame();
			refreshTableItems();
			this.teamEconomyTableModel.fireTableDataChanged();
			break;
		case OrderChangedEvent:
			this.teamEconomyTableModel.fireTableDataChanged();
			break;
		}
	}
	class CellRendererStyle implements org.joverseer.ui.support.CellRendererStyle {
		private Color background;
		private Color warningForeground;
		private Color warningBackground;
		private Color selectForeground;
		private Color selectBackground;
		private Color foreground;
		private Color focusBorder;
		private Color lastRowBackground;
		private Color taxWarningForeground;
		private Color taxWarningBackground;
		
		public CellRendererStyle() {
			ColorPicker cp = ColorPicker.getInstance();
			this.warningBackground = cp.getColor("EconomyCalculator.red");
			this.foreground = cp.getColor("EconomyCalculator.text");
			this.background = UIManager.getColor("Table.background");
			this.focusBorder = Color.red;
			this.lastRowBackground = cp.getColor("EconomyCalculator.grey");
			this.taxWarningForeground = cp.getColor("EconomyCalculator.green");
		}
		@Override
		public Color getBackground() {
			return this.background;
		}
		@Override
		public Color getForeground() {
			return this.foreground;
		}
		@Override
		public Color getWarningForeground() {
			return this.warningForeground;
		}
		@Override
		public Color getWarningBackground() {
			return this.warningBackground;
		}
		@Override
		public Color getSelectedForeground() {
			return this.selectForeground;
		}
		@Override
		public Color getSelectedBackground() {
			return this.selectBackground;
		}
		@Override
		public Color getForeground(boolean isSelected) {
			if (isSelected) {
				return getSelectedForeground();
			} else {
				return getForeground();
			}
		}
		@Override
		public Color getBackground(boolean isSelected) {
			if (isSelected) {
				return getSelectedBackground();
			} else {
				return getBackground();
			}
		}
		@Override
		public Color getFocusBorder() {
			return this.focusBorder;
		}
		@Override
		public Color getLastRowBackground() {
			return this.lastRowBackground;
		}
		@Override
		public Color getTaxWarningForeground() {
			return this.taxWarningForeground;
		}
		public Color getTaxWarningBackground() {
			return this.taxWarningBackground;
		}
	}
	
	/**
	 * Renderer for the team economy main table
	 *
	 * @author Marios Skounakis
	 */
	class IntegerTeamEconomyTableRenderer extends DefaultTableCellRenderer {
		/**
		 *
		 */
		private static final long serialVersionUID = -1808381074745472954L;

		private transient CellRendererStyle style;
		public IntegerTeamEconomyTableRenderer(CellRendererStyle style2) {
			super();
			this.style = style2;
		}
		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			if (value == null)
				return lbl;
			lbl.setForeground(this.style.getForeground());
			//if (!isSelected) {
			if (row == TeamEconomyView.this.teamEconomyTableModel.getRowCount() - 1) {
				lbl.setBackground(this.style.getLastRowBackground());
			} else {
				lbl.setBackground(this.style.getBackground());
			}
		//}
			
			if (column == TeamEconomyTableModel.iFinalGold) {
				Integer amt = (Integer) value;
				if (amt < 0) {
					lbl.setBackground(this.style.getWarningBackground());
				}
			} else if (column == TeamEconomyTableModel.iMarketSales) {
				if (row < TeamEconomyView.this.teamEconomyTableModel.getRowCount() - 1) {
					Integer amt = (Integer) value;
					if (amt > EconomyCalculator.getMarketLimitWarningThreshhold()) {
						lbl.setBackground(this.style.getWarningBackground());
					}
				}
			} else if (column == TeamEconomyTableModel.iSurplus) {
				Integer amt = (Integer) value;
				if (amt < 0) {
					lbl.setBackground(this.style.getWarningBackground());
				}
			} else if (column == TeamEconomyTableModel.iTaxRate) {
				Integer amt = (Integer) value;
				if (amt < 60) {
					lbl.setBackground(this.style.getTaxWarningForeground());
				} else if (amt > 60) {
					lbl.setBackground(this.style.getWarningBackground());
				}
			} else if (column == TeamEconomyTableModel.iHikedTaxRate) {
				if (row == TeamEconomyView.this.teamEconomyTableModel.getRowCount() - 1) {
					lbl.setText("");
				} else {
					Integer amt = (Integer) value;
					if (amt >=100) {
						lbl.setBackground(this.style.getWarningBackground());
					} else if (amt == TeamEconomyView.this.teamEconomyTableModel.getValueAt(row, TeamEconomyTableModel.iTaxRate)) {
						lbl.setText("");
					}
				}
			} else {
				lbl.setForeground(this.style.getForeground());
			}

			if (TeamEconomyView.this.teamEconomyTableModel.getColumnClass(column) == Integer.class) {
				lbl.setHorizontalAlignment(SwingConstants.RIGHT);
			}


			if (hasFocus) {
				lbl.setBorder(BorderFactory.createLineBorder(this.style.getFocusBorder(), 1));
			}
			return lbl;
		}

	}

	class StringTeamEconomyTableRenderer extends DefaultTableCellRenderer {
		/**
		 *
		 */
		private static final long serialVersionUID = -2226537511337457982L;
		private transient CellRendererStyle style;

		public StringTeamEconomyTableRenderer(CellRendererStyle style) {
			super();
			this.style = style;
		}

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			lbl.setForeground(this.style.getForeground());
			//if (!isSelected) {
				if (row == TeamEconomyView.this.teamEconomyTableModel.getRowCount() - 1) {
					lbl.setBackground(this.style.getLastRowBackground());
				} else {
					lbl.setBackground(this.style.getBackground());
				}
			//}
			return lbl;
		}
	};
}
