package org.joverseer.ui.listviews;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Comparator;

import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import org.jdesktop.swingx.autocomplete.ComboBoxCellEditor;
import org.joverseer.domain.Character;
import org.joverseer.domain.CharacterDeathReasonEnum;
import org.joverseer.domain.Order;
import org.joverseer.domain.PlayerInfo;
import org.joverseer.domain.PopulationCenter;
import org.joverseer.game.Game;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.metadata.GameMetadata;
import org.joverseer.metadata.orders.OrderMetadata;
import org.joverseer.support.Container;
import org.joverseer.support.GameHolder;
import org.joverseer.tools.OrderParameterValidator;
import org.joverseer.tools.OrderValidationResult;
import org.joverseer.tools.ordercheckerIntegration.OrderResult;
import org.joverseer.tools.ordercheckerIntegration.OrderResultContainer;
import org.joverseer.ui.LifecycleEventsEnum;
import org.joverseer.ui.orders.OrderVisualizationData;
import org.joverseer.ui.support.GraphicUtils;
import org.joverseer.ui.support.JOverseerEvent;
import org.joverseer.ui.support.controls.AutocompletionComboBox;
import org.joverseer.ui.support.controls.JOverseerTable;
import org.springframework.binding.value.support.ListListModel;
import org.springframework.context.ApplicationEvent;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.command.ActionCommand;
import org.springframework.richclient.command.CommandGroup;
import org.springframework.richclient.list.ComboBoxListModelAdapter;
import org.springframework.richclient.list.SortedListModel;
import org.springframework.richclient.table.BeanTableModel;
import org.springframework.richclient.table.ColumnToSort;
import org.springframework.richclient.table.SortOrder;
import org.springframework.richclient.table.SortableTableModel;
import org.springframework.richclient.table.TableUtils;
import org.springframework.richclient.table.renderer.BooleanTableCellRenderer;

/**
 * List view that shows orders and also allows the editing of these orders It is
 * also integrated with the OrderParameterValidator object to display errors in
 * the orders.
 * 
 * @author Marios Skounakis
 */
public class OrderEditorListView extends ItemListView {

	ActionCommand deleteOrderAction = new DeleteOrderAction();
	ActionCommand editOrderAction = new EditOrderAction();
	JComboBox combo;
	OrderParameterValidator validator = new OrderParameterValidator();
	Color paramErrorColor = Color.decode("#ffff99");
	Color paramWarningColor = Color.decode("#99ffff");
	Color paramInfoColor = Color.decode("#99FF99");

	public OrderEditorListView() {
		super(TurnElementsEnum.Character, OrderEditorTableModel.class);
	}

	@Override
	protected int[] columnWidths() {
		return new int[] { 32, 64, 32, 64, 80, 48, 48, 27, 27, 27, 27, 27, 27, 27, 27, 27, 27, 27, 27, 27, 30, 64, 64, 64 };
	}

	@Override
	protected void setItems() {
		Game g = ((GameHolder) Application.instance().getApplicationContext().getBean("gameHolder")).getGame();
		if (!Game.isInitialized(g))
			return;
		ArrayList<Order> orders = new ArrayList<Order>();
		for (Character c : g.getTurn().getCharacters()) {
			boolean acceptChar = true;
			for (JComboBox filter : filters) {
				if (filter.getSelectedItem() != null) {
					acceptChar = acceptChar && ((OrderFilter) filter.getSelectedItem()).acceptCharacter(c);
				}
			}
			if (!acceptChar)
				continue;
			for (int i = 0; i < c.getNumberOfOrders(); i++) {
				orders.add(c.getOrders()[i]);
			}
		}

		int row = table.getSelectedRow();
		Object o = null;
		try {
			o = tableModel.getRow(row); // get the object for this row
		} catch (Exception e) {
			// do nothing
		}
		int column = table.getSelectedColumn();
		tableModel.setRows(orders);
		try {
			if (o != null && o.equals(tableModel.getRow(row))) {
				// if row is still showing same order, keep selection
				table.setRowSelectionInterval(row, row);
				table.setColumnSelectionInterval(column, column);
			}
		} catch (Exception e) {
			// do nothing
		}
	}

	private ArrayList<OrderFilter> createOrderTypeFilterList() {
		ArrayList<OrderFilter> filterList = new ArrayList<OrderFilter>();

		OrderFilter f = new OrderFilter("All") {
			@Override
			public boolean acceptCharacter(Character c) {
				return true;
			}
		};
		filterList.add(f);

		f = new OrderFilter("Emissaries (E>=30)") {
			@Override
			public boolean acceptCharacter(Character c) {
				return c.getEmmisary() >= 30;
			}
		};
		filterList.add(f);

		f = new OrderFilter("Agent (A>=30)") {
			@Override
			public boolean acceptCharacter(Character c) {
				return c.getAgent() >= 30;
			}
		};
		filterList.add(f);

		f = new OrderFilter("Army/Navy Movement") {
			@Override
			public boolean acceptCharacter(Character c) {
				return characterHasOrderInList(c, "830,840,850,860");
			}
		};
		filterList.add(f);

		f = new OrderFilter("Char/Comp Movement") {
			@Override
			public boolean acceptCharacter(Character c) {
				return characterHasOrderInList(c, "810,820,870,825");
			}
		};
		filterList.add(f);

		f = new OrderFilter("Recon/Scout/Palantir") {
			@Override
			public boolean acceptCharacter(Character c) {
				return characterHasOrderInList(c, "925,905,910,915,920,925,930,935");
			}
		};
		filterList.add(f);

		f = new OrderFilter("Product Transfers") {
			@Override
			public boolean acceptCharacter(Character c) {
				return characterHasOrderInList(c, "947,948");
			}
		};
		filterList.add(f);

		f = new OrderFilter("Gold Transfers") {
			@Override
			public boolean acceptCharacter(Character c) {
				return characterHasGoldTransferOrder(c);
			}
		};
		filterList.add(f);

		f = new OrderFilter("Character Naming") {
			@Override
			public boolean acceptCharacter(Character c) {
				return characterHasOrderInList(c, "725,728,731,734,737");
			}
		};
		filterList.add(f);

		f = new OrderFilter("Lore Spells") {
			@Override
			public boolean acceptCharacter(Character c) {
				return characterHasOrderInList(c, "940");
			}
		};
		filterList.add(f);

		f = new OrderFilter("Combat") {
			@Override
			public boolean acceptCharacter(Character c) {
				return characterHasOrderInList(c, "230,235,240,250,255,498");
			}
		};
		filterList.add(f);

		return filterList;
	}

	protected boolean characterHasOrderInList(Character c, String orderList) {
		String[] parts = orderList.split(",");
		for (String p : parts) {
			int orderNo = Integer.parseInt(p);
			for (Order o : c.getOrders()) {
				if (o.getOrderNo() == orderNo)
					return true;
			}
		}
		return false;
	}

	protected boolean characterHasGoldTransferOrder(Character c) {
		for (Order o : c.getOrders()) {
			if (o.getOrderNo() == 948) {
				if ("go".equals(o.getP2())) {
					return true;
				}
			}
		}
		return false;
	}

	private ArrayList<OrderFilter> createOrderNationFilterList() {
		ArrayList<OrderFilter> filterList = new ArrayList<OrderFilter>();
		OrderFilter f = new OrderFilter("All characters with orders") {

			@Override
			public boolean acceptCharacter(Character c) {
				return c.getDeathReason().equals(CharacterDeathReasonEnum.NotDead) && c.getX() > 0 && (!c.getOrders()[0].isBlank() || !c.getOrders()[1].isBlank());
			}
		};
		filterList.add(f);

		f = new OrderFilter("All Imported") {

			@Override
			public boolean acceptCharacter(Character c) {
				if (!GameHolder.hasInitializedGame())
					return false;
				PlayerInfo pi = (PlayerInfo) GameHolder.instance().getGame().getTurn().getContainer(TurnElementsEnum.PlayerInfo).findFirstByProperty("nationNo", c.getNationNo());
				return pi != null && c.getDeathReason().equals(CharacterDeathReasonEnum.NotDead) && c.getX() > 0;
			}
		};
		filterList.add(f);

		if (GameHolder.hasInitializedGame()) {
			Game g = GameHolder.instance().getGame();
			GameMetadata gm = g.getMetadata();
			for (int i = 1; i < 26; i++) {
				PlayerInfo pi = (PlayerInfo) g.getTurn().getContainer(TurnElementsEnum.PlayerInfo).findFirstByProperty("nationNo", i);
				if (pi == null)
					continue;
				f = new OrderFilter(gm.getNationByNum(i).getName()) {

					@Override
					public boolean acceptCharacter(Character c) {
						Game g = GameHolder.instance().getGame();
						GameMetadata gm = g.getMetadata();
						return c.getDeathReason().equals(CharacterDeathReasonEnum.NotDead) && c.getX() > 0 && gm.getNationByNum(c.getNationNo()).getName().equals(getDescription());
					}
				};
				filterList.add(f);
			}
		}
		return filterList;
	}

	@Override
	protected AbstractListViewFilter[][] getFilters() {
		return new AbstractListViewFilter[][] { createOrderNationFilterList().toArray(new AbstractListViewFilter[] {}), createOrderTypeFilterList().toArray(new AbstractListViewFilter[] {}), };
	}

	@Override
	protected JTable createTable() {
		JTable table = TableUtils.createStandardSortableTable(tableModel);
		JTable newTable = new JOverseerTable(table.getModel()) {

			Color selectionBackground = (Color) UIManager.get("Table.selectionBackground");
			Color normalBackground = (Color) UIManager.get("Table.background");

			@Override
			public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
				Component c = super.prepareRenderer(renderer, row, column);
				if (isCellSelected(row, column)) {
					if (!c.getBackground().equals(paramErrorColor) && !c.getBackground().equals(paramWarningColor) && !c.getBackground().equals(paramInfoColor)) {
						c.setBackground(selectionBackground);
					}
				} else if ((row / 2) % 2 == 1) {
					if (!c.getBackground().equals(paramErrorColor) && !c.getBackground().equals(paramWarningColor) && !c.getBackground().equals(paramInfoColor)) {
						c.setBackground(Color.decode("#eeeeee"));
					}
				} else {
					if (!c.getBackground().equals(paramErrorColor) && !c.getBackground().equals(paramWarningColor) && !c.getBackground().equals(paramInfoColor)) {
						c.setBackground(normalBackground);
					}
				}
				return c;
			}

		};
		newTable.setColumnModel(table.getColumnModel());
		newTable.setAutoResizeMode(table.getAutoResizeMode());
		table = null;
		return newTable;
	}

	@Override
	protected JComponent createControlImpl() {
		JComponent tableComp = super.createControlImpl();

		// TableLayoutBuilder tlb = new TableLayoutBuilder();
		// tlb.cell(combo = new JComboBox(), "align=left");
		// combo.setPreferredSize(new Dimension(200, 24));
		// combo.addActionListener(new ActionListener() {
		//
		// public void actionPerformed(ActionEvent e) {
		// setItems();
		// }
		// });

		GraphicUtils.setTableColumnRenderer(table, OrderEditorTableModel.iDraw, new BooleanTableCellRenderer() {

			Color selectionBackground = (Color) UIManager.get("Table.selectionBackground");
			Color normalBackground = (Color) UIManager.get("Table.background");

			// specialized renderer for rendering the "Draw" column
			// it is rendered with a check box
			// and shown only when appropriate
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
				try {
					Order o = (Order) tableModel.getRow(((SortableTableModel) table.getModel()).convertSortedIndexToDataIndex(row));
					JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, "", isSelected, hasFocus, row, column);
					if (GraphicUtils.canRenderOrder(o)) {
						JCheckBox b = new JCheckBox();
						b.setSelected((Boolean) value);
						b.setHorizontalAlignment(JCheckBox.CENTER);
						System.out.println("row == table.getSelectedRow() = " + String.valueOf(row == table.getSelectedRow()));
						System.out.println("isSelected = " + String.valueOf(isSelected));
						b.setBackground(row == table.getSelectedRow() && isSelected ? selectionBackground : normalBackground);
						return b;
					} else {
						return lbl;
					}
				} catch (Exception exc) {
					// do nothing
					JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, "", isSelected, hasFocus, row, column);
					return lbl;
				}
			}

		});

		// specialized renderer for the icon returned by the orderResultType
		// virtual field
		GraphicUtils.setTableColumnRenderer(table, OrderEditorTableModel.iResults, new DefaultTableCellRenderer() {

			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
				try {
					ImageIcon ico = (ImageIcon) value;
					JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, "", isSelected, hasFocus, row, column);
					lbl.setIcon(ico);
					if (ico != null) {
						OrderResultContainer container = (OrderResultContainer) Application.instance().getApplicationContext().getBean("orderResultContainer");
						int idx = ((SortableTableModel) table.getModel()).convertSortedIndexToDataIndex(row);
						Object obj = tableModel.getRow(idx);
						Order o = (Order) obj;
						String txt = "";
						for (OrderResult result : container.getResultsForOrder(o)) {
							txt += (txt.equals("") ? "" : "") + "<li>" + result.getType().toString() + ": " + result.getMessage() + "</li>";
						}
						txt = "<html><body><lu>" + txt + "</lu></body></html>";
						lbl.setToolTipText(txt);
					}
					lbl.setHorizontalAlignment(JLabel.CENTER);
					return lbl;
				} catch (Exception exc) {
					// do nothing
					JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, "", isSelected, hasFocus, row, column);
					return lbl;
				}
			}

		});

		// renderer for hex - boldify capital hex
		GraphicUtils.setTableColumnRenderer(table, OrderEditorTableModel.iHexNo, new DefaultTableCellRenderer() {

			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
				try {
					JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
					// find capital and compare
					int idx = ((SortableTableModel) table.getModel()).convertSortedIndexToDataIndex(row);
					Object obj = tableModel.getRow(idx);
					Order o = (Order) obj;
					Character c = o.getCharacter();
					PopulationCenter capital = (PopulationCenter) GameHolder.instance().getGame().getTurn().getContainer(TurnElementsEnum.PopulationCenter).findFirstByProperties(new String[] { "nationNo", "capital" }, new Object[] { c.getNationNo(), Boolean.TRUE });
					if (capital != null && c.getHexNo() == capital.getHexNo()) {
						lbl.setFont(GraphicUtils.getFont(lbl.getFont().getName(), Font.BOLD, lbl.getFont().getSize()));
					}
					lbl.setHorizontalAlignment(JLabel.CENTER);
					return lbl;
				} catch (Exception exc) {
					// do nothing
					JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, "", isSelected, hasFocus, row, column);
					return lbl;
				}
			};
		});

		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer() {

			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
				JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				lbl.setHorizontalAlignment(JLabel.CENTER);
				return lbl;
			};
		};

		// render stats - center alignment
		GraphicUtils.setTableColumnRenderer(table, OrderEditorTableModel.iStats, centerRenderer);

		for (int i = OrderEditorTableModel.iParamStart; i <= OrderEditorTableModel.iParamEnd; i++) {
			GraphicUtils.setTableColumnRenderer(table, i, new OrderParameterCellRenderer(i - OrderEditorTableModel.iParamStart));
		}
		GraphicUtils.setTableColumnRenderer(table, OrderEditorTableModel.iNoAndCode, new OrderNumberCellRenderer());

		// tlb.row();
		// tlb.cell(tableComp);
		// tlb.row();
		// return tlb.getPanel();
		return tableComp;
	}

	@Override
	public void onApplicationEvent(ApplicationEvent applicationEvent) {
		if (applicationEvent instanceof JOverseerEvent) {
			JOverseerEvent e = (JOverseerEvent) applicationEvent;
			if (e.getEventType().equals(LifecycleEventsEnum.SelectedTurnChangedEvent.toString())) {
				refreshFilters();
				setItems();
			} else if (e.getEventType().equals(LifecycleEventsEnum.SelectedHexChangedEvent.toString())) {
				setItems();
			} else if (e.getEventType().equals(LifecycleEventsEnum.GameChangedEvent.toString())) {
				// setFilters();
				refreshFilters();
				TableColumn noAndCodeColumn = table.getColumnModel().getColumn(OrderEditorTableModel.iNoAndCode);
				Game g = ((GameHolder) Application.instance().getApplicationContext().getBean("gameHolder")).getGame();
				ListListModel orders = new ListListModel();
				orders.add(Order.NA);
				if (Game.isInitialized(g)) {
					GameMetadata gm = g.getMetadata();
					Container<OrderMetadata> orderMetadata = gm.getOrders();
					for (OrderMetadata om : orderMetadata) {
						orders.add(om.getNumber() + " " + om.getCode());
					}
				}
				SortedListModel slm = new SortedListModel(orders);

				// ComboBox Editor for the order number
				final JComboBox comboBox = new AutocompletionComboBox(new ComboBoxListModelAdapter(slm));
				comboBox.setEditable(true);
				comboBox.putClientProperty("JComboBox.isTableCellEditor", Boolean.TRUE);
				final ComboBoxCellEditor editor = new ComboBoxCellEditor(comboBox);
				noAndCodeColumn.setCellEditor(editor);

				editor.addCellEditorListener(new CellEditorListener() {

					public void editingCanceled(ChangeEvent e) {
						table.requestFocus();
					}

					public void editingStopped(ChangeEvent e) {
						table.requestFocus();
					}

				});
				comboBox.getEditor().getEditorComponent().addKeyListener(new KeyAdapter() {

					@Override
					public void keyPressed(KeyEvent arg0) {
						if (arg0.getKeyCode() == KeyEvent.VK_ESCAPE) {
							editor.cancelCellEditing();
							arg0.consume();
						}
					}

				});

				setItems();
			} else if (e.getEventType().equals(LifecycleEventsEnum.OrderChangedEvent.toString())) {
				// setItems();
			} else if (e.getEventType().equals(LifecycleEventsEnum.RefreshMapItems.toString())) {
				setItems();
			} else if (e.getEventType().equals(LifecycleEventsEnum.RefreshOrders.toString())) {
				setItems();
			}
		}
	}

	@Override
	public JPopupMenu getPopupMenu(boolean hasSelectedItem) {
		if (!hasSelectedItem)
			return null;
		CommandGroup group = Application.instance().getActiveWindow().getCommandManager().createCommandGroup("orderCommandGroup", new Object[] { editOrderAction, deleteOrderAction,
		// sendOrderByChatAction,
				"separator", new DrawAllOrdersAction(), new UnDrawAllOrdersAction(),
		// new SendAllOrdersByChatAction()
				});
		return group.createPopupMenu();
	}

	/**
	 * Edit the selected order
	 * 
	 * @author Marios Skounakis
	 */
	private class EditOrderAction extends ActionCommand {

		@Override
		protected void doExecuteCommand() {
			int row = table.getSelectedRow();
			if (row >= 0) {
				int idx = ((SortableTableModel) table.getModel()).convertSortedIndexToDataIndex(row);
				if (idx >= tableModel.getRowCount())
					return;
				try {
					Object obj = tableModel.getRow(idx);
					Order order = (Order) obj;
					Application.instance().getApplicationContext().publishEvent(new JOverseerEvent(LifecycleEventsEnum.EditOrderEvent.toString(), order, this));
				} catch (Exception exc) {

				}
			}
		}
	}

	/**
	 * Delete the selected order
	 * 
	 * @author Marios Skounakis
	 */
	private class DeleteOrderAction extends ActionCommand {

		@Override
		protected void doExecuteCommand() {
			int row = table.getSelectedRow();
			if (row >= 0) {
				int idx = ((SortableTableModel) table.getModel()).convertSortedIndexToDataIndex(row);
				if (idx >= tableModel.getRowCount())
					return;
				try {
					Object obj = tableModel.getRow(idx);
					Order order = (Order) obj;
					order.clear();
					((BeanTableModel) table.getModel()).fireTableDataChanged();
					Application.instance().getApplicationContext().publishEvent(new JOverseerEvent(LifecycleEventsEnum.OrderChangedEvent.toString(), order, this));
				} catch (Exception exc) {
					System.out.println(exc);
				}
			}
		}

	}

	/**
	 * Set Draw = true for all orders in the list view
	 * 
	 * @author Marios Skounakis
	 */
	private class DrawAllOrdersAction extends ActionCommand {

		@Override
		protected void doExecuteCommand() {
			OrderVisualizationData ovd = (OrderVisualizationData) Application.instance().getApplicationContext().getBean("orderVisualizationData");
			for (Object o : tableModel.getRows()) {
				Order order = (Order) o;
				if (GraphicUtils.canRenderOrder(order)) {
					ovd.addOrder(order);
				}
			}
			Application.instance().getApplicationContext().publishEvent(new JOverseerEvent(LifecycleEventsEnum.RefreshMapItems.toString(), this, this));
		}
	}

	/**
	 * Set Draw = false for all orders in the list view
	 * 
	 * @author Marios Skounakis
	 */
	private class UnDrawAllOrdersAction extends ActionCommand {

		@Override
		protected void doExecuteCommand() {
			OrderVisualizationData ovd = (OrderVisualizationData) Application.instance().getApplicationContext().getBean("orderVisualizationData");
			ovd.clear();
			Application.instance().getApplicationContext().publishEvent(new JOverseerEvent(LifecycleEventsEnum.RefreshMapItems.toString(), this, this));
		}
	}

	/**
	 * Generic Order filter
	 * 
	 * @author Marios Skounakis
	 */
	private abstract class OrderFilter extends AbstractListViewFilter {

		public abstract boolean acceptCharacter(Character c);

		// dummy, basically ignored
		@Override
		public boolean accept(Object o) {
			return true;
		}

		public OrderFilter(String description) {
			super(description);
		}
	}

	@Override
	public ColumnToSort[] getDefaultSort() {
		// set comparator to sort using the character ids
		((SortableTableModel) table.getModel()).setComparator(1, new Comparator<String>() {
			public int compare(String o1, String o2) {
				if (o1 != null && o2 != null) {
					return Character.getIdFromName(o1).compareTo(Character.getIdFromName(o2));
				}
				return 0;
			}
		});
		return new ColumnToSort[] { new ColumnToSort(0, 0, SortOrder.ASCENDING), new ColumnToSort(0, 1, SortOrder.ASCENDING) };
	}

	/**
	 * Cell renderer for the order parameters - Changes the background to
	 * erroneous parameters - Shows a tooltip with the error message
	 * 
	 * @author Marios Skounakis
	 */
	class OrderParameterCellRenderer extends DefaultTableCellRenderer {

		int paramNo;
		Color selectionBackground = (Color) UIManager.get("Table.selectionBackground");
		Color normalBackground = (Color) UIManager.get("Table.background");

		private OrderParameterCellRenderer(int paramNo) {
			super();
			this.paramNo = paramNo;
		}

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			int idx = ((SortableTableModel) table.getModel()).convertSortedIndexToDataIndex(row);
			Object obj = tableModel.getRow(idx);
			Order o = (Order) obj;
			OrderValidationResult res = null;
			// TODO handle larger paramNos
			if (paramNo < 9) {
				res = validator.checkParam(o, paramNo);
			}

			if (res != null) {
				if (isSelected) {
					lbl.setBackground(selectionBackground);
				} else if (res.getLevel() == OrderValidationResult.ERROR) {
					lbl.setBackground(paramErrorColor);
				} else if (res.getLevel() == OrderValidationResult.WARNING) {
					lbl.setBackground(paramWarningColor);
				} else if (res.getLevel() == OrderValidationResult.INFO) {
					lbl.setBackground(paramInfoColor);
				}
				lbl.setToolTipText(res.getMessage());
			} else {
				if (isSelected) {
					lbl.setBackground(selectionBackground);
				} else {
					lbl.setBackground(normalBackground);
				}
				lbl.setToolTipText(null);
			}
			return lbl;
		}
	}

	/**
	 * Renderer for the order number - Changes the background to erroneous
	 * orders - Shows a tooltip with the error message
	 * 
	 * @author Marios Skounakis
	 */
	class OrderNumberCellRenderer extends DefaultTableCellRenderer {

		Color selectionBackground = (Color) UIManager.get("Table.selectionBackground");
		Color normalBackground = (Color) UIManager.get("Table.background");

		private OrderNumberCellRenderer() {
			super();
		}

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			int idx = ((SortableTableModel) table.getModel()).convertSortedIndexToDataIndex(row);
			Object obj = tableModel.getRow(idx);
			Order o = (Order) obj;
			OrderValidationResult res = validator.checkOrder(o);
			if (res != null) {
				if (isSelected) {
					lbl.setBackground(selectionBackground);
				} else if (res.getLevel() == OrderValidationResult.ERROR) {
					lbl.setBackground(paramErrorColor);
				} else if (res.getLevel() == OrderValidationResult.WARNING) {
					lbl.setBackground(paramWarningColor);
				} else if (res.getLevel() == OrderValidationResult.INFO) {
					lbl.setBackground(paramInfoColor);
				}
				lbl.setToolTipText(res.getMessage());
			} else {
				if (isSelected) {
					lbl.setBackground(selectionBackground);
				} else {
					lbl.setBackground(normalBackground);
				}
				lbl.setToolTipText(null);
			}
			return lbl;
		}
	}
}
