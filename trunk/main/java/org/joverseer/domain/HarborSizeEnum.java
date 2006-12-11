package org.joverseer.domain;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: mskounak
 * Date: Sep 13, 2006
 * Time: 7:35:59 PM
 * To change this template use File | Settings | File Templates.
 */
public enum HarborSizeEnum implements Serializable {
    none (0),
    port (1),
    harbor (2);

    private final int size;

    HarborSizeEnum(int size) {
        this.size = size;
   }
}