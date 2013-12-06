package org.joverseer.ui.listviews;

import org.joverseer.domain.IBelongsToNation;
import org.joverseer.domain.IHasMapLocation;
import org.joverseer.domain.IHasTurnNumber;
import org.springframework.context.MessageSource;

/**
 * Table model for Encounters
 * 
 * @author Marios Skounakis
 */
public class EncounterTableModel extends ItemTableModel {
	public EncounterTableModel(MessageSource messageSource) {
		super(EncounterWrapper.class, messageSource);
	}

	@Override
	protected String[] createColumnPropertyNames() {
		return new String[] { "turnNo", "hexNo", "nationNo", "character", "description" };
	}

	@Override
	@SuppressWarnings("unchecked")
	protected Class[] createColumnClasses() {
		return new Class[] { Integer.class, Integer.class, String.class, String.class, String.class };
	}

	public static class EncounterWrapper implements IHasMapLocation, IHasTurnNumber, IBelongsToNation {
		int turnNo;
		int hexNo;
		int nationNo;
		String character;
		String description;

		@Override
		public int getTurnNo() {
			return this.turnNo;
		}

		public void setTurnNo(int turnNo) {
			this.turnNo = turnNo;
		}

		public int getHexNo() {
			return this.hexNo;
		}

		public void setHexNo(int hexNo) {
			this.hexNo = hexNo;
		}

		@Override
		public Integer getNationNo() {
			return this.nationNo;
		}

		@Override
		public void setNationNo(Integer nationNo) {
			this.nationNo = nationNo;
		}

		public String getCharacter() {
			return this.character;
		}

		public void setCharacter(String character) {
			this.character = character;
		}

		public String getDescription() {
			return this.description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		@Override
		public int getX() {
			return getHexNo() / 100;
		}

		@Override
		public int getY() {
			return getHexNo() % 100;
		}

	}

}
