package com.middleearthgames.updater;


public class FourpartVersion extends ThreepartVersion implements IFourpartVersion {

	private String preRelease;

	public FourpartVersion(String version) {
		super(version);
		preRelease = "";
	}

	@Override
	public void unparsed(String theRest) {
		this.preRelease = theRest;
	}

	public String getPreRelease() {
		return this.preRelease;
	}

	public void setPreRelease(String value) {
		this.preRelease = value;
	}
	
	/**
	 * 
	 * @param test
	 * @return
	 * use this when you really want to compare IFourpartVersions
	 * Otherwise you'll get Comparable<IThreepartVersion),
	 * and you can't have more than one Comparable<> or base and derived on a class, sigh.
	 */
	public int compareTo2(IFourpartVersion test) {
		int comparison = this.compareTo(test);
		if (comparison == 0) {
			if (this.preRelease.equals(test.getPreRelease())) {
				return 0;
			}
			// strip any common separators
			int count = leftmostEqualCount(this.preRelease,test.getPreRelease());
			// check for case where a release version is compared against a preRelease version.
			if (count == 0) {
				if (this.preRelease.length() == 0) {
					return 1;
				} else {
					return -1;
				}
			}
			String left = this.preRelease.substring(count);
			String right = test.getPreRelease().substring(count);
			comparison = left.compareTo(right);
		}
		return comparison;
	}
	public boolean isLaterThan(IFourpartVersion test) {
		return (this.compareTo2(test) == 1);
	}

	private int leftmostEqualCount(String a,String b)
	{
		int count = 0;
		int minLength = a.length();
		if (b.length() < minLength) {
			minLength = b.length();
		}
		for (;count < minLength;count++) {
			if (a.charAt(count) != b.charAt(count)) {
				// leave early.
				return count;
			}
		}
		return count;
	}

}
