package org.joverseer.ui.listviews;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellEditor;

import org.joverseer.domain.Note;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.preferences.PreferenceRegistry;
import org.joverseer.support.GameHolder;
import org.joverseer.ui.LifecycleEventsEnum;
import org.joverseer.ui.command.AddEditNoteCommand;
import org.joverseer.ui.listviews.filters.TextFilter;
import org.joverseer.ui.support.JOverseerEvent;
import org.joverseer.ui.support.controls.TextAreaEditor;
import org.joverseer.ui.support.controls.TextAreaRenderer;
import org.springframework.context.MessageSource;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.command.ActionCommand;
import org.springframework.richclient.command.CommandGroup;
import org.springframework.richclient.layout.TableLayoutBuilder;
import org.springframework.richclient.table.BeanTableModel;
import org.springframework.richclient.table.ShuttleSortableTableModel;
import org.springframework.richclient.table.TableUtils;

import com.jidesoft.grid.JideTable;
import com.jidesoft.grid.MultilineTableCellRenderer;
import com.jidesoft.grid.SortTableHeaderRenderer;
import com.jidesoft.grid.SortableTable;
import com.jidesoft.grid.SortableTableModel;

public class NotesListView extends ItemListView {

    public NotesListView() {
        super(TurnElementsEnum.Notes, NotesTableModel.class);
    }


    protected int[] columnWidths() {
        return new int[] {42, 96, 64, 96, 300, 64};
    }

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
//        table = new SortableTable(table.getModel()) {
//
//            protected void initTable() {
//                super.initTable();
//                setSortTableHeaderRenderer();
//            }
//
//            protected SortTableHeaderRenderer createSortHeaderRenderer() {
//                return new SortTableHeaderRenderer() {
//
//                    protected void initComponents() {
//                        super.initComponents();
//                        _headerPanel.add(_titlePanel, BorderLayout.CENTER);
//                    }
//
//                    protected JLabel createLabel(String text) {
//                        return new JLabel(text, JLabel.LEADING);
//                    }
//                };
//            }
//
//            protected JTableHeader createDefaultTableHeader() {
//                return new JTableHeader(columnModel);
//            }
//        };
//        ((JideTable) table).setRowResizable(true);
//        ((JideTable) table).setRowAutoResizes(true);
        org.joverseer.ui.support.controls.TableUtils.setTableColumnWidths(table, columnWidths());

        String pval = PreferenceRegistry.instance().getPreferenceValue("listviews.autoresizeCols");
        if (pval.equals("yes")) {
            table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        } else {
            table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        }

        table.getTableHeader().setBackground(Color.WHITE);
        table.setRowHeight(12);
        table.addMouseListener(this);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setOpaque(true);
        scrollPane.getViewport().setBackground(table.getBackground());
        tlb.cell(scrollPane);

        JPanel p = tlb.getPanel();
        p.setBackground(Color.WHITE);

        //MultilineTableCellRenderer r = new MultilineTableCellRenderer();
        //r.setWrapStyleWord(true);
        //r.setLineWrap(true);
        TextAreaRenderer r = new TextAreaRenderer();
        table.setDefaultRenderer(String.class, r);
        table.setDefaultRenderer(Integer.class, r);
        
        table.getColumnModel().getColumn(NotesTableModel.iText).setCellEditor(new TextAreaEditor());
        
        return p;
    }


    public JPopupMenu getPopupMenu() {
        CommandGroup group = Application.instance().getActiveWindow().getCommandManager().createCommandGroup(
                "noteCommandGroup", new Object[] {new EditNoteCommand(), new DeleteNoteCommand()});
        return group.createPopupMenu();
    }
    
    class DeleteNoteCommand extends ActionCommand {

        protected void doExecuteCommand() {
            int row = table.getSelectedRow();
            if (row >= 0) {
                //int idx = ((SortableTableModel) table.getModel()).getRowAt(row);
                int idx = ((ShuttleSortableTableModel)table.getModel()).convertSortedIndexToDataIndex(row);
                if (idx >= tableModel.getRowCount())
                    return;
                try {
                    Object obj = tableModel.getRow(idx);
                    Note note = (Note) obj;
                    GameHolder.instance().getGame().getTurn().getContainer(TurnElementsEnum.Notes).removeItem(note);
                    Application.instance().getApplicationContext().publishEvent(
                            new JOverseerEvent(LifecycleEventsEnum.ListviewRefreshItems.toString(), this, this));

                } catch (Exception exc) {

                }
            }
        }
        
    }

    class EditNoteCommand extends ActionCommand {

        protected void doExecuteCommand() {
            int row = table.getSelectedRow();
            if (row >= 0) {
                //int idx = ((SortableTableModel) table.getModel()).getRowAt(row);
                int idx = ((ShuttleSortableTableModel)table.getModel()).convertSortedIndexToDataIndex(row);
                if (idx >= tableModel.getRowCount())
                    return;
                try {
                    Object obj = tableModel.getRow(idx);
                    Note note = (Note) obj;
                    new AddEditNoteCommand(note).execute();
                } catch (Exception exc) {
                    exc.printStackTrace();
                }
            }
        }
        
    }

    protected AbstractListViewFilter[][] getFilters() {
        return new AbstractListViewFilter[][]{
                new AbstractListViewFilter[]{
                        new TextFilter("All", "tags", null),
                        new TextFilter("No tags", "tags", ""),
                        new TextFilter("Agents", "tags", "Agents"),
                        new TextFilter("Emissaries", "tags", "Emissaries"),
                        new TextFilter("Mages", "tags", "Mages"),
                        new TextFilter("Artifacts", "tags", "Artifacts"),
                        new TextFilter("Gold", "tags", "Gold"),
                        new TextFilter("Order Comments", "tags", "Order")
                }};
        }
    

    
    
}
