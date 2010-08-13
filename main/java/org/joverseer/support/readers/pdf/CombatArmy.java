package org.joverseer.support.readers.pdf;

import java.util.ArrayList;

import org.joverseer.support.Container;

/**
 * Stores information about an army participating in a combat.
 * 
 * @author Marios Skounakis
 */
public class CombatArmy {
        String nation;
        String commanderName;
        String commanderTitle;
        String losses;
        String morale;
        boolean survived;
        String commanderOutcome;
        Container regiments = new Container();
        ArrayList<String> attackedArmies = new ArrayList<String>();
        
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

		public String getCommanderTitle() {
			return commanderTitle;
		}

		public void setCommanderTitle(String commanderTitle) {
			this.commanderTitle = commanderTitle;
		}

		public String getCommanderOutcome() {
			return commanderOutcome;
		}

		public void setCommanderOutcome(String commanderOutcome) {
			this.commanderOutcome = commanderOutcome;
		}

		public ArrayList<String> getAttackedArmies() {
			return attackedArmies;
		}
        
        public void AddAttackedArmy(String armyCommander) {
        	attackedArmies.add(armyCommander);
        }
    }