package org.joverseer.support;

import java.io.File;
import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GameFileComparator implements Comparator<File> {
	private final Pattern a = Pattern.compile("g(\\d{3})n(\\d{2})t(\\d{3})\\.(xml|pdf)");

	@Override
	public int compare(File o1, File o2) {
		Matcher matcher1,matcher2;
		int result;
		String f1,f2;
		f1 = o1.getName();
		f2 = o2.getName();
		matcher1 = this.a.matcher(f1);
		matcher2 = this.a.matcher(f2);
		result = f1.compareTo(f2);
		if (matcher1.matches() && matcher2.matches()) {
			int grp1 = matcher1.groupCount();
			int grp2 = matcher2.groupCount();
			String g1 = matcher1.group(1);
			String g2 = matcher2.group(1);
			result =  g1.compareTo(g2); // compare game
			if (result == 0) {
				if ((grp1 == grp2) &&  (grp1 >2)) {
					String t1 = matcher1.group(3);
					String t2 = matcher2.group(3);
					result =  t1.compareTo(t2); // compare turn
					if (result == 0) {
						result =  matcher1.group(2).compareTo(matcher2.group(2)); // compare nation
						if (result == 0) {
							// we want xml to be processed before any pdf.
							if (f1.endsWith(".pdf")) {
								result = 1;
							} else {
								result = -1;
							}
						}
					}
				}
			}
		}
		return result;
	}

}