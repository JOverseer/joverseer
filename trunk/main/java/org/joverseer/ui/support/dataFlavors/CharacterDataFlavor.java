package org.joverseer.ui.support.dataFlavors;

import java.awt.datatransfer.DataFlavor;

import org.joverseer.domain.Character;

public class CharacterDataFlavor extends DataFlavor {

	public CharacterDataFlavor() throws ClassNotFoundException {
		super(DataFlavor.javaJVMLocalObjectMimeType + ";class=" + Character.class.getName() );
	}
	
}
