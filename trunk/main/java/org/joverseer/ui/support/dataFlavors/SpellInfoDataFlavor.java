package org.joverseer.ui.support.dataFlavors;

import java.awt.datatransfer.DataFlavor;

import org.joverseer.metadata.domain.SpellInfo;

public class SpellInfoDataFlavor extends DataFlavor {
	public SpellInfoDataFlavor() throws ClassNotFoundException {
		super(DataFlavor.javaJVMLocalObjectMimeType + ";class=" + SpellInfo.class.getName());
	}


}
