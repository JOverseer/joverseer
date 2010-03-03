package org.joverseer.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Hashtable;
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
			ssql = sql.replaceAll("\\{[a-z0-9_]+\\}", "?");
			s = conn.prepareStatement(ssql);
		}
		catch (Exception exc) {
			throw new RuntimeException(exc);
		}
	}
	
	public void setNull(String p, int sqlType) {
		try {
			s.setNull(getParamIndex(sql, p), sqlType);
		}
		catch (Exception exc) {
			throw new RuntimeException(exc);
		}
	}
	
	public void setInt(int index, int v) {
		try {
			s.setInt(index, v);
		}
		catch (Exception exc) {
			throw new RuntimeException(exc);
		}
	}
	
	public void setInt(String p, int v) {
		try {
			s.setInt(getParamIndex(sql, p), v);
		}
		catch (Exception exc) {
			throw new RuntimeException(exc);
		}
	}
	
	public void setString(int index, String v) {
		try {
			s.setString(index, v);
		}
		catch (Exception exc) {
			throw new RuntimeException(exc);
		}
	}
	
	public void setString(String p, String v) {
		try {
			s.setString(getParamIndex(sql, p), v);
		}
		catch (Exception exc) {
			throw new RuntimeException(exc);
		}
	}
	
	public void setBoolean(int index, Boolean v) {
		try {
			s.setBoolean(index, v);
		}
		catch (Exception exc) {
			throw new RuntimeException(exc);
		}
	}
	
	public void setBoolean(String p, Boolean v) {
		try {
			s.setBoolean(getParamIndex(sql, p), v);
		}
		catch (Exception exc) {
			throw new RuntimeException(exc);
		}
	}
	
	public void execute() {
		try {
			s.execute();
			s.close();
		}
		catch (Exception exc) {
			throw new RuntimeException(exc);
		}
	}
	
	public int getParamIndex(String sql, String parameter) {
		String pat = "\\{[a-z0-9_]+\\}";
		Pattern p = Pattern.compile(pat);
		Matcher m = p.matcher(sql);
		int s = 0;
		int i = 1;
		while (m.find(s)) {
			s = m.start();
			if (sql.substring(s, s + parameter.length()).equals(parameter)) return i;
			s = m.end();
			i++;
		}
		throw new RuntimeException("Failed to find parameter " + parameter + " in sql " + sql);
	}
}
