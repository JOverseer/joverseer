package org.joverseer.ui.listviews;

import org.joverseer.game.TurnElementsEnum;
import org.joverseer.game.Game;
import org.joverseer.metadata.GameMetadata;
import org.joverseer.metadata.orders.OrderMetadata;
import org.joverseer.support.GameHolder;
import org.joverseer.support.Container;
import org.joverseer.domain.Character;
import org.joverseer.domain.Order;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.command.ActionCommand;
import org.springframework.richclient.command.CommandGroup;
import org.springframework.richclient.layout.TableLayoutBuilder;
import org.springframework.richclient.list.ComboBoxListModelAdapter;
import org.springframework.richclient.list.SortedListModel;
import org.springframework.richclient.table.BeanTableModel;
import org.springframework.richclient.table.SortableTableModel;
import org.springframework.binding.value.support.ListListModel;
import org.springframework.context.ApplicationEvent;
import org.joverseer.domain.Character;
import org.joverseer.ui.support.JOverseerEvent;
import org.joverseer.ui.LifecycleEventsEnum;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;


public class OrderListView extends ItemListView {

    ActionCommand deleteOrderAction = new DeleteOrderAction();
    JComboBox combo;

    public OrderListView() {
        super(TurnElementsEnum.Character, OrderTableModel.class);
    }

    protected int[] columnWidths() {
        return new int[] {64, 64, 96, 170};
    }

    protected void setItems() {
        Game g = ((GameHolder) Application.instance().getApplicationContext().getBean("gameHolder")).getGame();
        if (!Game.isInitialized(g))
            return;
        Container items = g.getTurn().getContainer(turnElementType);
        ArrayList orders = new ArrayList();
        OrderFilter f = (OrderFilter)combo.getSelectedItem();
        if (f != null) { 
            for (Character c : (ArrayList<Character>) items.getItems()) {
                if (!f.acceptCharacter(c)) continue;
                for (Order o : c.getOrders()) {
                    orders.add(o);
                }
            }
        }
        tableModel.setRows(orders);
    }

    private ArrayList<OrderFilter> createOrderFilterList() {
        ArrayList<OrderFilter> filterList = new ArrayList<OrderFilter>();
        OrderFilter f = new OrderFilter("All characters with orders") {
            public boolean acceptCharacter(Character c) {
                return c.getX() > 0 && (!c.getOrders()[0].isBlank() || !c.getOrders()[1].isBlank());
            }
        };
        filterList.add(f);

        if (GameHolder.hasInitializedGame()) {
            Game g = GameHolder.instance().getGame();
            GameMetadata gm = g.getMetadata();
            for (int i = 1; i < 26; i++) {
                f = new OrderFilter(gm.getNationByNum(i).getName()) {

                    public boolean acceptCharacter(Character c) {
                        Game g = GameHolder.instance().getGame();
                        GameMetadata gm = g.getMetadata();
                        return c.getX() > 0 && gm.getNationByNum(c.getNationNo()).getName().equals(getDescription());
                    }
                };
                filterList.add(f);
            }
        }
        return filterList;
    }

    protected void setFilters() {        
       combo.removeAllItems();
       ArrayList<OrderFilter> filterList = createOrderFilterList();
       for (OrderFilter f : filterList) {
           combo.addItem(f);
       }
    }

    protected JComponent createControlImpl() {
        JComponent tableComp = super.createControlImpl();
        TableLayoutBuilder tlb = new TableLayoutBuilder();
        tlb.cell(combo = new JComboBox(), "align=left");
        combo.setPreferredSize(new Dimension(200, 24));
        combo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setItems();
            }
        });
        tlb.row();
        tlb.cell(tableComp);
        tlb.row();
        return tlb.getPanel();
    }

    public void onApplicationEvent(ApplicationEvent applicationEvent) {
        if (applicationEvent instanceof JOverseerEvent) {
            JOverseerEvent e = (JOverseerEvent) applicationEvent;
            if (e.getEventType().equals(LifecycleEventsEnum.SelectedTurnChangedEvent.toString())) {
                setItems();
            } else if (e.getEventType().equals(LifecycleEventsEnum.SelectedHexChangedEvent.toString())) {
                setItems();
            } else if (e.getEventType().equals(LifecycleEventsEnum.GameChangedEvent.toString())) {
                setFilters();
                TableColumn noAndCodeColumn = table.getColumnModel().getColumn(2);
                Game g = ((GameHolder) Application.instance().getApplicationContext().getBean("gameHolder")).getGame();
                ListListModel orders = new ListListModel();
                orders.add(Order.NA);
                if (Game.isInitialized(g)) {
                    GameMetadata gm = g.getMetadata();
                    Container orderMetadata = gm.getOrders();
                    for (OrderMetadata om : (ArrayList<OrderMetadata>) orderMetadata.getItems()) {
                        orders.add(om.getNumber() + " " + om.getCode());
                    }
                }
                SortedListModel slm = new SortedListModel(orders);

                JComboBox comboBox = new JComboBox(new ComboBoxListModelAdapter(slm));
                noAndCodeColumn.setCellEditor(new DefaultCellEditor(comboBox));
                setItems();
            } else if (e.getEventType().equals(LifecycleEventsEnum.OrderChangedEvent.toString())) {
                setItems();
            }
        }
    }


    public JPopupMenu getPopupMenu() {
        CommandGroup group = Application.instance().getActiveWindow().getCommandManager().createCommandGroup(
                "orderCommandGroup", new Object[] {deleteOrderAction});
        return group.createPopupMenu();
    }

    private class DeleteOrderAction extends ActionCommand {

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
                } catch (Exception exc) {

                }
            }
        }

    }

    private abstract class OrderFilter {

        String description;

        public abstract boolean acceptCharacter(Character c);

        public OrderFilter(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String toString() {
            return getDescription();
        }
    }
}
