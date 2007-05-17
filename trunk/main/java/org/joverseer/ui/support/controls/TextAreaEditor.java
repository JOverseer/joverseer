package org.joverseer.ui.support.controls;

import java.awt.Component;
import java.util.EventObject;
import java.util.Vector;

import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.table.TableCellEditor;


public class TextAreaEditor extends AbstractCellEditor implements TableCellEditor
{
        protected JTextArea myEditor;
        protected JScrollPane myScrollPane;
        
        public TextAreaEditor() {
                myEditor = new JTextArea();
                myEditor.setWrapStyleWord(true);
                myEditor.setLineWrap(true);
                myEditor.setOpaque(true);
                myEditor.setRows(3);
                myScrollPane = new JScrollPane(myEditor);
                myScrollPane.setBorder(BorderFactory.createEmptyBorder());
                myScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
                myScrollPane.getVerticalScrollBar().setFocusable(false);
                myScrollPane.getHorizontalScrollBar().setFocusable(false);
        }

        public Component getTableCellEditorComponent(JTable table, Object value,
                        boolean isSelected, int row, int column) 
        {
                myEditor.setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
                myEditor.setForeground(isSelected ? table.getSelectionForeground() : table.getForeground());
                myEditor.setText(value==null ? "" : value.toString());

                myScrollPane.setBounds(0,0,table.getColumnModel().getColumn(column).getWidth(), 0);
                int rowHeight = table.getRowHeight(row);
                int thisHeight = myScrollPane.getPreferredSize().height+2;
                if (thisHeight>rowHeight) table.setRowHeight(row, thisHeight);
                
                return myScrollPane;
        }

        public Object getCellEditorValue() {
                return myEditor.getText();
        }       
}