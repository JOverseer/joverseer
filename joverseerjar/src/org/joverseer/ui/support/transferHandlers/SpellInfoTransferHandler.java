package org.joverseer.ui.support.transferHandlers;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import javax.swing.JComponent;
import javax.swing.TransferHandler;

import org.joverseer.metadata.domain.SpellInfo;
import org.joverseer.ui.support.dataFlavors.SpellInfoDataFlavor;

/**
 * Transfer handler for SpellInfo objects
 * 
 * @author Marios Skounakis
 */
@SuppressWarnings("serial")
public class SpellInfoTransferHandler extends TransferHandler {

    SpellInfo spellInfo;

    public SpellInfoTransferHandler(SpellInfo spellInfo) {
        super();
        this.spellInfo = spellInfo;
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
                    if (flavor.equals(new SpellInfoDataFlavor())) {
                        return SpellInfoTransferHandler.this.spellInfo;
                    }
                } catch (Exception exc) {

                }
                ;
                if (flavor.equals(DataFlavor.stringFlavor)) {
                    return SpellInfoTransferHandler.this.spellInfo.getNumber();
                }
                return null;
            }

            @Override
			public DataFlavor[] getTransferDataFlavors() {
                try {
                    return new DataFlavor[] {new SpellInfoDataFlavor(), DataFlavor.stringFlavor};
                } catch (Exception exc) {
                    return new DataFlavor[] {DataFlavor.stringFlavor};
                }
            }

            @Override
			public boolean isDataFlavorSupported(DataFlavor flavor) {
                for (DataFlavor f : getTransferDataFlavors()) {
                    if (flavor.equals(f))
                        return true;
                }
                return false;
            }

        };
        return t;

    }
}
