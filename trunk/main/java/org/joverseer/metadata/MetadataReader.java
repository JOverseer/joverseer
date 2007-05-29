package org.joverseer.metadata;

import java.io.IOException;

/**
 * Interface for Metadata Readers. All classes that read metadata information must implement
 * this interface
 * 
 * @author Marios Skounakis
 *
 */
public interface MetadataReader {
    public void load(GameMetadata gm) throws IOException, MetadataReaderException;
}
