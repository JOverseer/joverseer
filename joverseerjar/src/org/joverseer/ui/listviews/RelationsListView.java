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
import javax.swing.UIManager;

import org.joverseer.JOApplication;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.ui.listviews.filters.AllegianceFilter;
import org.joverseer.ui.listviews.filters.NationFilter;
import org.joverseer.ui.listviews.renderers.AllegianceColorCellRenderer;
import org.joverseer.ui.support.drawing.ColorPicker;
import org.springframework.context.MessageSource;
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
    
    
    @Override
	protected JComponent createControlImpl() {
        JComponent c = super.createControlImpl();
        this.table.setDefaultRenderer(String.class, new RelationsTableCellRenderer(this.tableModel));
        // set combo box editor for the relations
        this.table.setDefaultEditor(String.class, new DefaultCellEditor(new JComboBox(new String[]{"F", "T", "N", "D", "H"})));
        return c;
    }



    @Override
	protected int[] columnWidths() {
        return new int[]{48, 80, 58,
                        32, 32, 32, 32, 32, 
                        32, 32, 32, 32, 32,
                        32, 32, 32, 32, 32,
                        32, 32, 32, 32, 32,
                        32, 32, 32, 32, 32};
    }

    @Override
	protected void setItems() {
        super.setItems();
        try {
        	//TODO: work out why this exception happens.
        	if (this.table != null) { // stop a predictable silent exception
        		for (int i=1; i<26; i++) {
        			this.table.getColumnModel().getColumn(i+2).setHeaderValue(this.tableModel.getColumnName(i+2));
        		}
        	}
        } catch (Exception exc) {};
    }
    
    
    
    @Override
	protected AbstractListViewFilter[][] getFilters() {
    	ArrayList<AbstractListViewFilter> filters1 = new ArrayList<AbstractListViewFilter>();
        filters1.addAll(Arrays.asList(NationFilter.createAllAndAllImportedNationFilters()));
        filters1.addAll(Arrays.asList(AllegianceFilter.createAllegianceFilters(true)));
        return new AbstractListViewFilter[][]{filters1.toArray(new AbstractListViewFilter[]{})};
    }


    public class RelationsTableCellRenderer extends AllegianceColorCellRenderer {

        /**
		 * 
		 */
		private static final long serialVersionUID = 7723284888715216718L;

		public RelationsTableCellRenderer(BeanTableModel tableModel) {
			super(tableModel);
		}

        @Override
		public Component getTableCellRendererComponent(JTable table1, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table1, value, isSelected, hasFocus, row, column);
            if (column < 3) return c;
            //c.setForeground(UIManager.getColor("Label.foreground"));
            String relation = value.toString();
            Color bgColor = UIManager.getColor("Table.background");
            if (relation.equals("F")) {
                bgColor = ColorPicker.getInstance().getColor("relations.friendly");
            } else if (relation.equals("T")) {
                bgColor = ColorPicker.getInstance().getColor("relations.tolerated");
            } else if (relation.equals("D")) {
                bgColor = ColorPicker.getInstance().getColor("relations.disliked");
            } else if (relation.equals("H")) {
                bgColor = ColorPicker.getInstance().getColor("relations.hated");
            }	
            JLabel lbl = ((JLabel)c);
            c.setBackground(bgColor);
            lbl.setForeground(UIManager.getColor("Table.foreground"));
            return lbl;
        }
        
    }
}
