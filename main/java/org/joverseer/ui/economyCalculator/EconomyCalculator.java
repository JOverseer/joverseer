package org.joverseer.ui.economyCalculator;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.richclient.application.support.AbstractView;
import org.springframework.richclient.layout.GridBagLayoutBuilder;
import org.springframework.richclient.layout.TableLayoutBuilder;


public class EconomyCalculator  extends AbstractView implements ApplicationListener {
    JTable marketTable;
    JTable economyTable;
    JTable pcTable;
    JComboBox nationCombo;
    
    public void onApplicationEvent(ApplicationEvent arg0) {
    }
    
    protected JComponent createControl() {
        TableLayoutBuilder lb = new TableLayoutBuilder();
        
        lb.cell(nationCombo = new JComboBox());
        nationCombo.setPreferredSize(new Dimension(200, 24));
        lb.row();
        
        MarketTableModel mtm = new MarketTableModel();
        marketTable = new JTable(mtm);
        marketTable.getTableHeader().setPreferredSize(new Dimension(400, 16));
        for (int i=0; i<mtm.getColumnCount(); i++) {
            marketTable.getColumnModel().getColumn(i).setPreferredWidth(mtm.getColumnWidth(i));
        }
        marketTable.setDefaultRenderer(Integer.class, new ColorRenderer());
        JScrollPane scp = new JScrollPane(marketTable);
        scp.setPreferredSize(new Dimension(400, 400));
        
        lb.cell(scp);
        
        lb.row();
        
        return lb.getPanel();
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
