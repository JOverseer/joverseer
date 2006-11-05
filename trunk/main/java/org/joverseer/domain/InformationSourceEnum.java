package org.joverseer.domain;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: mskounak
 * Date: Sep 16, 2006
 * Time: 8:34:26 PM
 * To change this template use File | Settings | File Templates.
 */
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
