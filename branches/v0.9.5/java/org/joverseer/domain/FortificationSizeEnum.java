package org.joverseer.domain;

import java.io.Serializable;


public enum FortificationSizeEnum implements Serializable {
    none (0),
    tower (1),
    fort (2),
    castle (3),
    keep (4),
    citadel (5);

    private final int size;

    FortificationSizeEnum(int size) {
        this.size = size;
    }

    
    public int getSize() {
        return size;
    }
    
    
}
