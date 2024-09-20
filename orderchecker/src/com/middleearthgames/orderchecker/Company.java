package com.middleearthgames.orderchecker;

import java.text.Normalizer;
import java.util.Vector;

public class Company {
	private int commanderNation;
	private String commander;
	private Vector charsWith;
	private int location;
	
	public Company(int loc) {
		this.location = loc;
		this.commander = null;
		this.charsWith = new Vector();
		this.commanderNation = -1;
	}
	
	public int getCurrentCapacity() {
		return 1 + this.charsWith.size();
	}
	
    Character getCharacterIdInComp(String id)
    {
        if(this.commander == null)
        {
            return null;
        }
        String charName = "";
        if(this.commander.length() >= 5)
        {
            charName = (this.commander.substring(0, 5)).toLowerCase();
            charName = Normalizer.normalize(charName, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
        }
        if(!charName.equalsIgnoreCase(id))
        {
            if(this.charsWith.size() == 0)
            {
                return null;
            }
            for(int i = 0; i < this.charsWith.size() && !charName.equalsIgnoreCase(id); i++)
            {
                if(((String) this.charsWith.get(i)).length() >= 5)
                {
                    charName = ((String) this.charsWith.get(i)).substring(0, 5);
                    charName = Normalizer.normalize(charName, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
                }
            }

        }
        if(charName.equalsIgnoreCase(id))
        {
            Character newChar = new Character(id);
            newChar.setName(id);
            newChar.setNewLocation(this.location, 0);
            return newChar;
        } else
        {
            return null;
        }
    }
    
    public String getDescription(String com) {
    	String str = "";
    	Vector charsWithFullName;
    	try {
    		charsWithFullName = this.getFullNameCharsWith();
    	} catch (Exception e) {
    		return "";
    	}
    	if (this.isCharTheCommander(com)) {
    		str += "Commands the Company of ";
    		for (int i = 0; i < this.getCharsWith().size(); i++) {
        		if (i != 0) {
        			if(i == this.getCharsWith().size() - 1) str += " and ";
        			else str += ", ";
        		}
    			str += (String) charsWithFullName.get(i);
    		}
    		return str;
    	}
    	else {
    		str += "Member of " + this.getCommanderFullName() + "'s Company";
    		return str;
    	}
    }
    
    public boolean isCharTheCommander(String com) {
    	com = Normalizer.normalize(com, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
    	if (com.length() >= 5) {
    		com = com.substring(0, 5);
    	}
    	String charName = "";
    	if (this.commander.length() >= 5) {
    		charName = this.commander.substring(0, 5);
    		charName = Normalizer.normalize(charName, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
    	}
    	
    	if (charName.equalsIgnoreCase(com)) return true;
    	return false;
    }
    
    public boolean isCharInCompany(String character) {
    	character = Normalizer.normalize(character, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
    	if (character.length() >= 5) {
    		character = character.substring(0, 5);
    	}
    	
    	String charName = "";
    	for (int i = 0; i < this.getCharsWith().size(); i++) {
    		if(((String) (this.getCharsWith().get(i))).length() >= 5) {
    			charName = ((String) (this.getCharsWith().get(i))).substring(0, 5);
    			charName = Normalizer.normalize(charName, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
    			if(charName.equalsIgnoreCase(character)) return true;
    		}
    	}

    	return false;
    }
    
	
	public String getCommander() {
		return this.commander;
	}
	public void setCommander(String commander) {
        String charName = commander;
        if(commander.length() >= 5)
        {
        	charName = "";
            charName = (commander.substring(0, 5)).toLowerCase();
            charName = Normalizer.normalize(charName, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
        }
		this.commander = charName;
	}
	
	public String getCommanderFullName() {
		Nation n = Main.main.getNation();
		return n.findCharacterById(this.commander).getName();
	}

	public int getCommanderNation() {
		return this.commanderNation;
	}

	public void setCommanderNation(int commanderNation) {
		this.commanderNation = commanderNation;
	}

	public int getLocation() {
		return this.location;
	}

	public void setLocation(int location) {
		this.location = location;
	}

	public Vector getCharsWith() {
		return this.charsWith;
	}
	
	public Vector getFullNameCharsWith() throws NullPointerException {
		Nation n = Main.main.getNation();
		Vector fullNames = new Vector();
		for(int i = 0; i < this.getCurrentCapacity()-1; i++) {
			fullNames.add((Object) (n.findCharacterById((String) this.getCharsWith().get(i))).getName());
		}
		return fullNames;
	}

	public void addCharacter(String charName) {
        String charNameID = charName;
        if(charName.length() >= 5)
        {
        	charNameID = "";
            charNameID = (charName.substring(0, 5)).toLowerCase();
            charNameID = Normalizer.normalize(charNameID, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
        }
		this.charsWith.add((Object) charNameID);
	}
	
	public void removeCharacter(String name) {
        String charNameID = name;
        if(name.length() >= 5)
        {
        	charNameID = "";
            charNameID = (name.substring(0, 5)).toLowerCase();
            charNameID = Normalizer.normalize(charNameID, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
        }
		this.charsWith.remove((Object) charNameID);
	}
	

}
