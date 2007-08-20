package org.joverseer.ui.support.dataFlavors;

import java.awt.datatransfer.DataFlavor;

import org.joverseer.metadata.domain.SpellInfo;

/**
 * Data flavor for SpellInfo
 * @author Marios Skounakis
 */
public class SpellInfoDataFlavor extends DataFlavor {

    private static final long serialVersionUID = -3889072087382104382L;

    public SpellInfoDataFlavor() throws ClassNotFoundException {
        super(DataFlavor.javaJVMLocalObjectMimeType + ";class=" + SpellInfo.class.getName());
    }


}
