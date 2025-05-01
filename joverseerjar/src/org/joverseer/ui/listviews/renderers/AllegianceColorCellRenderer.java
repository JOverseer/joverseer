package org.joverseer.ui.listviews.renderers;

import java.awt.Color;
import java.awt.Component;
import java.util.Locale;

import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;

import org.joverseer.JOApplication;
import org.joverseer.domain.IBelongsToNation;
import org.joverseer.domain.NationRelations;
import org.joverseer.game.Game;
import org.joverseer.metadata.domain.NationAllegianceEnum;
import org.joverseer.preferences.PreferenceRegistry;
import org.joverseer.support.GameHolder;
import org.joverseer.ui.listviews.AdvancedArtifactTableModel;
import org.joverseer.ui.support.drawing.ColorPicker;
import org.springframework.context.MessageSource;
import org.springframework.richclient.table.BeanTableModel;
import org.springframework.richclient.table.SortableTableModel;

/**
 * Cell renderer that colors the row background according to the allegiance of the item shown
 * The Item must implement the IBelongsToNation interface
 * 
 * Can be used only with with tables that use BeanTableModels
 * 
 * @author Marios Skounakis
 */
@SuppressWarnings("serial")
public class AllegianceColorCellRenderer extends DefaultTableCellRenderer {
    BeanTableModel tableModel;
    
    public AllegianceColorCellRenderer(BeanTableModel tableModel) {
        this.tableModel = tableModel;
    }
    
    @Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
            boolean hasFocus, int row, int column) {

        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        String pval = PreferenceRegistry.instance().getPreferenceValue("listviews.bgColor");
        if (pval.equals("none")) return c;
        if (isSelected) {
        	c.setForeground(Color.white);
            return c;
        }
        c.setForeground(UIManager.getColor("Table.foreground"));
        int objRow = ((SortableTableModel) table.getModel()).convertSortedIndexToDataIndex(row);
        Object obj = this.tableModel.getRow(objRow);
        
        //Colors artifact table white when editing
        //Prob should be separate class/render but this was a workaround which cut down time to make significantly
        if(this.canTableBeEdited()) {
        	c.setBackground(Color.WHITE);
        	return c;
        }

        if (IBelongsToNation.class.isInstance(obj)) {
            IBelongsToNation natObj = (IBelongsToNation) obj;
            if (natObj == null || natObj.getNationNo() == null)
                return c;
            int nationNo = natObj.getNationNo();

            Game g = GameHolder.instance().getGame();
            NationRelations nr = g.getTurn().getNationRelations(nationNo);
            NationAllegianceEnum allegiance = null;
            if (nr != null && nationNo > 0) {
            	allegiance = nr.getAllegiance();
            }
            //MessageSource colorSource = JOApplication.getColorSource();
            String colorKey = allegiance != null ? "Listview." + allegiance.toString() : "ListView.unknownAllegiance";
            Color bg = ColorPicker.getInstance().getColor(colorKey);
            c.setBackground(bg);
            //c.setForeground(Color.black);
            return c;
        } else {
            return c;
        }
    }

    
    public BeanTableModel getTableModel() {
        return this.tableModel;
    }

    
    public void setTableModel(BeanTableModel tableModel) {
        this.tableModel = tableModel;
    }
    
    private boolean canTableBeEdited() {
    	if (AdvancedArtifactTableModel.class.isInstance(this.getTableModel())) {
    		return ((AdvancedArtifactTableModel) this.getTableModel()).getEditable();
    	}
    	return false;
    }

}