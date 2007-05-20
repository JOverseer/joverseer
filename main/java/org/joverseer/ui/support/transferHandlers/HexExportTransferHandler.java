package org.joverseer.ui.support.transferHandlers;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import javax.swing.JComponent;
import javax.swing.TransferHandler;

import org.joverseer.metadata.domain.Hex;
import org.joverseer.ui.support.dataFlavors.HexDataFlavor;

public class HexExportTransferHandler extends TransferHandler {
	Hex hex;
	
	public HexExportTransferHandler(Hex hex) {
		super();
		this.hex = hex;
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
					if (flavor.equals(new HexDataFlavor())) {
						return hex;
					}
				}
				catch (Exception exc) {
					
				};
				if (flavor.equals(DataFlavor.stringFlavor)) {
					return String.valueOf(hex.getHexNo());
				}
				return null;
			}

			public DataFlavor[] getTransferDataFlavors() {
				try {
					return new DataFlavor[]{new HexDataFlavor(), DataFlavor.stringFlavor};
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
