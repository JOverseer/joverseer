package org.joverseer.ui.support.dataFlavors;

import java.awt.datatransfer.DataFlavor;

import org.joverseer.domain.Artifact;

/**
 * Data flavor for Artifact
 * @author Marios Skounakis
 */
public class ArtifactDataFlavor extends DataFlavor {
        public ArtifactDataFlavor() throws ClassNotFoundException {
                super(DataFlavor.javaJVMLocalObjectMimeType + ";class=" + Artifact.class.getName());
        }

}