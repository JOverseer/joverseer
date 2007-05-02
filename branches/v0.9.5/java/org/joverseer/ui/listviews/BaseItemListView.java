package org.joverseer.ui.listviews;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.prefs.Preferences;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import org.joverseer.domain.CharacterDeathReasonEnum;
import org.joverseer.domain.IBelongsToNation;
import org.joverseer.domain.IHasMapLocation;
import org.joverseer.domain.NationRelations;
import org.joverseer.game.Game;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.preferences.PreferenceRegistry;
import org.joverseer.support.GameHolder;
import org.joverseer.support.infoSources.InfoSource;
import org.joverseer.ui.JOverseerClient;
import org.joverseer.ui.LifecycleEventsEnum;
import org.joverseer.ui.listviews.filters.AndFilter;
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
import org.springframework.richclient.command.ActionCommand;
import org.springframework.richclient.command.support.AbstractActionCommandExecutor;
import org.springframework.richclient.image.ImageSource;
import org.springframework.richclient.layout.TableLayoutBuilder;
import org.springframework.richclient.table.BeanTableModel;
import org.springframework.richclient.table.ColumnToSort;
import org.springframework.richclient.table.SortableTableModel;
import org.springframework.richclient.table.TableUtils;


public abstract class BaseItemListView extends AbstractView implements ApplicationListener, MouseListener {

    protected BeanTableModel tableModel;

    protected JTable table;
    protected ArrayList<JComboBox> filters = new ArrayList<JComboBox>();
    protected Class tableModelClass;
    protected SelectHexCommandExecutor selectHexCommandExecutor = new SelectHexCommandExecutor();
    protected JPanel buttonPanel;

    public BaseItemListView(Class tableModelClass) {
        this.tableModelClass = tableModelClass;
    }

    protected abstract void setItems();

    protected abstract int[] columnWidths();

    protected ColumnToSort[] getDefaultSort() {
        return null;
    }

    protected void registerLocalCommandExecutors(PageComponentContext pageComponentContext) {
        pageComponentContext.register("selectHexCommand", selectHexCommandExecutor);
        selectHexCommandExecutor.setEnabled(GameHolder.hasInitializedGame());
    }

    private class SelectHexCommandExecutor extends AbstractActionCommandExecutor {

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

    protected AbstractListViewFilter[][] getFilters() {
        return null;
    }

    protected AbstractListViewFilter getActiveFilter() {
        if (filters == null)
            return null;
        AndFilter f = new AndFilter();
        for (JComboBox filter : filters) {
        	f.addFilter((AbstractListViewFilter)filter.getSelectedItem());
        }
        return f;
    }
    
    protected JTable createTable() {
    	return TableUtils.createStandardSortableTable(tableModel);
    }
    
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
            return new JComponent[]{restoreSorting};
        }
        return new JComponent[]{};
    }

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

        // create the filter combo
        AbstractListViewFilter[][] filterLists = getFilters();
        if (filterLists != null) {
        	TableLayoutBuilder lb = new TableLayoutBuilder();
        	for (AbstractListViewFilter[] filterList : filterLists) {
        		JComboBox filter = new JComboBox(filterList);
	            filters.add(filter);
	            filter.addActionListener(new ActionListener() {
	
	                public void actionPerformed(ActionEvent e) {
	                    setItems();
	                }
	            });
	            filter.setPreferredSize(new Dimension(150, 20));
	            filter.setOpaque(true);
	            lb.cell(filter, "colspec=left:150px");
	            lb.gapCol();
        	}
        	JPanel p = lb.getPanel();
        	p.setOpaque(true);
        	tlb.cell(p, "align=left");
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
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setOpaque(true);
        scrollPane.getViewport().setBackground(table.getBackground());
        scrollPane.setPreferredSize(new Dimension(1200, 1200));
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

    public void onApplicationEvent(ApplicationEvent applicationEvent) {
        if (applicationEvent instanceof JOverseerEvent) {
            JOverseerEvent e = (JOverseerEvent) applicationEvent;
            if (e.getEventType().equals(LifecycleEventsEnum.SelectedTurnChangedEvent.toString())) {
                setItems();
            } else if (e.getEventType().equals(LifecycleEventsEnum.SelectedHexChangedEvent.toString())) {
                //setItems();
            } else if (e.getEventType().equals(LifecycleEventsEnum.GameChangedEvent.toString())) {
                if (filters.size() > 0) {
                    AbstractListViewFilter[][] filterLists = getFilters();
                    for (int i=0; i<filterLists.length; i++) {
                    	filters.get(i).removeAllItems();
                    	for (AbstractListViewFilter f : filterLists[i]) {
                    		filters.get(i).addItem(f);
                    	}
                    	filters.get(i).updateUI();
                    }
                }
                setItems();
            } else if (e.getEventType().equals(LifecycleEventsEnum.ListviewTableAutoresizeModeToggle.toString())) {
                String pval = PreferenceRegistry.instance().getPreferenceValue("listviews.autoresizeCols");
                if (pval.equals("yes")) {
                    table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
                } else {
                    table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
                }
            } else if (e.getEventType().equals(LifecycleEventsEnum.ListviewRefreshItems.toString())) {
                setItems();
            }
        }
    }

    public void showContextMenu(MouseEvent e) {
        JPopupMenu pm = getPopupMenu();
        if (pm == null)
            return;
        if (table.getSelectedRowCount() == 0)
            return;
        pm.show(table, e.getX(), e.getY());
    };

    public JPopupMenu getPopupMenu() {
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
        // To change body of implemented methods use File | Settings | File Templates.
    }

    public void mouseExited(MouseEvent e) {
        // To change body of implemented methods use File | Settings | File Templates.
    }

    public void mousePressed(MouseEvent e) {
        // To change body of implemented methods use File | Settings | File Templates.
    }

    public void mouseReleased(MouseEvent e) {
        // To change body of implemented methods use File | Settings | File Templates.
    }

    public class ToggleNationNoDisplay extends ActionCommand {

        protected void doExecuteCommand() {
            Preferences prefs = Preferences.userNodeForPackage(JOverseerClient.class);
            String nationAsNumber = prefs.get("nationAsNumber", null);
            if (nationAsNumber != null && nationAsNumber.equalsIgnoreCase("true")) {
                nationAsNumber = "false";
            } else {
                nationAsNumber = "true";
            }
            prefs.put("nationAsNumber", nationAsNumber);
        }

    }



}
