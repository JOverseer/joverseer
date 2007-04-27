package org.joverseer.ui.economyCalculator;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;

import org.joverseer.domain.Character;
import org.joverseer.domain.NationEconomy;
import org.joverseer.domain.PopulationCenter;
import org.joverseer.domain.PopulationCenterSizeEnum;
import org.joverseer.game.Game;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.metadata.domain.Nation;
import org.joverseer.support.GameHolder;
import org.joverseer.tools.orderCostCalculator.OrderCostCalculator;
import org.joverseer.ui.LifecycleEventsEnum;
import org.joverseer.ui.support.GraphicUtils;
import org.joverseer.ui.support.JOverseerEvent;
import org.joverseer.ui.support.controls.JOverseerTable;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.richclient.application.support.AbstractView;
import org.springframework.richclient.layout.TableLayoutBuilder;
import org.springframework.richclient.table.BeanTableModel;
import org.springframework.richclient.table.TableUtils;


public class EconomyCalculator extends AbstractView implements ApplicationListener {
    
    JLabel autocalcOrderCost;
    JTable marketTable;
    JTable totalsTable;
    JTable pcTable;
    JCheckBox sellBonus;
    JComboBox nationCombo;
    JLabel marketLimitWarning;
    JLabel taxIncrease;
    BeanTableModel lostPopsTableModel;
    
    public void onApplicationEvent(ApplicationEvent applicationEvent) {
        if (applicationEvent instanceof JOverseerEvent) {
            JOverseerEvent e = (JOverseerEvent)applicationEvent;
            if (e.getEventType().equals(LifecycleEventsEnum.EconomyCalculatorUpdate.toString())) {
                ((AbstractTableModel)marketTable.getModel()).fireTableDataChanged();
                ((AbstractTableModel)totalsTable.getModel()).fireTableDataChanged();
                refreshMarketLimitWarning();
                refreshTaxIncrease();
                refreshAutocalcOrderCost();
            } else if (e.getEventType().equals(LifecycleEventsEnum.SelectedTurnChangedEvent.toString())) {
                loadNationCombo();
                try {
                    ((AbstractTableModel)marketTable.getModel()).fireTableDataChanged();
                    ((AbstractTableModel)totalsTable.getModel()).fireTableDataChanged();
                    refreshMarketLimitWarning();
                    refreshTaxIncrease();
                    refreshAutocalcOrderCost();
                }
                catch (Exception exc) {
                    exc.printStackTrace();
                }
            } else if (e.getEventType().equals(LifecycleEventsEnum.GameChangedEvent.toString())) {
                loadNationCombo();
//                if (GameHolder.hasInitializedGame() && GameHolder.instance().getGame().getTurn() != null) {
//                    refreshMarketLimitWarning();
//                    refreshTaxIncrease();
//                    refreshAutocalcOrderCost();
//                }
            } else if  (e.getEventType().equals(LifecycleEventsEnum.OrderChangedEvent.toString())) {
                refreshAutocalcOrderCost();
            }

        }
    }
    
    public int getMarketLimitWarningThreshhold() {
        return 20000;
    }
    
    private void refreshMarketLimitWarning() {
        int marketProfits = ((MarketTableModel)marketTable.getModel()).getEconomyCalculatorData().getMarketProfits();
        if (marketProfits >= getMarketLimitWarningThreshhold()) {
            marketLimitWarning.setText("Market limit warning!");
            marketLimitWarning.setVisible(true);
        } else {
            marketLimitWarning.setVisible(false);
        }
    }
    
    private void refreshTaxIncrease() {
        int taxIncreaseAmt = ((EconomyTotalsTableModel)totalsTable.getModel()).getTaxIncrease();
        if (taxIncreaseAmt == 0) {
            taxIncrease.setVisible(false);
        } else {
            taxIncrease.setVisible(true);
            taxIncrease.setText("Your taxes will go up by " + taxIncreaseAmt + "%!");
        }
    }
    
    private void loadNationCombo() {
        nationCombo.removeAllItems();
        Game g = GameHolder.instance().getGame();
        if (!Game.isInitialized(g)) return;
        if (g.getTurn() == null) return;
        for (Nation n : (ArrayList<Nation>)g.getMetadata().getNations()) {
            NationEconomy ne = (NationEconomy)g.getTurn().getContainer(TurnElementsEnum.NationEconomy).findFirstByProperty("nationNo", n.getNumber());
            // load only nations for which economy has been imported
            if (ne == null) continue;
            nationCombo.addItem(n.getName());
        }
        if (nationCombo.getItemCount() > 0) {
            nationCombo.setSelectedIndex(0);
        }
    }
    
    private void refreshAutocalcOrderCost() {
        int totalCost = 0;
        OrderCostCalculator calc = new OrderCostCalculator();
        for (Character c : (ArrayList<Character>)GameHolder.instance().getGame().getTurn().getContainer(TurnElementsEnum.Character).findAllByProperty("nationNo", getSelectedNationNo())) {
            for (int i=0; i<2; i++) {
                if (c.getOrders()[i].isBlank()) continue;
                int no = c.getOrders()[i].getOrderNo();
                if (no == 320 || no == 315 || no == 310 || no == 325) continue;
                int cost = calc.getOrderCost(c.getOrders()[i]);
                if (cost > 0) {
                    totalCost += cost;
                }
            }
        }
        autocalcOrderCost.setText(String.valueOf(totalCost));
    }
    
    private int getSelectedNationNo() {
        Game g = GameHolder.instance().getGame();
        if (nationCombo.getSelectedItem() == null) return -1;
        Nation n = g.getMetadata().getNationByName(nationCombo.getSelectedItem().toString());
        return n.getNumber();
    }
    
    protected JComponent createControl() {
        TableLayoutBuilder lb = new TableLayoutBuilder();

        lb.relatedGapRow();
        
        lb.separator("Nation");
        lb.row();
        lb.relatedGapRow();
        
        lb.cell(nationCombo = new JComboBox(), "align=left");
        nationCombo.setPreferredSize(new Dimension(200, 24));
        nationCombo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Game g = GameHolder.instance().getGame();
                if (nationCombo.getSelectedItem() == null) return;
                Nation n = g.getMetadata().getNationByName(nationCombo.getSelectedItem().toString());
                ((MarketTableModel)marketTable.getModel()).setSelectedNationNo(n.getNumber());
                ((EconomyTotalsTableModel)totalsTable.getModel()).setSelectedNationNo(n.getNumber());
                ((AbstractTableModel)marketTable.getModel()).fireTableDataChanged();
                ((AbstractTableModel)totalsTable.getModel()).fireTableDataChanged();
                refreshPcs(n.getNumber());
                refreshMarketLimitWarning();
                refreshAutocalcOrderCost();
                refreshTaxIncrease();
                sellBonus.setSelected(((EconomyTotalsTableModel)totalsTable.getModel()).getEconomyCalculatorData().getSellBonus());
            }
        });
        lb.row();
        
        lb.cell(sellBonus = new JCheckBox(), "align=left");
        sellBonus.setText("sell bonus: ");
        sellBonus.setHorizontalTextPosition(JCheckBox.LEFT);
        sellBonus.setBackground(Color.white);
        sellBonus.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Game g = GameHolder.instance().getGame();
                if (!Game.isInitialized(g)) return;
                if (nationCombo.getSelectedItem() == null) return;
                if (((MarketTableModel)marketTable.getModel()).getEconomyCalculatorData() == null) return;
                ((MarketTableModel)marketTable.getModel()).getEconomyCalculatorData().setSellBonus(sellBonus.isSelected());
                ((AbstractTableModel)marketTable.getModel()).fireTableDataChanged();
                ((AbstractTableModel)totalsTable.getModel()).fireTableDataChanged();
            }
        });
        lb.row();

        lb.relatedGapRow();

        lb.separator("Market");
        lb.row();
        lb.relatedGapRow();

        MarketTableModel mtm = new MarketTableModel();
        marketTable = new JOverseerTable(mtm);
        
        marketTable.getTableHeader().setPreferredSize(new Dimension(400, 16));
        marketTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        for (int i=0; i<mtm.getColumnCount(); i++) {
            marketTable.getColumnModel().getColumn(i).setPreferredWidth(mtm.getColumnWidth(i));
        }
        marketTable.setDefaultRenderer(Integer.class, new MarketRenderer());
        marketTable.setDefaultRenderer(String.class, new MarketRenderer());
        marketTable.setBackground(Color.white);
        JScrollPane scp = new JScrollPane(marketTable);
        scp.setPreferredSize(new Dimension(600, 200));
        scp.getViewport().setBackground(Color.white);
        scp.getViewport().setOpaque(true);
        lb.cell(scp);
        
        lb.row();
        lb.relatedGapRow();
        
        lb.separator("Totals");
        lb.row();
        lb.relatedGapRow();
        
        EconomyTotalsTableModel ettm = new EconomyTotalsTableModel();
        totalsTable = new JOverseerTable(ettm); 
        totalsTable.getTableHeader().setVisible(false);
        for (int i=0; i<ettm.getColumnCount(); i++) {
            totalsTable.getColumnModel().getColumn(i).setPreferredWidth(ettm.getColumnWidth(i));
        }
        totalsTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        totalsTable.setDefaultRenderer(String.class, new TotalsRenderer());
        totalsTable.setDefaultRenderer(Integer.class, new TotalsRenderer());
        totalsTable.setBackground(Color.white);
        scp = new JScrollPane(totalsTable);
        scp.setPreferredSize(new Dimension(600, 100));
        scp.getViewport().setBackground(Color.white);
        scp.getViewport().setOpaque(true);
        lb.cell(scp);
        
        TableLayoutBuilder tlb = new TableLayoutBuilder();
        tlb.cell(new JLabel("Autocalc order cost: "), "colspec=left:100px");
        tlb.gapCol();
        autocalcOrderCost = new JLabel("0");
        tlb.cell(autocalcOrderCost, "align=left");
        
        tlb.row();
        tlb.relatedGapRow();
        
        JButton btn = new JButton("<- update");
        btn.setPreferredSize(new Dimension(100, 24));
        btn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                ((EconomyTotalsTableModel)totalsTable.getModel()).setOrdersCost(Integer.parseInt(autocalcOrderCost.getText()));
                ((AbstractTableModel)totalsTable.getModel()).fireTableDataChanged();                
            }
        });
        tlb.cell(btn);
        tlb.cell(new JLabel());
        JPanel pnl = tlb.getPanel();
        pnl.setBackground(Color.white);
        lb.gapCol();
        lb.cell(pnl, "valign=top");
        lb.row();
        
        marketLimitWarning = new JLabel("Market limit warning!");
        marketLimitWarning.setFont(GraphicUtils.getFont(marketLimitWarning.getFont().getName(), Font.BOLD, marketLimitWarning.getFont().getSize()));
        marketLimitWarning.setForeground(Color.red);
        lb.cell(marketLimitWarning);
        lb.row();
        lb.relatedGapRow();
        
        taxIncrease = new JLabel("Your taxes will go up.");
        taxIncrease.setFont(GraphicUtils.getFont(taxIncrease.getFont().getName(), Font.BOLD, taxIncrease.getFont().getSize()));
        taxIncrease.setForeground(Color.red);
        lb.cell(taxIncrease);
        lb.row();
        
        lb.relatedGapRow();
        lb.separator("Pop Centers expected to be lost this turn");
        lb.row();
        lb.relatedGapRow();
        
        lostPopsTableModel = new LostPopsTableModel();
        //pcTable = new JTable(lostPopsTableModel);
        pcTable = TableUtils.createStandardSortableTable(lostPopsTableModel);
        pcTable.setBackground(Color.white);
        pcTable.setDefaultRenderer(Boolean.class, totalsTable.getDefaultRenderer(Boolean.class));
        pcTable.setDefaultEditor(Boolean.class, totalsTable.getDefaultEditor(Boolean.class));
        org.joverseer.ui.support.controls.TableUtils.setTableColumnWidths(pcTable, getLostPCColumWidths());
        scp = new JScrollPane(pcTable);
        scp.setPreferredSize(new Dimension(600, 120));
        scp.getViewport().setBackground(Color.white);
        scp.getViewport().setOpaque(true);
        lb.cell(scp); 
        
        lb.row();
        
        JPanel p = lb.getPanel();
        p.setBackground(Color.white);
        scp = new JScrollPane(p);
        scp.getViewport().setBackground(Color.white);
        scp.getViewport().setOpaque(true);
        return scp;
    }
    
    public void refreshPcs(int nationNo) {
        ArrayList items = new ArrayList();
        Game g = GameHolder.instance().getGame();
        if (Game.isInitialized(g) && g.getTurn() != null) { 
            for (PopulationCenter pc : (ArrayList<PopulationCenter>)g.getTurn().getContainer(TurnElementsEnum.PopulationCenter).getItems()) {
                if (pc.getNationNo() == nationNo && pc.getSize() != PopulationCenterSizeEnum.ruins) {
                    items.add(pc);
                }
            }
        }
        lostPopsTableModel.setRows(items);
        lostPopsTableModel.fireTableDataChanged();
    }
    
    public class MarketRenderer extends DefaultTableCellRenderer {
        Color[] rowColors = new Color[] {
                Color.decode("#ADD3A6"), 
                Color.decode("#ADD3A6"), 
                Color.decode("#FFCCAA"), 
                Color.decode("#FFCCAA"), 
                Color.decode("#ADD3A6"), 
                Color.white, 
                Color.white, 
                Color.decode("#ADD3A6"), 
                Color.decode("#ADD3A6"), 
                Color.white, 
                Color.lightGray
                };
        
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (!isSelected && column>0) {
                c.setBackground(rowColors[row]);
            }
            JLabel lbl = ((JLabel)c);
            if (hasFocus) {
            	lbl.setBorder(BorderFactory.createLineBorder(Color.red, 1));
            }
            lbl.setHorizontalAlignment(JLabel.RIGHT);
            
            return c;
        }
    }
    
    public class TotalsRenderer extends DefaultTableCellRenderer {
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            JLabel lbl = ((JLabel)c);
            lbl.setHorizontalAlignment(JLabel.RIGHT);
            if (row == 3 && column == 5 || row == 0 && column == 5) { // final gold or orders cost
                lbl.setFont(GraphicUtils.getFont(lbl.getFont().getName(), Font.BOLD, lbl.getFont().getSize()));
            } else {
                lbl.setFont(GraphicUtils.getFont(lbl.getFont().getName(), Font.PLAIN, lbl.getFont().getSize()));
            }
            if (row == 3 && column == 3 && !value.toString().equals("")) {
                int amount = Integer.parseInt(value.toString());
                if (amount >= getMarketLimitWarningThreshhold()) {
                    if (!isSelected) {
                        lbl.setBackground(Color.red);
                        return c;
                    }
                }
            }
            if (!isSelected) {
                if (row == 0 && column == 5 || row == 2 && column == 3) { // orders cost , gold production
                    lbl.setBackground(Color.decode("#ADD3A6"));
                } else {
                    lbl.setBackground(Color.white);
                }
            }
            return c;
        }
    }


    public int[] getLostPCColumWidths() {
        return new int[]{140, 64, 64, 64, 65, 96};
    }
    
}

