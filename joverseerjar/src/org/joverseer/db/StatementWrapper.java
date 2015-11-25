package org.joverseer.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StatementWrapper {
	String sql;
	PreparedStatement s;
	String ssql;
	
	public StatementWrapper(String sql, Connection conn) throws RuntimeException {
		super();
		try {
			this.sql = sql;
			this.ssql = sql.replaceAll("\\{[a-z0-9_]+\\}", "?");
			this.s = conn.prepareStatement(this.ssql);
		}
		catch (Exception exc) {
			throw new RuntimeException(exc);
		}
	}
	
	public void setNull(String p, int sqlType) {
		try {
			this.s.setNull(getParamIndex(this.sql, p), sqlType);
		}
		catch (Exception exc) {
			throw new RuntimeException(exc);
		}
	}
	
	public void setInt(int index, int v) {
		try {
			this.s.setInt(index, v);
		}
		catch (Exception exc) {
			throw new RuntimeException(exc);
		}
	}
	
	public void setInt(String p, int v) {
		try {
			this.s.setInt(getParamIndex(this.sql, p), v);
		}
		catch (Exception exc) {
			throw new RuntimeException(exc);
		}
	}
	
	public void setString(int index, String v) {
		try {
			this.s.setString(index, v);
		}
		catch (Exception exc) {
			throw new RuntimeException(exc);
		}
	}
	
	public void setString(String p, String v) {
		try {
			this.s.setString(getParamIndex(this.sql, p), v);
		}
		catch (Exception exc) {
			throw new RuntimeException(exc);
		}
	}
	
	public void setBoolean(int index, Boolean v) {
		try {
			this.s.setBoolean(index, v);
		}
		catch (Exception exc) {
			throw new RuntimeException(exc);
		}
	}
	
	public void setBoolean(String p, Boolean v) {
		try {
			this.s.setBoolean(getParamIndex(this.sql, p), v);
		}
		catch (Exception exc) {
			throw new RuntimeException(exc);
		}
	}
	
	public void execute() {
		try {
			this.s.execute();
			this.s.close();
		}
		catch (Exception exc) {
			throw new RuntimeException(exc);
		}
	}
	
	public int getParamIndex(String sql1, String parameter) {
		String pat = "\\{[a-z0-9_]+\\}";
		Pattern p = Pattern.compile(pat);
		Matcher m = p.matcher(sql1);
		int s1 = 0;
		int i = 1;
		while (m.find(s1)) {
			s1 = m.start();
			if (sql1.substring(s1, s1 + parameter.length()).equals(parameter)) return i;
			s1 = m.end();
			i++;
		}
		throw new RuntimeException("Failed to find parameter " + parameter + " in sql " + sql1);
	}
}
