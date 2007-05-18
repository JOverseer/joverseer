package org.joverseer.ui.support.controls;

import java.awt.Color;
import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.TableCellRenderer;


public class TextAreaRenderer extends JTextArea implements TableCellRenderer {

    public TextAreaRenderer() {
        super();
        setWrapStyleWord(true);
        setLineWrap(true);
    }

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
            int row, int column) {
        JTextArea ta = new JTextArea();
        ta.setWrapStyleWord(true);
        ta.setLineWrap(true);
        if (isSelected) {
            ta.setBackground(getSelectionColor());
            ta.setForeground(getSelectedTextColor());
        } else {
            ta.setBackground(Color.white);
            ta.setForeground(Color.black);
        }

        // Nun der Text rein:
        if (value == null) value = "";
        
        ta.setText(value.toString());

        JScrollPane scp = new JScrollPane(ta);
        scp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        //scp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scp.setBorder(BorderFactory.createEmptyBorder());

        int rowHeight = table.getRowHeight(row);
        int thisHeight = this.getPreferredSize().height;
        if (thisHeight>rowHeight) table.setRowHeight(row, thisHeight);
        return scp;
    }
}
