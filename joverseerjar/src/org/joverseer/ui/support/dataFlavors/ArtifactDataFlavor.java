package org.joverseer.ui.support.dataFlavors;

import java.awt.datatransfer.DataFlavor;

import org.joverseer.domain.Artifact;

/**
 * Data flavor for Artifact
 * @author Marios Skounakis
 */
public class ArtifactDataFlavor extends DataFlavor {

    private static final long serialVersionUID = 2762174405077315282L;

    public ArtifactDataFlavor() throws ClassNotFoundException {
        super(DataFlavor.javaJVMLocalObjectMimeType + ";class=" + Artifact.class.getName());
    }

}