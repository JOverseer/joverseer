package org.joverseer.ui.listviews;

import java.awt.Color;
import java.awt.Component;
import java.util.Locale;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import org.joverseer.game.TurnElementsEnum;
import org.joverseer.ui.listviews.filters.NationFilter;
import org.joverseer.ui.listviews.renderers.AllegianceColorCellRenderer;
import org.springframework.context.MessageSource;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.table.BeanTableModel;


public class RelationsListView extends ItemListView {
    public RelationsListView() {
        super(TurnElementsEnum.NationRelation, RelationsTableModel.class);
    }
    
    
    protected JComponent createControlImpl() {
        JComponent c = super.createControlImpl();
        table.setDefaultRenderer(String.class, new RelationsTableCellRenderer(tableModel));
        return c;
    }



    protected int[] columnWidths() {
        return new int[]{64, 96, 
                        32, 32, 32, 32, 32, 
                        32, 32, 32, 32, 32,
                        32, 32, 32, 32, 32,
                        32, 32, 32, 32, 32,
                        32, 32, 32, 32, 32};
    }

    protected void setItems() {
        super.setItems();
        try {
            for (int i=1; i<26; i++) {
                table.getColumnModel().getColumn(i+1).setHeaderValue(tableModel.getColumnName(i+1));
            }
        } catch (Exception exc) {};
    }
    
    
    
    protected AbstractListViewFilter[][] getFilters() {
        return new AbstractListViewFilter[][]{NationFilter.createAllAndAllImportedNationFilters()};
    }



    public class RelationsTableCellRenderer extends AllegianceColorCellRenderer {

        public RelationsTableCellRenderer(BeanTableModel tableModel) {
			super(tableModel);
		}

		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (column < 2) return c;
            MessageSource colorSource = (MessageSource)Application.instance().getApplicationContext().getBean("colorSource");
            String relation = value.toString();
            Color bgColor = Color.WHITE;
            if (relation.equals("F")) {
                bgColor = Color.decode(colorSource.getMessage("relations.friendly.color", null, Locale.getDefault()));
            } else if (relation.equals("T")) {
                bgColor = Color.decode(colorSource.getMessage("relations.tolerated.color", null, Locale.getDefault()));
            } else if (relation.equals("D")) {
                bgColor = Color.decode(colorSource.getMessage("relations.disliked.color", null, Locale.getDefault()));
            } else if (relation.equals("H")) {
                bgColor = Color.decode(colorSource.getMessage("relations.hated.color", null, Locale.getDefault()));
            }
            JLabel lbl = ((JLabel)c);
            c.setBackground(bgColor);
            lbl.setForeground(Color.black);
            return lbl;
        }
        
    }
}
