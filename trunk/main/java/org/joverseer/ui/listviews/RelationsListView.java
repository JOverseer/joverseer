package org.joverseer.ui.listviews;

import java.awt.Color;
import java.awt.Component;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTable;

import org.joverseer.game.TurnElementsEnum;
import org.joverseer.ui.listviews.filters.AllegianceFilter;
import org.joverseer.ui.listviews.filters.NationFilter;
import org.joverseer.ui.listviews.renderers.AllegianceColorCellRenderer;
import org.springframework.context.MessageSource;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.table.BeanTableModel;

/**
 * List view for NationRelation objects
 * 
 * @author Marios Skounakis
 */
public class RelationsListView extends ItemListView {
    public RelationsListView() {
        super(TurnElementsEnum.NationRelation, RelationsTableModel.class);
    }
    
    
    protected JComponent createControlImpl() {
        JComponent c = super.createControlImpl();
        table.setDefaultRenderer(String.class, new RelationsTableCellRenderer(tableModel));
        // set combo box editor for the relations
        table.setDefaultEditor(String.class, new DefaultCellEditor(new JComboBox(new String[]{"F", "T", "N", "D", "H"})));
        return c;
    }



    protected int[] columnWidths() {
        return new int[]{48, 80, 58,
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
                table.getColumnModel().getColumn(i+2).setHeaderValue(tableModel.getColumnName(i+2));
            }
        } catch (Exception exc) {};
    }
    
    
    
    protected AbstractListViewFilter[][] getFilters() {
    	ArrayList<AbstractListViewFilter> filters = new ArrayList<AbstractListViewFilter>();
        filters.addAll(Arrays.asList(NationFilter.createAllAndAllImportedNationFilters()));
        filters.addAll(Arrays.asList(AllegianceFilter.createAllegianceFilters()));
        return new AbstractListViewFilter[][]{filters.toArray(new AbstractListViewFilter[]{})};
    }


    public class RelationsTableCellRenderer extends AllegianceColorCellRenderer {

        public RelationsTableCellRenderer(BeanTableModel tableModel) {
			super(tableModel);
		}

        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (column < 3) return c;
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
