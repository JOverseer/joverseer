package org.joverseer.ui.support.dataFlavors;

import java.awt.datatransfer.DataFlavor;

import org.joverseer.metadata.domain.ArtifactInfo;

/**
 * Data flavor for ArtifactInfo
 * @author Marios Skounakis
 */
public class ArtifactInfoDataFlavor extends DataFlavor {
	public ArtifactInfoDataFlavor() throws ClassNotFoundException {
		super(DataFlavor.javaJVMLocalObjectMimeType + ";class=" + ArtifactInfo.class.getName());
	}

}
