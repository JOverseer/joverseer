package org.joverseer.ui.support.transferHandlers;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import javax.swing.JComponent;
import javax.swing.TransferHandler;

import org.joverseer.domain.Order;
import org.joverseer.ui.support.dataFlavors.OrderDataFlavor;

/**
 * TransferHandler for Order objects
 * @author Marios Skounakis
 */
public class OrderExportTransferHandler extends TransferHandler {
    /**
	 * 
	 */
	private static final long serialVersionUID = 7385275781865512724L;
	org.joverseer.domain.Order order;

    public OrderExportTransferHandler(org.joverseer.domain.Order order) {
        super();
        this.order = order;
    }

    @Override
	public boolean canImport(JComponent arg0, DataFlavor[] arg1) {
        return false;
    }

    @Override
	public int getSourceActions(JComponent c) {
        return COPY_OR_MOVE;
    }

//    @Override
//	protected void exportDone(JComponent c, Transferable data, int action) {
//    }

    @Override
	protected Transferable createTransferable(JComponent arg0) {
        Transferable t = new Transferable() {

            @Override
			public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
                    try {
						if (flavor.equals(new OrderDataFlavor())) {
						    return OrderExportTransferHandler.this.order;
						}
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                if (flavor.equals(DataFlavor.stringFlavor)) {
                    String orderTxt = "";
                    orderTxt += OrderExportTransferHandler.this.order.getNoAndCode();
                    orderTxt += Order.getParametersAsString(OrderExportTransferHandler.this.order.getParameters());
                    return orderTxt;
                }
                return null;
            }

            @Override
			public DataFlavor[] getTransferDataFlavors() {
            	try {
            		return new DataFlavor[]{new OrderDataFlavor(), DataFlavor.stringFlavor};
				} catch (ClassNotFoundException e) {
					return new DataFlavor[]{DataFlavor.stringFlavor};
				}
            }

            @Override
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
