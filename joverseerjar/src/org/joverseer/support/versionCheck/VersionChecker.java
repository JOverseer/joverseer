// this has been superceeded by com.middleearthgames.Updater etc
package org.joverseer.support.versionCheck;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.application.support.DefaultApplicationDescriptor;

public class VersionChecker {
	public boolean newVersionExists() throws Exception {
		HttpClient httpClient = new HttpClient();
		String url = "http://code.google.com/p/joverseer/downloads/list";
		GetMethod get = new GetMethod(url);
		
		httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(5000);
        int status = httpClient.executeMethod(get);

        DefaultApplicationDescriptor descriptor = (DefaultApplicationDescriptor)Application.instance().getApplicationContext().getBean("applicationDescriptor");
        
        int currentVersion = parseVersionString(descriptor.getVersion());
        
        if (status == HttpStatus.SC_OK) {
        	String response = get.getResponseBodyAsString();
        	
        	Pattern regex = Pattern.compile("/joverseer-v(\\d\\.\\d+\\.\\d+).*\\.zip");
        	Matcher matcher = regex.matcher(response);
        	while (matcher.find()) {
        		//String match = response.substring(matcher.start(), matcher.end());
            	
        		String group = matcher.group(1);
        		int fv = parseVersionString(group);
        		if (fv > currentVersion) {
        			return true;
        		}
        	}
        	
        }
        return false;
	}
	
	public int parseVersionString(String versionString) {
		String[] parts = versionString.split("\\.");
		if (parts.length != 3) return -1;
		int v = 0;
		v = Integer.parseInt(parts[0]) * 10000 + Integer.parseInt(parts[1]) * 100 + Integer.parseInt(parts[2]);
		return v;
	}
}
