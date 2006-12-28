package org.joverseer.metadata;

import java.io.IOException;



public interface MetadataReader {
    public void load(GameMetadata gm) throws IOException, MetadataReaderException;
}
