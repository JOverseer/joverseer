package org.joverseer.domain;

import java.io.Serializable;

import org.joverseer.ui.support.UIUtils;

/**
 * Enumeration for population center harbor sizes
 * 
 * @author Marios Skounakis
 *
 */
public enum HarborSizeEnum implements Serializable {
    none (0),
    port (1),
    harbor (2);

    private final int size;

    HarborSizeEnum(int size) {
        this.size = size;
   }
    
    public int getSize() {
        return size;
    }
    
    public String getRenderString() {
 	   return UIUtils.enumToString(this);
    }
}
