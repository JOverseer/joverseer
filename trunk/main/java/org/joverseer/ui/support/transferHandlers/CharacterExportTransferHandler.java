package org.joverseer.ui.support.transferHandlers;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import javax.swing.JComponent;
import javax.swing.TransferHandler;

import org.joverseer.ui.support.dataFlavors.CharacterDataFlavor;

/**
 * TransferHandler for Character objects
 * @author Marios Skounakis
 */
public class CharacterExportTransferHandler extends TransferHandler {
	org.joverseer.domain.Character character;
	
	public CharacterExportTransferHandler(org.joverseer.domain.Character character) {
		super();
		this.character = character;
	}

	@Override
	public boolean canImport(JComponent arg0, DataFlavor[] arg1) {
		return false;
	}
	
	@Override
	public int getSourceActions(JComponent c) {
        return COPY_OR_MOVE;
    }
	
	@Override
	protected void exportDone(JComponent c, Transferable data, int action) {
    }

	@Override
	protected Transferable createTransferable(JComponent arg0) {
		Transferable t = new Transferable() {

			@Override
			public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
				try {
					if (flavor.equals(new CharacterDataFlavor())) {
						return CharacterExportTransferHandler.this.character;
					}
				}
				catch (Exception exc) {
					
				};
				if (flavor.equals(DataFlavor.stringFlavor)) {
					return CharacterExportTransferHandler.this.character.getName();
				}
				return null;
			}

			@Override
			public DataFlavor[] getTransferDataFlavors() {
				try {
					return new DataFlavor[]{new CharacterDataFlavor(), DataFlavor.stringFlavor};
				}
				catch (Exception exc) {
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
