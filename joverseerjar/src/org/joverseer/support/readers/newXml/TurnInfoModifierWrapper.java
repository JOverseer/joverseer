/**
 * 
 */
package org.joverseer.support.readers.newXml;

/**
 * 
 */
public class TurnInfoModifierWrapper {

	String climate;
	String terrain;
	Double modifier;

	public String getClimate() {
		return this.climate;
	}
	public void setClimate(String climate) {
		this.climate = climate;
	}
	public String getTerrain() {
		return this.terrain;
	}
	public void setTerrain(String terrain) {
		this.terrain = terrain;
	}
	public Double getModifier() {
		return this.modifier;
	}
	public void setModifier(String modifier) {
		this.modifier = Double.parseDouble(modifier);
	}
	
	public void setModifier(Double modifier) {
		this.modifier = modifier;
	}


}
