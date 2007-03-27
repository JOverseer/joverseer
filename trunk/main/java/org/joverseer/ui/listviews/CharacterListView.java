package org.joverseer.ui.listviews;

import java.awt.Component;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import org.joverseer.game.Game;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.metadata.domain.Nation;
import org.joverseer.support.GameHolder;
import org.joverseer.ui.listviews.filters.NationFilter;
import org.joverseer.domain.Character;
import org.springframework.richclient.table.ColumnToSort;


public class CharacterListView extends ItemListView {

    protected AbstractListViewFilter[] getFilters() {
        return NationFilter.createNationFilters();
    }

    public CharacterListView() {
        super(TurnElementsEnum.Character, CharacterTableModel.class);
    }

    protected int[] columnWidths() {
        return new int[] {40, 120, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32, 32};
    }


    protected ColumnToSort[] getDefaultSort() {
        return new ColumnToSort[] {new ColumnToSort(0, 2), new ColumnToSort(1, 1)};
    }

    protected JComponent createControlImpl() {
        JComponent c = super.createControlImpl();
        table.setDefaultRenderer(Integer.class, new BaseItemListView.AllegianceColorCellRenderer() {

            public Component getTableCellRendererComponent(JTable arg0, Object arg1, boolean arg2, boolean arg3,
                    int arg4, int arg5) {
                Component c = super.getTableCellRendererComponent(arg0, arg1, arg2, arg3, arg4, arg5);
                JLabel lbl = (JLabel) c;
                Integer v = (Integer) arg1;
                if (v == null || v.equals(0)) {
                    lbl.setText("");
                }
                return c;
            }

        });
        return c;
    }


    public class CharacterNationFilter extends AbstractListViewFilter {

        int nationNo;

        public CharacterNationFilter(String description, int nationNo) {
            super(description);
            this.nationNo = nationNo;
        }

        public boolean accept(Object obj) {
            Character ch = (Character) obj;
            return (nationNo == -1 || ch.getNationNo() == nationNo);
        }


    }

}
