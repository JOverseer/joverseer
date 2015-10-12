package org.joverseer.metadata.domain;

import java.io.Serializable;

/**
 * Enumeration for the possible elements of a hex-side
 * 
 * Value are according to the palantir data files
 * 
 * @author Marios Skounakis
 *
 */
public enum HexSideElementEnum implements Serializable {
    MajorRiver (3),
    MinorRiver (4),
    Road (5),
    Bridge (2),
    Ford (1);

    int element;

    HexSideElementEnum(int el) {
        this.element = el;
    }

    public int getElement() {
        return this.element;
    }

    public static HexSideElementEnum fromValue(int i) {
        for (HexSideElementEnum e : HexSideElementEnum.values()) {
            if (e.getElement() == i) return e;
        }
        return null;
    }

}
