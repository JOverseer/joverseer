package org.joverseer.metadata;


/**
 * Exception for metadata readers
 * 
 * @author Marios Skounakis
 *
 */

public class MetadataReaderException extends Exception {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

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
