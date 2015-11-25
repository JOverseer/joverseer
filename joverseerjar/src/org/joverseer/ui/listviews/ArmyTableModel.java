package org.joverseer.ui.listviews;

import org.joverseer.domain.Army;
import org.joverseer.domain.ArmyElement;
import org.joverseer.domain.ArmyElementType;
import org.joverseer.domain.ArmySizeEnum;
import org.joverseer.ui.support.GraphicUtils;
import org.joverseer.ui.support.UIUtils;
import org.springframework.context.MessageSource;

/**
 * Table model for Army objects
 * 
 * @author Marios Skounakis
 */
// TODO remove hard-coded references to column numbers
public class ArmyTableModel extends ItemTableModel {

	private static final long serialVersionUID = 1L;

	public ArmyTableModel(MessageSource messageSource) {
		super(Army.class, messageSource);
	}

	@Override
	protected String[] createColumnPropertyNames() {
		return new String[] { "hexNo", "nationNo", "commanderName", "size", "info", "totalTroops", "enHI", "food", "characters", "maintenance" };
	}

	@Override
	@SuppressWarnings("unchecked")
	protected Class[] createColumnClasses() {
		return new Class[] { String.class, String.class, String.class, String.class, String.class, String.class, Integer.class, Integer.class, String.class, Integer.class };
	}

	@Override
	protected Object getValueAtInternal(Object object, int i) {
		Army army = (Army) object;
		if (i == 2) {
			String commander = army.getCommanderName();
			commander = GraphicUtils.parseName(commander);
			if (commander.equals("Unknown")) {
				commander = "-";
			}
			return commander;
		} else if (i == 3) {
			if (army.getSize() == null || army.getSize().equals(ArmySizeEnum.unknown)) {
				return "";
			}
			return army.getSize().toString();
		} else if (i == 4) {
			if (army.getElements().size() > 0) {
				String txt = "";
				for (ArmyElement element : army.getElements()) {
					txt += UIUtils.OptSpace(txt,element.getLocalizedDescription());
				}
				return txt;
			} else if (army.getTroopCount() > 0) {
				return "~" + army.getTroopCount() + " men";
			} else {
				return "-";
			}
		} else if (i == 5) {
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
		} else if (i == 6) {
			return army.getENHI();
		} else if (i == 8) {
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
