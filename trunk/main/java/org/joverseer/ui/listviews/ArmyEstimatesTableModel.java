package org.joverseer.ui.listviews;

import org.joverseer.domain.ArmyElementType;
import org.joverseer.domain.ArmyEstimate;
import org.joverseer.domain.ArmyEstimateElement;
import org.springframework.context.MessageSource;

public class ArmyEstimatesTableModel extends ItemTableModel {
	public static int iLosses = 3;
	public static int iNumber = 5;
	public static int iType = 6;
	public static int iWeapon = 7;
	public static int iTraining = 9;
	public static int iArmor = 8;
		
	public ArmyEstimatesTableModel(MessageSource messageSource) {
		super(ArmyEstimate.class, messageSource);
	}

	protected String[] createColumnPropertyNames() {
		return new String[]{"hexNo", "commanderName", "commanderTitle", "losses", "moraleRange", "number", "type", "weapon", "armor", "training"};
	}

	protected Class[] createColumnClasses() {
		return new Class[]{String.class, String.class, String.class, String.class, String.class,  
							String.class, String.class, String.class, String.class, String.class};
	}

	protected Object getValueAtInternal(Object object, int i) {
		ArmyEstimate ae = (ArmyEstimate)object;
		if (i == iLosses) {
			String res = "";
			for (int j=0; j<ae.getLossesDescriptions().size(); j++) {
				res += (res.equals("") ? "" : ", ") + ae.getLossesDescriptions().get(j) + " (" + ae.getLossesRanges().get(j) + ")";
			}
			return res;
		} else if (i == iNumber) {
			String res = "";
			for (ArmyEstimateElement aee : ae.getRegiments()) {
				res += (res.equals("") ? "" : "\n") + aee.getNumber();
			}
			return res;
		} else if (i == iType) {
			String res = "";
			for (ArmyEstimateElement aee : ae.getRegiments()) {
				ArmyElementType at = aee.getType();
				if (at != null) {
					res += (res.equals("") ? "" : "\n") + at.toString();
				} else {
					res += (res.equals("") ? "" : "\n") + aee.getDescription();
				}
				
			}
			return res;
			
		} else if (i == iWeapon) {
			String res = "";
			for (ArmyEstimateElement aee : ae.getRegiments()) {
				res += (res.equals("") ? "" : "\n") + aee.getWeaponsDescription() + " (" + aee.getWeaponsRange() + ")";
			}
			return res;
			
		} else if (i == iArmor) {
			String res = "";
			for (ArmyEstimateElement aee : ae.getRegiments()) {
				res += (res.equals("") ? "" : "\n") + aee.getArmorDescription() + " (" + aee.getArmorRange() + ")";
			}
			return res;
			
		} else if (i == iTraining) {
			String res = "";
			for (ArmyEstimateElement aee : ae.getRegiments()) {
				res += (res.equals("") ? "" : "\n") + aee.getTrainingDescription() + " (" + aee.getTrainingRange() + ")";
			}
			return res;
			
		}
		return super.getValueAtInternal(object, i);
	}
	
	
	

}