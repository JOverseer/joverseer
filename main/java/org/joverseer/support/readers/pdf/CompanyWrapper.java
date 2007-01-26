package org.joverseer.support.readers.pdf;

import java.util.ArrayList;

import org.joverseer.domain.Company;


public class CompanyWrapper {
    String commanderName;
    int hexNo;
    String members = new String();
    
    public String getCommanderName() {
        return commanderName;
    }
    
    public void setCommanderName(String commanderName) {
        this.commanderName = commanderName;
    }
    
    public int getHexNo() {
        return hexNo;
    }
    
    public void setHexNo(int hexNo) {
        this.hexNo = hexNo;
    }
    
    
    public String getMembers() {
        return members;
    }

    
    public void setMembers(String members) {
        this.members = members;
    }
    
    public Company getCompany() {
        Company c = new Company();
        c.setHexNo(hexNo);
        c.setCommander(getCommanderName());
        String[] members = getMembers().split("-");
        for (String m : members) {
            c.addMember(m.trim().replace("&#13;", ""));
        }
        return c;
    }
    
    
    
}
