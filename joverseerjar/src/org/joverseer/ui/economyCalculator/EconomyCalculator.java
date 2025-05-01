package org.joverseer.ui.economyCalculator;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.AbstractBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import org.joverseer.JOApplication;
import org.joverseer.domain.EconomyCalculatorData;
import org.joverseer.domain.IHasMapLocation;
import org.joverseer.domain.PopulationCenter;
import org.joverseer.domain.PopulationCenterSizeEnum;
import org.joverseer.game.Game;
import org.joverseer.game.Turn;
import org.joverseer.metadata.SNAEnum;
import org.joverseer.metadata.domain.Nation;
import org.joverseer.preferences.PreferenceRegistry;
import org.joverseer.support.GameHolder;
import org.joverseer.tools.orderCostCalculator.OrderCostCalculator;
import org.joverseer.ui.BaseView;
import org.joverseer.ui.LifecycleEventsEnum;
import org.joverseer.ui.listviews.renderers.HexNumberCellRenderer;
import org.joverseer.ui.support.GraphicUtils;
import org.joverseer.ui.support.JOverseerEvent;
import org.joverseer.ui.support.Messages;
import org.joverseer.ui.support.PLaFHelper;
import org.joverseer.ui.support.UIUtils;
import org.joverseer.ui.support.controls.JOverseerTable;
import org.joverseer.ui.support.controls.NationComboBox;
import org.joverseer.ui.support.controls.TableUtils;
import org.joverseer.ui.support.drawing.ColorPicker;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.richclient.application.PageComponentContext;
import org.springframework.richclient.command.support.AbstractActionCommandExecutor;
import org.springframework.richclient.dialog.MessageDialog;
import org.springframework.richclient.layout.TableLayoutBuilder;
import org.springframework.richclient.table.BeanTableModel;
import org.springframework.richclient.table.SortableTableModel;

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
public class EconomyCalculator extends BaseView implements ApplicationListener, MouseListener {

	public EconomyCalculator() {
		super();
		// TODO Auto-generated constructor stub
	}

	int tranCarOrderCost = 0;
	JLabel autocalcOrderCost;
	JLabel finalGoldWarning;
	JTable marketTable;
	JTable totalsTable;
	JTable pcTable;
	JCheckBox sellBonus;
	JCheckBox cheaperShips;
	JCheckBox cheapestShips;
	JCheckBox cheapFortifications;
	JCheckBox freeArmyHire;
	JCheckBox marketInfluence;
	NationComboBox nationCombo;
	JLabel marketLimitWarning;
	JLabel taxIncrease;
	BeanTableModel lostPopsTableModel;

	protected SelectHexCommandExecutor selectHexCommandExecutor = new SelectHexCommandExecutor();
	
	@Override
	public void onApplicationEvent(ApplicationEvent applicationEvent) {
		if (applicationEvent instanceof JOverseerEvent) {
			this.onJOEvent((JOverseerEvent) applicationEvent);
		}
	}
	public void onJOEvent(JOverseerEvent e) {
		switch(e.getType()) {
		case EconomyCalculatorUpdate:
			((AbstractTableModel) this.marketTable.getModel()).fireTableDataChanged();
			((AbstractTableModel) this.totalsTable.getModel()).fireTableDataChanged();
			refreshMarketLimitWarning();
			refreshTaxIncrease();
			refreshAutocalcOrderCost();
			refreshFinalGoldWarning();
			break;
		case SelectedTurnChangedEvent:
			if (this.nationCombo.load(false,true) != null) {
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
			}
			break;
		case GameChangedEvent:
			super.resetGame();
			((MarketTableModel) this.marketTable.getModel()).setGame(null);
			((EconomyTotalsTableModel) this.totalsTable.getModel()).setGame(null);
			Nation n = this.nationCombo.load(true, true);
			if (n!= null) {
				refreshSNA(n);
			}
			break;
		case OrderChangedEvent:
			refreshAutocalcOrderCost();
			if ("yes".equals(PreferenceRegistry.instance().getPreferenceValue("currentHexView.autoUpdateEconCalcMarketFromOrders"))) { //$NON-NLS-1$ //$NON-NLS-2$
				((EconomyTotalsTableModel) this.totalsTable.getModel()).updateMarketFromOrders();
				refreshMarketLimitWarning();
				((AbstractTableModel) this.marketTable.getModel()).fireTableDataChanged();
				((AbstractTableModel) this.totalsTable.getModel()).fireTableDataChanged();
			}
			refreshFinalGoldWarning();
			refreshTaxIncrease();
			break;
		}
	}
	/**
	 * If sell amount is above this amount give a "market limit warning"
	 */
	public static int getMarketLimitWarningThreshhold() {
		String pval = PreferenceRegistry.instance().getPreferenceValue("general.marketSellLimit"); //$NON-NLS-1$
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
		int marketSales = ((MarketTableModel) this.marketTable.getModel()).getEconomyCalculatorData().getMarketSales();
		if (marketSales >= getMarketLimitWarningThreshhold()) {
			this.marketLimitWarning.setText(Messages.getString("EconomyCalculator.MarketLimitWarning")); //$NON-NLS-1$
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
					this.finalGoldWarning.setText(Messages.getString("EconomyCalculator.NegativeOrderCost")); //$NON-NLS-1$
				} else if (this.tranCarOrderCost < -finalGold) {
					this.finalGoldWarning.setVisible(true);
					this.finalGoldWarning.setText(Messages.getString("EconomyCalculator.NegativeFinalGold")); //$NON-NLS-1$
				} else {
					this.finalGoldWarning.setVisible(true);
					this.finalGoldWarning.setText(Messages.getString("EconomyCalculator.NegativeFinalGold948")); //$NON-NLS-1$
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
			this.taxIncrease.setText(Messages.getString("EconomyCalculator.taxesUp", new Object[] { taxIncreaseAmt, finalTaxAmt})); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}
	}

	/**
	 * Refreshes the autocalc order cost field
	 */
	private void refreshAutocalcOrderCost() {
		Turn t = this.getTurn();
		OrderCostCalculator calc = new OrderCostCalculator();
		int totalCost = calc.getTotalOrderCostForNation(t, this.nationCombo.getSelectedNationNo());
		this.tranCarOrderCost = calc.getTotalTranCarOrderCostForNation(t, this.nationCombo.getSelectedNationNo());

		this.autocalcOrderCost.setText(String.valueOf(totalCost));
	}


	private void setSellBonusFromSNA(Nation n) {
		EconomyCalculatorData ecd = ((EconomyTotalsTableModel) this.totalsTable.getModel()).getEconomyCalculatorData();
		
		if (n.getSnas().contains(SNAEnum.BuySellBonus)) {
			this.sellBonus.setSelected(true);
			ecd.setSellBonus(true);
			ecd.setSellBonusAmount(20);
		}
		else if(n.getSnas().contains(SNAEnum.BuySellBonus10)) {
			this.sellBonus.setSelected(true);
			ecd.setSellBonus(true);
			ecd.setSellBonusAmount(10);			
		}
		else {
			this.sellBonus.setSelected(false);
			ecd.setSellBonus(false);			
		}
	}
	private void setCheaperShipsFromSNA(Nation n) {
		this.cheaperShips.setSelected(n.getSnas().contains(SNAEnum.ShipsWith750Timber));
	}
	private void setCheapestShipsFromSNA(Nation n) {
		this.cheapestShips.setSelected(n.getSnas().contains(SNAEnum.ShipsWith500Timber));
	}
	private void setCheapFortificationsFromSNA(Nation n) {
		this.cheapFortifications.setSelected(n.getSnas().contains(SNAEnum.FortificationsWithHalfTimber));
	}
	private void setFreeArmyHireFromSNA(Nation n) {
		this.freeArmyHire.setSelected(n.getSnas().contains(SNAEnum.FreeHire));
	}
	private void setMarketInfluenceFromSNA(Nation n) {
		this.marketInfluence.setSelected(n.getSnas().contains(SNAEnum.Influence));
	}
	private void refreshSNA(Nation n) {
		setSellBonusFromSNA(n);
		setCheaperShipsFromSNA(n);
		setCheapestShipsFromSNA(n);
		setCheapFortificationsFromSNA(n);
		setFreeArmyHireFromSNA(n);
		setMarketInfluenceFromSNA(n);
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getClickCount() == 2 && e.getButton() == 1) {
			this.selectHexCommandExecutor.execute();
		}
	}
	
	@Override
	protected void registerLocalCommandExecutors(PageComponentContext pageComponentContext) {
		pageComponentContext.register("selectHexCommand", this.selectHexCommandExecutor);
		this.selectHexCommandExecutor.setEnabled(GameHolder.hasInitializedGame());
	}

	/**
	 * What happens when you double click on a row that implements the
	 * IHasMapLocation interface ... the respective hex is selected
	 */
	private class SelectHexCommandExecutor extends AbstractActionCommandExecutor {

		// TODO move to a separate class?
		@Override
		public void execute() {
			int row = EconomyCalculator.this.pcTable.getSelectedRow();
			if (row >= 0) {
				int idx = 0;
				if (SortableTableModel.class.isInstance(EconomyCalculator.this.pcTable.getModel())) {
					idx = ((SortableTableModel) EconomyCalculator.this.pcTable.getModel()).convertSortedIndexToDataIndex(row);
				} else if (com.jidesoft.grid.SortableTableModel.class.isInstance(EconomyCalculator.this.pcTable.getModel())) {
					idx = ((com.jidesoft.grid.SortableTableModel) EconomyCalculator.this.pcTable.getModel()).getActualRowAt(row);
				}
				if (idx >= EconomyCalculator.this.lostPopsTableModel.getRowCount())
					return;
				try {
					Object obj = EconomyCalculator.this.lostPopsTableModel.getRow(idx);
					if (!IHasMapLocation.class.isInstance(obj))
						return;
					IHasMapLocation selectedItem = (IHasMapLocation) obj;
					Point selectedHex = new Point(selectedItem.getX(), selectedItem.getY());
					JOApplication.publishEvent(LifecycleEventsEnum.SelectedHexChangedEvent, selectedHex, this);
				} catch (Exception exc) {
					// do nothing
				}
			}
		}
	}

	/**
	 * @wbp.parser.entryPoint
	 */
	@SuppressWarnings("serial")
	@Override
	protected JComponent createControl() {
		TableLayoutBuilder lb = new TableLayoutBuilder();

		lb.relatedGapRow();

		lb.separator(Messages.getString("EconomyCalculator.Nation")); //$NON-NLS-1$
		lb.row();
//		lb.relatedGapRow();

		JPanel nationPanel = new JPanel();
		nationPanel.add(this.nationCombo = new NationComboBox(this.getGameHolder()));
		this.nationCombo.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// when selected nation changed
				// refresh all tables and warnings
				Game g = EconomyCalculator.this.getGame();
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
//				EconomyCalculator.this.sellBonus.setSelected(((EconomyTotalsTableModel) EconomyCalculator.this.totalsTable.getModel()).getEconomyCalculatorData().getSellBonus());
				refreshSNA(n);
				}
		});

//		lb.relatedGapRow();
		this.sellBonus = new JCheckBox();
		this.cheaperShips = new JCheckBox();
		this.cheapestShips = new JCheckBox();
		this.cheapFortifications = new JCheckBox();
		this.freeArmyHire = new JCheckBox();
		this.marketInfluence = new JCheckBox();
		JPanel snaPanel = new JPanel();
		snaPanel.add(this.sellBonus, "align=left"); //$NON-NLS-1$
		this.sellBonus.setText(Messages.getString("EconomyCalculator.SellBonus")); //$NON-NLS-1$
		this.sellBonus.setHorizontalTextPosition(SwingConstants.LEFT);
		this.sellBonus.setEnabled(false);
		Game g = this.getGame();
		if (EconomyCalculator.this.nationCombo.getSelectedItem() != null) {
			Nation n = g.getMetadata().getNationByName(EconomyCalculator.this.nationCombo.getSelectedItem().toString());
			refreshSNA(n);
		}
		snaPanel.add(this.cheaperShips, "align=left"); //$NON-NLS-1$
		this.cheaperShips.setText(Messages.getString("EconomyCalculator.cheaperShips")); //$NON-NLS-1$
		this.cheaperShips.setHorizontalTextPosition(SwingConstants.LEFT);
		this.cheaperShips.setEnabled(false);
		snaPanel.add(this.cheapestShips, "align=left"); //$NON-NLS-1$
		this.cheapestShips.setText(Messages.getString("EconomyCalculator.cheapestShips")); //$NON-NLS-1$
		this.cheapestShips.setHorizontalTextPosition(SwingConstants.LEFT);
		this.cheapestShips.setEnabled(false);
		snaPanel.add(this.cheapFortifications, "align=left"); //$NON-NLS-1$
		this.cheapFortifications.setText(Messages.getString("EconomyCalculator.cheapFortifications")); //$NON-NLS-1$
		this.cheapFortifications.setHorizontalTextPosition(SwingConstants.LEFT);
		this.cheapFortifications.setEnabled(false);
		snaPanel.add(this.freeArmyHire, "align=left"); //$NON-NLS-1$
		this.freeArmyHire.setText(Messages.getString("EconomyCalculator.freeArmyHire")); //$NON-NLS-1$
		this.freeArmyHire.setHorizontalTextPosition(SwingConstants.LEFT);
		this.freeArmyHire.setEnabled(false);
		snaPanel.add(this.marketInfluence, "align=left"); //$NON-NLS-1$
		this.marketInfluence.setText(Messages.getString("EconomyCalculator.marketInfluence")); //$NON-NLS-1$
		this.marketInfluence.setHorizontalTextPosition(SwingConstants.LEFT);
		this.marketInfluence.setEnabled(false);

		nationPanel.add(snaPanel);
		nationPanel.getInsets().set(0, 0, 0, 0);
		lb.cell(nationPanel,"align=left");
//		lb.cell(this.nationCombo = new NationComboBox(GameHolder.instance()), "align=left"); //$NON-NLS-1$

/*		this.sellBonus.addActionListener(new ActionListener() {
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
*/
		lb.row();

		lb.relatedGapRow();

		lb.separator(Messages.getString("EconomyCalculator.Market")); //$NON-NLS-1$
		lb.row();
		lb.relatedGapRow();

		MarketTableModel mtm = new MarketTableModel(this.gameHolder);
		this.marketTable = new JOverseerTable(mtm);
		mtm.setTable(this.marketTable);

		this.marketTable.getTableHeader().setPreferredSize(new Dimension(400, 16));
		this.marketTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		for (int i = 0; i < mtm.getColumnCount(); i++) {
			this.marketTable.getColumnModel().getColumn(i).setPreferredWidth(mtm.getColumnWidth(i));
		}
		this.marketTable.setDefaultRenderer(Integer.class, new MarketRenderer());
		this.marketTable.setDefaultRenderer(String.class, new MarketRenderer());
		JScrollPane scp = new JScrollPane(this.marketTable);
		scp.setPreferredSize(new Dimension(600, 278));
		scp.getViewport().setOpaque(true);
		lb.cell(scp);

		JButton priceHistory = new JButton();
		priceHistory.setText(Messages.getString("EconomyCalculator.PriceHistory")); //$NON-NLS-1$
		priceHistory.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				final JidePopup popup = new JidePopup();
				popup.getContentPane().setLayout(new BorderLayout());
				TableLayoutBuilder lb1 = new TableLayoutBuilder();
				JButton closePopup = new JButton(Messages.getString("EconomyCalculator.Close")); //$NON-NLS-1$
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
				if (priceHistory1 == null || priceHistory1.equals("")) { //$NON-NLS-1$
					Turn t = EconomyCalculator.this.getTurn();
					if (t == null)
						return;
					String error = Messages.getString("EconomyCalculator.SelectProduct"); //$NON-NLS-1$
					if (t.getTurnNo() == 0) {
						error = Messages.getString("EconomyCalculator.NoHistoryForT0"); //$NON-NLS-1$
					}
					MessageDialog dlg = new MessageDialog(Messages.getString("EconomyCalculator.PriceHistoryTitle"), error); //$NON-NLS-1$
					dlg.showDialog();
					return;
				}
				lbl.setText(priceHistory1);
				lb1.cell(lbl);
				lb1.relatedGapRow();
				lb1.cell(closePopup, "align=center"); //$NON-NLS-1$
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
		lb.cell(priceHistory, "valign=top"); //$NON-NLS-1$

		lb.row();
		lb.relatedGapRow();

		lb.separator(Messages.getString("EconomyCalculator.Totals")); //$NON-NLS-1$
		lb.row();
		lb.relatedGapRow();

		EconomyTotalsTableModel ettm = new EconomyTotalsTableModel(this.gameHolder);

		mtm.setTotalsModel(ettm);
		this.totalsTable = new JOverseerTable(ettm) {

			public boolean doHighlight(int row,int column) {
				if ((row == EconomyTotalsTableModel.iTotalRevenueRow) && ((column ==EconomyTotalsTableModel.iValueCol0)||(column==EconomyTotalsTableModel.iValueCol1)||(column==EconomyTotalsTableModel.iValueCol2))
						|| ((row ==EconomyTotalsTableModel.iFinalGoldRow) && (column==EconomyTotalsTableModel.iValueCol3) )
						|| ((row ==EconomyTotalsTableModel.iStartingGoldRow) && (column==EconomyTotalsTableModel.iValueCol0) )
						) {
					return true;
				}
				return false;
			}
			@Override
			public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
				Component c = super.prepareRenderer(renderer, row, column);
				if (this.doHighlight(row, column)) {
					if (c instanceof JComponent) {
						if(PLaFHelper.isDarkMode()) ((JComponent) c).setBorder(new TopBottomBorder(Color.white, 1));
						else ((JComponent) c).setBorder(new TopBottomBorder(Color.black, 1));
	                }
				}
				return c;
			}

		};
		ettm.setTable(this.totalsTable);
		this.totalsTable.getTableHeader().setVisible(false);
		this.totalsTable.getTableHeader().setPreferredSize(new Dimension(400, 0));
		for (int i = 0; i < ettm.getColumnCount(); i++) {
			this.totalsTable.getColumnModel().getColumn(i).setPreferredWidth(ettm.getColumnWidth(i));
		}
		this.totalsTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		this.totalsTable.setDefaultRenderer(String.class, new TotalsRenderer());
		this.totalsTable.setDefaultRenderer(Integer.class, new TotalsRenderer());
		scp = new JScrollPane(this.totalsTable);
		scp.setPreferredSize(new Dimension(590, 124));
		//scp.getViewport().setOpaque(true);
		lb.cell(scp);

		TableLayoutBuilder tlb = new TableLayoutBuilder();
		tlb.cell(new JLabel(Messages.getString("EconomyCalculator.Autocalc")), "colspec=left:110px"); //$NON-NLS-1$ //$NON-NLS-2$
		tlb.gapCol();
		this.autocalcOrderCost = new JLabel("0"); //$NON-NLS-1$
		tlb.cell(this.autocalcOrderCost, "align=left"); //$NON-NLS-1$

		tlb.row();
		tlb.relatedGapRow();

		JButton btn = new JButton(Messages.getString("EconomyCalculator.UpdateCost")); //$NON-NLS-1$
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
		btn = new JButton(Messages.getString("EconomyCalculator.UpdateMarket")); //$NON-NLS-1$
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
		lb.gapCol();
		lb.cell(pnl, "colspec=left:150px valign=top"); //$NON-NLS-1$
		lb.row();

		this.marketLimitWarning = new JLabel("Market limit warning!"); //$NON-NLS-1$
		this.marketLimitWarning.setFont(GraphicUtils.getFont(this.marketLimitWarning.getFont().getName(), Font.BOLD, this.marketLimitWarning.getFont().getSize()));
		this.marketLimitWarning.setForeground(Color.red);
		lb.cell(this.marketLimitWarning);
		lb.row();
		lb.relatedGapRow();

		this.finalGoldWarning = new JLabel(Messages.getString("EconomyCalculator.FinalGoldWarning")); //$NON-NLS-1$
		this.finalGoldWarning.setFont(GraphicUtils.getFont(this.finalGoldWarning.getFont().getName(), Font.BOLD, this.marketLimitWarning.getFont().getSize()));
		this.finalGoldWarning.setForeground(Color.red);
		lb.cell(this.finalGoldWarning);
		lb.row();
		lb.relatedGapRow();

		this.taxIncrease = new JLabel(Messages.getString("EconomyCalculator.TaxesUp")); //$NON-NLS-1$
		this.taxIncrease.setFont(GraphicUtils.getFont(this.taxIncrease.getFont().getName(), Font.BOLD, this.taxIncrease.getFont().getSize()));
		this.taxIncrease.setForeground(Color.red);
		lb.cell(this.taxIncrease);
		lb.row();

		lb.relatedGapRow();
		lb.separator(Messages.getString("EconomyCalculator.PClost")); //$NON-NLS-1$
		lb.row();
		lb.relatedGapRow();

		this.lostPopsTableModel = new LostPopsTableModel();
		// pcTable = new JTable(lostPopsTableModel);
		this.pcTable = org.springframework.richclient.table.TableUtils.createStandardSortableTable(this.lostPopsTableModel);
		this.pcTable.addMouseListener(this);
		this.pcTable.setDefaultRenderer(Boolean.class, this.totalsTable.getDefaultRenderer(Boolean.class));
		this.pcTable.setDefaultEditor(Boolean.class, this.totalsTable.getDefaultEditor(Boolean.class));
		TableUtils.setTableColumnRenderer(this.pcTable, LostPopsTableModel.iHex, new HexNumberCellRenderer(this.lostPopsTableModel) );
		org.joverseer.ui.support.controls.TableUtils.setTableColumnWidths(this.pcTable, getLostPCColumWidths());
		this.pcTable.setMaximumSize(new Dimension(600, 1000));
		lb.cell(this.pcTable, "align=left"); //$NON-NLS-1$

		lb.row();

		JPanel p = lb.getPanel();
		scp = new JScrollPane(p);
		scp.getViewport().setOpaque(true);

		UIUtils.fixScrollPaneMouseScroll(scp);

		return scp;
	}

	public void refreshPcs(int nationNo) {
		ArrayList<PopulationCenter> items = new ArrayList<PopulationCenter>();
		Game g = this.getGame();
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
	public void refreshPcs() {
		if (EconomyCalculator.this.nationCombo.getSelectedItem() == null)
			return;
		Game g = this.getGame();
		if (Game.isInitialized(g) && g.getTurn() != null) {
			Nation n = g.getMetadata().getNationByName(EconomyCalculator.this.nationCombo.getSelectedItem().toString());
			this.refreshPcs(n.getNumber());
		}
		
	}

	/**
	 * Renderer for the Market Table
	 *
	 * @author Marios Skounakis
	 */
	public class MarketRenderer extends DefaultTableCellRenderer {
		/**
		 *
		 */
		private static final long serialVersionUID = 9079326762672678398L;
		// TODO export colors to color.properties
		ColorPicker colorPicker = ColorPicker.getInstance();
		Color green = this.colorPicker.getColor("EconomyCalculator.green");
		Color blue = this.colorPicker.getColor("EconomyCalculator.blue");
		Color grey = this.colorPicker.getColor("EconomyCalculator.grey");
		Color backgroundC = UIManager.getColor("Table.background");
		Color[] rowColors = new Color[] { this.green, this.green, this.blue, this.blue, this.green, this.backgroundC, this.backgroundC, this.green, this.green, this.backgroundC, this.backgroundC, this.backgroundC, this.grey }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$

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
			
			if(column > 0) c.setForeground(this.colorPicker.getColor("EconomyCalculator.text"));

			return c;
		}
	}

	/**
	 * Renderer for the totals table
	 *
	 * @author Marios Skounakis
	 */
	public class TotalsRenderer extends DefaultTableCellRenderer {
		/**
		 *
		 */
		private static final long serialVersionUID = 4072426945699643672L;

		// TODO export colors to resources
		// TODO remove hard-coded column and row numbers
		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			
			
			ColorPicker colorPicker = ColorPicker.getInstance();
			c.setForeground(colorPicker.getColor("EconomyCalculator.text"));
			
			JLabel lbl = ((JLabel) c);
			lbl.setHorizontalAlignment(SwingConstants.RIGHT);
			if (row == EconomyTotalsTableModel.iOrdersCostRow && column == EconomyTotalsTableModel.iValueCol3
					|| row == EconomyTotalsTableModel.iFinalGoldRow && column == EconomyTotalsTableModel.iValueCol3) {
				lbl.setFont(GraphicUtils.getFont(lbl.getFont().getName(), Font.BOLD, lbl.getFont().getSize()));
			} else {
				lbl.setFont(GraphicUtils.getFont(lbl.getFont().getName(), Font.PLAIN, lbl.getFont().getSize()));
			}
			if (row == EconomyTotalsTableModel.iMarketSalesRow && column == EconomyTotalsTableModel.iValueCol2 && !value.toString().equals("")) { //$NON-NLS-1$
				int amount = Integer.parseInt(value.toString());
				if (amount >= getMarketLimitWarningThreshhold()) {
					if (!isSelected) {
						lbl.setBackground(colorPicker.getColor("EconomyCalculator.red"));
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
					lbl.setBackground(colorPicker.getColor("EconomyCalculator.green")); //$NON-NLS-1$
				} else {
					//lbl.setBackground(Color.white);
					lbl.setBackground(UIManager.getColor("Table.background"));
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

	public class TopBottomBorder extends AbstractBorder
	{
		private static final long serialVersionUID = 1L;
		protected int thickness;
	    protected Color lineColor;
	    protected int gap;

	    public TopBottomBorder(Color color) {
	        this(color, 1, 1);
	    }

	    public TopBottomBorder(Color color, int thickness)  {
	        this(color, thickness, thickness);
	    }

	    public TopBottomBorder(Color color, int thickness, int gap)  {
	        this.lineColor = color;
	        this.thickness = thickness;
	        this.gap = gap;
	    }

	    @Override
		public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
	        Color oldColor = g.getColor();
	        int i;

	        g.setColor(this.lineColor);
	        for(i = 0; i < this.thickness; i++)  {
		          g.drawLine(x, y-i, x+width, y-i);
		          g.drawLine(x, y-i+height-1, x+width, y-i+height-1);
	        }
	        g.setColor(oldColor);
	    }

	    /**
	     * Returns the insets of the border.
	     * @param c the component for which this border insets value applies
	     */
	    @Override
		public Insets getBorderInsets(Component c)       {
	        return new Insets(0, 0, 0,this.gap);
	    }

	    @Override
		public Insets getBorderInsets(Component c, Insets insets) {
	        insets.left = 0;
	        insets.top = 0;
	        insets.right = this.gap;
	        insets.bottom = 0;
	        return insets;
	    }

	    /**
	     * Returns the color of the border.
	     */
	    public Color getLineColor()     {
	        return this.lineColor;
	    }

	    /**
	     * Returns the thickness of the border.
	     */
	    public int getThickness()       {
	        return this.thickness;
	    }

	    /**
	     * Returns whether or not the border is opaque.
	     */
	    @Override
		public boolean isBorderOpaque() {
	        return false;
	    }

	  public int getGap() {
	    return this.gap;
	  }

	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
}
