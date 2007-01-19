package org.joverseer.ui.economyCalculator;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;

import org.joverseer.domain.NationEconomy;
import org.joverseer.domain.PopulationCenter;
import org.joverseer.game.Game;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.metadata.domain.Nation;
import org.joverseer.support.GameHolder;
import org.joverseer.ui.LifecycleEventsEnum;
import org.joverseer.ui.support.JOverseerEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.richclient.application.support.AbstractView;
import org.springframework.richclient.layout.TableLayoutBuilder;
import org.springframework.richclient.table.BeanTableModel;
import org.springframework.richclient.table.TableUtils;


public class EconomyCalculator  extends AbstractView implements ApplicationListener {
    JTable marketTable;
    JTable totalsTable;
    JTable pcTable;
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
        
        lb.cell(nationCombo = new JComboBox(), "align=left");
        nationCombo.setPreferredSize(new Dimension(200, 24));
        nationCombo.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                Game g = GameHolder.instance().getGame();
                Nation n = g.getMetadata().getNationByName(nationCombo.getSelectedItem().toString());
                ((MarketTableModel)marketTable.getModel()).setSelectedNationNo(n.getNumber());
                ((EconomyTotalsTableModel)totalsTable.getModel()).setSelectedNationNo(n.getNumber());
                ((AbstractTableModel)marketTable.getModel()).fireTableDataChanged();
                ((AbstractTableModel)totalsTable.getModel()).fireTableDataChanged();
                refreshPcs(n.getNumber());
            }
            
        });
        lb.row();
        
        MarketTableModel mtm = new MarketTableModel();
        marketTable = new JTable(mtm);
        marketTable.getTableHeader().setPreferredSize(new Dimension(400, 16));
        marketTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        for (int i=0; i<mtm.getColumnCount(); i++) {
            marketTable.getColumnModel().getColumn(i).setPreferredWidth(mtm.getColumnWidth(i));
        }
        marketTable.setDefaultRenderer(Integer.class, new ColorRenderer());
        marketTable.setBackground(Color.white);
        JScrollPane scp = new JScrollPane(marketTable);
        scp.setPreferredSize(new Dimension(600, 200));
        scp.getViewport().setBackground(Color.white);
        scp.getViewport().setOpaque(true);
        lb.cell(scp);
        
        lb.row();
        
        EconomyTotalsTableModel ettm = new EconomyTotalsTableModel();
        totalsTable = new JTable(ettm);
        totalsTable.getTableHeader().setVisible(false);
        for (int i=0; i<ettm.getColumnCount(); i++) {
            totalsTable.getColumnModel().getColumn(i).setPreferredWidth(ettm.getColumnWidth(i));
        }
        totalsTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        totalsTable.setBackground(Color.white);
        scp = new JScrollPane(totalsTable);
        scp.setPreferredSize(new Dimension(600, 100));
        scp.getViewport().setBackground(Color.white);
        scp.getViewport().setOpaque(true);
        lb.cell(scp);
        
        lb.row();
        
        lostPopsTableModel = new LostPopsTableModel();
        //pcTable = new JTable(lostPopsTableModel);
        pcTable = TableUtils.createStandardSortableTable(lostPopsTableModel);
        pcTable.setBackground(Color.white);
        pcTable.setDefaultRenderer(Boolean.class, totalsTable.getDefaultRenderer(Boolean.class));
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
                if (pc.getNationNo() == nationNo) {
                    items.add(pc);
                }
            }
        }
        lostPopsTableModel.setRows(items);
        lostPopsTableModel.fireTableDataChanged();
    }


    
    public class ColorRenderer extends DefaultTableCellRenderer {
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
            if (!isSelected) {
                c.setBackground(rowColors[row]);
            }
            ((JLabel)c).setHorizontalAlignment(JLabel.RIGHT);
            return this;
        }
    }


}

