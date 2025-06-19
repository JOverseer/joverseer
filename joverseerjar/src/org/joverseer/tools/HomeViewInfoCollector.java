package org.joverseer.tools;

import java.awt.Color;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.ResourceBundle;
import java.util.Scanner;

import javax.swing.UIManager;

import org.joverseer.JOApplication;
import org.joverseer.support.GameHolder;
import org.joverseer.support.RecentGames;
import org.joverseer.support.RecentGames.RecentGameInfo;
import org.joverseer.ui.support.PLaFHelper;
import org.joverseer.ui.views.Messages;
import com.jidesoft.tipoftheday.ResourceBundleTipOfTheDaySource;

/**
 * Collects all info for the Home page
 *
 * @author Samuel Terrett
 */
public class HomeViewInfoCollector {
	GameHolder gameHolder;
	final String newsletterURL = "https://gamesystems.com/latest-newsletter";

	public HomeViewInfoCollector() {
	}
	
	/**
	 * returns the html text to put in the main view and the side bar view
	 * @return array containing the texts for the 2 panes
	 */
	public String[] renderReport() {
		String[] s = {this.renderMainViewReport(), this.renderSideViewReport()};
		return s;
	}
	
	/**
	 * Collects html for the main view
	 * @return
	 */
	private String renderMainViewReport() {
		String s = "";
		s += renderNewsletter();
		return s;
	}
	
	/**
	 * Collects html for the pane on the side 
	 * @return
	 */
	private String renderSideViewReport() {
		String s = "";
		s += "<div style='font-family:MS Sans Serif; font-size:11pt'>";
		String temp = this.renderRecentGames();
		if (temp.equals("")) s += "<br/>" + Messages.getString("homeView.defaultWelcomeText");
		else s += "<h3>Your Games:</h3>" + temp;
		s += "<br/>";
		s += "<h3>Tip of the Day:</h3>";
		s += this.getTipOfTheDay();
		return s;
	}
	
	/**
	 * Self-explanatory, gets the tip of the day
	 * @return
	 */
	private String getTipOfTheDay() {
		ResourceBundle rb;
		ResourceBundleTipOfTheDaySource tipOfTheDaySource = new ResourceBundleTipOfTheDaySource(rb = ResourceBundle.getBundle("tips"));
		int count = 0;
		Enumeration e = rb.getKeys();
		while (e.hasMoreElements()) {
			count++;
			e.nextElement();
		}
		tipOfTheDaySource.setCurrentTipIndex((int) (Math.random() * count));
		return formatTip(tipOfTheDaySource.getNextTip());
		
	}
	
	/**
	 * Takes the inputed string and formats it correctly, taking out html tags etc.
	 * Exists as the tip of the day is wrapped in its own html which I wanted to remove.
	 * @param s
	 * @return
	 */
	private String formatTip(String s) {
	    String bagBegin = s.substring(0,12);
	    String bagEnd = s.substring(12);
	    String res = bagBegin + "<div style='font-family:MS Sans Serif; font-size:11pt; text-align:center'>" + bagEnd;
	    String stripped = res.replaceAll("<html>", "");
	    stripped = stripped.replaceAll("<body>", "");
	    stripped = stripped.replaceAll("</body>", "");
	    stripped = stripped.replaceAll("</html>", "");
	    return stripped;
	}
	
	/**
	 * Collects all recent games and lists them with a hyperlink to the game file to open it with
	 * @return
	 */
	private String renderRecentGames() {
		RecentGames rg = new RecentGames();
		ArrayList<RecentGames.RecentGameInfo> rgis = rg.getRecentGameInfo();
		String s = "";
		
		for (RecentGameInfo rgi : rgis) {
			final RecentGameInfo frgi = rgi;
			 
			s += this.renderRecentGameLine(frgi);
			s += "<br/>";
		}
		
		return s;
	}
	
	/**
	 * Collects and formats the information about a recent game
	 * @param frgi
	 * @return
	 */
	private String renderRecentGameLine(RecentGameInfo frgi) {
		String s = "";
		String imagePath = "";
		String middleString = "";
		String urlOrderSent = "'http://'";
		try {
			if(!frgi.hasSentOrd()) imagePath = JOApplication.getImageSource().getImageResource("orderresult.error.icon").getURL().toString();
			else imagePath = JOApplication.getImageSource().getImageResource("orderresult.okay.icon").getURL().toString();
			if (frgi.formatOrdersSentDateInf().equals("Import new results.")) {
				middleString = ". Import new results.";
				
			}
			else {
				urlOrderSent = "'http://event?orderSentOn=" + frgi.formatOrdersSentDateInf() + "'";
				middleString = ", orders due: " + frgi.getDate();
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		s += "<a href='http://event?recentgame=" + frgi.getFile().replace("'", "~~") + "'>" + frgi.getNumber() + "</a>" + middleString + " <a href=" + urlOrderSent + "> <img border=\"0\" src='" + imagePath + "'></a>";
		return s;
	}
	
	private String renderNewsletter() {
		String page = getPage(this.newsletterURL);
		
		if(PLaFHelper.isDarkMode()) return darkMode(page);
		
		return page;
	}
	
	/**
	 * Gets html from URL
	 * @param Url
	 * @return
	 */
	public String getPage(String Url) {
		String content = null;
		URLConnection connection = null;
		try {
		  connection =  new URI(getFinalURL(Url)).toURL().openConnection();
		  Scanner scanner = new Scanner(connection.getInputStream(), "UTF-8");
		  scanner.useDelimiter("\\Z");
		  
		  content = scanner.next();
		  
		  scanner.close();
		}catch ( Exception ex ) {
			content = Messages.getString("homeView.noInternetWelcomeText");
		    ex.printStackTrace();
		}
		return content;
	}
	
	/**
	 * Gets the redirected URL from the inputed URL
	 * @param url
	 * @return
	 */
	public static String getFinalURL(String url) {
		try {
		    HttpURLConnection con = (HttpURLConnection) new URI(url).toURL().openConnection();
		    con.setInstanceFollowRedirects(false);
		    con.connect();
		    con.getInputStream();
	
		    if (con.getResponseCode() == HttpURLConnection.HTTP_MOVED_PERM || con.getResponseCode() == HttpURLConnection.HTTP_MOVED_TEMP) {
		        String redirectUrl = con.getHeaderField("Location");
		        return getFinalURL(redirectUrl);
		    }
		}catch(Exception e) {}
	    return url;	
	}
	
	/**
	 * Does some janky search and replace to make the newsletter 'Dark Mode'
	 */
	public String darkMode(String page) {
		//String backgroundC = 
		String backgroundColor = this.colorToString("Panel.background", false);
		String textColor = this.colorToString("Label.foreground", false);
		
		String s = page.replaceAll("<div class=\"text-block\" style=\"", "<div class=\"text-block\" style=\"color: " + textColor + "; ");
		s = s.replaceAll("<div class=\"text-block fr-inner\" style=\"", "<div class=\"text-block fr-inner\" style=\"color: " + textColor + "; ");
		s = s.replaceAll("bgcolor=\"#ffffff\"", "bgcolor=\"" + backgroundColor + "\"");
		s = s.replaceAll("background-color: #ffffff;", "background-color: "+ backgroundColor +";");
		return s;
	}
	
	public String colorToString(String code, boolean darken) {
		Color c = UIManager.getColor(code);
		if(darken) c = c.darker();

		int R = c.getRed();
		int G = c.getGreen();
		int B = c.getBlue();

		return "#" + Integer.toHexString(R) + Integer.toHexString(G) + Integer.toHexString(B);

	}
}
