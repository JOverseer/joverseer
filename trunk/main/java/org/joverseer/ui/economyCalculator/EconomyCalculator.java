package org.joverseer.ui.economyCalculator;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;

import org.joverseer.domain.EconomyCalculatorData;
import org.joverseer.domain.NationEconomy;
import org.joverseer.domain.PopulationCenter;
import org.joverseer.domain.PopulationCenterSizeEnum;
import org.joverseer.game.Game;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.metadata.domain.Nation;
import org.joverseer.support.GameHolder;
import org.joverseer.ui.LifecycleEventsEnum;
import org.joverseer.ui.support.GraphicUtils;
import org.joverseer.ui.support.JOverseerEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.richclient.application.support.AbstractView;
import org.springframework.richclient.layout.TableLayoutBuilder;
import org.springframework.richclient.table.BeanTableModel;
import org.springframework.richclient.table.TableUtils;

import com.sun.corba.se.spi.legacy.connection.GetEndPointInfoAgainException;


public class EconomyCalculator  extends AbstractView implements ApplicationListener {
    JTable marketTable;
    JTable totalsTable;
    JTable pcTable;
    JCheckBox sellBonus;
    JComboBox nationCombo;
    BeanTableModel lostPopsTableModel;
    
    public void onApplicationEvent(ApplicationEvent applicationEvent) {
        if (applicationEvent instanceof JOverseerEvent) {
            JOverseerEvent e = (JOverseerEvent)applicationEvent;
            if (e.getEventType().equals(LifecycleEventsEnum.EconomyCalculatorUpdate.toString()) ||
                    e.getEventType().equals(LifecycleEventsEnum.SelectedTurnChangedEvent.toString())) {
                ((AbstractTableModel)marketTable.getModel()).fireTableDataChanged();
                ((AbstractTableModel)totalsTable.getModel()).fireTableDataChanged();
            } else if (e.getEventType().equals(LifecycleEventsEnum.GameChangedEvent.toString())) {
                loadNationCombo();
            }

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
        marketTable = new JTable(mtm);
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
        totalsTable = new JTable(ettm);
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
        org.joverseer.ui.support.TableUtils.setTableColumnWidths(pcTable, getLostPCColumWidths());
        scp = new JScrollPane(pcTable);
        scp.setPreferredSize(new Dimension(600, 120));
        scp.getViewport().setBackground(Color.white);
        scp.getViewport().setOpaque(true);
        lb.cell(scp); 
        
        lb.row();
        
        JPanel p = lb.getPanel();
        p.setBackground(Color.white);
        p.setPreferredSize(new Dimension(600, 1000));
        scp = new JScrollPane(p);
        scp.setPreferredSize(new Dimension(600, 1000));
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
                Color.decode("#99FF99"), 
                Color.decode("#99FF99"), 
                Color.decode("#FFCCAA"), 
                Color.decode("#FFCCAA"), 
                Color.decode("#99FF99"), 
                Color.white, 
                Color.white, 
                Color.decode("#99FF99"), 
                Color.decode("#99FF99"), 
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
            if (!isSelected) {
                if (row == 0 && column == 5) { // orders cost
                    lbl.setBackground(Color.decode("#99FF99"));
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

