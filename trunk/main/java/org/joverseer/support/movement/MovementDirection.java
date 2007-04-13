package org.joverseer.support.movement;

public enum MovementDirection {
	NorthWest ("nw"),
	NorthEast ("ne"),
	East ("e"),
	SouthEast("se"),
	SouthWest("sw"),
	West("w"),
        Home("h");
	
	private String dir;
	
	private MovementDirection(String d) {
		dir = d;
	}
	
	public String getDir() {
		return dir;
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
