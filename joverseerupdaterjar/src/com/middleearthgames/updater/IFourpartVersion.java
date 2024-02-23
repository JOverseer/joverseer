package com.middleearthgames.updater;

// Semantic versioning https://semver.org/
public interface IFourpartVersion extends IThreepartVersion {
	public String getPreRelease();
	public void setPreRelease(String value);

}
