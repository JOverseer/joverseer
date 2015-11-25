package org.joverseer.domain;

/**
 * Interface for items that have a map location (hex number)
 * 
 * When both getX and getY are zero, the location is unknown
 * 
 * @author Marios Skounakis
 *
 */
public interface IHasMapLocation {
    public int getX();
    public int getY();
}
