package org.joverseer.support.readers.newXml;

import java.util.ArrayList;

import org.joverseer.domain.Company;

public class CompanyWrapper {
	int hexNo;
	String commander;
	ArrayList<String> members = new ArrayList<String>();

	public String getCommander() {
		return this.commander;
	}

	public void setCommander(String commander) {
		this.commander = commander;
	}

	public int getHexNo() {
		return this.hexNo;
	}

	public void setHexNo(int hexNo) {
		this.hexNo = hexNo;
	}

	public ArrayList<String> getMembers() {
		return this.members;
	}

	public void setMembers(ArrayList<String> members) {
		this.members = members;
	}

	public void addMember(String member) {
		getMembers().add(member);
	}

	public Company getCompany() {
		Company c = new Company();
		c.setHexNo(this.hexNo);
		c.setCommander(getCommander());
		for (String m : getMembers()) {
			c.addMember(m.trim());
		}
		return c;
	}
}
