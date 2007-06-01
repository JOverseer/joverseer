package org.joverseer.ui.listviews;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.InvocationTargetException;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.TransferHandler;
import javax.swing.table.JTableHeader;

import org.joverseer.domain.ArmyEstimate;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.preferences.PreferenceRegistry;
import org.joverseer.ui.listviews.renderers.AllegianceColorCellRenderer;
import org.joverseer.ui.support.transferHandlers.GenericExportTransferHandler;
import org.joverseer.ui.support.transferHandlers.GenericTransferable;
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
 * List view that shows ArmyEstimate objects
 * 
 * @author Marios Skounakis
 */
public class ArmyEstimatesListView extends ItemListView {

    public ArmyEstimatesListView() {
        super(TurnElementsEnum.ArmyEstimate, ArmyEstimatesTableModel.class);
    }

    protected int[] columnWidths() {
        return new int[] {42, 80, 64, 130, 48, 48, 100, 120, 120, 120};
    }

    /**
     * Implements its own createControlImpl without calling the base because I need to
     * create a JideTable (to use the MutliLineRenderer) instead of a standard Spring/Swing table
     */
    protected JComponent createControlImpl() {
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
                tlb.cell(filter, "align=left");
                tlb.gapCol();
            }
            tlb.row();
        }

        setItems();

        // create the JTable instance
        table = TableUtils.createStandardSortableTable(tableModel);
        table = new SortableTable(table.getModel()) {

            protected void initTable() {
                super.initTable();
                setSortTableHeaderRenderer();
            }

            protected SortTableHeaderRenderer createSortHeaderRenderer() {
                return new SortTableHeaderRenderer() {

                    protected void initComponents() {
                        super.initComponents();
                        _headerPanel.add(_titlePanel, BorderLayout.CENTER);
                    }

                    protected JLabel createLabel(String text) {
                        return new JLabel(text, JLabel.LEADING);
                    }
                };
            }

            protected JTableHeader createDefaultTableHeader() {
                return new JTableHeader(columnModel);
            }
        };
        ((JideTable) table).setRowResizable(true);
        ((JideTable) table).setRowAutoResizes(true);
        org.joverseer.ui.support.controls.TableUtils.setTableColumnWidths(table, columnWidths());

        String pval = PreferenceRegistry.instance().getPreferenceValue("listviews.autoresizeCols");
        if (pval.equals("yes")) {
            table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        } else {
            table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        }

        table.getTableHeader().setBackground(Color.WHITE);
        table.setDefaultRenderer(String.class, new AllegianceColorCellRenderer(tableModel));
        table.setDefaultRenderer(Integer.class, new AllegianceColorCellRenderer(tableModel));
        table.setDefaultRenderer(Boolean.class, new AllegianceColorCellRenderer(tableModel));
        table.addMouseListener(this);
        table.addMouseMotionListener(this);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setOpaque(true);
        scrollPane.getViewport().setBackground(table.getBackground());
        tlb.cell(scrollPane);

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
            tlb.cell(restoreSorting, "colspec=left:30px valign=top");
        }
        JPanel p = tlb.getPanel();
        p.setBackground(Color.WHITE);

        MultilineTableCellRenderer r = new MultilineTableCellRenderer();
        r.setWrapStyleWord(true);
        r.setLineWrap(true);
        table.setDefaultRenderer(String.class, r);
        table.setDefaultRenderer(Integer.class, r);

        return p;
    }

    protected void startDragAndDropAction(MouseEvent e) {
        final ArmyEstimate[] selectedArmies = new ArmyEstimate[table.getSelectedRowCount()];
        String copyString = "";
        for (int i = 0; i < table.getSelectedRowCount(); i++) {
            int idx = ((com.jidesoft.grid.SortableTableModel) table.getModel())
                    .getActualRowAt(table.getSelectedRows()[i]);
            ArmyEstimate a = (ArmyEstimate) tableModel.getRow(idx);
            selectedArmies[i] = a;
            String ln = "";
            for (int j = 0; j < table.getColumnCount(); j++) {
                Object v = table.getValueAt(i, j);
                if (v == null)
                    v = "";
                ln += (ln.equals("") ? "" : "\t") + v;
            }
            copyString += (copyString.equals("") ? "" : "\n") + ln;
        }
        final String str = copyString;

        TransferHandler handler = new GenericExportTransferHandler() {

            protected Transferable createTransferable(JComponent arg0) {
                try {
                    Transferable t = new GenericTransferable(new DataFlavor[] {
                            new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType + ";class=\""
                                    + ArmyEstimate[].class.getName() + "\""),
                            new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType + ";class="
                                    + ArmyEstimate.class.getName()), DataFlavor.stringFlavor}, new Object[] {
                            selectedArmies, selectedArmies[0], str});
                    return t;
                } catch (Exception exc) {
                    exc.printStackTrace();
                    return null;
                }

            }
        };
        table.setTransferHandler(handler);
        handler.exportAsDrag(table, e, TransferHandler.COPY);
    }


}
