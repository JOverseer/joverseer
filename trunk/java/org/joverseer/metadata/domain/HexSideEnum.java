package org.joverseer.metadata.domain;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: mskounak
 * Date: Sep 10, 2006
 * Time: 11:23:20 PM
 * To change this template use File | Settings | File Templates.
 */
public enum HexSideEnum implements Serializable {
    TopLeft (1),
    TopRight (2),
    Right (3),
    BottomRight (4),
    BottomLeft (5),
    Left (6);

    private final int side;

    HexSideEnum(int s) {
        side = s;
    }

    public int getSide() {
        return side;
    }

    public static HexSideEnum fromValue(int i) {
        for (HexSideEnum e : HexSideEnum.values()) {
            if (e.getSide() == i) return e;
        }
        return null;
    }
}
