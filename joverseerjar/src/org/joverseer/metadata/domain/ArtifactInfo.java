package org.joverseer.metadata.domain;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Background information for artifacts. It holds the number, name, alignment,
 * starting owner and powers for each artifact.
 *
 * @author Marios Skounakis
 *
 */

public class ArtifactInfo implements Serializable {
	private static final long serialVersionUID = -2804713282789639647L;
	public static final String EMPTY_POWER = "Unknown";
	String name;
	int no;
	ArrayList<String> powers;
	String alignment;
	String owner;
	int currentlyHiddenPopCenter = -1;
	transient String unAccentedName;
	
	public ArtifactInfo() {
		this.powers = new ArrayList<String>(2);
		this.powers.add(EMPTY_POWER);
		this.powers.add(EMPTY_POWER);
	}

	public String getAlignment() {
		return this.alignment;
	}

	public void setAlignment(String alignment) {
		this.alignment = alignment;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getNo() {
		return this.no;
	}

	public void setNo(int no) {
		this.no = no;
	}

	public String getOwner() {
		return this.owner;
	}

	public void setOwner(String owner) {
		// some data cleaning.
		if (owner != null) {
			if (owner.endsWith(",")) {
				this.owner = owner.substring(0,owner.length()-1);
			} else {
				this.owner = owner;
			}
		}
	}

	public ArrayList<String> getPowers() {
		return this.powers;
	}

	public void setPowers(ArrayList<String> powers) {
		this.powers = powers;
	}

	public String getPower1() {
		if (this.powers.size() == 0) {
			return "";
		}
		final String power1 = this.powers.get(0); 
		if (power1 == null) {	
			return "";
		}
		return power1.toString();
	}

	public String getPower2() {
		if (this.powers.size() < 2) {
			return "";
		}
		final String power2 = this.powers.get(1); 
		if (power2 == null) {	
			return "";
		}
		return power2.toString();
	}

	public void setPower(int index, String updatedPower) {
		if (this.powers.size() <= index) {
			this.powers.add(updatedPower);
		} else {
			this.powers.set(index, updatedPower);
		}
	}

	public String getBonusType() {
		if (getPower1() == null)
			return null;
		if (getPower1().startsWith("Command"))
			return "Command";
		if (getPower1().startsWith("Mage"))
			return "Mage";
		if (getPower1().startsWith("Agent"))
			return "Agent";
		if (getPower1().startsWith("Emmisary"))
			return "Emissary";
		if (getPower1().startsWith("Stealth"))
			return "Stealth";
		if (getPower1().startsWith("Combat"))
			return "Combat";
		return null;
	}

	public int getBonusRank() {
		if (getBonusType() == null)
			return 0;
		String power1 = getPower1();
		if (power1.endsWith("*")) {
			power1 = power1.substring(0, power1.length() - 1);
		}
		String[] ps = power1.split(" ");
		try {
			if (ps.length == 2) {
				return Integer.parseInt(ps[1]);
			}
		} catch (Exception exc) {
			return 0;
		}
		return 0;

	}

	public int getCurrentlyHiddenPopCenter() {
		return this.currentlyHiddenPopCenter;
	}

	public void setCurrentlyHiddenPopCenter(int currentlyHiddenPopCenter) {
		this.currentlyHiddenPopCenter = currentlyHiddenPopCenter;
	}

	public String getStats() {
		String bonusType = getBonusType();
		if (bonusType == null)
			return getPower1();
		if (bonusType.equals("Combat"))
			return "Cb" + getBonusRank();
		return bonusType.substring(0, 1) + getBonusRank();
	}
	private void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException {
		ois.defaultReadObject();
		// force the data cleanup.
		this.setOwner(this.owner);
	}
	//use this for finding by name (assuming no two artifacts just differ by accents).
	public String getUnAccentedName() {
		if (this.unAccentedName == null) {
			if (this.name != null) {
				this.unAccentedName =  org.joverseer.support.StringUtils.normalizeNamesForComparison(this.name);
			}
		}
		return this.unAccentedName;
	}
}
