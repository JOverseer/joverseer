package org.joverseer.support.readers.newXml;

import java.util.ArrayList;

import org.joverseer.domain.Company;

public class CompanyWrapper {
	int hexNo;
	String commander;
	ArrayList members = new ArrayList();
	
	public String getCommander() {
		return commander;
	}
	public void setCommander(String commander) {
		this.commander = commander;
	}
	public int getHexNo() {
		return hexNo;
	}
	public void setHexNo(int hexNo) {
		this.hexNo = hexNo;
	}
	public ArrayList getMembers() {
		return members;
	}
	public void setMembers(ArrayList members) {
		this.members = members;
	}
	
	public void addMember(String member) {
    	getMembers().add(member);
    }
	
	public Company getCompany() {
        Company c = new Company();
        c.setHexNo(hexNo);
        c.setCommander(getCommander());
        for (String m : (ArrayList<String>)getMembers()) {
            c.addMember(m.trim());
        }
        return c;
    }
}
