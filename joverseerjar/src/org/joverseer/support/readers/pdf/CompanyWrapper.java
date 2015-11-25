package org.joverseer.support.readers.pdf;

import org.joverseer.domain.Company;

/**
 * Holds information about companies
 * 
 * @author Marios Skounakis
 */
public class CompanyWrapper {
    String commanderName;
    int hexNo;
    String members = new String();
    
    public String getCommanderName() {
        return this.commanderName;
    }
    
    public void setCommanderName(String commanderName) {
        this.commanderName = commanderName;
    }
    
    public int getHexNo() {
        return this.hexNo;
    }
    
    public void setHexNo(int hexNo) {
        this.hexNo = hexNo;
    }
    
    
    public String getMembers() {
        return this.members;
    }

    
    public void setMembers(String members) {
        this.members = members;
    }
    
    
    
    public Company getCompany() {
        Company c = new Company();
        c.setHexNo(this.hexNo);
        c.setCommander(getCommanderName());
        String[] members1 = getMembers().split("-");
        for (String m : members1) {
            c.addMember(m.trim().replace("&#13;", ""));
        }
        return c;
    }
    
    
    
}
