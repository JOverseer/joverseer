package org.joverseer.ui.support.dataFlavors;

import java.awt.datatransfer.DataFlavor;

import org.joverseer.metadata.domain.Hex;

public class HexDataFlavor extends DataFlavor {

	public HexDataFlavor() throws ClassNotFoundException {
		super(DataFlavor.javaJVMLocalObjectMimeType + ";class=" + Hex.class.getName() );
	}

}
