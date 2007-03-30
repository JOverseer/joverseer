package org.joverseer.ui.listviews;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Comparator;

import javax.swing.AbstractCellEditor;
import javax.swing.DefaultCellEditor;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;

import org.joverseer.domain.Character;
import org.joverseer.domain.CharacterDeathReasonEnum;
import org.joverseer.domain.Order;
import org.joverseer.domain.PlayerInfo;
import org.joverseer.game.Game;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.metadata.GameMetadata;
import org.joverseer.metadata.orders.OrderMetadata;
import org.joverseer.support.Container;
import org.joverseer.support.GameHolder;
import org.joverseer.tools.ordercheckerIntegration.OrderResult;
import org.joverseer.tools.ordercheckerIntegration.OrderResultContainer;
import org.joverseer.tools.ordercheckerIntegration.OrderResultTypeEnum;
import org.joverseer.ui.LifecycleEventsEnum;
import org.joverseer.ui.orderEditor.OrderEditor;
import org.joverseer.ui.support.GraphicUtils;
import org.joverseer.ui.support.JOverseerEvent;
import org.springframework.binding.value.support.ListListModel;
import org.springframework.context.ApplicationEvent;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.command.ActionCommand;
import org.springframework.richclient.command.CommandGroup;
import org.springframework.richclient.image.ImageSource;
import org.springframework.richclient.layout.TableLayoutBuilder;
import org.springframework.richclient.list.ComboBoxListModelAdapter;
import org.springframework.richclient.list.SortedListModel;
import org.springframework.richclient.table.BeanTableModel;
import org.springframework.richclient.table.ColumnToSort;
import org.springframework.richclient.table.SortOrder;
import org.springframework.richclient.table.SortableTableModel;
import org.springframework.richclient.table.renderer.BooleanTableCellRenderer;

import com.jidesoft.swing.AutoCompletionComboBox;


public class OrderListView extends ItemListView {

    ActionCommand deleteOrderAction = new DeleteOrderAction();
    ActionCommand editOrderAction = new EditOrderAction();
    JComboBox combo;

    public OrderListView() {
        super(TurnElementsEnum.Character, OrderTableModel.class);
    }

    protected int[] columnWidths() {
        return new int[] {48, 64, 64, 96, 170, 30, 120, 64};
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
                return c.getDeathReason().equals(CharacterDeathReasonEnum.NotDead) && c.getX() > 0 && (!c.getOrders()[0].isBlank() || !c.getOrders()[1].isBlank());
            }
        };
        filterList.add(f);

        if (GameHolder.hasInitializedGame()) {
            Game g = GameHolder.instance().getGame();
            GameMetadata gm = g.getMetadata();
            for (int i = 1; i < 26; i++) {
                PlayerInfo pi = (PlayerInfo)g.getTurn().getContainer(TurnElementsEnum.PlayerInfo).findFirstByProperty("nationNo", i);
                if (pi == null) continue;
                f = new OrderFilter(gm.getNationByNum(i).getName()) {
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
        table.setDefaultRenderer(Boolean.class, new BooleanTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Order o = (Order)tableModel.getRow(((SortableTableModel)table.getModel()).convertSortedIndexToDataIndex(row));
                if (GraphicUtils.canRenderOrder(o)) {
                    JCheckBox b = new JCheckBox();
                    b.setSelected((Boolean)value);
                    b.setHorizontalAlignment(JCheckBox.CENTER);
                    if (isSelected) {
                        b.setBackground(Color.gray);
                    } else {
                        b.setBackground(Color.white);
                    }
                    return b;
                } else {
                    JLabel b = new JLabel(" ");
                    if (isSelected) {
                        b.setBackground(Color.gray);
                    } else {
                        b.setBackground(Color.white);
                    }
                    return b;
                }
            }
            
        });
        // specialized renderer for the icon returned by the orderResultType virtual field
        table.setDefaultRenderer(ImageIcon.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                ImageIcon ico = (ImageIcon)value;
                JLabel lbl = new JLabel();
                lbl.setIcon(ico);
                if (ico != null) {
                    OrderResultContainer container = (OrderResultContainer)Application.instance().getApplicationContext().getBean("orderResultContainer");
                    int idx = ((SortableTableModel)table.getModel()).convertSortedIndexToDataIndex(row);
                    Object obj = tableModel.getRow(idx);
                    Order o = (Order)obj;
                    String txt = "";
                    for (OrderResult result : container.getResultsForOrder(o)) {
                        txt += (txt.equals("") ? "" : "") + "<li>" + result.getType().toString() + ": " + result.getMessage() + "</li>";
                    }
                    txt = "<html><body><lu>" + txt + "</lu></body></html>";
                    lbl.setToolTipText(txt);
                } else {
                    lbl.setToolTipText(null);
                    lbl.setText(" ");
                }
                lbl.setHorizontalAlignment(JLabel.CENTER);
                if (isSelected) {
                    lbl.setBackground(Color.gray);
                } else {
                    lbl.setBackground(Color.white);
                }
                return lbl;
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
                TableColumn noAndCodeColumn = table.getColumnModel().getColumn(3);
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

                //JComboBox comboBox = new JComboBox(new ComboBoxListModelAdapter(slm));
                JComboBox comboBox = new AutoCompletionComboBox(new ComboBoxListModelAdapter(slm));
                noAndCodeColumn.setCellEditor(new DefaultCellEditor(comboBox));
                setItems();
            } else if (e.getEventType().equals(LifecycleEventsEnum.OrderChangedEvent.toString())) {
                setItems();
            } else if (e.getEventType().equals(LifecycleEventsEnum.RefreshMapItems.toString())) {
                setItems();
            } else if (e.getEventType().equals(LifecycleEventsEnum.RefreshOrders.toString())) {
                setItems();
            }
        }
    }


    public JPopupMenu getPopupMenu() {
        CommandGroup group = Application.instance().getActiveWindow().getCommandManager().createCommandGroup(
                "orderCommandGroup", new Object[] {editOrderAction, deleteOrderAction});
        return group.createPopupMenu();
    }
    
    private class EditOrderAction extends ActionCommand {
        protected void doExecuteCommand() {
            int row = table.getSelectedRow();
            if (row >= 0) {
                int idx = ((SortableTableModel) table.getModel()).convertSortedIndexToDataIndex(row);
                if (idx >= tableModel.getRowCount())
                    return;
                try {
                    Object obj = tableModel.getRow(idx);
                    Order order = (Order) obj;
                    Application.instance().getApplicationContext().publishEvent(
                            new JOverseerEvent(LifecycleEventsEnum.EditOrderEvent.toString(), order, this));
                } catch (Exception exc) {

                }
            }
        }
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
    
    public ColumnToSort[] getDefaultSort() {
        return new ColumnToSort[]{new ColumnToSort(0, 0, SortOrder.ASCENDING),
                new ColumnToSort(0, 1, SortOrder.ASCENDING)};
    }
    
}
