package org.joverseer.support.infoSources;


public class UserInfoBasedOnOtherInfoSource extends UserInfoSource {

	private static final long serialVersionUID = -7150846171785571107L;
	private InfoSource otherInfoSource;
	
	public UserInfoBasedOnOtherInfoSource(InfoSource oif, int t) {
		this.otherInfoSource = oif;
		this.turnNo = t;
	}
	
	public InfoSource getOtherInfoSource() {
		return this.otherInfoSource;
	}
	
	public void setOtherInfoSource(InfoSource is) {
		this.otherInfoSource = is;
	}
	
	@Override
	public String toString() {
    	return "User (Based on: " + this.otherInfoSource.getDescription() + ")";
    }
}
