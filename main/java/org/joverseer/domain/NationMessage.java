package org.joverseer.domain;

import java.io.Serializable;

import org.joverseer.metadata.domain.Nation;
import org.joverseer.support.NationMap;
import org.joverseer.support.StringUtils;

/**
 * Stores a nation message
 * 
 * Although messages don't normally have a hex number, they are assigned hex numbers using the
 * Nation Message Parser
 * 
 * @author Marios Skounakis
 *
 */

public class NationMessage implements Serializable, IBelongsToNation, IHasMapLocation {
    private static final long serialVersionUID = -5607141998688317604L;
    int x = -1;
    int y = -1;
    Integer nationNo;

    int x2;
    int y2;

    String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getNationNo() {
        return nationNo;
    }

    public void setNationNo(Integer nationNo) {
        this.nationNo = nationNo;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getX2() {
        return x2;
    }

    public void setX2(int x2) {
        this.x2 = x2;
    }

    public int getY2() {
        return y2;
    }

    public void setY2(int y2) {
        this.y2 = y2;
    }
    
    public boolean isStealRumor() {
    	return getMessage().contains("Gold was stolen at");
    }
    
    public int getStealAmount() {
    	int i = getMessage().indexOf("Gold was stolen at");
    	String amount = getMessage().substring(0, i).trim();
    	try {
    		return Integer.parseInt(amount);
    	}
    	catch (Exception e) {
    		return 0;
    	}
    }
    
    public boolean isFriendlyTransportMessage() {
    	return getMessage().contains(" transported from ");
    }
    
    public int getFriendlyTrasnportFromNation() {
    	String nation = StringUtils.getUniquePart(getMessage(), "transported from ", "to", false, false);
    	if (nation.startsWith("the")) nation = StringUtils.stripFirstWord(nation);
    	Nation n = NationMap.getNationFromName(nation);
    	if (n != null) return n.getNumber();
    	return 0;
    }
    
    public String getFriendlyTransportDestPop() {
    	return StringUtils.getUniquePart(getMessage(), " to ", "\\.", false, false);
    }
    
    public boolean isEnemyTransportMessage() {
    	return getMessage().contains("There are rumors of Gold being transported by caravan");
    }
    
    public String getEnemyTransportDestPop() {
    	return StringUtils.getUniquePart(getMessage(), " to ", "\\.", false, false);
    }
    
    public String getEnemyTransportOriginPop() {
    	return StringUtils.getUniquePart(getMessage(), " transported by caravan from ", " to ", false, false);
    }
    
    public boolean isBridgeSabotagedRumor() {
    	return getMessage().contains("bridge was sabotaged at");
    }
    
    public String getBridgeSabotagedLocation() {
    	String target = StringUtils.getUniquePart(getMessage(), "bridge was sabotaged at ", "\\.", false, false);
    	return target;
    }
    
    public boolean isEncounterRumor() {
    	return getMessage().contains("There are rumors of an encounter involving");
    }
    
    public int getEncounterHexNo() {
    	String hexNo = StringUtils.getUniquePart(getMessage(), " at ", "\\.", false, false);
    	try {
    		return Integer.parseInt(hexNo);
    	}
    	catch (Exception exc) {
    		return 0;
    	}
    }
    
    public String getEncounterCharacter() {
    	return StringUtils.getUniquePart(getMessage(), "There are rumors of an encounter involving", " at ", false, false);
    }
    
    public boolean isInfOtherRumor() {
    	return getMessage().contains("The loyalty was influenced/reduced at ");
    }
    
    public String getInfoOtherPop() {
    	return StringUtils.getUniquePart(getMessage(), "The loyalty was influenced/reduced at ", "\\.", false, false);
    }
    
    public boolean isAssassinationRumor() 
    {
    	return getMessage().contains("There are rumors of an assassination attempt involving ");
    }
    
    public boolean isKidnapRumor() 
    {
    	return getMessage().contains("There are rumors of a kidnap attempt involving");
    }
    
    public String getAssassinationTarget() {
    	return StringUtils.getUniquePart(getMessage(), " and ", "\\.", false, false);
    }
    
    public String getAssassinationAttacker() {
    	return StringUtils.getUniquePart(getMessage(), " attempt involving ", " and ", false, false);
    }
    
    public String getKidnapTarget() {
    	return getAssassinationTarget();
    }
    
    public String getKidnapAttacker() {
    	return getAssassinationAttacker();
    }
}
