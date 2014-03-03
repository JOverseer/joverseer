package org.joverseer.ui.economyCalculator;

import java.awt.BorderLayout;
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
import javax.swing.SwingConstants;
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
import org.springframework.richclient.dialog.MessageDialog;
import org.springframework.richclient.layout.TableLayoutBuilder;
import org.springframework.richclient.table.BeanTableModel;
import org.springframework.richclient.table.TableUtils;

import com.jidesoft.popup.JidePopup;

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

	@Override
	public void onApplicationEvent(ApplicationEvent applicationEvent) {
		if (applicationEvent instanceof JOverseerEvent) {
			JOverseerEvent e = (JOverseerEvent) applicationEvent;
			if (e.getEventType().equals(LifecycleEventsEnum.EconomyCalculatorUpdate.toString())) {
				((AbstractTableModel) this.marketTable.getModel()).fireTableDataChanged();
				((AbstractTableModel) this.totalsTable.getModel()).fireTableDataChanged();
				refreshMarketLimitWarning();
				refreshTaxIncrease();
				refreshAutocalcOrderCost();
				refreshFinalGoldWarning();
			} else if (e.getEventType().equals(LifecycleEventsEnum.SelectedTurnChangedEvent.toString())) {
				loadNationCombo(false);
				try {
					((AbstractTableModel) this.marketTable.getModel()).fireTableDataChanged();
					((AbstractTableModel) this.totalsTable.getModel()).fireTableDataChanged();
					refreshMarketLimitWarning();
					refreshTaxIncrease();
					refreshAutocalcOrderCost();
					refreshFinalGoldWarning();
				} catch (Exception exc) {
					exc.printStackTrace();
				}
			} else if (e.getEventType().equals(LifecycleEventsEnum.GameChangedEvent.toString())) {
				((MarketTableModel) this.marketTable.getModel()).setGame(null);
				((EconomyTotalsTableModel) this.totalsTable.getModel()).setGame(null);
				loadNationCombo(true);
			} else if (e.getEventType().equals(LifecycleEventsEnum.OrderChangedEvent.toString())) {
				refreshAutocalcOrderCost();
				if ("yes".equals(PreferenceRegistry.instance().getPreferenceValue("currentHexView.autoUpdateEconCalcMarketFromOrders"))) {
					((EconomyTotalsTableModel) this.totalsTable.getModel()).updateMarketFromOrders();
					refreshMarketLimitWarning();
					((AbstractTableModel) this.marketTable.getModel()).fireTableDataChanged();
					((AbstractTableModel) this.totalsTable.getModel()).fireTableDataChanged();
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
		int marketProfits = ((MarketTableModel) this.marketTable.getModel()).getEconomyCalculatorData().getMarketProfits();
		if (marketProfits >= getMarketLimitWarningThreshhold()) {
			this.marketLimitWarning.setText("Market limit warning!");
			this.marketLimitWarning.setVisible(true);
		} else {
			this.marketLimitWarning.setVisible(false);
		}
	}

	private void refreshFinalGoldWarning() {
		int finalGold = ((EconomyTotalsTableModel) this.totalsTable.getModel()).getFinalGold();
		if (finalGold >= 0) {
			this.finalGoldWarning.setVisible(false);
		} else {
			// check orders cost
			int ordersCost = ((MarketTableModel) this.marketTable.getModel()).getEconomyCalculatorData().getOrdersCost();
			if (ordersCost > 0) {
				// find cost for TranCar orders
				if (ordersCost < -finalGold) {
					this.finalGoldWarning.setVisible(true);
					this.finalGoldWarning.setText("Danger - negative final gold without including order cost!");
				} else if (this.tranCarOrderCost < -finalGold) {
					this.finalGoldWarning.setVisible(true);
					this.finalGoldWarning.setText("Negative final gold. Some of your orders may not be executed.");
				} else {
					this.finalGoldWarning.setVisible(true);
					this.finalGoldWarning.setText("Negative final gold. Some of your TranCar (948) orders may be adjusted.");
				}
			}
		}

	}

	/**
	 * Refreshes the tax increase message
	 */
	private void refreshTaxIncrease() {
		int taxIncreaseAmt = ((EconomyTotalsTableModel) this.totalsTable.getModel()).getTaxIncrease();
		if (taxIncreaseAmt == 0) {
			this.taxIncrease.setVisible(false);
		} else {
			this.taxIncrease.setVisible(true);
			int finalTaxAmt = ((EconomyTotalsTableModel) this.totalsTable.getModel()).getTaxRate() + taxIncreaseAmt; 
			this.taxIncrease.setText("Your taxes will go up by " + taxIncreaseAmt + "% to " + finalTaxAmt + "!");
		}
	}

	private void loadNationCombo(boolean autoFocusOnGameNation) {
		this.nationCombo.removeAllItems();
		Game g = GameHolder.instance().getGame();
		if (!Game.isInitialized(g))
			return;
		if (g.getTurn() == null)
			return;
		int selectedIndex = 0;
		int i = 0;
		for (Nation n : g.getMetadata().getNations()) {
			NationEconomy ne = (NationEconomy) g.getTurn().getContainer(TurnElementsEnum.NationEconomy).findFirstByProperty("nationNo", n.getNumber());
			// load only nations for which economy has been imported
			if (ne == null)
				continue;
			this.nationCombo.addItem(n.getName());
			if (autoFocusOnGameNation && n.getNumber() == g.getMetadata().getNationNo()) {
				selectedIndex = i;
			}
			i++;
		}
		if (this.nationCombo.getItemCount() > 0) {
			this.nationCombo.setSelectedIndex(selectedIndex);
		}

	}

	/**
	 * Refreshes the autocalc order cost field
	 */
	private void refreshAutocalcOrderCost() {
		OrderCostCalculator calc = new OrderCostCalculator();
		int totalCost = calc.getTotalOrderCostForNation(GameHolder.instance().getGame().getTurn(), getSelectedNationNo());
		this.tranCarOrderCost = calc.getTotalTranCarOrderCostForNation(GameHolder.instance().getGame().getTurn(), getSelectedNationNo());

		this.autocalcOrderCost.setText(String.valueOf(totalCost));
	}

	private int getSelectedNationNo() {
		Game g = GameHolder.instance().getGame();
		if (this.nationCombo.getSelectedItem() == null)
			return -1;
		Nation n = g.getMetadata().getNationByName(this.nationCombo.getSelectedItem().toString());
		return n.getNumber();
	}

	@Override
	protected JComponent createControl() {
		TableLayoutBuilder lb = new TableLayoutBuilder();

		lb.relatedGapRow();

		lb.separator("Nation");
		lb.row();
		lb.relatedGapRow();

		lb.cell(this.nationCombo = new JComboBox(), "align=left");
		this.nationCombo.setPreferredSize(new Dimension(200, 24));
		this.nationCombo.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// when selected nation changed
				// refresh all tables and warnings
				Game g = GameHolder.instance().getGame();
				if (EconomyCalculator.this.nationCombo.getSelectedItem() == null)
					return;
				Nation n = g.getMetadata().getNationByName(EconomyCalculator.this.nationCombo.getSelectedItem().toString());
				((MarketTableModel) EconomyCalculator.this.marketTable.getModel()).setNationNo(n.getNumber());
				((EconomyTotalsTableModel) EconomyCalculator.this.totalsTable.getModel()).setNationNo(n.getNumber());
				((AbstractTableModel) EconomyCalculator.this.marketTable.getModel()).fireTableDataChanged();
				((AbstractTableModel) EconomyCalculator.this.totalsTable.getModel()).fireTableDataChanged();
				refreshPcs(n.getNumber());
				refreshMarketLimitWarning();
				refreshAutocalcOrderCost();
				refreshFinalGoldWarning();
				refreshTaxIncrease();
				EconomyCalculator.this.sellBonus.setSelected(((EconomyTotalsTableModel) EconomyCalculator.this.totalsTable.getModel()).getEconomyCalculatorData().getSellBonus());
			}
		});
		lb.row();

		lb.cell(this.sellBonus = new JCheckBox(), "align=left");
		this.sellBonus.setText("sell bonus: ");
		this.sellBonus.setHorizontalTextPosition(SwingConstants.LEFT);
		this.sellBonus.setBackground(Color.white);
		this.sellBonus.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// when sell bonus changed
				// update the economy calculator data for this nation
				// refresh the economy tables and the warnings
				Game g = GameHolder.instance().getGame();
				if (!Game.isInitialized(g))
					return;
				if (EconomyCalculator.this.nationCombo.getSelectedItem() == null)
					return;
				if (((MarketTableModel) EconomyCalculator.this.marketTable.getModel()).getEconomyCalculatorData() == null)
					return;
				((MarketTableModel) EconomyCalculator.this.marketTable.getModel()).getEconomyCalculatorData().setSellBonus(EconomyCalculator.this.sellBonus.isSelected());
				((AbstractTableModel) EconomyCalculator.this.marketTable.getModel()).fireTableDataChanged();
				((AbstractTableModel) EconomyCalculator.this.totalsTable.getModel()).fireTableDataChanged();
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
		this.marketTable = new JOverseerTable(mtm);
		mtm.setTable(this.marketTable);

		this.marketTable.getTableHeader().setPreferredSize(new Dimension(400, 16));
		this.marketTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		for (int i = 0; i < mtm.getColumnCount(); i++) {
			this.marketTable.getColumnModel().getColumn(i).setPreferredWidth(mtm.getColumnWidth(i));
		}
		this.marketTable.setDefaultRenderer(Integer.class, new MarketRenderer());
		this.marketTable.setDefaultRenderer(String.class, new MarketRenderer());
		this.marketTable.setBackground(Color.white);
		JScrollPane scp = new JScrollPane(this.marketTable);
		scp.setPreferredSize(new Dimension(600, 226));
		scp.getViewport().setBackground(Color.white);
		scp.getViewport().setOpaque(true);
		lb.cell(scp);

		JButton priceHistory = new JButton();
		priceHistory.setText("price history");
		priceHistory.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				final JidePopup popup = new JidePopup();
				popup.getContentPane().setLayout(new BorderLayout());
				TableLayoutBuilder lb1 = new TableLayoutBuilder();
				JButton closePopup = new JButton("Close");
				closePopup.setPreferredSize(new Dimension(70, 20));
				closePopup.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e1) {
						popup.hidePopup();
					}
				});

				JLabel lbl = new JLabel();
				int selCol = EconomyCalculator.this.marketTable.getSelectedColumn();
				String priceHistory1 = null;
				if (selCol > -1) {
					priceHistory1 = ((MarketTableModel) EconomyCalculator.this.marketTable.getModel()).getPriceHistory(selCol, 10);
				}
				if (priceHistory1 == null || priceHistory1.equals("")) {
					Game g = GameHolder.instance().getGame();
					if (!Game.isInitialized(g))
						return;
					String error = "You must select a product (click anywhere on a product column in the table to the left.";
					if (g.getCurrentTurn() == 0) {
						error = "Price history not available on Turn 0.";
					}
					MessageDialog dlg = new MessageDialog("Price History", error);
					dlg.showDialog();
					return;
				}
				lbl.setText(priceHistory1);
				lb1.cell(lbl);
				lb1.relatedGapRow();
				lb1.cell(closePopup, "align=center");
				lb1.relatedGapRow();

				JScrollPane scp1 = new JScrollPane(lb1.getPanel());
				scp1.setPreferredSize(new Dimension(160, 328));
				scp1.getVerticalScrollBar().setUnitIncrement(16);
				popup.getContentPane().add(scp1);
				popup.updateUI();
				popup.setResizable(true);
				popup.setMovable(true);
				popup.showPopup();
			}
		});
		lb.cell(priceHistory, "valign=top");

		lb.row();
		lb.relatedGapRow();

		lb.separator("Totals");
		lb.row();
		lb.relatedGapRow();

		EconomyTotalsTableModel ettm = new EconomyTotalsTableModel();

		mtm.setTotalsModel(ettm);
		this.totalsTable = new JOverseerTable(ettm);
		ettm.setTable(this.totalsTable);
		this.totalsTable.getTableHeader().setVisible(false);
		for (int i = 0; i < ettm.getColumnCount(); i++) {
			this.totalsTable.getColumnModel().getColumn(i).setPreferredWidth(ettm.getColumnWidth(i));
		}
		this.totalsTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		this.totalsTable.setDefaultRenderer(String.class, new TotalsRenderer());
		this.totalsTable.setDefaultRenderer(Integer.class, new TotalsRenderer());
		this.totalsTable.setBackground(Color.white);
		scp = new JScrollPane(this.totalsTable);
		scp.setPreferredSize(new Dimension(600, 90));
		scp.getViewport().setBackground(Color.white);
		scp.getViewport().setOpaque(true);
		lb.cell(scp);

		TableLayoutBuilder tlb = new TableLayoutBuilder();
		tlb.cell(new JLabel("Autocalc order cost: "), "colspec=left:110px");
		tlb.gapCol();
		this.autocalcOrderCost = new JLabel("0");
		tlb.cell(this.autocalcOrderCost, "align=left");

		tlb.row();
		tlb.relatedGapRow();

		JButton btn = new JButton("<- update cost");
		btn.setPreferredSize(new Dimension(130, 24));
		btn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// when button clicked
				// update the economy calculator data with the autocalc order
				// cost value
				((EconomyTotalsTableModel) EconomyCalculator.this.totalsTable.getModel()).setOrdersCost(Integer.parseInt(EconomyCalculator.this.autocalcOrderCost.getText()));
				((AbstractTableModel) EconomyCalculator.this.totalsTable.getModel()).fireTableDataChanged();
				refreshFinalGoldWarning();
				refreshTaxIncrease();
			}
		});
		tlb.cell(btn);
		tlb.relatedGapRow();
		btn = new JButton("<- update market");
		btn.setPreferredSize(new Dimension(130, 24));

		btn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				((EconomyTotalsTableModel) EconomyCalculator.this.totalsTable.getModel()).updateMarketFromOrders();
				((AbstractTableModel) EconomyCalculator.this.totalsTable.getModel()).fireTableDataChanged();
				((AbstractTableModel) EconomyCalculator.this.marketTable.getModel()).fireTableDataChanged();
				refreshMarketLimitWarning();
				refreshFinalGoldWarning();
				refreshTaxIncrease();
			}
		});
		tlb.cell(btn);
		tlb.relatedGapRow();

		JPanel pnl = tlb.getPanel();
		pnl.setBackground(Color.white);
		lb.gapCol();
		lb.cell(pnl, "colspec=left:150px valign=top");
		lb.row();

		this.marketLimitWarning = new JLabel("Market limit warning!");
		this.marketLimitWarning.setFont(GraphicUtils.getFont(this.marketLimitWarning.getFont().getName(), Font.BOLD, this.marketLimitWarning.getFont().getSize()));
		this.marketLimitWarning.setForeground(Color.red);
		lb.cell(this.marketLimitWarning);
		lb.row();
		lb.relatedGapRow();

		this.finalGoldWarning = new JLabel("Final gold warning!");
		this.finalGoldWarning.setFont(GraphicUtils.getFont(this.finalGoldWarning.getFont().getName(), Font.BOLD, this.marketLimitWarning.getFont().getSize()));
		this.finalGoldWarning.setForeground(Color.red);
		lb.cell(this.finalGoldWarning);
		lb.row();
		lb.relatedGapRow();

		this.taxIncrease = new JLabel("Your taxes will go up.");
		this.taxIncrease.setFont(GraphicUtils.getFont(this.taxIncrease.getFont().getName(), Font.BOLD, this.taxIncrease.getFont().getSize()));
		this.taxIncrease.setForeground(Color.red);
		lb.cell(this.taxIncrease);
		lb.row();

		lb.relatedGapRow();
		lb.separator("Pop Centers expected to be lost this turn");
		lb.row();
		lb.relatedGapRow();

		this.lostPopsTableModel = new LostPopsTableModel();
		// pcTable = new JTable(lostPopsTableModel);
		this.pcTable = TableUtils.createStandardSortableTable(this.lostPopsTableModel);
		this.pcTable.setBackground(Color.white);
		this.pcTable.setDefaultRenderer(Boolean.class, this.totalsTable.getDefaultRenderer(Boolean.class));
		this.pcTable.setDefaultEditor(Boolean.class, this.totalsTable.getDefaultEditor(Boolean.class));
		org.joverseer.ui.support.controls.TableUtils.setTableColumnWidths(this.pcTable, getLostPCColumWidths());
		this.pcTable.setMaximumSize(new Dimension(600, 1000));
		lb.cell(this.pcTable, "align=left");

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
		this.lostPopsTableModel.setRows(items);
		this.lostPopsTableModel.fireTableDataChanged();
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
				c.setBackground(this.rowColors[row]);
			}
			JLabel lbl = ((JLabel) c);
			if (hasFocus) {
				lbl.setBorder(BorderFactory.createLineBorder(Color.red, 1));
			}
			lbl.setHorizontalAlignment(SwingConstants.RIGHT);

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
			lbl.setHorizontalAlignment(SwingConstants.RIGHT);
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
