package org.joverseer.domain;

import java.io.Serializable;

/**
 * Enumeration for relations between nations
 * 
 * @author Marios Skounakis
 *
 */
public enum NationRelationsEnum implements Serializable {
    Hated,
    Disliked,
    Neutral,
    Tolerated,
    Friendly
}
