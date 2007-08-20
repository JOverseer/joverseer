package org.joverseer.ui.support.dataFlavors;

import java.awt.datatransfer.DataFlavor;

import org.joverseer.domain.Character;

/**
 * Data flavor for Character
 * @author Marios Skounakis
 */
public class CharacterDataFlavor extends DataFlavor {

    private static final long serialVersionUID = -7846687282681278994L;

    public CharacterDataFlavor() throws ClassNotFoundException {
        super(DataFlavor.javaJVMLocalObjectMimeType + ";class=" + Character.class.getName() );
    }

}
