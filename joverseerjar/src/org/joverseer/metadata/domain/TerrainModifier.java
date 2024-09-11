/**
 * 
 */
package org.joverseer.metadata.domain;

import java.io.Serializable;

/**
 * 
 */
public class TerrainModifier  implements Serializable {
	private static final long serialVersionUID = 123L;
	public int NationNo;
	public HexTerrainEnum Terrain;
	public Double Modifier;
	
	public int getNationNo() {
		return this.NationNo;
	}
	public void setNationNo(int nationNo) {
		this.NationNo = nationNo;
	}
	public HexTerrainEnum getTerrain() {
		return this.Terrain;
	}
	public void setTerrain(HexTerrainEnum terrain) {
		this.Terrain = terrain;
	}
	public Double getModifier() {
		return this.Modifier;
	}
	public void setModifier(Double modifier) {
		this.Modifier = modifier;
	}

}
