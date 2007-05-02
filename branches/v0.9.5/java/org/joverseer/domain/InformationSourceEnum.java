package org.joverseer.domain;

import java.io.Serializable;


public enum InformationSourceEnum implements Serializable {
    exhaustive (4),
    detailed (3),
    some (1),
    limited (0);

    int value;

    InformationSourceEnum(int value) {
       this.value = value;
    }

    public int getValue() {
        return value;
    }
}
