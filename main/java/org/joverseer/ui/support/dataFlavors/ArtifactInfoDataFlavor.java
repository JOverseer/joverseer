package org.joverseer.ui.support.dataFlavors;

import java.awt.datatransfer.DataFlavor;

import org.joverseer.metadata.domain.ArtifactInfo;

public class ArtifactInfoDataFlavor extends DataFlavor {
	public ArtifactInfoDataFlavor() throws ClassNotFoundException {
		super(DataFlavor.javaJVMLocalObjectMimeType + ";class=" + ArtifactInfo.class.getName());
	}

}
