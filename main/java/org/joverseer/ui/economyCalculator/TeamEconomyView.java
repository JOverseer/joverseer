package org.joverseer.ui.economyCalculator;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

import org.joverseer.domain.EconomyCalculatorData;
import org.joverseer.domain.NationEconomy;
import org.joverseer.game.Game;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.support.GameHolder;
import org.joverseer.tools.orderCostCalculator.OrderCostCalculator;
import org.joverseer.ui.LifecycleEventsEnum;
import org.joverseer.ui.listviews.NationEconomyListView;
import org.joverseer.ui.listviews.NationProductionListView;
import org.joverseer.ui.listviews.NationStatisticsListView;
import org.joverseer.ui.support.JOverseerEvent;
import org.joverseer.ui.support.UIUtils;
import org.joverseer.ui.support.controls.JOverseerTable;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.richclient.application.support.AbstractView;
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
public class TeamEconomyView extends AbstractView implements ApplicationListener {
	JTable teamEconomyTable;
	TeamEconomyTableModel teamEconomyTableModel;
	JComboBox showProductAsCombo;
	NationProductionListView nationProductionListView;
	NationStatisticsListView nationStatisticsListView;
	NationEconomyListView nationEconomyListView;

	@Override
	protected JComponent createControl() {
		TableLayoutBuilder lb = new TableLayoutBuilder();

		lb.relatedGapRow();

		lb.separator("Team Economy");
		lb.row();
		lb.relatedGapRow();

		this.teamEconomyTableModel = new TeamEconomyTableModel();
		this.teamEconomyTable = new JOverseerTable(new com.jidesoft.grid.SortableTableModel(this.teamEconomyTableModel));
		this.teamEconomyTable.getTableHeader().setPreferredSize(new Dimension(400, 16));
		this.teamEconomyTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		for (int i = 0; i < this.teamEconomyTableModel.getColumnCount(); i++) {
			this.teamEconomyTable.getColumnModel().getColumn(i).setPreferredWidth(this.teamEconomyTableModel.getColumnWidth(i));
		}
		this.teamEconomyTable.setDefaultRenderer(Integer.class, new IntegerTeamEconomyTableRenderer());
		this.teamEconomyTable.setDefaultRenderer(String.class, new StringTeamEconomyTableRenderer());
		this.teamEconomyTable.setBackground(Color.white);
		JScrollPane scp = new JScrollPane(this.teamEconomyTable);
		scp.setPreferredSize(new Dimension(600, 250));
		scp.getViewport().setBackground(Color.white);
		scp.getViewport().setOpaque(true);
		lb.cell(scp);

		lb.row();
		lb.relatedGapRow();

		this.showProductAsCombo = new JComboBox(new String[] { TeamEconomyTableModel.PROD_TOTAL, TeamEconomyTableModel.PROD_GAIN, TeamEconomyTableModel.PROD_PRODUCTION, TeamEconomyTableModel.PROD_STORES });
		this.showProductAsCombo.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				TeamEconomyView.this.teamEconomyTableModel.setShowProductsAs(TeamEconomyView.this.showProductAsCombo.getSelectedItem().toString());
				TeamEconomyView.this.teamEconomyTableModel.fireTableDataChanged();
			}
		});
		this.showProductAsCombo.setPreferredSize(new Dimension(230, 20));

		TableLayoutBuilder tlb = new TableLayoutBuilder();
		tlb.cell(new JLabel("products: "));
		tlb.gapCol();
		tlb.cell(this.showProductAsCombo, "colspec=left:230px");
		tlb.gapCol();

		lb.cell(tlb.getPanel(), "align=left");

		JButton btn = new JButton("Update market and order cost");
		btn.setToolTipText("Updates the market profit/cost and order cost for all nations");
		btn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!GameHolder.hasInitializedGame())
					return;
				OrderCostCalculator occ = new OrderCostCalculator();
				Game g = GameHolder.instance().getGame();
				for (EconomyCalculatorData ecd : g.getTurn().getEconomyCalculatorData().getItems()) {
					ecd.setOrdersCost(occ.getTotalOrderCostForNation(g.getTurn(), ecd.getNationNo()));
					ecd.updateMarketFromOrders();
				}

				TeamEconomyView.this.teamEconomyTableModel.fireTableDataChanged();
			}
		});
		lb.gapCol();
		btn.setPreferredSize(new Dimension(200, 20));
		lb.cell(btn, "colspec=right:200px valign=top");

		lb.relatedGapRow();

		lb.separator("Team Products");
		lb.relatedGapRow();

		this.nationProductionListView = new NationProductionListView();
		JPanel pnl = (JPanel) this.nationProductionListView.getControl();
		pnl.setPreferredSize(new Dimension(300, 300));
		lb.cell(pnl);

		lb.relatedGapRow();

		lb.separator("Team Economy");
		lb.relatedGapRow();

		this.nationEconomyListView = new NationEconomyListView();
		pnl = (JPanel) this.nationEconomyListView.getControl();
		pnl.setPreferredSize(new Dimension(300, 270));
		lb.cell(pnl);

		lb.relatedGapRow();

		lb.separator("Team Statistics");
		lb.relatedGapRow();

		this.nationStatisticsListView = new NationStatisticsListView();
		pnl = (JPanel) this.nationStatisticsListView.getControl();
		pnl.setPreferredSize(new Dimension(300, 270));
		lb.cell(pnl);

		lb.relatedGapRow();

		scp = new JScrollPane(lb.getPanel());
		UIUtils.fixScrollPaneMouseScroll(scp);
		return scp;
	}

	public void refreshTableItems() {
		ArrayList<EconomyCalculatorData> ecds = new ArrayList<EconomyCalculatorData>();
		GameHolder.instance();
		if (GameHolder.hasInitializedGame()) {
			for (NationEconomy ne : GameHolder.instance().getGame().getTurn().getNationEconomies().getItems()) {
				EconomyCalculatorData ecd = (EconomyCalculatorData) GameHolder.instance().getGame().getTurn().getContainer(TurnElementsEnum.EconomyCalucatorData).findFirstByProperty("nationNo", ne.getNationNo());
				if (ecd == null) {
					ecd = new EconomyCalculatorData();
					ecd.setNationNo(ne.getNationNo());
					GameHolder.instance().getGame().getTurn().getEconomyCalculatorData().addItem(ecd);

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
			JOverseerEvent e = (JOverseerEvent) applicationEvent;
			if (e.getEventType().equals(LifecycleEventsEnum.EconomyCalculatorUpdate.toString())) {
				this.teamEconomyTableModel.fireTableDataChanged();
			} else if (e.getEventType().equals(LifecycleEventsEnum.SelectedTurnChangedEvent.toString())) {
				try {
					refreshTableItems();
					this.teamEconomyTableModel.fireTableDataChanged();
				} catch (Exception exc) {
					exc.printStackTrace();
				}
			} else if (e.getEventType().equals(LifecycleEventsEnum.GameChangedEvent.toString())) {
				refreshTableItems();
				this.teamEconomyTableModel.fireTableDataChanged();
			} else if (e.getEventType().equals(LifecycleEventsEnum.OrderChangedEvent.toString())) {
				this.teamEconomyTableModel.fireTableDataChanged();
			}

		}
	}

	/**
	 * Renderer for the team economy main table
	 * 
	 * @author Marios Skounakis
	 */
	class IntegerTeamEconomyTableRenderer extends DefaultTableCellRenderer {
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
			} else if (column == TeamEconomyTableModel.iMarket) {
				if (row < TeamEconomyView.this.teamEconomyTableModel.getRowCount() - 1) {
					Integer amt = (Integer) value;
					if (amt > EconomyCalculator.getMarketLimitWarningThreshhold()) {
						lbl.setForeground(Color.red);
					}
				}
				// } else if (column ==
				// teamEconomyTableModel.getBestNatSellIndex(row)) {
				// lbl.setForeground(Color.green);
				// } else if (column ==
				// teamEconomyTableModel.getSecondBestNatSellIndex(row)) {
				// lbl.setForeground(Color.green);
			} else if (column == TeamEconomyTableModel.iSurplus) {
				Integer amt = (Integer) value;
				if (amt < 0) {
					lbl.setForeground(Color.red);
				}
			} else if (column == TeamEconomyTableModel.iTaxRate) {
				Integer amt = (Integer) value;
				if (amt < 60) {
					lbl.setForeground(Color.decode("#009900"));
				} else if (amt > 60) {
					lbl.setForeground(Color.red);
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
					lbl.setBackground(Color.decode("#d7dfe7"));
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
		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			if (!isSelected) {
				if (row == TeamEconomyView.this.teamEconomyTableModel.getRowCount() - 1) {
					lbl.setBackground(Color.decode("#d7dfe7"));
				} else {
					lbl.setBackground(Color.white);
				}
			}
			return lbl;
		}
	};
}
