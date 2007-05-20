package org.joverseer.ui.support.transferHandlers;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import javax.swing.JComponent;
import javax.swing.TransferHandler;

import org.joverseer.ui.support.dataFlavors.CharacterDataFlavor;

public abstract class GenericExportTransferHandler  extends TransferHandler {

	public boolean canImport(JComponent arg0, DataFlavor[] arg1) {
		return false;
	}
	
	public int getSourceActions(JComponent c) {
        return COPY_OR_MOVE;
    }
	
	protected void exportDone(JComponent c, Transferable data, int action) {
    }

	protected abstract Transferable createTransferable(JComponent arg0);

}
