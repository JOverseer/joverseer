package org.joverseer.metadata.domain;

import java.util.ArrayList;
import java.io.Serializable;

/**
 * Background information for artifacts. It holds the number, name, alignment, 
 * starting owner and powers for each artifact.
 * 
 * @author Marios Skounakis
 *
 */

public class ArtifactInfo implements Serializable {
    private static final long serialVersionUID = -2804713282789639647L;
    public static final String EMPTY_POWER = "Unknown" ;
    String name;
    int no;
    ArrayList<String> powers ;
    String alignment;
    String owner;

    public ArtifactInfo() {
    	powers = new ArrayList<String>(2) ;
    	powers.add(EMPTY_POWER) ;
    	powers.add(EMPTY_POWER) ;
    }
    
    public String getAlignment() {
        return alignment;
    }

    public void setAlignment(String alignment) {
        this.alignment = alignment;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNo() {
        return no;
    }

    public void setNo(int no) {
        this.no = no;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public ArrayList<String> getPowers() {
        return powers;
    }

    public void setPowers(ArrayList<String> powers) {
        this.powers = powers;
    }

    public String getPower1() {
        if (powers.size() == 0) {
            return "";
        }
        return powers.get(0).toString();
    }

    public String getPower2() {
        if (powers.size() < 2) {
            return "";
        }
        return powers.get(1).toString(); 
    }

	public void setPower(int index, String updatedPower) {
		if (powers.size() <= index) {
			powers.add(updatedPower);
		} else {
			powers.set(index, updatedPower);
		}
	}
}
