package org.joverseer.domain;

import java.io.Serializable;


public enum HarborSizeEnum implements Serializable {
    none (0),
    port (1),
    harbor (2);

    private final int size;

    HarborSizeEnum(int size) {
        this.size = size;
   }
    
    public int getSize() {
        return size;
    }
}
