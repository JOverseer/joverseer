package com.middleearthgames.updater;



public class ThreepartVersion implements IThreepartVersion,
		Comparable<IThreepartVersion> {
	private int major;
	private int minor;
	private int build;

	@Override
	public int getMajor() {
		return this.major;
	}

	@Override
	public void setMajor(int major) {
		this.major = major;
	}

	@Override
	public int getMinor() {
		return this.minor;
	}

	@Override
	public void setMinor(int minor) {
		this.minor = minor;
	}

	@Override
	public int getBuild() {
		return this.build;
	}

	@Override
	public void setBuild(int build) {
		this.build = build;
	}

	public ThreepartVersion(int Major, int Minor, int Build) {
		init(Major, Minor, Build);
	}

	public ThreepartVersion(String version) {
		init(0, 0, 0);
		parse(version);
	}

	public void init(int major, int minor, int build) {
		this.major = major;
		this.minor = minor;
		this.build = build;
	}

	public IThreepartVersion CopyFrom(IThreepartVersion source) {
		init(source.getMajor(), source.getMinor(), source.getBuild());
		return this;
	}

	public void parse(String version) {
		String[] parts = version.split("\\.");
		if (parts.length > 0) {
			this.major = Integer.parseInt(parts[0]);
		}
		if (parts.length > 1) {
			this.minor = Integer.parseInt(parts[1]);
		}
		if (parts.length > 2) {
			this.build = Integer.parseInt(parts[2]);
		}
	}
	@Override
	public int compareTo(IThreepartVersion paramT) {
		if (this.major < paramT.getMajor())
			return -1;
		if (this.major > paramT.getMajor())
			return 1;
		if (this.minor < paramT.getMinor())
			return -1;
		if (this.minor > paramT.getMinor())
			return 1;
		if (this.build < paramT.getBuild())
			return -1;
		if (this.build > paramT.getBuild())
			return 1;
		return 0;
	}
/*
 * not used but sensible when defining compareTo
 */
	public boolean equals(IThreepartVersion test) {
		return (this.major == test.getMajor())
				&& (this.minor == test.getMinor())
				&& (this.build == test.getBuild());
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.major);
		sb.append(".");
		sb.append(this.minor);
		sb.append(".");
		sb.append(this.build);
		return sb.toString();
	}

/*
 *  a convenience method to hide the 1/-1/0 of compareTo
 */
	public boolean isLaterThan(IThreepartVersion test) {
		return (this.compareTo(test) == 1);
	}
}
