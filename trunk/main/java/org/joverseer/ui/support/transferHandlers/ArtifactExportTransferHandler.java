package org.joverseer.ui.support.transferHandlers;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import javax.swing.JComponent;
import javax.swing.TransferHandler;

import org.joverseer.domain.Artifact;
import org.joverseer.metadata.domain.ArtifactInfo;
import org.joverseer.support.GameHolder;
import org.joverseer.ui.support.dataFlavors.ArtifactDataFlavor;
import org.joverseer.ui.support.dataFlavors.ArtifactInfoDataFlavor;


public class ArtifactExportTransferHandler extends TransferHandler {
    Artifact artifact;

    public ArtifactExportTransferHandler(Artifact artifact) {
        super();
        this.artifact = artifact;
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
                    if (flavor.equals(new ArtifactDataFlavor())) {
                        return artifact;
                    }
                }
                catch (Exception exc) {

                };
                try {
                    if (flavor.equals(new ArtifactInfoDataFlavor())) {
                        return GameHolder.instance().getGame().getMetadata().getArtifacts().findFirstByProperty("no", artifact.getNumber());
                    }
                }
                catch (Exception exc) {

                };
                if (flavor.equals(DataFlavor.stringFlavor)) {
                    return String.valueOf("#" + artifact.getNumber() + " " + artifact.getName());
                }
                return null;
            }

            public DataFlavor[] getTransferDataFlavors() {
                try {
                    return new DataFlavor[]{new ArtifactDataFlavor(), new ArtifactInfoDataFlavor(), DataFlavor.stringFlavor};
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


