package org.joverseer.domain;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: mskounak
 * Date: 30 Σεπ 2006
 * Time: 11:26:42 μμ
 * To change this template use File | Settings | File Templates.
 */
public enum ArmySizeEnum implements Serializable {
    unknown(0),
    small (1),
    army (2),
    largeArmy (3),
    hugeArmy (4);

    int size;

    private ArmySizeEnum(int s) {
        size = s;
    }

}
