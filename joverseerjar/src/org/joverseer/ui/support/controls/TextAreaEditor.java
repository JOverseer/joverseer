package org.joverseer.ui.support.controls;

import java.awt.Component;

import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.table.TableCellEditor;

/**
 * Cell Editor that uses a Text Area
 * 
 * Has various problems
 * 
 * @author Marios Skounakis
 */
//TODO Fix or delete
public class TextAreaEditor extends AbstractCellEditor implements TableCellEditor
{
        protected JTextArea myEditor;
        protected JScrollPane myScrollPane;
        
        public TextAreaEditor() {
                this.myEditor = new JTextArea();
                this.myEditor.setWrapStyleWord(true);
                this.myEditor.setLineWrap(true);
                this.myEditor.setOpaque(true);
                this.myEditor.setRows(3);
                this.myScrollPane = new JScrollPane(this.myEditor);
                this.myScrollPane.setBorder(BorderFactory.createEmptyBorder());
                this.myScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
                this.myScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
                this.myScrollPane.getVerticalScrollBar().setFocusable(false);
                this.myScrollPane.getHorizontalScrollBar().setFocusable(false);
        }

        @Override
		public Component getTableCellEditorComponent(JTable table, Object value,
                        boolean isSelected, int row, int column) 
        {
                this.myEditor.setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
                this.myEditor.setForeground(isSelected ? table.getSelectionForeground() : table.getForeground());
                this.myEditor.setText(value==null ? "" : value.toString());

                this.myScrollPane.setBounds(0,0,table.getColumnModel().getColumn(column).getWidth(), 30);
                int rowHeight = table.getRowHeight(row);
                int thisHeight = this.myScrollPane.getPreferredSize().height+2;
                if (thisHeight>rowHeight) table.setRowHeight(row, thisHeight);
                
                return this.myScrollPane;
        }

        @Override
		public Object getCellEditorValue() {
                return this.myEditor.getText();
        }       
}