package org.joverseer.ui.support.transferHandlers;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

/**
 * A generic transferable for use with the GenericExportTransferHandler
 * 
 * Can be created using an array of DataFlavors and an array of associated objects
 * 
 * @author Marios Skounakis
 */
public class GenericTransferable implements Transferable {

    DataFlavor[] dataFlavors;
    Object[] data;

    public GenericTransferable(DataFlavor[] dataFlavors, Object[] data) {
        super();
        this.dataFlavors = dataFlavors;
        this.data = data;
    }

    @Override
	public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
        for (int i = 0; i < this.dataFlavors.length; i++) {
            if (this.dataFlavors[i].equals(flavor)) {
                return this.data[i];
            }
        }
        return null;
    }

    @Override
	public DataFlavor[] getTransferDataFlavors() {
        return this.dataFlavors;
    }

    @Override
	public boolean isDataFlavorSupported(DataFlavor flavor) {
        for (int i = 0; i < this.dataFlavors.length; i++) {
            if (this.dataFlavors[i].equals(flavor)) {
                return true;
            }
        }
        return false;
    }


}
