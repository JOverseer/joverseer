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
    }

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
            int row, int column) {

        if (isSelected) {
            setBackground(getSelectionColor());
            setForeground(getSelectedTextColor());
        } else {
            setBackground(Color.white);
            setForeground(Color.black);
        }

        // Nun der Text rein:
        if (value == null) value = "";
        setText(value.toString());

        JScrollPane scp = new JScrollPane(this);
        scp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        scp.setBorder(BorderFactory.createEmptyBorder());

        int rowHeight = table.getRowHeight(row);
        int thisHeight = scp.getPreferredSize().height+2;
        if (thisHeight>rowHeight) table.setRowHeight(row, thisHeight);
        return scp;
    }
}
