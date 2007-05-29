package org.joverseer.metadata;


/**
 * Exception for metadata readers
 * 
 * TODO: Check if this is used anywhere. If not, delete.
 * 
 * @author Marios Skounakis
 *
 */
public class MetadataReaderException extends Exception {

    public MetadataReaderException() {
        super();
    }

    public MetadataReaderException(String message, Throwable cause) {
        super(message, cause);
    }

    public MetadataReaderException(String message) {
        super(message);
    }

    public MetadataReaderException(Throwable cause) {
        super(cause);
    }
    

}
