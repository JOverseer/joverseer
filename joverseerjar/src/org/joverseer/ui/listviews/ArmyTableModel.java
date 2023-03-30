package org.joverseer.ui.listviews;

import org.joverseer.domain.Army;
import org.joverseer.domain.ArmyElement;
import org.joverseer.domain.ArmyElementType;
import org.joverseer.domain.ArmySizeEnum;
import org.joverseer.preferences.PreferenceRegistry;
import org.joverseer.support.GameHolder;
import org.joverseer.ui.support.GraphicUtils;
import org.joverseer.ui.support.UIUtils;
import org.springframework.context.MessageSource;

/**
 * Table model for Army objects
 * 
 * @author Marios Skounakis
 */
public class ArmyTableModel extends ItemTableModel {

	private static final long serialVersionUID = 1L;
	final static int iHex = 0;
	final static int iCommanderName = 2;
	final static int iSize = 3;
	final static int iInfo = 4;
	final static int iTotalTroops = 5;
	final static int iEnHi = 6;
	final static int iCharacters = 8;

	public ArmyTableModel(MessageSource messageSource,GameHolder gameHolder,PreferenceRegistry preferenceRegistry) {
		super(Army.class, messageSource,gameHolder,preferenceRegistry);
	}

	@Override
	protected String[] createColumnPropertyNames() {
		return new String[] { "hexNo", "nationNo", "commanderName", "size", "info", "totalTroops", "enHI", "food", "characters", "maintenance" };
	}

	@Override
	protected Class[] createColumnClasses() {
		return new Class[] { String.class, String.class, String.class, String.class, String.class, String.class, Integer.class, Integer.class, String.class, Integer.class };
	}

	@Override
	protected Object getValueAtInternal(Object object, int i) {
		Army army = (Army) object;
		if (i == iCommanderName) {
			String commander = army.getCommanderName();
			commander = GraphicUtils.parseName(commander);
			if (commander.equals("Unknown")) {
				commander = "-";
			}
			return commander;
		} else if (i == iSize) {
			if (army.getSize() == null || army.getSize().equals(ArmySizeEnum.unknown)) {
				return "";
			}
			return army.getSize().toString();
		} else if (i == iInfo) {
			if (army.getElements().size() > 0) {
				String txt = "";
				for (ArmyElement element : army.getElements()) {
					txt = UIUtils.OptSpace(txt,element.getLocalizedDescription());
				}
				return txt;
			} else if (army.getTroopCount() > 0) {
				return "~" + army.getTroopCount() + " men";
			} else {
				return "-";
			}
		} else if (i == iTotalTroops) {
			if (army.getElements().size() > 0) {
				int count = 0;
				for (ArmyElement el : army.getElements()) {
					if (el.getArmyElementType() != ArmyElementType.WarMachimes && el.getArmyElementType() != ArmyElementType.Warships && el.getArmyElementType() != ArmyElementType.Transports) {
						count += el.getNumber();
					}
				}
				return count;
			} else if (army.getTroopCount() > 0) {
				return army.getTroopCount();
			} else {
				return null;
			}
		} else if (i == iEnHi) {
			return army.getENHI();
		} else if (i == iCharacters) {
			String chars = "";
			for (String ch : army.getCharacters()) {
				chars += UIUtils.OptCommaSpace(chars,ch);
			}
			return chars;
		} else {
			return super.getValueAtInternal(object, i);
		}
	}

}
