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
		this.teamEconomyTable.setDefaultRenderer(Integer.class, new IntegerTeamEconomyTableRenderer());
		this.teamEconomyTable.setDefaultRenderer(String.class, new StringTeamEconomyTableRenderer());
		this.teamEconomyTable.setBackground(Color.white);
		// we set up the reference to the NationStatisticsModel once we've created it in the view.

		JScrollPane scp = new JScrollPane(this.teamEconomyTable);
		scp.setPreferredSize(new Dimension(600, 250));
		scp.getViewport().setBackground(Color.white);
		scp.getViewport().setOpaque(true);
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

		JButton btn = new JButton(Messages.getString("TeamEconomyView.UpdateMarket")); //$NON-NLS-1$
		btn.setToolTipText(Messages.getString("TeamEconomyView.UpdateAll")); //$NON-NLS-1$
		btn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				updateMarketAndOrderCosts();
			}
		});
		btn.setPreferredSize(new Dimension(200, 20));
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

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			if (value == null)
				return lbl;
			if (column == TeamEconomyTableModel.iFinalGold) {
				Integer amt = (Integer) value;
				if (amt < 0) {
					lbl.setForeground(Color.red);
				}
			} else if (column == TeamEconomyTableModel.iMarketSales) {
				if (row < TeamEconomyView.this.teamEconomyTableModel.getRowCount() - 1) {
					Integer amt = (Integer) value;
					if (amt > EconomyCalculator.getMarketLimitWarningThreshhold()) {
						lbl.setForeground(Color.red);
					}
				}
			} else if (column == TeamEconomyTableModel.iSurplus) {
				Integer amt = (Integer) value;
				if (amt < 0) {
					lbl.setForeground(Color.red);
				}
			} else if (column == TeamEconomyTableModel.iTaxRate) {
				Integer amt = (Integer) value;
				if (amt < 60) {
					lbl.setForeground(Color.decode("#009900")); //$NON-NLS-1$
				} else if (amt > 60) {
					lbl.setForeground(Color.red);
				}
			} else if (column == TeamEconomyTableModel.iHikedTaxRate) {
				if (row == TeamEconomyView.this.teamEconomyTableModel.getRowCount() - 1) {
					lbl.setText("");
				} else {
					Integer amt = (Integer) value;
					if (amt >=100) {
						lbl.setForeground(Color.red);
					} else if (amt == TeamEconomyView.this.teamEconomyTableModel.getValueAt(row, TeamEconomyTableModel.iTaxRate)) {
						lbl.setText("");
					}
				}
			} else {
				if (isSelected) {
					lbl.setForeground(Color.white);
				} else {
					lbl.setForeground(Color.black);
				}
			}

			if (TeamEconomyView.this.teamEconomyTableModel.getColumnClass(column) == Integer.class) {
				lbl.setHorizontalAlignment(SwingConstants.RIGHT);
			}

			if (!isSelected) {
				if (row == TeamEconomyView.this.teamEconomyTableModel.getRowCount() - 1) {
					lbl.setBackground(Color.decode("#d7dfe7")); //$NON-NLS-1$
				} else {
					lbl.setBackground(Color.white);
				}
			}
			if (hasFocus) {
				lbl.setBorder(BorderFactory.createLineBorder(Color.red, 1));
			}
			return lbl;
		}

	}

	class StringTeamEconomyTableRenderer extends DefaultTableCellRenderer {
		/**
		 *
		 */
		private static final long serialVersionUID = -2226537511337457982L;

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			if (!isSelected) {
				if (row == TeamEconomyView.this.teamEconomyTableModel.getRowCount() - 1) {
					lbl.setBackground(Color.decode("#d7dfe7")); //$NON-NLS-1$
				} else {
					lbl.setBackground(Color.white);
				}
			}
			return lbl;
		}
	};
}
