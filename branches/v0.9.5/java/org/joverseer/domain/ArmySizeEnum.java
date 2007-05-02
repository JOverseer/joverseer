package org.joverseer.domain;

import java.io.Serializable;


public enum ArmySizeEnum implements Serializable {
    unknown(0),
    tiny (5),
    small (1),
    army (2),
    large (3),
    huge (4);

    int size;

    private ArmySizeEnum(int s) {
        size = s;
    }

    public int getSize() {
    	return size;
    }
}
