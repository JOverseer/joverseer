package org.joverseer.ui.listviews;

import org.joverseer.domain.Encounter;
import org.joverseer.domain.IBelongsToNation;
import org.joverseer.domain.IHasMapLocation;
import org.joverseer.domain.IHasTurnNumber;
import org.joverseer.game.Game;
import org.joverseer.game.Turn;
import org.joverseer.game.TurnElementsEnum;
import org.joverseer.support.GameHolder;
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

    protected String[] createColumnPropertyNames() {
        return new String[] {"turnNo", "hexNo", "nationNo", "character", "description"};
    }

    protected Class[] createColumnClasses() {
        return new Class[] { Integer.class, Integer.class, String.class, String.class, String.class};
    }

  

	public static class EncounterWrapper implements IHasMapLocation, IHasTurnNumber, IBelongsToNation {
		int turnNo;
		int hexNo;
		int nationNo;
		String character;
		String description;
		public int getTurnNo() {
			return turnNo;
		}
		public void setTurnNo(int turnNo) {
			this.turnNo = turnNo;
		}
		public int getHexNo() {
			return hexNo;
		}
		public void setHexNo(int hexNo) {
			this.hexNo = hexNo;
		}
		public Integer getNationNo() {
			return nationNo;
		}
		public void setNationNo(Integer nationNo) {
			this.nationNo = nationNo;
		}
		public String getCharacter() {
			return character;
		}
		public void setCharacter(String character) {
			this.character = character;
		}
		public String getDescription() {
			return description;
		}
		public void setDescription(String description) {
			this.description = description;
		}
		public int getX() {
			return getHexNo() / 100;
		}
		public int getY() {
			return getHexNo() % 100;
		}
		
		
	}

}
