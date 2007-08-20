package org.joverseer.ui.support.transferHandlers;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import javax.swing.JComponent;
import javax.swing.TransferHandler;

import org.joverseer.domain.Order;
import org.joverseer.ui.support.dataFlavors.CharacterDataFlavor;
import org.joverseer.ui.support.dataFlavors.OrderDataFlavor;

/**
 * TransferHandler for Order objects
 * @author Marios Skounakis
 */
public class OrderExportTransferHandler extends TransferHandler {
    org.joverseer.domain.Order order;

    public OrderExportTransferHandler(org.joverseer.domain.Order order) {
        super();
        this.order = order;
    }

    public boolean canImport(JComponent arg0, DataFlavor[] arg1) {
        return false;
    }

    public int getSourceActions(JComponent c) {
        return COPY_OR_MOVE;
    }

    protected void exportDone(JComponent c, Transferable data, int action) {
    }

    protected Transferable createTransferable(JComponent arg0) {
        Transferable t = new Transferable() {

            public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
                try {
                    if (flavor.equals(new OrderDataFlavor())) {
                        return order;
                    }
                }
                catch (Exception exc) {

                };
                if (flavor.equals(DataFlavor.stringFlavor)) {
                    String orderTxt = "";
                    orderTxt += order.getNoAndCode();
                    orderTxt += Order.getParametersAsString(order.getParameters());
                    return orderTxt;
                }
                return null;
            }

            public DataFlavor[] getTransferDataFlavors() {
                try {
                    return new DataFlavor[]{new OrderDataFlavor(), DataFlavor.stringFlavor};
                }
                catch (Exception exc) {
                    return new DataFlavor[]{DataFlavor.stringFlavor};
                }
            }

            public boolean isDataFlavorSupported(DataFlavor flavor) {
                for (DataFlavor f : getTransferDataFlavors()) {
                    if (flavor.equals(f)) return true;
                }
                return false;
            }

        };
        return t;

    }


}
