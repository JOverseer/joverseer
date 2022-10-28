package com.middleearthgames.updater;

import java.io.InputStream;
import java.net.URL;
/**
 *
 * @author Thomas Otero H3R3T1C modified by DAS for RSS compatibility.
 *
 * This checks a RSS URL for the latest version.
 * For simplicity it doesn't really parse the XML,
 * it just looks for the first &lt;item&gt; and assumes the title is the version number.
 */
public class UpdateChecker {
//    private final static String RSSfeed = "https://www.middleearthgames.com/software/joverseer/feed.xml";
    private static String data;
    public static ThreepartVersion getLatestVersion(String RSSfeed) throws Exception
    {
    	String interested;
    	data = getData(RSSfeed);
    	if (isRedirected()) {
    		throw new Exception("Got permanent redirect. Please correct RSS feed URL in preferences.");
    	}
    	interested = firstTag(data, "item");
        return new ThreepartVersion(firstTag(interested, "title"));

    }
    public static boolean isRedirected() {
    	return data.contains("301 Moved Permanently");
    }
    public static String getWhatsNew(String RSSfeed) throws Exception
    {
    	String interested;
    	if (data == null) getData(RSSfeed);
    	interested = firstTag(data, "item");
        return firstTag(interested, "description");
    }
    private static String getData(String address)throws Exception
    {
        URL url = new URL(address);

        InputStream html = null;

        html = url.openStream();

        int c = 0;
        StringBuffer buffer = new StringBuffer("");

        while(c != -1) {
            c = html.read();

        buffer.append((char)c);
        }
        return buffer.toString();
    }
    public static String firstTag(String whole,String tag)
    {
    	int startTagPos = whole.indexOf("<"+tag+">");
    	int endTagPos = whole.indexOf("</" + tag +">");
    	if ((startTagPos < 0)||(endTagPos <0)) {
    		return "";
    	}
    	return whole.substring(startTagPos+tag.length()+2,endTagPos);
    }
}
