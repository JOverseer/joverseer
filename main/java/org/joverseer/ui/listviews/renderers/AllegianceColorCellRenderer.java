package org.joverseer.ui.listviews.renderers;

import java.awt.Color;
import java.awt.Component;
import java.util.Locale;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import org.joverseer.domain.IBelongsToNation;
import org.joverseer.domain.NationRelations;
import org.joverseer.game.Game;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.preferences.PreferenceRegistry;
import org.joverseer.support.GameHolder;
import org.springframework.context.MessageSource;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.table.BeanTableModel;
import org.springframework.richclient.table.SortableTableModel;


public class AllegianceColorCellRenderer extends DefaultTableCellRenderer {
    BeanTableModel tableModel;
    
    public AllegianceColorCellRenderer(BeanTableModel tableModel) {
        this.tableModel = tableModel;
    }
    
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
            boolean hasFocus, int row, int column) {

        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        String pval = PreferenceRegistry.instance().getPreferenceValue("listviews.bgColor");
        if (pval.equals("none")) return c;
        if (isSelected) {
            return c;
        }
        int objRow = ((SortableTableModel) table.getModel()).convertSortedIndexToDataIndex(row);
        Object obj = tableModel.getRow(objRow);
        if (IBelongsToNation.class.isInstance(obj)) {
            IBelongsToNation natObj = (IBelongsToNation) obj;
            if (natObj == null || natObj.getNationNo() == null || natObj.getNationNo() == 0)
                return c;
            int nationNo = natObj.getNationNo();

            Game g = GameHolder.instance().getGame();
            NationRelations nr = (NationRelations) g.getTurn().getContainer(TurnElementsEnum.NationRelation)
                    .findFirstByProperty("nationNo", nationNo);
            if (nr == null)
                return c;
            MessageSource colorSource = (MessageSource) Application.instance().getApplicationContext().getBean(
                    "colorSource");
            String colorKey = "Listview." + nr.getAllegiance().toString() + ".color";
            Color bg = Color.decode(colorSource.getMessage(colorKey, new Object[] {}, Locale.getDefault()));
            c.setBackground(bg);
            return c;
        } else {
            return c;
        }
    }

}