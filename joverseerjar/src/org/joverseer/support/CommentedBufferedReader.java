package org.joverseer.support;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

public class CommentedBufferedReader extends BufferedReader {

	public CommentedBufferedReader(Reader in) {
		super(in);
	}

	public CommentedBufferedReader(Reader in, int sz) {
		super(in, sz);
	}

	// skip blank lines and comments.
	@Override
	public String readLine() throws IOException {
		String ln = super.readLine();
		if (ln == null) return null;
		while ( ln.startsWith("#") || (ln.length()==0)) {
			 ln = super.readLine();
			 if (ln == null) return null;
		}
		return ln;
	}

}
