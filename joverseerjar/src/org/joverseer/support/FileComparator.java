package org.joverseer.support;

import java.io.File;
import java.util.Comparator;

 public class FileComparator implements Comparator<File> {

	@Override
	public int compare(File arg0, File arg1) {
		String fn1 = arg0.getName();
		String fn2 = arg1.getName();
		int i = fn1.indexOf(".");
		int j = fn2.indexOf(".");
		if (i == -1 || j == -1)
			return 0;
		fn1 = fn1.substring(i - 4, 4);
		fn2 = fn2.substring(i - 4, 4);
		return fn1.compareTo(fn2);
	}

}