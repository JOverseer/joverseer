package org.joverseer.ui.listviews;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.JTableHeader;

import org.joverseer.domain.Character;
import org.joverseer.domain.Encounter;
import org.joverseer.game.Game;
import org.joverseer.game.Turn;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.preferences.PreferenceRegistry;
import org.joverseer.support.GameHolder;
import org.joverseer.ui.listviews.filters.NationFilter;
import org.joverseer.ui.listviews.filters.TurnFilter;
import org.joverseer.ui.listviews.renderers.AllegianceColorCellRenderer;
import org.joverseer.ui.support.Messages;
import org.springframework.context.MessageSource;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.image.ImageSource;
import org.springframework.richclient.layout.TableLayoutBuilder;
import org.springframework.richclient.table.BeanTableModel;
import org.springframework.richclient.table.SortableTableModel;
import org.springframework.richclient.table.TableUtils;

import com.jidesoft.grid.JideTable;
import com.jidesoft.grid.MultilineTableCellRenderer;
import com.jidesoft.grid.SortTableHeaderRenderer;
import com.jidesoft.grid.SortableTable;

/**
 * List view for encounters
 * 
 * @author Marios Skounakis
 */
public class EncounterListView extends BaseItemListView {
	public EncounterListView() {
		super(EncounterTableModel.class);
	}

	@Override
	protected int[] columnWidths() {
		return new int[] { 40, 40, 64, 96, 370 };
	}

	/**
	 * Overrides and ignores the base class implementation because it needs a
	 * JideTable to use the MultiLineRenderer
	 */
	@Override
	protected JComponent createControlImpl() {
		// fetch the messageSource instance from the application context
		MessageSource messageSource = (MessageSource) getApplicationContext().getBean("messageSource"); //$NON-NLS-1$

		// create the table model
		try {
			this.tableModel = (BeanTableModel) this.tableModelClass.getConstructor(new Class[] { MessageSource.class }).newInstance(new Object[] { messageSource });
		} catch (InstantiationException e) {
			e.printStackTrace(); // To change body of catch statement use File |
			// Settings | File Templates.
		} catch (IllegalAccessException e) {
			e.printStackTrace(); // To change body of catch statement use File |
			// Settings | File Templates.
		} catch (InvocationTargetException e) {
			e.printStackTrace(); // To change body of catch statement use File |
			// Settings | File Templates.
		} catch (NoSuchMethodException e) {
			e.printStackTrace(); // To change body of catch statement use File |
			// Settings | File Templates.
		}

		TableLayoutBuilder tlb = new TableLayoutBuilder();

		// create the filter combo
		AbstractListViewFilter[][] filterLists = getFilters();
		if (filterLists != null) {
			for (AbstractListViewFilter[] filterList : filterLists) {
				JComboBox filter = new JComboBox(filterList);
				this.filters.add(filter);
				filter.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						setItems();
					}
				});
				filter.setPreferredSize(new Dimension(150, 20));
				filter.setOpaque(true);
				tlb.cell(filter, "align=left"); //$NON-NLS-1$
				tlb.gapCol();
			}
			tlb.row();
		}

		setItems();

		// create the JTable instance
		this.table = TableUtils.createStandardSortableTable(this.tableModel);
		this.table = new SortableTable(this.table.getModel()) {
			@Override
			protected void initTable() {
				super.initTable();
				setSortTableHeaderRenderer();
			}

			@Override
			protected SortTableHeaderRenderer createSortHeaderRenderer() {
				return new SortTableHeaderRenderer() {
					@Override
					protected void initComponents() {
						super.initComponents();
						this._headerPanel.add(this._titlePanel, BorderLayout.CENTER);
					}

					@Override
					protected JLabel createLabel(String text) {
						return new JLabel(text, SwingConstants.LEADING);
					}
				};
			}

			@Override
			protected JTableHeader createDefaultTableHeader() {
				return new JTableHeader(this.columnModel);
			}
		};
		((JideTable) this.table).setRowResizable(true);
		((JideTable) this.table).setRowAutoResizes(true);
		org.joverseer.ui.support.controls.TableUtils.setTableColumnWidths(this.table, columnWidths());

		String pval = PreferenceRegistry.instance().getPreferenceValue("listviews.autoresizeCols"); //$NON-NLS-1$
		if (pval.equals("yes")) { //$NON-NLS-1$
			this.table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		} else {
			this.table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		}

		this.table.getTableHeader().setBackground(Color.WHITE);
		this.table.setDefaultRenderer(String.class, new AllegianceColorCellRenderer(this.tableModel));
		this.table.setDefaultRenderer(Integer.class, new AllegianceColorCellRenderer(this.tableModel));
		this.table.setDefaultRenderer(Boolean.class, new AllegianceColorCellRenderer(this.tableModel));
		this.table.addMouseListener(this);
		JScrollPane scrollPane = new JScrollPane(this.table);
		scrollPane.getViewport().setOpaque(true);
		scrollPane.getViewport().setBackground(this.table.getBackground());
		tlb.cell(scrollPane);

		if (getDefaultSort() != null) {
			ImageSource imgSource = (ImageSource) Application.instance().getApplicationContext().getBean("imageSource"); //$NON-NLS-1$
			Icon ico = new ImageIcon(imgSource.getImage("restoreSorting.icon")); //$NON-NLS-1$
			JLabel restoreSorting = new JLabel();
			restoreSorting.setIcon(ico);
			restoreSorting.setPreferredSize(new Dimension(16, 16));
			restoreSorting.addMouseListener(new MouseAdapter() {

				@Override
				public void mouseClicked(MouseEvent arg0) {
					((SortableTableModel) EncounterListView.this.table.getModel()).sortByColumns(getDefaultSort());
				}

			});
			((SortableTableModel) this.table.getModel()).sortByColumns(getDefaultSort());
			restoreSorting.setToolTipText(Messages.getString("EncounterListView.RestoreDefaultSortOrder")); //$NON-NLS-1$
			tlb.cell(restoreSorting, "colspec=left:30px valign=top"); //$NON-NLS-1$
		}
		JPanel p = tlb.getPanel();
		p.setBackground(Color.WHITE);

		MultilineTableCellRenderer r = new MultilineTableCellRenderer();
		r.setWrapStyleWord(true);
		r.setLineWrap(true);
		this.table.setDefaultRenderer(String.class, r);
		this.table.setDefaultRenderer(Integer.class, r);
		this.table.setFont(new Font(this.table.getFont().getFamily(), Font.PLAIN, this.table.getFont().getSize() - 1));
		return p;
	}

	@Override
	protected AbstractListViewFilter[][] getFilters() {
		ArrayList<AbstractListViewFilter> filters1 = new ArrayList<AbstractListViewFilter>();
		filters1.addAll(Arrays.asList(NationFilter.createNationFilters()));
		return new AbstractListViewFilter[][] { filters1.toArray(new AbstractListViewFilter[] {}), TurnFilter.createTurnFiltersCurrentTurnAndAllTurns(), };
	}

	@Override
	protected void setItems() {
		ArrayList<Object> items = new ArrayList<Object>();
		Game g = GameHolder.instance().getGame();
		if (g != null) {
			for (int i = 0; i <= g.getMaxTurn(); i++) {
				Turn t = g.getTurn(i);
				if (t == null)
					continue;
				for (Encounter e : t.getEncounters()) {
					EncounterTableModel.EncounterWrapper ew = new EncounterTableModel.EncounterWrapper();
					ew.setTurnNo(i);
					ew.setCharacter(e.getCharacter());
					ew.setDescription(e.getDescription());
					ew.setHexNo(e.getHexNo());
					Character c = (Character) t.getContainer(TurnElementsEnum.Character).findFirstByProperty("name", ew.getCharacter()); //$NON-NLS-1$
					ew.setNationNo(0);
					if (c != null)
						ew.setNationNo(c.getNationNo());
					if (getActiveFilter().accept(ew))
						items.add(ew);
				}
			}
		}
		this.tableModel.setRows(items);

	}

}
