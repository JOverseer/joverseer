package org.joverseer.ui.listviews;

import java.awt.Point;
import java.awt.dnd.DnDConstants;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.TransferHandler;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.MouseInputAdapter;

import org.joverseer.domain.Army;
import org.joverseer.domain.Character;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.ui.listviews.filters.NationFilter;
import org.joverseer.ui.support.transferHandlers.CharIdTransferHandler;
import org.joverseer.ui.support.transferHandlers.ParamTransferHandler;
import org.joverseer.ui.support.transferHandlers.StringTransferHandler;
import org.springframework.richclient.table.SortableTableModel;

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

    protected JTable createTable() {
        final JTable table = super.createTable();
        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                int idx = table.getSelectedRow();
                if (idx == -1) return;
                idx = ((SortableTableModel)table.getModel()).convertSortedIndexToDataIndex(idx);
                Army a = (Army)tableModel.getRow(idx);
                if (a.getCommanderName().startsWith("Unknown")) return;
                table.setTransferHandler(new ParamTransferHandler(a.getCommanderName()));
            }
            
        });
        TableDNDRecognizer tableDNDRecognizer = new TableDNDRecognizer(); 
        table.addMouseListener(tableDNDRecognizer);
        table.addMouseMotionListener(tableDNDRecognizer);
        return table;
    }

    class TableDNDRecognizer extends MouseInputAdapter {
        
        private boolean recognized;
        protected Point pressedPoint;
        private boolean isDragged;
        
        public void mousePressed(MouseEvent e) {
            pressedPoint = e.getPoint();
        }
     
        public void mouseDragged(MouseEvent e) {
            Point p = e.getPoint();
            if (!recognized && e.isAltDown() &&
                ((Math.abs(pressedPoint.x - p.x) > 5) ||
                 (Math.abs(pressedPoint.y - p.y) > 5))) {
                isDragged = true;
                recognized = true;
                JComponent c = (JComponent) e.getSource();
                TransferHandler th = c.getTransferHandler();
                if (th != null) {
                    th.exportAsDrag(c, e, DnDConstants.ACTION_COPY);
                }//if
            }
        }
     
        public void mouseReleased(MouseEvent e) {
            recognized = false;
            isDragged = false;
            pressedPoint = null;
        }
    }
}
