package org.joverseer.ui.support.transferHandlers;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import javax.swing.JComponent;
import javax.swing.TransferHandler;

import org.joverseer.domain.Artifact;
import org.joverseer.support.GameHolder;
import org.joverseer.ui.support.dataFlavors.ArtifactDataFlavor;
import org.joverseer.ui.support.dataFlavors.ArtifactInfoDataFlavor;


public class ArtifactExportTransferHandler extends TransferHandler {

    Artifact artifact;

    public ArtifactExportTransferHandler(Artifact artifact) {
        super();
        this.artifact = artifact;
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
                    if (flavor.equals(new ArtifactDataFlavor())) {
                        return ArtifactExportTransferHandler.this.artifact;
                    }
                }
                catch (Exception exc) {

                };
                try {
                    if (flavor.equals(new ArtifactInfoDataFlavor())) {
                        return GameHolder.instance().getGame().getMetadata().getArtifacts().findFirstByProperty("no", ArtifactExportTransferHandler.this.artifact.getNumber());
                    }
                }
                catch (Exception exc) {

                };
                if (flavor.equals(DataFlavor.stringFlavor)) {
                    return String.valueOf("#" + ArtifactExportTransferHandler.this.artifact.getNumber() + " " + ArtifactExportTransferHandler.this.artifact.getName());
                }
                return null;
            }

            @Override
			public DataFlavor[] getTransferDataFlavors() {
                try {
                    return new DataFlavor[]{new ArtifactDataFlavor(), new ArtifactInfoDataFlavor(), DataFlavor.stringFlavor};
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


