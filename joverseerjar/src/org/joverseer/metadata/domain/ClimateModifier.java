package org.joverseer.metadata.domain;

import java.io.Serializable;

import org.joverseer.domain.ClimateEnum;


public class ClimateModifier implements Serializable {
	private static final long serialVersionUID = 123L;
	public int NationNo;
	public ClimateEnum Climate;
	public Double Modifier;
	
	public int getNationNo() {
		return this.NationNo;
	}
	public void setNationNo(int nationNo) {
		this.NationNo = nationNo;
	}
	public ClimateEnum getClimate() {
		return this.Climate;
	}
	public void setClimate(ClimateEnum climate) {
		this.Climate = climate;
	}
	public Double getModifier() {
		return this.Modifier;
	}
	public void setModifier(Double modifier) {
		this.Modifier = modifier;
	}
	
	
}
