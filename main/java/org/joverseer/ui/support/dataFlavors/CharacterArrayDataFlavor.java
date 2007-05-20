package org.joverseer.ui.support.dataFlavors;

import java.awt.datatransfer.DataFlavor;

import org.joverseer.domain.Character;

public class CharacterArrayDataFlavor extends DataFlavor {

	public CharacterArrayDataFlavor() throws ClassNotFoundException {
		super(DataFlavor.javaJVMLocalObjectMimeType + ";class=" +  Character[].class.getName() );
	}

}
