package org.joverseer.ui.economyCalculator;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import org.joverseer.domain.EconomyCalculatorData;
import org.joverseer.domain.NationEconomy;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.support.GameHolder;
import org.joverseer.ui.LifecycleEventsEnum;
import org.joverseer.ui.support.JOverseerEvent;
import org.joverseer.ui.support.controls.JOverseerTable;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.richclient.application.support.AbstractView;
import org.springframework.richclient.layout.TableLayoutBuilder;


public class TeamEconomyView extends AbstractView implements ApplicationListener {
    JTable teamEconomyTable;
    TeamEconomyTableModel teamEconomyTableModel;
    JComboBox showProductAsCombo;
    
    protected JComponent createControl() {
        TableLayoutBuilder lb = new TableLayoutBuilder();

        lb.relatedGapRow();
        
        lb.separator("Team Economy");
        lb.row();
        lb.relatedGapRow();
        
        teamEconomyTableModel = new TeamEconomyTableModel();
        teamEconomyTable = new JOverseerTable(teamEconomyTableModel);
        
        teamEconomyTable.getTableHeader().setPreferredSize(new Dimension(400, 16));
        teamEconomyTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        for (int i=0; i<teamEconomyTableModel.getColumnCount(); i++) {
            teamEconomyTable.getColumnModel().getColumn(i).setPreferredWidth(teamEconomyTableModel.getColumnWidth(i));
        }
        teamEconomyTable.setDefaultRenderer(Integer.class, new TeamEconomyTableRenderer());
//        teamEconomyTable.setDefaultRenderer(String.class, new MarketRenderer());
        teamEconomyTable.setBackground(Color.white);
        JScrollPane scp = new JScrollPane(teamEconomyTable);
        scp.setPreferredSize(new Dimension(600, 250));
        scp.getViewport().setBackground(Color.white);
        scp.getViewport().setOpaque(true);
        lb.cell(scp);
        
        lb.row();
        lb.relatedGapRow();
        
        showProductAsCombo = new JComboBox(new String[]{
                TeamEconomyTableModel.PROD_TOTAL,
                TeamEconomyTableModel.PROD_GAIN,
                TeamEconomyTableModel.PROD_PRODUCTION,
                TeamEconomyTableModel.PROD_STORES
            });
        showProductAsCombo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                teamEconomyTableModel.setShowProductsAs(showProductAsCombo.getSelectedItem().toString());
                teamEconomyTableModel.fireTableDataChanged();
            }
        });
        showProductAsCombo.setPreferredSize(new Dimension(100, 20));
        
        TableLayoutBuilder tlb = new TableLayoutBuilder();
        tlb.cell(new JLabel("products: "));
        tlb.gapCol();
        tlb.cell(showProductAsCombo);
        
        lb.cell(tlb.getPanel(), "align=left");
        lb.relatedGapRow();
        
        return lb.getPanel();
    }
    
    public void refreshTableItems() {
        ArrayList<EconomyCalculatorData> ecds = new ArrayList<EconomyCalculatorData>();
        if (GameHolder.instance().hasInitializedGame()) {
            for (NationEconomy ne : (ArrayList<NationEconomy>)GameHolder.instance().getGame().getTurn().getContainer(TurnElementsEnum.NationEconomy).getItems()) {
                EconomyCalculatorData ecd = (EconomyCalculatorData)GameHolder.instance().getGame().getTurn().getContainer(TurnElementsEnum.EconomyCalucatorData).findFirstByProperty("nationNo", ne.getNationNo());
                if (ecd == null) {
                    ecd = new EconomyCalculatorData();
                    ecd.setNationNo(ne.getNationNo());
                    GameHolder.instance().getGame().getTurn().getContainer(TurnElementsEnum.EconomyCalucatorData).addItem(ecd);
                    
                }
                ecds.add(ecd);
            }
        }
        teamEconomyTableModel.setRows(ecds);
                
    }
    
    public void onApplicationEvent(ApplicationEvent applicationEvent) {
        if (applicationEvent instanceof JOverseerEvent) {
            JOverseerEvent e = (JOverseerEvent)applicationEvent;
            if (e.getEventType().equals(LifecycleEventsEnum.EconomyCalculatorUpdate.toString())) {
                teamEconomyTableModel.fireTableDataChanged();
            } else if (e.getEventType().equals(LifecycleEventsEnum.SelectedTurnChangedEvent.toString())) {
                try {
                    refreshTableItems();
                    teamEconomyTableModel.fireTableDataChanged();
                }
                catch (Exception exc) {
                    exc.printStackTrace();
                }
            } else if (e.getEventType().equals(LifecycleEventsEnum.GameChangedEvent.toString())) {
                refreshTableItems();
                teamEconomyTableModel.fireTableDataChanged();
            } else if  (e.getEventType().equals(LifecycleEventsEnum.OrderChangedEvent.toString())) {
                teamEconomyTableModel.fireTableDataChanged();
            }

        }
    }
    
    
    class TeamEconomyTableRenderer extends DefaultTableCellRenderer {
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel lbl = (JLabel)super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (column == TeamEconomyTableModel.iFinalGold) {
                Integer amt = (Integer)value;
                if (amt < 0) {
                    lbl.setForeground(Color.red);
                }
            } else if (column == TeamEconomyTableModel.iMarket) {
                Integer amt = (Integer)value;
                if (amt > EconomyCalculator.getMarketLimitWarningThreshhold()) {
                    lbl.setForeground(Color.red);
                }
//            } else if (column == teamEconomyTableModel.getBestNatSellIndex(row)) {
//                lbl.setForeground(Color.green);
//            } else if (column == teamEconomyTableModel.getSecondBestNatSellIndex(row)) {
//                lbl.setForeground(Color.green);
            } else if (column == TeamEconomyTableModel.iSurplus) {
                Integer amt = (Integer)value;
                if (amt < 0) {
                    lbl.setForeground(Color.red);
                }
            } else {
                if (isSelected) {
                    lbl.setForeground(Color.white);
                } else {
                    lbl.setForeground(Color.black);
                }
            }
            if (teamEconomyTableModel.getColumnClass(column) == Integer.class) {
                lbl.setHorizontalAlignment(JLabel.RIGHT);
            }
            return lbl;
        }
        
    }
}
