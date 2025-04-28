package org.joverseer.ui.listviews;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import org.joverseer.JOApplication;
import org.joverseer.domain.Note;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.preferences.PreferenceRegistry;
import org.joverseer.ui.LifecycleEventsEnum;
import org.joverseer.ui.command.AddEditNoteCommand;
import org.joverseer.ui.listviews.filters.NationFilter;
import org.joverseer.ui.listviews.filters.TextFilter;
import org.joverseer.ui.listviews.renderers.HexNumberCellRenderer;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.command.ActionCommand;
import org.springframework.richclient.command.CommandGroup;
import org.springframework.richclient.layout.TableLayoutBuilder;
import org.springframework.richclient.table.ShuttleSortableTableModel;
import org.springframework.richclient.table.TableUtils;

/**
 * List view for notes objects
 *
 * @author Marios Skounakis
 */
public class NotesListView extends ItemListView {

    public NotesListView() {
        super(TurnElementsEnum.Notes, NotesTableModel.class);
    }


    @Override
	protected int[] columnWidths() {
        return new int[] {42, 96, 64, 96, 300, 64};
    }

    //TODO issues with the cell renderers - need multiline or not?
    @Override
	protected JComponent createControlImpl() {
        this.tableModel = super.createBeanTableModel();

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
                tlb.cell(filter, "align=left");
                tlb.gapCol();
            }
            tlb.row();
        }

        setItems();

        // create the JTable instance
        this.table = TableUtils.createStandardSortableTable(this.tableModel);
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
        org.joverseer.ui.support.controls.TableUtils.setTableColumnWidths(this.table, columnWidths());

        String pval = PreferenceRegistry.instance().getPreferenceValue("listviews.autoresizeCols");
        if (pval.equals("yes")) {
            this.table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        } else {
            this.table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        }

        this.table.addMouseListener(this);
        JScrollPane scrollPane = new JScrollPane(this.table);
        scrollPane.getViewport().setOpaque(true);
        scrollPane.getViewport().setBackground(this.table.getBackground());
        tlb.cell(scrollPane);

        org.joverseer.ui.support.controls.TableUtils.setTableColumnRenderer(this.table, NotesTableModel.iHexNo, new HexNumberCellRenderer(this.tableModel));

        JPanel p = tlb.getPanel();

        //MultilineTableCellRenderer r = new MultilineTableCellRenderer();
        //r.setWrapStyleWord(true);
        //r.setLineWrap(true);
//        TextAreaRenderer r = new TextAreaRenderer();
//        table.setDefaultRenderer(String.class, r);
//        table.setDefaultRenderer(Integer.class, r);
//
        //table.getColumnModel().getColumn(NotesTableModel.iText).setCellEditor(new TextAreaEditor());

        return p;
    }

    @Override
    public JPopupMenu getPopupMenu(boolean hasSelectedItem) {
    	if (!hasSelectedItem) return null;
        CommandGroup group = Application.instance().getActiveWindow().getCommandManager().createCommandGroup(
                "noteCommandGroup", new Object[] {new EditNoteCommand(), new DeleteNoteCommand()});
        return group.createPopupMenu();
    }

    class DeleteNoteCommand extends ActionCommand {

        @Override
		protected void doExecuteCommand() {
            int row = NotesListView.this.table.getSelectedRow();
            if (row >= 0) {
                //int idx = ((SortableTableModel) table.getModel()).getRowAt(row);
                int idx = ((ShuttleSortableTableModel)NotesListView.this.table.getModel()).convertSortedIndexToDataIndex(row);
                if (idx >= NotesListView.this.tableModel.getRowCount())
                    return;
                try {
                    Object obj = NotesListView.this.tableModel.getRow(idx);
                    Note note = (Note) obj;
                    NotesListView.this.getTurn().getContainer(TurnElementsEnum.Notes).removeItem(note);
                    JOApplication.publishEvent(LifecycleEventsEnum.ListviewRefreshItems, this, this);

                } catch (Exception exc) {

                }
            }
        }

    }

    class EditNoteCommand extends ActionCommand {

        @Override
		protected void doExecuteCommand() {
            int row = NotesListView.this.table.getSelectedRow();
            if (row >= 0) {
                //int idx = ((SortableTableModel) table.getModel()).getRowAt(row);
                int idx = ((ShuttleSortableTableModel)NotesListView.this.table.getModel()).convertSortedIndexToDataIndex(row);
                if (idx >= NotesListView.this.tableModel.getRowCount())
                    return;
                try {
                    Object obj = NotesListView.this.tableModel.getRow(idx);
                    Note note = (Note) obj;
                    new AddEditNoteCommand(note,NotesListView.this.gameHolder).execute();
                } catch (Exception exc) {
                    exc.printStackTrace();
                }
            }
        }

    }

    @Override
	protected AbstractListViewFilter[][] getFilters() {
        return new AbstractListViewFilter[][]{
                NationFilter.createNationFilters(),
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
