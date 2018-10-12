package org.joverseer.support;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

public class CommentedBufferedReader extends BufferedReader {

	public CommentedBufferedReader(Reader in) {
		super(in);
		// TODO Auto-generated constructor stub
	}

	public CommentedBufferedReader(Reader in, int sz) {
		super(in, sz);
		// TODO Auto-generated constructor stub
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
