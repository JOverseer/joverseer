package org.joverseer.ui.support.transferHandlers;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;

import javax.swing.JComponent;
import javax.swing.TransferHandler;

/**
 * A generic transfer handler for exporting data via dnd from a component
 * Users must implement the createTransferable method to provide the transferable
 * 
 * @author Marios Skounakis
 */
public abstract class GenericExportTransferHandler extends TransferHandler {

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
