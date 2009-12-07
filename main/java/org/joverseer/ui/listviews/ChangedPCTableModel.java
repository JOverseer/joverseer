package org.joverseer.ui.listviews;

import java.util.ArrayList;

import org.joverseer.domain.Army;
import org.joverseer.domain.ArmyElement;
import org.joverseer.domain.ArmyElementType;
import org.joverseer.domain.ArmySizeEnum;
import org.joverseer.ui.domain.ChangedPCInfo;
import org.joverseer.ui.support.GraphicUtils;
import org.springframework.context.MessageSource;

public class ChangedPCTableModel extends ItemTableModel {

	public ChangedPCTableModel(MessageSource messageSource) {
		super(ChangedPCInfo.class, messageSource);
	}

	protected String[] createColumnPropertyNames() {
		return new String[]{"hexNo", "nationNo", "name", "size", "reason"};
	}

	protected Class[] createColumnClasses() {
		return new Class[]{String.class, String.class, String.class, String.class, String.class}; 
	}

	
	

}