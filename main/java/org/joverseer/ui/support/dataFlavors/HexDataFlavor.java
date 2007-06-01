package org.joverseer.ui.support.dataFlavors;

import java.awt.datatransfer.DataFlavor;

import org.joverseer.metadata.domain.Hex;

/**
 * Data flavor for Hex objects
 * @author Marios Skounakis
 */
public class HexDataFlavor extends DataFlavor {

	public HexDataFlavor() throws ClassNotFoundException {
		super(DataFlavor.javaJVMLocalObjectMimeType + ";class=" + Hex.class.getName() );
	}

}
