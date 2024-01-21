/**
 * 
 */
package org.joverseer.domain;

import java.io.Serializable;

/**
 * Stores a diplomatic message for a nation
 * 
 * @author Sam Terrett
 */
public class Diplo implements IBelongsToNation, Serializable, IHasTurnNumber {

	private static final long serialVersionUID = -1847450303277111366L;
	
	private String dMessage = null;
	private int turnNo;
	private Integer nationNo;
	private int numberOfNations;
	private String[] nations = null;
	public static final int charPerNation = 135;
	
	
	
	public String getMessage() {
		return this.dMessage;
	}
	
	public void setMessage(String mess) {
		this.dMessage = mess;
	}

	public void setTurnNo(int no) {
		this.turnNo = no;
	}
	
	@Override
	public int getTurnNo() {
		return this.turnNo;
	}

	@Override
	public Integer getNationNo() {
		return this.nationNo;
	}

	@Override
	public void setNationNo(Integer no) {
		this.nationNo = no;
	}

	public int getNumberOfNations() {
		return getNations().length;
	}

	public String[] getNations() {
		return this.nations;
	}

	public void setNations(String[] nations) {
		this.nations = nations;
	}



}
