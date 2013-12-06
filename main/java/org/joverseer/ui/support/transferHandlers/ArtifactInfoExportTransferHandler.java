package org.joverseer.ui.support.transferHandlers;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import javax.swing.JComponent;
import javax.swing.TransferHandler;

import org.joverseer.metadata.domain.ArtifactInfo;
import org.joverseer.ui.support.dataFlavors.ArtifactInfoDataFlavor;

/**
 * TransferHandler for ArtifactInfo objects
 * @author Marios Skounakis
 */
public class ArtifactInfoExportTransferHandler extends TransferHandler {
	ArtifactInfo artifactInfo;
	
	public ArtifactInfoExportTransferHandler(ArtifactInfo artifactInfo) {
		super();
		this.artifactInfo = artifactInfo;
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
					if (flavor.equals(new ArtifactInfoDataFlavor())) {
						return ArtifactInfoExportTransferHandler.this.artifactInfo;
					}
				}
				catch (Exception exc) {
					
				};
				if (flavor.equals(DataFlavor.stringFlavor)) {
					return String.valueOf("#" + ArtifactInfoExportTransferHandler.this.artifactInfo.getNo() + " " + ArtifactInfoExportTransferHandler.this.artifactInfo.getName());
				}
				return null;
			}

			@Override
			public DataFlavor[] getTransferDataFlavors() {
				try {
					return new DataFlavor[]{new ArtifactInfoDataFlavor(), DataFlavor.stringFlavor};
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