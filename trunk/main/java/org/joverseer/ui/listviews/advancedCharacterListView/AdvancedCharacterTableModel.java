package org.joverseer.ui.listviews.advancedCharacterListView;

import org.joverseer.domain.CharacterDeathReasonEnum;
import org.joverseer.support.infoSources.InfoSource;
import org.joverseer.ui.domain.ArtifactWrapper;
import org.joverseer.ui.listviews.ItemTableModel;
import org.springframework.context.MessageSource;

public class AdvancedCharacterTableModel extends ItemTableModel {

	public AdvancedCharacterTableModel(MessageSource messageSource) {
		super(AdvancedCharacterWrapper.class, messageSource);
	}

	protected String[] createColumnPropertyNames() {
		return new String[]{"name", "hexNo", "nationNo", 
                        "command", "agent", "emmisary", 
                        "mage", "stealth", "health", 
                        "challenge", 
                        "a0", "a1", "a2", "a3", "a4", "a5",
                        "travellingWith",
                        "deathReason",
                        "infoSource", "turnNo"};
	}

	protected Class[] createColumnClasses() {
		return new Class[]{String.class, String.class,  String.class,  
                        CharacterAttributeWrapper.class, CharacterAttributeWrapper.class, CharacterAttributeWrapper.class, 
                        CharacterAttributeWrapper.class, CharacterAttributeWrapper.class, CharacterAttributeWrapper.class, 
                        CharacterAttributeWrapper.class, 
                        ArtifactWrapper.class, ArtifactWrapper.class, ArtifactWrapper.class, ArtifactWrapper.class, ArtifactWrapper.class, ArtifactWrapper.class,
                        String.class,
                        CharacterDeathReasonEnum.class,
                        InfoSource.class, String.class}; 
	}

}