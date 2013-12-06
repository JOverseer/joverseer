package org.joverseer.domain;

import java.io.Serializable;

/**
 * Enumeration for army sizes. The numeric value reflects the value used in the turn xml files.
 * 
 * @author Marios Skounakis
 *
 */
public enum ArmySizeEnum implements Serializable {
    unknown(0),
    tiny (5),
    small (1),
    army (2),
    large (3),
    huge (4);

    int size;

    private ArmySizeEnum(int s) {
        this.size = s;
    }

    public int getSize() {
    	return this.size;
    }
}
