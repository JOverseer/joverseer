package org.joverseer.domain;

import java.io.Serializable;

/**
 * Enumeration for the information source read from the xml turn files
 * 
 * DAS: note this is NOT the same value in the XML for information source.
 * 
 * @author Marios Skounakis
 *
 */
public enum InformationSourceEnum implements Serializable {
    exhaustive (4),
    detailed (3),
    someMore (2),
    some (1),
    limited (0);

    int value;

    InformationSourceEnum(int value) {
       this.value = value;
    }

    public int getValue() {
        return this.value;
    }
}
