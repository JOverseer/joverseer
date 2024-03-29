package org.joverseer.domain;

import java.io.Serializable;

import org.joverseer.game.Game;
import org.joverseer.metadata.GameMetadata;
import org.joverseer.metadata.orders.OrderMetadata;
import org.joverseer.support.GameHolder;

/**
 * Stores an order for a character
 * 
 * The order parameters are stored all within the same string, separated by the
 * DELIM character
 * 
 * @author Marios Skounakis
 */
public class Order implements IBelongsToNation, IHasMapLocation, Serializable {

	private static final long serialVersionUID = 1643607461991378403L;
	public static String NA = " N/A"; // Description for the blank order
	public static String DELIM = "#";

	Integer nationNo; // TODO is this needed? is this set anywhere?

	int orderNo = -1;
	String parameters = "";

	String notes; // TODO delete

	Character character;

	public Order(Character c) {
		this.character = c;
	}

	@Override
	public Integer getNationNo() {
		return this.nationNo;
	}

	@Override
	public void setNationNo(Integer nationNo) {
		this.nationNo = nationNo;
	}

	@Override
	public int getX() {
		return getCharacter().getX();
	}

	public void setX(int x) {
	}

	@Override
	public int getY() {
		return getCharacter().getY();
	}

	public void setY(int y) {
	}

	public int getOrderNo() {
		return this.orderNo;
	}

	public void setOrderNo(int orderNo) {
		this.orderNo = orderNo;
	}

	/***
	 * @return the parameters as one string delimited by DELIM
	 */
	public String getParameters() {
		return this.parameters;
	}

	public void setParameters(String parameters) {
		this.parameters = parameters;
	}

	public Character getCharacter() {
		return this.character;
	}

	public void setCharacter(Character character) {
		this.character = character;
	}

	public void setNoAndCode(String noAndDescr) {
		if (noAndDescr.equals(NA) || (noAndDescr.length() ==0)) {
			setOrderNo(-1);
			return;
		}
		int i = noAndDescr.indexOf(' ');
		if (i == -1) {
			i = 3;
		}
		try {
			String no = noAndDescr.substring(0, i);
			setOrderNo(Integer.parseInt(no));
		} catch (NumberFormatException exc) {
			clear();
		}
	}

	public String getNoAndCode() {
		if (getOrderNo() <= 0)
			return NA;
		OrderMetadata om = getMetadata();
		if (om == null)
			return NA;
		return om.getNumber() + " " + om.getCode();
	}

	/**
	 * Returns the metadata for this order (description, etc) by automatically
	 * looking it up in the game metadata
	 */
	public OrderMetadata getMetadata() {
		Game g = GameHolder.instance().getGame();
		if (g == null)
			return null;
		GameMetadata gm = g.getMetadata();
		if (gm == null)
			return null;
		OrderMetadata om = gm.getOrders().findFirstByProperty("number", Integer.valueOf(getOrderNo()));
		return om;
	}

	public String getMetadataDescription() {
		OrderMetadata om = getMetadata();
		if (om == null)
			return "";
		return om.getName() + " , " + om.getDifficulty() + ", " + om.getRequirement();
	}

	public void setMetadataDescription(String value) {
		// do nothing
		// simple placeholder so that form code can work with Order
	}

	public boolean isBlank() {
		return this.orderNo <= 0;
	}

	public String getParameter(int i) {
		String[] params = getParameters().split(DELIM);
		if (params.length > i) {
			return params[i];
		}
		return null;
	}

	public int getParameterInt(int i) {
		String pv = getParameter(i);
		if (pv == null)
			return -1;
		try {
			return Integer.parseInt(pv);
		} catch (NumberFormatException exc) {
			return -1;
		}
	}

	public String getNotes() {
		return this.notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	/**
	 * Clears the order
	 */
	public void clear() {
		this.orderNo = -1;
		this.parameters = "";
		this.notes = "";
	}

	/**
	 * Utility function to replace DELIM with spaces. 
	 * @param params
	 * @return
	 */
	public static String getParametersAsString(String params) {
		return params.replace(DELIM, " ");
	}

	public void setParameters(String[] params) {
		String p = "";
		for (String param : params) {
			p += (p.equals("") ? "" : DELIM) + param;
		}
		setParameters(p);
	}

	public void setParameter(int idx, String param) {
		String[] params = this.parameters.split(Order.DELIM);
		if (idx < params.length) {
			params[idx] = param;
			setParameters(paramStringFromArray(params));
		} else {
			String[] ps = new String[idx + 1];
			for (int i = 0; i < params.length; i++) {
				ps[i] = params[i];
			}
			for (int i = params.length; i < idx; i++) {
				ps[i] = "-";
			}
			ps[idx] = param;
			setParameters(paramStringFromArray(ps));
		}
	}

	
	//TODO: check for possible no/ev//at end.
	public int getLastMovementIndex() {
		int n = getLastParamIndex();
		int i = 0;
		for (int j = 0; j < n; j++) {
			if (getParameter(j) != null) {
				if (!getParameter(j).equals("-")) {
					i = j;
				}
			}
		}
		return i;
	}
	public int getLastParamIndex() {
		int i = 0;
		for (int j = 0; j < 16; j++) {
			if (getParameter(j) != null) {
				i = j;
			}
		}
		return i;
	}

	public String getP0() {
		return getParameter(0);
	}

	public void setP0(String v) {
		setParameter(0, v);
	}

	public String getP1() {
		return getParameter(1);
	}

	public void setP1(String v) {
		setParameter(1, v);
	}

	public String getP2() {
		return getParameter(2);
	}

	public void setP2(String v) {
		setParameter(2, v);
	}

	public String getP3() {
		return getParameter(3);
	}

	public void setP3(String v) {
		setParameter(3, v);
	}

	public String getP4() {
		return getParameter(4);
	}

	public void setP4(String v) {
		setParameter(4, v);
	}

	public String getP5() {
		return getParameter(5);
	}

	public void setP5(String v) {
		setParameter(5, v);
	}

	public String getP6() {
		return getParameter(6);
	}

	public void setP6(String v) {
		setParameter(6, v);
	}

	public String getP7() {
		return getParameter(7);
	}

	public void setP7(String v) {
		setParameter(7, v);
	}

	public String getP8() {
		return getParameter(8);
	}

	public void setP8(String v) {
		setParameter(8, v);
	}

	public String getP9() {
		return getParameter(9);
	}

	public void setP9(String v) {
		setParameter(9, v);
	}

	public String getP10() {
		return getParameter(10);
	}

	public void setP10(String v) {
		setParameter(10, v);
	}

	public String getP11() {
		return getParameter(11);
	}

	public void setP11(String v) {
		setParameter(11, v);
	}

	public String getP12() {
		return getParameter(12);
	}

	public void setP12(String v) {
		setParameter(12, v);
	}

	public String getP13() {
		return getParameter(13);
	}

	public void setP13(String v) {
		setParameter(13, v);
	}

	public String getP14() {
		return getParameter(14);
	}

	public void setP14(String v) {
		setParameter(14, v);
	}

	/***
	 * parameters.isBlank() didn't compile on Appveyor :(
	 * TODO check compile version on Appveyor... or is this my hacked runtime that's the problem.
	 * @return
	 */
	public boolean isParametersBlank() {
		if (this.parameters == null)
			return true;
		if (this.parameters.length() == 0)
			return true;
		if (this.parameters.trim().length() == 0) {
			return true;
		}
		return false;
	}
	
	public void checkForDefaultGenderAndName() {
		String params=getParameters();
		// parameters.isBlank() didn't compile on Appveyor :(
		if (isParametersBlank()) {
			// default to male
			setParameters("m");
			params=getParameters();
		}
		// is the name defaulted?
		if (params.length()==1) {
			switch (params.charAt(0)) {
			case 'm':
			case 'M':
				setParameters(Order.DELIM + "m");
				break;
			case 'f':
			case 'F':
				setParameters(Order.DELIM + "f");
				break;
			default:
				// it's a single character name with a defaulted gender
				setParameters(params.charAt(0) + Order.DELIM + "m");
				break;		
			}
		}		
	}
	public boolean isArmyMovementOrderCapableOfEvasion() {
		return (this.orderNo == 830) || (this.orderNo == 850) || (this.orderNo == 860);
	}

	public static Order getOtherOrder(Order o) {
		return getOtherOrder(o.getCharacter(), o);
	}

	public static Order getOtherOrder(Character c, Order o) {
		if (c.getOrders()[0] == o)
			return c.getOrders()[1];
		return c.getOrders()[0];
	}

	public static String paramStringFromArray(String[] ps) {
		String p = "";
		for (String pm : ps) {
			p += (p.equals("") ? "" : Order.DELIM) + pm;
		}
		return p;
	}
	
}
