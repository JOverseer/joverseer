package org.joverseer.ui.viewers;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class MovementCost {
	private final SimpleStringProperty direction;
	private final SimpleIntegerProperty infantryCost;
	private final SimpleIntegerProperty cavalryCost;
	
	public MovementCost(String aDirection,Integer anInfantryCost, Integer aCavalryCost)
	{
		this.direction = new SimpleStringProperty(aDirection);
		this.infantryCost = new SimpleIntegerProperty(anInfantryCost);
		this.cavalryCost = new SimpleIntegerProperty(aCavalryCost);
	}
	public String getDirection()
	{
		return this.direction.get();
	}
	public void setDirection(String v)
	{
		this.direction.set(v);
	}
	public Integer getInfantryCost()
	{
		return this.infantryCost.get();
	}
	public void setInfantryCost(Integer v)
	{
		this.infantryCost.set(v);
	}
	public Integer getCavalryCost()
	{
		return this.cavalryCost.get();
	}
	public void setCavalryCost(Integer v)
	{
		this.cavalryCost.set(v);
	}
	
}
