package com.middleearthgames.updater;

/*
 * An interface spec for a 3 part version string,
 * mostly so that the concrete class below can use
 * common method to initialise/copy
 */
public interface IThreepartVersion {
	public int getMajor();

	public void setMajor(int major);

	public int getMinor();

	public void setMinor(int minor);

	public int getBuild();

	public void setBuild(int build);
}