package org.joverseer.domain;

import org.springframework.core.enums.LabeledEnum;

/**
 * Created by IntelliJ IDEA.
 * User: mskounak
 * Date: Sep 10, 2006
 * Time: 11:28:06 PM
 * To change this template use File | Settings | File Templates.
 */
public enum PopulationCenterSizeEnum {
    ruins (0),
    camp (1),
    village (2),
    town (3),
    majorTown (4),
    city (5);

    private final int size;

    PopulationCenterSizeEnum(int size) {
        this.size = size;
   }


    public Class getType() {
        return PopulationCenterSizeEnum.class;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Comparable getCode() {
        return size;
    }


    public String getLabel() {
        switch (size) {
            case 0:
                return "Ruins";
            case 1:
                return "Camp";
            case 2:
                return "Village";
            case 3:
                return "Town";
            case 4:
                return "Major Town";
            case 5:
                return "City";
        }
        return "";
    }

}
