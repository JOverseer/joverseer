package org.joverseer.metadata.domain;

import java.io.Serializable;


public enum HexSideElementEnum implements Serializable {
    MajorRiver (3),
    MinorRiver (4),
    Road (5),
    Bridge (2),
    Ford (1);

    int element;

    HexSideElementEnum(int el) {
        element = el;
    }

    public int getElement() {
        return element;
    }

    public static HexSideElementEnum fromValue(int i) {
        for (HexSideElementEnum e : HexSideElementEnum.values()) {
            if (e.getElement() == i) return e;
        }
        return null;
    }

}
