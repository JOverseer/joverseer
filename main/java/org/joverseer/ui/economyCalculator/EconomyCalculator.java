package org.joverseer.ui.economyCalculator;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;

import org.joverseer.domain.NationEconomy;
import org.joverseer.domain.PopulationCenter;
import org.joverseer.domain.PopulationCenterSizeEnum;
import org.joverseer.game.Game;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.metadata.domain.Nation;
import org.joverseer.preferences.PreferenceRegistry;
import org.joverseer.support.GameHolder;
import org.joverseer.tools.orderCostCalculator.OrderCostCalculator;
import org.joverseer.ui.LifecycleEventsEnum;
import org.joverseer.ui.support.GraphicUtils;
import org.joverseer.ui.support.JOverseerEvent;
import org.joverseer.ui.support.UIUtils;
import org.joverseer.ui.support.controls.JOverseerTable;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.richclient.application.support.AbstractView;
import org.springframework.richclient.layout.TableLayoutBuilder;
import org.springframework.richclient.table.BeanTableModel;
import org.springframework.richclient.table.TableUtils;

/**
 * The economy calculator view
 * 
 * The basic functionality is: - a combo box allows the user to select a nation
 * - the view displays all economy information for this nation - economy
 * information can be edited accordingly by the user
 * 
 * @author Marios Skounakis
 */
public class EconomyCalculator extends AbstractView implements ApplicationListener {

	int tranCarOrderCost = 0;
	JLabel autocalcOrderCost;
	JLabel finalGoldWarning;
	JTable marketTable;
	JTable totalsTable;
	JTable pcTable;
	JCheckBox sellBonus;
	JComboBox nationCombo;
	JLabel marketLimitWarning;
	JLabel taxIncrease;
	BeanTableModel lostPopsTableModel;

	public void onApplicationEvent(ApplicationEvent applicationEvent) {
		if (applicationEvent instanceof JOverseerEvent) {
			JOverseerEvent e = (JOverseerEvent) applicationEvent;
			if (e.getEventType().equals(LifecycleEventsEnum.EconomyCalculatorUpdate.toString())) {
				((AbstractTableModel) marketTable.getModel()).fireTableDataChanged();
				((AbstractTableModel) totalsTable.getModel()).fireTableDataChanged();
				refreshMarketLimitWarning();
				refreshTaxIncrease();
				refreshAutocalcOrderCost();
				refreshFinalGoldWarning();
			} else if (e.getEventType().equals(LifecycleEventsEnum.SelectedTurnChangedEvent.toString())) {
				loadNationCombo(false);
				try {
					((AbstractTableModel) marketTable.getModel()).fireTableDataChanged();
					((AbstractTableModel) totalsTable.getModel()).fireTableDataChanged();
					refreshMarketLimitWarning();
					refreshTaxIncrease();
					refreshAutocalcOrderCost();
					refreshFinalGoldWarning();
				} catch (Exception exc) {
					exc.printStackTrace();
				}
			} else if (e.getEventType().equals(LifecycleEventsEnum.GameChangedEvent.toString())) {
				((MarketTableModel) marketTable.getModel()).setGame(null);
				((EconomyTotalsTableModel) totalsTable.getModel()).setGame(null);
				loadNationCombo(true);
			} else if (e.getEventType().equals(LifecycleEventsEnum.OrderChangedEvent.toString())) {
				refreshAutocalcOrderCost();
				if ("yes".equals(PreferenceRegistry.instance().getPreferenceValue("currentHexView.autoUpdateEconCalcMarketFromOrders"))) {
					((EconomyTotalsTableModel) totalsTable.getModel()).updateMarketFromOrders();
					refreshMarketLimitWarning();
					((AbstractTableModel) marketTable.getModel()).fireTableDataChanged();
					((AbstractTableModel) totalsTable.getModel()).fireTableDataChanged();
				}
				refreshFinalGoldWarning();
			}

		}
	}

	/**
	 * If sell amount is above this amount give a "market limit warning"
	 */
	public static int getMarketLimitWarningThreshhold() {
		String pval = PreferenceRegistry.instance().getPreferenceValue("general.marketSellLimit");
		try {
			return Integer.parseInt(pval);
		} catch (Exception exc) {
			return 20000;
		}
	}

	/**
	 * Refreshes the market limit warning message
	 */
	private void refreshMarketLimitWarning() {
		int marketProfits = ((MarketTableModel) marketTable.getModel()).getEconomyCalculatorData().getMarketProfits();
		if (marketProfits >= getMarketLimitWarningThreshhold()) {
			marketLimitWarning.setText("Market limit warning!");
			marketLimitWarning.setVisible(true);
		} else {
			marketLimitWarning.setVisible(false);
		}
	}

	private void refreshFinalGoldWarning() {
		int finalGold = ((EconomyTotalsTableModel) totalsTable.getModel()).getFinalGold();
		if (finalGold >= 0) {
			finalGoldWarning.setVisible(false);
		} else {
			// check orders cost
			int ordersCost = ((MarketTableModel) marketTable.getModel()).getEconomyCalculatorData().getOrdersCost();
			if (ordersCost > 0) {
				// find cost for TranCar orders
				if (ordersCost < -finalGold) {
					finalGoldWarning.setVisible(true);
					finalGoldWarning.setText("Danger - negative final gold without including order cost!");
				} else if (tranCarOrderCost < -finalGold) {
					finalGoldWarning.setVisible(true);
					finalGoldWarning.setText("Negative final gold. Some of your orders may not be executed.");
				} else {
					finalGoldWarning.setVisible(true);
					finalGoldWarning.setText("Negative final gold. Some of your TranCar (948) orders may be adjusted.");
				}
			}
		}

	}

	/**
	 * Refreshes the tax increase message
	 */
	private void refreshTaxIncrease() {
		int taxIncreaseAmt = ((EconomyTotalsTableModel) totalsTable.getModel()).getTaxIncrease();
		if (taxIncreaseAmt == 0) {
			taxIncrease.setVisible(false);
		} else {
			taxIncrease.setVisible(true);
			taxIncrease.setText("Your taxes will go up by " + taxIncreaseAmt + "%!");
		}
	}

	private void loadNationCombo(boolean autoFocusOnGameNation) {
		nationCombo.removeAllItems();
		Game g = GameHolder.instance().getGame();
		if (!Game.isInitialized(g))
			return;
		if (g.getTurn() == null)
			return;
		int selectedIndex = 0;
		int i = 0;
		for (Nation n : (ArrayList<Nation>) g.getMetadata().getNations()) {
			NationEconomy ne = (NationEconomy) g.getTurn().getContainer(TurnElementsEnum.NationEconomy).findFirstByProperty("nationNo", n.getNumber());
			// load only nations for which economy has been imported
			if (ne == null)
				continue;
			nationCombo.addItem(n.getName());
			if (autoFocusOnGameNation && n.getNumber() == g.getMetadata().getNationNo()) {
				selectedIndex = i;
			}
			i++;
		}
		if (nationCombo.getItemCount() > 0) {
			nationCombo.setSelectedIndex(selectedIndex);
		}

	}

	/**
	 * Refreshes the autocalc order cost field
	 */
	private void refreshAutocalcOrderCost() {
		OrderCostCalculator calc = new OrderCostCalculator();
		int totalCost = calc.getTotalOrderCostForNation(GameHolder.instance().getGame().getTurn(), getSelectedNationNo());
		tranCarOrderCost = calc.getTotalTranCarOrderCostForNation(GameHolder.instance().getGame().getTurn(), getSelectedNationNo());

		autocalcOrderCost.setText(String.valueOf(totalCost));
	}

	private int getSelectedNationNo() {
		Game g = GameHolder.instance().getGame();
		if (nationCombo.getSelectedItem() == null)
			return -1;
		Nation n = g.getMetadata().getNationByName(nationCombo.getSelectedItem().toString());
		return n.getNumber();
	}

	@Override
	protected JComponent createControl() {
		TableLayoutBuilder lb = new TableLayoutBuilder();

		lb.relatedGapRow();

		lb.separator("Nation");
		lb.row();
		lb.relatedGapRow();

		lb.cell(nationCombo = new JComboBox(), "align=left");
		nationCombo.setPreferredSize(new Dimension(200, 24));
		nationCombo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// when selected nation changed
				// refresh all tables and warnings
				Game g = GameHolder.instance().getGame();
				if (nationCombo.getSelectedItem() == null)
					return;
				Nation n = g.getMetadata().getNationByName(nationCombo.getSelectedItem().toString());
				((MarketTableModel) marketTable.getModel()).setNationNo(n.getNumber());
				((EconomyTotalsTableModel) totalsTable.getModel()).setNationNo(n.getNumber());
				((AbstractTableModel) marketTable.getModel()).fireTableDataChanged();
				((AbstractTableModel) totalsTable.getModel()).fireTableDataChanged();
				refreshPcs(n.getNumber());
				refreshMarketLimitWarning();
				refreshAutocalcOrderCost();
				refreshFinalGoldWarning();
				refreshTaxIncrease();
				sellBonus.setSelected(((EconomyTotalsTableModel) totalsTable.getModel()).getEconomyCalculatorData().getSellBonus());
			}
		});
		lb.row();

		lb.cell(sellBonus = new JCheckBox(), "align=left");
		sellBonus.setText("sell bonus: ");
		sellBonus.setHorizontalTextPosition(JCheckBox.LEFT);
		sellBonus.setBackground(Color.white);
		sellBonus.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// when sell bonus changed
				// update the economy calculator data for this nation
				// refresh the economy tables and the warnings
				Game g = GameHolder.instance().getGame();
				if (!Game.isInitialized(g))
					return;
				if (nationCombo.getSelectedItem() == null)
					return;
				if (((MarketTableModel) marketTable.getModel()).getEconomyCalculatorData() == null)
					return;
				((MarketTableModel) marketTable.getModel()).getEconomyCalculatorData().setSellBonus(sellBonus.isSelected());
				((AbstractTableModel) marketTable.getModel()).fireTableDataChanged();
				((AbstractTableModel) totalsTable.getModel()).fireTableDataChanged();
				refreshMarketLimitWarning();
				refreshFinalGoldWarning();
				refreshTaxIncrease();
			}
		});
		lb.row();

		lb.relatedGapRow();

		lb.separator("Market");
		lb.row();
		lb.relatedGapRow();

		MarketTableModel mtm = new MarketTableModel();
		marketTable = new JOverseerTable(mtm);
		mtm.setTable(marketTable);

		marketTable.getTableHeader().setPreferredSize(new Dimension(400, 16));
		marketTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		for (int i = 0; i < mtm.getColumnCount(); i++) {
			marketTable.getColumnModel().getColumn(i).setPreferredWidth(mtm.getColumnWidth(i));
		}
		marketTable.setDefaultRenderer(Integer.class, new MarketRenderer());
		marketTable.setDefaultRenderer(String.class, new MarketRenderer());
		marketTable.setBackground(Color.white);
		JScrollPane scp = new JScrollPane(marketTable);
		scp.setPreferredSize(new Dimension(600, 226));
		scp.getViewport().setBackground(Color.white);
		scp.getViewport().setOpaque(true);
		lb.cell(scp);

		lb.row();
		lb.relatedGapRow();

		lb.separator("Totals");
		lb.row();
		lb.relatedGapRow();

		EconomyTotalsTableModel ettm = new EconomyTotalsTableModel();

		mtm.setTotalsModel(ettm);
		totalsTable = new JOverseerTable(ettm);
		ettm.setTable(totalsTable);
		totalsTable.getTableHeader().setVisible(false);
		for (int i = 0; i < ettm.getColumnCount(); i++) {
			totalsTable.getColumnModel().getColumn(i).setPreferredWidth(ettm.getColumnWidth(i));
		}
		totalsTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		totalsTable.setDefaultRenderer(String.class, new TotalsRenderer());
		totalsTable.setDefaultRenderer(Integer.class, new TotalsRenderer());
		totalsTable.setBackground(Color.white);
		scp = new JScrollPane(totalsTable);
		scp.setPreferredSize(new Dimension(600, 90));
		scp.getViewport().setBackground(Color.white);
		scp.getViewport().setOpaque(true);
		lb.cell(scp);

		TableLayoutBuilder tlb = new TableLayoutBuilder();
		tlb.cell(new JLabel("Autocalc order cost: "), "colspec=left:110px");
		tlb.gapCol();
		autocalcOrderCost = new JLabel("0");
		tlb.cell(autocalcOrderCost, "align=left");

		tlb.row();
		tlb.relatedGapRow();

		JButton btn = new JButton("<- update cost");
		btn.setPreferredSize(new Dimension(130, 24));
		btn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				// when button clicked
				// update the economy calculator data with the autocalc order
				// cost value
				((EconomyTotalsTableModel) totalsTable.getModel()).setOrdersCost(Integer.parseInt(autocalcOrderCost.getText()));
				((AbstractTableModel) totalsTable.getModel()).fireTableDataChanged();
				refreshFinalGoldWarning();
			}
		});
		tlb.cell(btn);
		tlb.relatedGapRow();
		btn = new JButton("<- update market");
		btn.setPreferredSize(new Dimension(130, 24));
		btn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				((EconomyTotalsTableModel) totalsTable.getModel()).updateMarketFromOrders();
				((AbstractTableModel) totalsTable.getModel()).fireTableDataChanged();
				((AbstractTableModel) marketTable.getModel()).fireTableDataChanged();
				refreshMarketLimitWarning();
				refreshFinalGoldWarning();
			}
		});
		tlb.cell(btn);
		tlb.relatedGapRow();

		JPanel pnl = tlb.getPanel();
		pnl.setBackground(Color.white);
		lb.gapCol();
		lb.cell(pnl, "colspec=left:150px valign=top");
		lb.row();

		marketLimitWarning = new JLabel("Market limit warning!");
		marketLimitWarning.setFont(GraphicUtils.getFont(marketLimitWarning.getFont().getName(), Font.BOLD, marketLimitWarning.getFont().getSize()));
		marketLimitWarning.setForeground(Color.red);
		lb.cell(marketLimitWarning);
		lb.row();
		lb.relatedGapRow();

		finalGoldWarning = new JLabel("Final gold warning!");
		finalGoldWarning.setFont(GraphicUtils.getFont(finalGoldWarning.getFont().getName(), Font.BOLD, marketLimitWarning.getFont().getSize()));
		finalGoldWarning.setForeground(Color.red);
		lb.cell(finalGoldWarning);
		lb.row();
		lb.relatedGapRow();

		taxIncrease = new JLabel("Your taxes will go up.");
		taxIncrease.setFont(GraphicUtils.getFont(taxIncrease.getFont().getName(), Font.BOLD, taxIncrease.getFont().getSize()));
		taxIncrease.setForeground(Color.red);
		lb.cell(taxIncrease);
		lb.row();

		lb.relatedGapRow();
		lb.separator("Pop Centers expected to be lost this turn");
		lb.row();
		lb.relatedGapRow();

		lostPopsTableModel = new LostPopsTableModel();
		// pcTable = new JTable(lostPopsTableModel);
		pcTable = TableUtils.createStandardSortableTable(lostPopsTableModel);
		pcTable.setBackground(Color.white);
		pcTable.setDefaultRenderer(Boolean.class, totalsTable.getDefaultRenderer(Boolean.class));
		pcTable.setDefaultEditor(Boolean.class, totalsTable.getDefaultEditor(Boolean.class));
		org.joverseer.ui.support.controls.TableUtils.setTableColumnWidths(pcTable, getLostPCColumWidths());
		scp = new JScrollPane(pcTable);
		scp.setPreferredSize(new Dimension(600, 300));
		scp.getViewport().setBackground(Color.white);
		scp.getViewport().setOpaque(true);
		lb.cell(scp);

		lb.row();

		JPanel p = lb.getPanel();
		p.setBackground(Color.white);
		scp = new JScrollPane(p);
		scp.getViewport().setBackground(Color.white);
		scp.getViewport().setOpaque(true);

		UIUtils.fixScrollPaneMouseScroll(scp);

		return scp;
	}

	public void refreshPcs(int nationNo) {
		ArrayList<PopulationCenter> items = new ArrayList<PopulationCenter>();
		Game g = GameHolder.instance().getGame();
		if (Game.isInitialized(g) && g.getTurn() != null) {
			for (PopulationCenter pc : g.getTurn().getPopulationCenters().getItems()) {
				if (pc.getNationNo() == nationNo && pc.getSize() != PopulationCenterSizeEnum.ruins) {
					items.add(pc);
				}
			}
		}
		lostPopsTableModel.setRows(items);
		lostPopsTableModel.fireTableDataChanged();
	}

	/**
	 * Renderer for the Market Table
	 * 
	 * @author Marios Skounakis
	 */
	public class MarketRenderer extends DefaultTableCellRenderer {
		// TODO export colors to color.properties
		Color[] rowColors = new Color[] { Color.decode("#ADD3A6"), Color.decode("#ADD3A6"), Color.decode("#FFCCAA"), Color.decode("#FFCCAA"), Color.decode("#ADD3A6"), Color.white, Color.white, Color.decode("#ADD3A6"), Color.decode("#ADD3A6"), Color.white, Color.white, Color.white, Color.lightGray };

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			if (!isSelected && column > 0) {
				c.setBackground(rowColors[row]);
			}
			JLabel lbl = ((JLabel) c);
			if (hasFocus) {
				lbl.setBorder(BorderFactory.createLineBorder(Color.red, 1));
			}
			lbl.setHorizontalAlignment(JLabel.RIGHT);

			return c;
		}
	}

	/**
	 * Renderer for the totals table
	 * 
	 * @author Marios Skounakis
	 */
	public class TotalsRenderer extends DefaultTableCellRenderer {
		// TODO export colors to resources
		// TODO remove hard-coded column and row numbers
		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			JLabel lbl = ((JLabel) c);
			lbl.setHorizontalAlignment(JLabel.RIGHT);
			if (row == 3 && column == 5 || row == 0 && column == 5) { // final
				// gold
				// or
				// orders
				// cost
				lbl.setFont(GraphicUtils.getFont(lbl.getFont().getName(), Font.BOLD, lbl.getFont().getSize()));
			} else {
				lbl.setFont(GraphicUtils.getFont(lbl.getFont().getName(), Font.PLAIN, lbl.getFont().getSize()));
			}
			if (row == 3 && column == 3 && !value.toString().equals("")) {
				int amount = Integer.parseInt(value.toString());
				if (amount >= getMarketLimitWarningThreshhold()) {
					if (!isSelected) {
						lbl.setBackground(Color.red);
						return c;
					}
				}
			}
			if (!isSelected) {
				if (((EconomyTotalsTableModel) table.getModel()).isCellEditable(row, column)) { // orders
					// cost
					// ,
					// gold
					// production
					lbl.setBackground(Color.decode("#ADD3A6"));
				} else {
					lbl.setBackground(Color.white);
				}
			}
			return c;
		}
	}

	/**
	 * Column widths for the Lost Pop Centers list view
	 */
	public int[] getLostPCColumWidths() {
		return new int[] { 140, 64, 64, 64, 65, 96 };
	}

}
