package org.joverseer.ui.listviews;

import org.joverseer.domain.InfoSourceValue;
import org.joverseer.domain.PopulationCenter;
import org.joverseer.domain.ProductEnum;
import org.joverseer.support.infoSources.DerivedFromInfluenceOtherInfoSource;
import org.joverseer.support.infoSources.InfoSource;
import org.joverseer.tools.PopulationCenterLoyaltyEstimator;
import org.joverseer.ui.support.UIUtils;
import org.springframework.context.MessageSource;

/**
 * Table model for PopulationCenter objects
 * 
 * @author Marios Skounakis
 */
public class PopulationCenterTableModel extends ItemTableModel {
	static int iProductStart = 8;
	static int iLostThisTurn = 7;
	static int iLoyalty = 5;
	static int iSize = 3;
	static int iFort = 4;

	public PopulationCenterTableModel(MessageSource messageSource) {
		super(PopulationCenter.class, messageSource);
	}

	@Override
	protected String[] createColumnPropertyNames() {
		return new String[] { "hexNo", "name", "nationNo", "size", "fortification", "loyalty", "infoSource", "lostThisTurn", "le", "br", "st", "mi", "fo", "ti", "mo", "go" };
	}

	@Override
	@SuppressWarnings("unchecked")
	protected Class[] createColumnClasses() {
		return new Class[] { Integer.class, String.class, String.class, String.class, String.class, String.class, InfoSource.class, String.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class };
	}

	@Override
	protected Object getValueAtInternal(Object object, int i) {
		if (i >= iProductStart) {
			PopulationCenter pc = (PopulationCenter) object;
			ProductEnum pe = ProductEnum.getFromCode(createColumnPropertyNames()[i]);
			int s = pc.getStores(pe) == null ? 0 : pc.getStores(pe);
			int p = pc.getProduction(pe) == null ? 0 : pc.getProduction(pe);
			return s + p == 0 ? null : s + p;
		}
		if (i == iSize) {
			PopulationCenter pc = (PopulationCenter) object;
			return UIUtils.enumToString(pc.getSize());
		} else if (i == iFort) {
			PopulationCenter pc = (PopulationCenter) object;
			return UIUtils.enumToString(pc.getFortification());
		} else if (i == iLostThisTurn) {
			PopulationCenter pc = (PopulationCenter) object;
			return pc.getLostThisTurn() ? "yes" : "";
		} else if (i == iLoyalty) {
			PopulationCenter pc = (PopulationCenter) object;

			if (pc.getLoyalty() > 0) {
				return String.valueOf(pc.getLoyalty());
			} else {
				InfoSourceValue isv = PopulationCenterLoyaltyEstimator.getLoyaltyEstimateForPopCenter(pc);
				if (isv != null) {
					return isv.getValue().toString() + " (t" + ((DerivedFromInfluenceOtherInfoSource) isv.getInfoSource()).getTurnNo() + ")";
				}
			}
			return "";

		}
		return super.getValueAtInternal(object, i);
	}

}
