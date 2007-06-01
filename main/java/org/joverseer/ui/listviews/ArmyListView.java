package org.joverseer.ui.listviews;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;
import javax.swing.TransferHandler;

import org.joverseer.domain.Army;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.ui.listviews.filters.NationFilter;
import org.joverseer.ui.support.transferHandlers.GenericExportTransferHandler;
import org.joverseer.ui.support.transferHandlers.GenericTransferable;
import org.springframework.richclient.table.SortableTableModel;

/**
 * List view for Army objects
 * 
 * @author Marios Skounakis
 */
public class ArmyListView extends ItemListView {

    public ArmyListView() {
        super(TurnElementsEnum.Army, ArmyTableModel.class);
    }

    protected int[] columnWidths() {
        return new int[] {48, 64, 120, 64, 120, 48, 48, 48, 150};
    }

    protected AbstractListViewFilter[][] getFilters() {
        return new AbstractListViewFilter[][] {NationFilter.createNationFilters()};
    }


    protected JComponent createControlImpl() {
        JComponent c = super.createControlImpl();
        TransferHandler handler = new GenericExportTransferHandler() {

            protected Transferable createTransferable(JComponent arg0) {
                try {
                    Transferable t = new GenericTransferable(new DataFlavor[] {
                            new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType + ";class=" + Army[].class.getName()),
                            new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType + ";class=" + Army.class.getName()),
                            DataFlavor.stringFlavor}, new Object[] {new Object[] {}, new Object[] {}, "aaa"});
                    return t;
                } catch (Exception exc) {
                    return null;
                }

            }
        };
        table.setTransferHandler(handler);
        return c;
    }

    /**
     * Drag and drop generates a transferable with the following three flavors:
     * - Army[] (all the selected armies)
     * - Army (the first selected army)
     * - String (string representation of all selected armies)
     */
    protected void startDragAndDropAction(MouseEvent e) {
        final Army[] selectedArmies = new Army[table.getSelectedRowCount()];
        String copyString = "";
        for (int i = 0; i < table.getSelectedRowCount(); i++) {
            int idx = ((SortableTableModel) table.getModel()).convertSortedIndexToDataIndex(table.getSelectedRows()[i]);
            Army a = (Army) tableModel.getRow(idx);
            selectedArmies[i] = a;
            String ln = "";
            for (int j = 0; j < table.getColumnCount(); j++) {
                Object v = table.getValueAt(i, j);
                if (v == null)
                    v = "";
                ln += (ln.equals("") ? "" : "\t") + v;
            }
            copyString += (copyString.equals("") ? "" : "\n") + ln;
        }
        final String str = copyString;

        TransferHandler handler = new GenericExportTransferHandler() {

            protected Transferable createTransferable(JComponent arg0) {
                try {
                    Transferable t = new GenericTransferable(new DataFlavor[] {
                            new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType + ";class=\"" + Army[].class.getName()
                                    + "\""),
                            new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType + ";class=" + Army.class.getName()),
                            DataFlavor.stringFlavor}, new Object[] {selectedArmies, selectedArmies[0], str});
                    return t;
                } catch (Exception exc) {
                    exc.printStackTrace();
                    return null;
                }

            }
        };
        table.setTransferHandler(handler);
        handler.exportAsDrag(table, e, TransferHandler.COPY);
    }


}
