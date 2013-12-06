package org.joverseer.support.movement;

/**
 * Enumeration for movement directions
 * 
 * @author Marios Skounakis
 */
public enum MovementDirection {
    NorthWest("nw"), 
    NorthEast("ne"), 
    East("e"), 
    SouthEast("se"), 
    SouthWest("sw"), 
    West("w"), 
    Home("h");

    private String dir;

    private MovementDirection(String d) {
        this.dir = d;
    }

    public String getDir() {
        return this.dir;
    }

    public static MovementDirection getDirectionFromString(String dir) {
        for (MovementDirection md : MovementDirection.values()) {
            if (md.getDir().equals(dir)) {
                return md;
            }
        }
        return null;
    }
}
