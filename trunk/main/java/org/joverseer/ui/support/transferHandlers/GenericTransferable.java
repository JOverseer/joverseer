package org.joverseer.ui.support.transferHandlers;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import sun.awt.datatransfer.TransferableProxy;

public class GenericTransferable implements Transferable {
	DataFlavor[] dataFlavors;
	Object[] data;
	
	public GenericTransferable(DataFlavor[] dataFlavors, Object[] data) {
		super();
		this.dataFlavors = dataFlavors;
		this.data = data;
	}

	public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
		for (int i=0; i<dataFlavors.length; i++) {
			if (dataFlavors[i].equals(flavor)) {
				return data[i];
			}
		}
		return null;
	}

	public DataFlavor[] getTransferDataFlavors() {
		return dataFlavors;
	}

	public boolean isDataFlavorSupported(DataFlavor flavor) {
		for (int i=0; i<dataFlavors.length; i++) {
			if (dataFlavors[i].equals(flavor)) {
				return true;
			}
		}
		return false;
	}
	

}
