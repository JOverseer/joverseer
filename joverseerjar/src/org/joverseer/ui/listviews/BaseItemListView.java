package org.joverseer.ui.listviews;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.joverseer.JOApplication;
import org.joverseer.domain.CharacterDeathReasonEnum;
import org.joverseer.domain.IHasMapLocation;
import org.joverseer.game.Game;
import org.joverseer.metadata.domain.Nation;
import org.joverseer.preferences.PreferenceRegistry;
import org.joverseer.support.Container;
import org.joverseer.support.GameHolder;
import org.joverseer.support.infoSources.InfoSource;
import org.joverseer.ui.BaseView;
import org.joverseer.ui.LifecycleEventsEnum;
import org.joverseer.ui.listviews.filters.AndFilter;
import org.joverseer.ui.listviews.renderers.AllegianceColorCellRenderer;
import org.joverseer.ui.listviews.renderers.DeathReasonEnumRenderer;
import org.joverseer.ui.listviews.renderers.InfoSourceTableCellRenderer;
import org.joverseer.ui.support.JOverseerEvent;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.MessageSource;
import org.springframework.richclient.application.PageComponentContext;
import org.springframework.richclient.command.support.AbstractActionCommandExecutor;
import org.springframework.richclient.image.ImageSource;
import org.springframework.richclient.layout.TableLayoutBuilder;
import org.springframework.richclient.table.BeanTableModel;
import org.springframework.richclient.table.ColumnToSort;
import org.springframework.richclient.table.SortableTableModel;
import org.springframework.richclient.table.TableUtils;

/**
 * Base class for ListViews It basically provides support for: - having a table
 * with the given ItemTableModel descendant - having filters - having a default
 * sort order - having a popup menu for the table rows - having a set of buttons
 * on the right of the table
 *
 * @author Marios Skounakis
 */
public abstract class BaseItemListView extends BaseView implements ApplicationListener, MouseListener, MouseMotionListener,InitializingBean {

	PreferenceRegistry preferenceRegistry;

	public PreferenceRegistry getPreferenceRegistry() {
		return this.preferenceRegistry;
	}

	public void setPreferenceRegistry(PreferenceRegistry preferenceRegistry) {
		this.preferenceRegistry = preferenceRegistry;
	}

	/**
	 * There must be a better way to get these dependencies set.
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		this.gameHolder = GameHolder.instance(this.getApplicationContext());
		this.preferenceRegistry = PreferenceRegistry.instance(this.getApplicationContext());
	}

	protected int xDiff;
	protected int yDiff;

	protected BeanTableModel tableModel;

	protected JTable table;
	protected ArrayList<JComboBox> filters = new ArrayList<JComboBox>();
	protected JTextField textFilterField;
	protected Class<?> tableModelClass;
	protected SelectHexCommandExecutor selectHexCommandExecutor = new SelectHexCommandExecutor();
	protected JPanel buttonPanel;
	protected ArrayList<Object> filterOptionsCache = new ArrayList<Object>();

	protected boolean handleFilterEvents = true;

	public BaseItemListView(Class<?> tableModelClass) {
		this.tableModelClass = tableModelClass;
	}

	/**
	 * Set the contents of the table model here
	 */
	protected abstract void setItems();

	/**
	 * Return the desired column widths
	 */
	protected abstract int[] columnWidths();

	/**
	 * Return the desired default sort specification
	 */
	protected ColumnToSort[] getDefaultSort() {
		return null;
	}

	/**
	 * Return true if list view has a text filter where the user can type in
	 */
	protected boolean hasTextFilter() {
		return false;
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
			int row = BaseItemListView.this.table.getSelectedRow();
			if (row >= 0) {
				int idx = 0;
				if (SortableTableModel.class.isInstance(BaseItemListView.this.table.getModel())) {
					idx = ((SortableTableModel) BaseItemListView.this.table.getModel()).convertSortedIndexToDataIndex(row);
				} else if (com.jidesoft.grid.SortableTableModel.class.isInstance(BaseItemListView.this.table.getModel())) {
					idx = ((com.jidesoft.grid.SortableTableModel) BaseItemListView.this.table.getModel()).getActualRowAt(row);
				}
				if (idx >= BaseItemListView.this.tableModel.getRowCount())
					return;
				try {
					Object obj = BaseItemListView.this.tableModel.getRow(idx);
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

	@Override
	protected JComponent createControl() {
		return createControlImpl();
	}

	/**
	 * Return the desired filters Two dimensional array so you can return
	 * multiple groups of filters Each group of filters goes into a separate
	 * combo box
	 */
	protected AbstractListViewFilter[][] getFilters() {
		return null;
	}

	protected AbstractListViewFilter getTextFilter(String txt) {
		return new PositiveListViewFilter();
	}

	protected AbstractListViewFilter getActiveFilter() {
		AbstractListViewFilter textFilter = getTextFilter(this.textFilterField == null ? null : this.textFilterField.getText());
		if (this.filters == null)
			return textFilter;
		AndFilter f = new AndFilter();
		f.addFilter(textFilter);
		for (JComboBox filter : this.filters) {
			f.addFilter((AbstractListViewFilter) filter.getSelectedItem());
		}
		return f;
	}

	protected JTable createTable() {
		return TableUtils.createStandardSortableTable(this.tableModel);
	}

	/**
	 * Buttons on the right of the table
	 */
	protected JComponent[] getButtons() {
		if (getDefaultSort() != null) {
			ImageSource imgSource = JOApplication.getImageSource();
			Icon ico = new ImageIcon(imgSource.getImage("restoreSorting.icon"));
			JLabel restoreSorting = new JLabel();
			restoreSorting.setIcon(ico);
			restoreSorting.setPreferredSize(new Dimension(16, 16));
			restoreSorting.addMouseListener(new MouseAdapter() {

				@Override
				public void mouseClicked(MouseEvent arg0) {
					((SortableTableModel) BaseItemListView.this.table.getModel()).sortByColumns(getDefaultSort());
				}

			});
			((SortableTableModel) this.table.getModel()).sortByColumns(getDefaultSort());
			restoreSorting.setToolTipText("Restore default sort order");
			return new JComponent[] { restoreSorting };
		}
		return new JComponent[] {};
	}

	protected JPanel createFilterPanel() {

		// create the filter combo
		AbstractListViewFilter[][] filterLists = getFilters();

		boolean hasFilters = false;
		TableLayoutBuilder lb = new TableLayoutBuilder();
		if (filterLists != null) {
			for (AbstractListViewFilter[] filterList : filterLists) {
				JComboBox filter = new JComboBox(filterList);
				this.filters.add(filter);
				filter.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						if (BaseItemListView.this.handleFilterEvents)
							setItems();
					}
				});
				filter.setPreferredSize(new Dimension(150, 20));
				filter.setOpaque(true);
				lb.cell(filter, "colspec=left:150px");
				lb.gapCol();
			}
			hasFilters = true;
		}
		if (hasTextFilter()) {
			hasFilters = true;
			this.textFilterField = new JTextField();
			this.textFilterField.setPreferredSize(new Dimension(150, 20));
			this.textFilterField.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (BaseItemListView.this.handleFilterEvents)
						setItems();
				}
			});
			lb.cell(this.textFilterField, "colspec=left:150px");
			lb.gapCol();
		}

		if (hasFilters) {
			JPanel p = lb.getPanel();
			p.setOpaque(true);

			return p;
		}
		return null;
	}
	/**
	 * hook into the table model construction if needed.
	 * If you invoke this then you should normally assign the result to this.tableModel
	 * @return
	 */
	protected BeanTableModel createBeanTableModel() {
		BeanTableModel model= null;
		// create the table model
		try {
			model = (BeanTableModel) this.tableModelClass.getConstructor(new Class[] { MessageSource.class, GameHolder.class, PreferenceRegistry.class}).newInstance(new Object[] { this.getMessageSource(),this.gameHolder,this.preferenceRegistry });
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
		return model;
	}
	/**
	 * create the view...
	 *
	 * @return
	 */
	protected JComponent createControlImpl() {

		this.tableModel = this.createBeanTableModel();

		TableLayoutBuilder tlb = new TableLayoutBuilder();
		JPanel fp = createFilterPanel();
		if (fp != null) {
			tlb.cell(fp, "align=left");
			tlb.row();
		}

		setItems();

		// create the JTable instance
		this.table = createTable();
		org.joverseer.ui.support.controls.TableUtils.setTableColumnWidths(this.table, columnWidths());
		Color background = UIManager.getColor("Panel.background");

		String pval = PreferenceRegistry.instance().getPreferenceValue("listviews.autoresizeCols");
		if (pval.equals("yes")) {
			this.table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		}

		this.table.getTableHeader().setBackground(background);
		this.table.setDefaultRenderer(String.class, new AllegianceColorCellRenderer(this.tableModel));
		this.table.setDefaultRenderer(Integer.class, new AllegianceColorCellRenderer(this.tableModel));
		this.table.setDefaultRenderer(Boolean.class, new AllegianceColorCellRenderer(this.tableModel));
		this.table.setDefaultRenderer(InfoSource.class, new InfoSourceTableCellRenderer(this.tableModel));
		this.table.setDefaultRenderer(CharacterDeathReasonEnum.class, new DeathReasonEnumRenderer(this.tableModel));
		this.table.addMouseListener(this);
		this.table.addMouseMotionListener(this);
		// table.setDragEnabled(true);
		this.table.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		JScrollPane scrollPane = new JScrollPane(this.table);
		scrollPane.getViewport().setOpaque(true);
		scrollPane.getViewport().setBackground(this.table.getBackground());
		scrollPane.setPreferredSize(new Dimension(1200, 1200));
		scrollPane.addMouseListener(this);
		tlb.cell(scrollPane);

		TableLayoutBuilder lb = new TableLayoutBuilder();
		for (JComponent compo : getButtons()) {
			lb.cell(compo, "colspec=left:30px valign=top");
			lb.relatedGapRow();
			lb.row();
		}
		JPanel pnl = lb.getPanel();
		pnl.setBackground(background);
		tlb.cell(pnl, "colspec=left:30px valign=top");
		JPanel p = tlb.getPanel();
		p.setBackground(background);
		return p;
	}

	protected void refreshFilters() {
		if (this.filters.size() > 0) {
			AbstractListViewFilter[][] filterLists = getFilters();
			for (int i = 0; i < filterLists.length; i++) {
				this.filters.get(i).removeAllItems();
				for (AbstractListViewFilter f : filterLists[i]) {
					this.filters.get(i).addItem(f);
				}
				try {
					this.filters.get(i).updateUI();
				} catch (Exception exc) {
					this.logger.error(exc);
				}
			}
		}
	}

	@Override
	public void onApplicationEvent(ApplicationEvent applicationEvent) {
		if (applicationEvent instanceof JOverseerEvent) {
			onJOEvent((JOverseerEvent) applicationEvent);
		}
	}

	protected void onJOEvent(JOverseerEvent e) {
		switch (e.getType()) {
		case SelectedTurnChangedEvent:
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					cacheFilterOptions();
					refreshFilters();
					tryRestoreFilterOptions();
					setItems();
				}
			});
			break;
		case GameChangedEvent:
			super.resetGame();
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					refreshFilters();
					setItems();
				}
			});
			break;
		case ListviewTableAutoresizeModeToggle:
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					String pval = PreferenceRegistry.instance().getPreferenceValue("listviews.autoresizeCols");
					if (pval.equals("yes")) {
						BaseItemListView.this.table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
					} else {
						BaseItemListView.this.table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
					}
				}
			});
			break;
		case ListviewRefreshItems:
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					setItems();
				}
			});
			break;
		case GameLoadedEvent:
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					selectCurrentNationAsFilter();
				}
			});
			break;
		//case SelectedHexChangedEvent: break;
		}
	}

	public void showContextMenu(MouseEvent e) {
		JPopupMenu pm = getPopupMenu(this.table.getSelectedRowCount() != 0);
		if (pm == null)
			return;
		pm.show(e.getComponent(), e.getX(), e.getY());
	};

	public JPopupMenu getPopupMenu(boolean hasSelectedItem) {
		return null;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getClickCount() == 2 && e.getButton() == 1) {
			this.selectHexCommandExecutor.execute();
		}
		if (e.getClickCount() == 1 && e.getButton() == 3) {
			showContextMenu(e);
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {

		if (e.getButton() == MouseEvent.BUTTON1) {
			this.xDiff = e.getX();
			this.yDiff = e.getY();
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		int dx = Math.abs(e.getX() - this.xDiff);
		int dy = Math.abs(e.getY() - this.yDiff);
		if (dx > 5 || dy > 5) {
			startDragAndDropAction(e);
		}
	}

	/**
	 * Implement what you want to happen when a drag & drop action is started
	 */
	protected void startDragAndDropAction(MouseEvent e) {

	}

	@Override
	public void mouseMoved(MouseEvent e) {
	}

	protected  void applyFilter(ArrayList<Object> filteredItems,Container<?> items) {
		AbstractListViewFilter filter = getActiveFilter();
		for (Object o : items.getItems()) {
			if (filter == null || filter.accept(o))
				filteredItems.add(o);
		}
	}

	protected void cacheFilterOptions() {
		this.filterOptionsCache.clear();
		if (hasTextFilter()) {
			this.filterOptionsCache.add(this.textFilterField.getText());
		}
		if (this.filters == null)
			return;
		for (JComboBox filter : this.filters) {
			this.filterOptionsCache.add(filter.getSelectedItem());
		}
	}

	protected void tryRestoreFilterOptions() {
		try {

			if (this.filterOptionsCache == null)
				return;
			if (this.filterOptionsCache.size() == 0)
				return;
			this.handleFilterEvents = false;
			if (hasTextFilter() && this.textFilterField != null && this.filterOptionsCache.get(0) != null) {
				this.textFilterField.setText(this.filterOptionsCache.get(0).toString());
			}
			if (this.filters != null) {
				for (int i = 1; i < this.filterOptionsCache.size(); i++) {
					JComboBox filter = this.filters.get(i - 1);
					filter.setSelectedItem(this.filterOptionsCache.get(i));
				}
				this.handleFilterEvents = true;
			}
		} catch (Exception exc) {
			// do nothing
			throw new RuntimeException(exc);
		} finally {
			this.handleFilterEvents = true;
		}
	}

	public Object getSelectedObject() {
		int idx = this.getSelectedSortedRow();
		if (idx < 0) {
			return null;
		}
		try {
			Object obj = this.tableModel.getRow(idx);
			return obj;
		} catch (Exception e) {
		}
		return null;
	}
	/**
	 *
	 * @return -1 if not selected
	 */
	public int getSelectedSortedRow() {
		int row = this.table.getSelectedRow();
		if (row < 0 && this.table.getRowCount() == 1) {
			row = 0;
		}
		if (row < 0) {
			return row;
		}
		int idx = ((SortableTableModel) this.table.getModel()).convertSortedIndexToDataIndex(row);
		if (idx >= this.tableModel.getRowCount()) {
			return -1;
		}
		return idx;
	}
	public void selectCurrentNationAsFilter() {
		Game g = this.gameHolder.getGame();
		if (Game.isInitialized(g)) {
			if (this.filters.size() > 0) {
				JComboBox com = this.filters.get(0);
				Nation n = g.getMetadata().getNationByNum(g.getMetadata().getNationNo());
				String thisNationDescription = n.getName();
				AbstractListViewFilter a;
				for (int i=0; i<com.getItemCount();i++) {
					a = (AbstractListViewFilter)com.getItemAt(i);
					if (a.getDescription().equals(thisNationDescription)) {
						final int selected = i;
						SwingUtilities.invokeLater(new Runnable() {
							@Override
							public void run() {
								// the object might have changed by the time we get run.
								if (selected < com.getItemCount()) {
									com.setSelectedIndex(selected);
								}
							}
							});
						return;
					}
				}
			}
		}

	}
}
