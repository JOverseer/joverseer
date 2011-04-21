package org.joverseer.tools.infoCollectors.characters;

import org.joverseer.support.infoSources.DerivedFromArmyInfoSource;
import org.joverseer.support.infoSources.DerivedFromTitleInfoSource;
import org.joverseer.support.infoSources.InfoSource;
import org.joverseer.support.infoSources.RumorActionInfoSource;

/**
 * Wraps information about a character attribute.
 * 
 * Each character attribute has: - a value - a total value (modified by arties),
 * if applicable - a name - an information source that tells us how it was
 * derived (e.g. xml turn, title, rumor, etc)
 * 
 * @author Marios Skounakis
 * 
 */
public class CharacterAttributeWrapper implements Comparable<CharacterAttributeWrapper> {
	public static int COMPARE_BY_TOTAL_VALUE = 1;
	public static int COMPARE_BY_NET_VALUE = 2;
	public static int COMPARIZON_MODE = COMPARE_BY_TOTAL_VALUE;

	String attribute;

	InfoSource infoSource;

	Object value;

	Object totalValue;

	int turnNo;

	public CharacterAttributeWrapper(String attribute, Object value, int turnNo, InfoSource infoSource) {
		this.attribute = attribute;
		this.value = value;
		this.turnNo = turnNo;
		this.infoSource = infoSource;
	}

	public CharacterAttributeWrapper(String attribute, Object value, Object totalValue, int turnNo, InfoSource infoSource) {
		this.attribute = attribute;
		this.value = value;
		this.turnNo = turnNo;
		this.infoSource = infoSource;
		this.totalValue = totalValue;
	}

	public InfoSource getInfoSource() {
		return infoSource;
	}

	public void setInfoSource(InfoSource infoSource) {
		this.infoSource = infoSource;
	}

	public String getAttribute() {
		return attribute;
	}

	public void setAttribute(String attribute) {
		this.attribute = attribute;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public int getTurnNo() {
		return turnNo;
	}

	public void setTurnNo(int turnNo) {
		this.turnNo = turnNo;
	}

	public Object getTotalValue() {
		return totalValue;
	}

	public void setTotalValue(Object totalValue) {
		this.totalValue = totalValue;
	}

	@SuppressWarnings("unchecked")
	public int compareTo(CharacterAttributeWrapper caw) {
		if (caw == null)
			return 1;
		if (COMPARIZON_MODE == COMPARE_BY_TOTAL_VALUE) {
			Object v1 = getTotalValue();
			Object v2 = caw.getTotalValue();
			if (v1 == null && v2 != null)
				return -1;
			if (v1 != null && v2 == null)
				return 1;
			if (v1 != null && v2 != null) {
				return ((Comparable) v1).compareTo(v2);
			}
		}
		Object v1 = getValue();
		Object v2 = caw.getValue();
		if (v1 == null && v2 != null)
			return -1;
		if (v1 != null && v2 == null)
			return 1;
		if (v1 != null && v2 != null) {
			return ((Comparable) v1).compareTo(v2);
		}
		return 0;
	}

	@Override
	public String toString() {
		String v = getValue() == null ? "" : getValue().toString();
		InfoSource is = getInfoSource();
		if (DerivedFromTitleInfoSource.class.isInstance(is)) {
			v += "+";
		} else if (RumorActionInfoSource.class.isInstance(is)) {
			v += "+";
		} else if (DerivedFromArmyInfoSource.class.isInstance(is)) {
			v += "+";
		}

		if (getTotalValue() != null) {
			if (!getTotalValue().toString().equals(getValue().toString()) && !getTotalValue().toString().equals("0")) {
				v += "(" + getTotalValue().toString() + ")";
			}
		}
		return v;
	}

}
