package org.joverseer.support.readers.pdf;

import org.joverseer.support.Container;

/**
 * Stores information about an army participating in a combat.
 * 
 * @author Marios Skounakis
 */
public class CombatArmy {
        String nation;
        String commanderName;
        String losses;
        String morale;
        boolean survived;
        Container regiments = new Container();
        
        public String getCommanderName() {
            return commanderName;
        }
        
        public void setCommanderName(String commanderName) {
            this.commanderName = commanderName;
        }
        
        public String getLosses() {
            return losses;
        }
        
        public void setLosses(String losses) {
            this.losses = losses;
        }
        
        public String getNation() {
            return nation;
        }
        
        public void setNation(String nation) {
            this.nation = nation;
        }
        
        public boolean isSurvived() {
            return survived;
        }
        
        public void setSurvived(boolean survived) {
            this.survived = survived;
        }

        
        public Container getRegiments() {
            return regiments;
        }

        
        public void setRegiments(Container regiments) {
            this.regiments = regiments;
        }

		public String getMorale() {
			return morale;
		}

		public void setMorale(String morale) {
			this.morale = morale;
		}
        
        
    }