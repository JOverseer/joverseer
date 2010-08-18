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

import org.joverseer.domain.CharacterDeathReasonEnum;
import org.joverseer.domain.IHasMapLocation;
import org.joverseer.domain.Order;
import org.joverseer.preferences.PreferenceRegistry;
import org.joverseer.support.GameHolder;
import org.joverseer.support.infoSources.InfoSource;
import org.joverseer.ui.LifecycleEventsEnum;
import org.joverseer.ui.listviews.filters.AndFilter;
import org.joverseer.ui.listviews.filters.TextFilter;
import org.joverseer.ui.listviews.renderers.AllegianceColorCellRenderer;
import org.joverseer.ui.listviews.renderers.DeathReasonEnumRenderer;
import org.joverseer.ui.listviews.renderers.InfoSourceTableCellRenderer;
import org.joverseer.ui.support.JOverseerEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.MessageSource;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.application.PageComponentContext;
import org.springframework.richclient.application.support.AbstractView;
import org.springframework.richclient.command.support.AbstractActionCommandExecutor;
import org.springframework.richclient.image.ImageSource;
import org.springframework.richclient.layout.TableLayoutBuilder;
import org.springframework.richclient.table.BeanTableModel;
import org.springframework.richclient.table.ColumnToSort;
import org.springframework.richclient.table.SortableTableModel;
import org.springframework.richclient.table.TableUtils;

/**
 * Base class for ListViews It basically provides support for: - having a table with the given ItemTableModel descendant -
 * having filters - having a default sort order - having a popup menu for the table rows - having a set of buttons on
 * the right of the table
 * 
 * @author Marios Skounakis
 */
public abstract class BaseItemListView extends AbstractView implements ApplicationListener, MouseListener,
        MouseMotionListener {

    protected int xDiff;
    protected int yDiff;

    protected BeanTableModel tableModel;

    protected JTable table;
    protected ArrayList<JComboBox> filters = new ArrayList<JComboBox>();
    protected JTextField textFilterField;
    protected Class tableModelClass;
    protected SelectHexCommandExecutor selectHexCommandExecutor = new SelectHexCommandExecutor();
    protected JPanel buttonPanel;
    protected ArrayList filterOptionsCache = new ArrayList();
    
    protected boolean handleFilterEvents = true;
    
    public BaseItemListView(Class tableModelClass) {
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

    protected void registerLocalCommandExecutors(PageComponentContext pageComponentContext) {
        pageComponentContext.register("selectHexCommand", selectHexCommandExecutor);
        selectHexCommandExecutor.setEnabled(GameHolder.hasInitializedGame());
    }

    /**
     * What happens when you double click on a row that implements the IHasMapLocation interface ... the respective hex
     * is selected
     */
    private class SelectHexCommandExecutor extends AbstractActionCommandExecutor {

        // TODO move to a seperate class?
        public void execute() {
            int row = table.getSelectedRow();
            if (row >= 0) {
                int idx = 0;
                if (SortableTableModel.class.isInstance(table.getModel())) {
                    idx = ((SortableTableModel) table.getModel()).convertSortedIndexToDataIndex(row);
                } else if (com.jidesoft.grid.SortableTableModel.class.isInstance(table.getModel())) {
                    idx = ((com.jidesoft.grid.SortableTableModel) table.getModel()).getActualRowAt(row);
                }
                if (idx >= tableModel.getRowCount())
                    return;
                try {
                    Object obj = tableModel.getRow(idx);
                    if (!IHasMapLocation.class.isInstance(obj))
                        return;
                    IHasMapLocation selectedItem = (IHasMapLocation) obj;
                    Point selectedHex = new Point(selectedItem.getX(), selectedItem.getY());
                    Application.instance().getApplicationContext().publishEvent(
                            new JOverseerEvent(LifecycleEventsEnum.SelectedHexChangedEvent.toString(), selectedHex,
                                    this));
                } catch (Exception exc) {
                    // do nothing
                }
            }
        }
    }

    protected JComponent createControl() {
        return createControlImpl();
    }

    /**
     * Return the desired filters Two dimensional array so you can return multiple groups of filtes Each group of
     * filters goes into a separate combo box
     */
    protected AbstractListViewFilter[][] getFilters() {
        return null;
    }
    
    protected AbstractListViewFilter getTextFilter(String txt) {
    	return new PositiveListViewFilter();
    }
    
    protected AbstractListViewFilter getActiveFilter() {
    	AbstractListViewFilter textFilter = getTextFilter(textFilterField == null ? null : textFilterField.getText());
        if (filters == null) return textFilter;
        AndFilter f = new AndFilter();
        f.addFilter(textFilter);
        for (JComboBox filter : filters) {
            f.addFilter((AbstractListViewFilter) filter.getSelectedItem());
        }
        return f;
    }

    protected JTable createTable() {
        return TableUtils.createStandardSortableTable(tableModel);
    }

    /**
     * Buttons on the right of the table
     */
    protected JComponent[] getButtons() {
        if (getDefaultSort() != null) {
            ImageSource imgSource = (ImageSource) Application.instance().getApplicationContext().getBean("imageSource");
            Icon ico = new ImageIcon(imgSource.getImage("restoreSorting.icon"));
            JLabel restoreSorting = new JLabel();
            restoreSorting.setIcon(ico);
            restoreSorting.setPreferredSize(new Dimension(16, 16));
            restoreSorting.addMouseListener(new MouseAdapter() {

                public void mouseClicked(MouseEvent arg0) {
                    ((SortableTableModel) table.getModel()).sortByColumns(getDefaultSort());
                }

            });
            ((SortableTableModel) table.getModel()).sortByColumns(getDefaultSort());
            restoreSorting.setToolTipText("Restore default sort order");
            return new JComponent[] {restoreSorting};
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
                filters.add(filter);
                filter.addActionListener(new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        if (handleFilterEvents) setItems();
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
        	textFilterField = new JTextField();
        	textFilterField.setPreferredSize(new Dimension(150, 20));
        	textFilterField.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                	if (handleFilterEvents) setItems();
                }
            });
        	lb.cell(textFilterField, "colspec=left:150px");
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
     * create the view...
     * 
     * @return
     */
    protected JComponent createControlImpl() {

        // fetch the messageSource instance from the application context
        MessageSource messageSource = (MessageSource) getApplicationContext().getBean("messageSource");

        // create the table model
        try {
            tableModel = (BeanTableModel) tableModelClass.getConstructor(new Class[] {MessageSource.class})
                    .newInstance(new Object[] {messageSource});
        } catch (InstantiationException e) {
            e.printStackTrace(); // To change body of catch statement use File | Settings | File Templates.
        } catch (IllegalAccessException e) {
            e.printStackTrace(); // To change body of catch statement use File | Settings | File Templates.
        } catch (InvocationTargetException e) {
            e.printStackTrace(); // To change body of catch statement use File | Settings | File Templates.
        } catch (NoSuchMethodException e) {
            e.printStackTrace(); // To change body of catch statement use File | Settings | File Templates.
        }

        TableLayoutBuilder tlb = new TableLayoutBuilder();
        JPanel fp = createFilterPanel();
        if (fp != null) {
	        tlb.cell(fp, "align=left");
	        tlb.row();
        }

        setItems();

        // create the JTable instance
        table = createTable();
        org.joverseer.ui.support.controls.TableUtils.setTableColumnWidths(table, columnWidths());

        String pval = PreferenceRegistry.instance().getPreferenceValue("listviews.autoresizeCols");
        if (pval.equals("yes")) {
            table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        }

        table.getTableHeader().setBackground(Color.WHITE);
        table.setDefaultRenderer(String.class, new AllegianceColorCellRenderer(tableModel));
        table.setDefaultRenderer(Integer.class, new AllegianceColorCellRenderer(tableModel));
        table.setDefaultRenderer(Boolean.class, new AllegianceColorCellRenderer(tableModel));
        table.setDefaultRenderer(InfoSource.class, new InfoSourceTableCellRenderer(tableModel));
        table.setDefaultRenderer(CharacterDeathReasonEnum.class, new DeathReasonEnumRenderer(tableModel));
        table.addMouseListener(this);
        table.addMouseMotionListener(this);
        // table.setDragEnabled(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setOpaque(true);
        scrollPane.getViewport().setBackground(table.getBackground());
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
        pnl.setBackground(Color.WHITE);
        tlb.cell(pnl, "colspec=left:30px valign=top");
        JPanel p = tlb.getPanel();
        p.setBackground(Color.WHITE);
        return p;
    }

    protected void refreshFilters() {
        if (filters.size() > 0) {
            AbstractListViewFilter[][] filterLists = getFilters();
            for (int i = 0; i < filterLists.length; i++) {
                filters.get(i).removeAllItems();
                for (AbstractListViewFilter f : filterLists[i]) {
                    filters.get(i).addItem(f);
                }
                try {
                	filters.get(i).updateUI();
                }
                catch (Exception exc) {
                	logger.error(exc);
                }
            }
        }
    }

    public void onApplicationEvent(ApplicationEvent applicationEvent) {
        if (applicationEvent instanceof JOverseerEvent) {
            JOverseerEvent e = (JOverseerEvent) applicationEvent;
            if (e.getEventType().equals(LifecycleEventsEnum.SelectedTurnChangedEvent.toString())) {
            	SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						cacheFilterOptions();
		                refreshFilters();
		                tryRestoreFilterOptions();
		                setItems();
					}
            	});
            } else if (e.getEventType().equals(LifecycleEventsEnum.SelectedHexChangedEvent.toString())) {
                // setItems();
            } else if (e.getEventType().equals(LifecycleEventsEnum.GameChangedEvent.toString())) {
            	SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						refreshFilters();
		                setItems();
					}
            	});
            } else if (e.getEventType().equals(LifecycleEventsEnum.ListviewTableAutoresizeModeToggle.toString())) {
            	SwingUtilities.invokeLater(new Runnable() {
					public void run() {
		                String pval = PreferenceRegistry.instance().getPreferenceValue("listviews.autoresizeCols");
		                if (pval.equals("yes")) {
		                    table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		                } else {
		                    table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		                }
					}
            	});

            } else if (e.getEventType().equals(LifecycleEventsEnum.ListviewRefreshItems.toString())) {
            	SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						setItems();
					}
            	});
            }
        }
    }

    public void showContextMenu(MouseEvent e) {
        JPopupMenu pm = getPopupMenu(table.getSelectedRowCount() != 0);
        if (pm == null)
            return;
        pm.show(e.getComponent(), e.getX(), e.getY());
    };

    public JPopupMenu getPopupMenu(boolean hasSelectedItem) {
        return null;
    }


    public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() == 2 && e.getButton() == 1) {
            selectHexCommandExecutor.execute();
        }
        if (e.getClickCount() == 1 && e.getButton() == 3) {
            showContextMenu(e);
        }
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mousePressed(MouseEvent e) {

        if (e.getButton() == MouseEvent.BUTTON1) {
            xDiff = e.getX();
            yDiff = e.getY();
        }
    }

    public void mouseReleased(MouseEvent e) {
    }

    public void mouseDragged(MouseEvent e) {
        Point p = e.getPoint();
        int dx = Math.abs(e.getX() - xDiff);
        int dy = Math.abs(e.getY() - yDiff);
        if (dx > 5 || dy > 5) {
            startDragAndDropAction(e);
        }
    }

    /**
     * Implement what you want to happen when a drag & drop action is started
     */
    protected void startDragAndDropAction(MouseEvent e) {

    }

    public void mouseMoved(MouseEvent e) {
    }

    protected void cacheFilterOptions() {
    	filterOptionsCache.clear();
    	if (hasTextFilter()) {
    		filterOptionsCache.add(textFilterField.getText());
    	}
    	if (filters == null) return;
        for (JComboBox filter : filters) {
        	filterOptionsCache.add(filter.getSelectedItem());
        }
    }
    
    protected void tryRestoreFilterOptions() {
    	try {
    		
    		if (filterOptionsCache == null) return;
    		if (filterOptionsCache.size() == 0) return;
    		handleFilterEvents = false;
    		if (hasTextFilter() && textFilterField != null && filterOptionsCache.get(0)!=null) {
    			textFilterField.setText(filterOptionsCache.get(0).toString());
    		}
    		if (filters != null) {
    			for (int i=1; i<filterOptionsCache.size(); i++) {
    				JComboBox filter = filters.get(i-1);
    				filter.setSelectedItem(filterOptionsCache.get(i));
    			}
    			handleFilterEvents = true;
    		}
    	}
    	catch (Exception exc) {
    		// do nothing
    		int a = 1;
    		throw new RuntimeException(exc);
    	}
    	finally {
    		handleFilterEvents = true;
    	}
    }
    
    public Object getSelectedObject() {
    	int row = table.getSelectedRow();
        if (row >= 0) {
            int idx = ((SortableTableModel) table.getModel()).convertSortedIndexToDataIndex(row);
            if (idx >= tableModel.getRowCount())
                return null;
            try {
                Object obj = tableModel.getRow(idx);
                return obj;
            }
            catch (Exception e) {
            }
        }
        return null;
    }
}
