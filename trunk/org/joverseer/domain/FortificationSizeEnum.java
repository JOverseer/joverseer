package org.joverseer.domain;

/**
 * Created by IntelliJ IDEA.
 * User: mskounak
 * Date: Sep 13, 2006
 * Time: 6:35:01 PM
 * To change this template use File | Settings | File Templates.
 */
public enum FortificationSizeEnum {
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
}
