package org.joverseer.ui.listviews.advancedCharacterListView;

import org.joverseer.support.infoSources.InfoSource;
import org.joverseer.ui.listviews.ItemTableModel;
import org.springframework.context.MessageSource;

public class AdvancedCharacterTableModel extends ItemTableModel {

	public AdvancedCharacterTableModel(MessageSource messageSource) {
		super(AdvancedCharacterWrapper.class, messageSource);
	}

	protected String[] createColumnPropertyNames() {
		return new String[]{"name", "hexNo", "nationNo", "command", "agent", "emmisary", "mage", "infoSource", "turnNo"};
	}

	protected Class[] createColumnClasses() {
		return new Class[]{String.class, String.class,  String.class,  CharacterAttributeWrapper.class,  CharacterAttributeWrapper.class,  CharacterAttributeWrapper.class, CharacterAttributeWrapper.class, InfoSource.class, String.class}; 
	}

}
