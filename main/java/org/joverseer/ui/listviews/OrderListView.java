package org.joverseer.ui.listviews;

import org.joverseer.game.TurnElementsEnum;
import org.joverseer.game.Game;
import org.joverseer.metadata.GameMetadata;
import org.joverseer.metadata.orders.OrderMetadata;
import org.joverseer.support.GameHolder;
import org.joverseer.support.Container;
import org.joverseer.domain.Order;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.command.ActionCommand;
import org.springframework.richclient.command.CommandGroup;
import org.springframework.richclient.list.ComboBoxListModelAdapter;
import org.springframework.richclient.list.SortedListModel;
import org.springframework.richclient.table.BeanTableModel;
import org.springframework.richclient.table.SortableTableModel;
import org.springframework.binding.value.support.ListListModel;
import org.springframework.context.ApplicationEvent;
import org.joverseer.domain.Character;
import org.joverseer.ui.support.JOverseerEvent;
import org.joverseer.ui.LifecycleEventsEnum;

import java.util.ArrayList;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;


public class OrderListView extends ItemListView {
    ActionCommand deleteOrderAction = new DeleteOrderAction();
    
    public OrderListView() {
        super(TurnElementsEnum.Character, OrderTableModel.class);
    }

    protected int[] columnWidths() {
        return new int[]{64, 64, 96, 170};
    }

    protected void setItems() {
        Game g = ((GameHolder) Application.instance().getApplicationContext().getBean("gameHolder")).getGame();
        if (!Game.isInitialized(g)) return;
        Container items = g.getTurn().getContainer(turnElementType);
        ArrayList orders = new ArrayList();
        for (Character c : (ArrayList<Character>)items.getItems()) {
            for (Order o : c.getOrders()) {
                if (o.isBlank()) continue;
                orders.add(o);
            }
        }
        tableModel.setRows(orders);
    }
    
    

    public void onApplicationEvent(ApplicationEvent applicationEvent) {
        if (applicationEvent instanceof JOverseerEvent) {
            JOverseerEvent e = (JOverseerEvent)applicationEvent;
            if (e.getEventType().equals(LifecycleEventsEnum.SelectedTurnChangedEvent.toString())) {
                setItems();
            } else if (e.getEventType().equals(LifecycleEventsEnum.SelectedHexChangedEvent.toString())) {
                setItems();
            } else if (e.getEventType().equals(LifecycleEventsEnum.GameChangedEvent.toString())) {
                TableColumn noAndCodeColumn = table.getColumnModel().getColumn(2);
                Game g = ((GameHolder) Application.instance().getApplicationContext().getBean("gameHolder")).getGame();
                ListListModel orders = new ListListModel();
                if (Game.isInitialized(g)) {
                    GameMetadata gm = g.getMetadata();
                    Container orderMetadata = gm.getOrders();
                    for (OrderMetadata om : (ArrayList<OrderMetadata>)orderMetadata.getItems()) {
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
            int row =  table.getSelectedRow();
            if (row >= 0) {
                int idx = ((SortableTableModel)table.getModel()).convertSortedIndexToDataIndex(row);
                if (idx >= tableModel.getRowCount()) return;
                try {
                    Object obj = tableModel.getRow(idx);
                    Order order = (Order)obj;
                    order.clear();
                    ((BeanTableModel)table.getModel()).fireTableDataChanged();
                }
                catch (Exception exc) {
                    
                }
            }
        }
        
    }
    
    
}
