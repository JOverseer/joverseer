package org.joverseer.support.infoSources;

/**
 * Information found in the metadata files (such as starting pop center info)
 * @author Marios Skounakis
 *
 */
public class MetadataSource extends InfoSource {
    private static final long serialVersionUID = 9182797701178648131L;

	@Override
	public int getTurnNo() {
        return -1;
    }

    @Override
	public void setTurnNo(int turnNo) {
        // do nothing
    }
    
    @Override
	public String toString() {
    	return "Starting info";
    }
}
