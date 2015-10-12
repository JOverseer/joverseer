package org.joverseer.ui.support.dataFlavors;

import java.awt.datatransfer.DataFlavor;

import org.joverseer.domain.Character;

/**
 * Data flavor for Character[]
 * @author Marios Skounakis
 */
public class CharacterArrayDataFlavor extends DataFlavor {

    private static final long serialVersionUID = -6431618842452918230L;

    public CharacterArrayDataFlavor() throws ClassNotFoundException {
        super(DataFlavor.javaJVMLocalObjectMimeType + ";class=" +  Character[].class.getName() );
    }

}
