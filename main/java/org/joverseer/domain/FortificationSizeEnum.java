package org.joverseer.domain;

import java.io.Serializable;

import org.joverseer.ui.support.UIUtils;

/**
 * Enumeration for population center fortification sizes
 * 
 * @author Marios Skounakis
 *
 */
public enum FortificationSizeEnum implements Serializable {
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

    
    public int getSize() {
        return size;
    }
    
    public static FortificationSizeEnum getFromSize(int size) {
    	for (FortificationSizeEnum f : values()) {
    		if (f.getSize() == size) return f;
    	}
    	return null;
    }
    
    public String getRenderString() {
 	   return UIUtils.enumToString(this);
    }
    
}
