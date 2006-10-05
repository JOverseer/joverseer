package org.joverseer.metadata.domain;

/**
 * Created by IntelliJ IDEA.
 * User: mskounak
 * Date: Sep 12, 2006
 * Time: 8:39:38 AM
 * To change this template use File | Settings | File Templates.
 */
public enum HexSideElementEnum {
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
