package org.joverseer.tools;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.ResourceBundle;

import org.joverseer.preferences.PreferenceRegistry;
import org.joverseer.support.GameHolder;
import org.joverseer.support.RecentGames;
import org.joverseer.support.RecentGames.RecentGameInfo;
import org.joverseer.ui.views.Messages;

import com.jidesoft.swing.JideSwingUtilities;
import com.jidesoft.tipoftheday.ResourceBundleTipOfTheDaySource;
import com.jidesoft.tipoftheday.TipOfTheDayDialog;

public class HomeViewInfoCollector {
	GameHolder gameHolder;

	public HomeViewInfoCollector() {
		//this.gameHolder = gameHolder;
	}
	
	public String[] renderReport() {
		String[] s = {this.renderMainViewReport(), this.renderSideViewReport()};
		return s;
	}
	
	private String renderMainViewReport() {
		String s = "";
		s += "<div style='font-family:MS Sans Serif; font-size:11pt'><b style='font-family:Times New Roman; font-size:30pt'><strong>Welcome to JOverseer</strong></b><br/>";
		String temp = this.renderRecentGames();
		if (temp.equals("")) s += "To start a new game, click <b>Game</b> in the top left-hand corner, then select <b>New Game</b> and fill in the details.<br/>To import your results, click <b>Game</b> then <b>Import Results</b>.<br/>For more help, please take a look at our comprehensive guides:<br/>";
		else s += "Games:<br/>" + temp;
		return s;
	}
	
	private String renderSideViewReport() {
		String s = "";
		s += "<div style='font-family:MS Sans Serif; font-size:11pt'>";
		s += this.getTipOfTheDay();
		return s;
	}
	
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
		URL styleSheet = TipOfTheDayDialog.class.getResource("/tips.css");
		return stripHTMLTags(tipOfTheDaySource.getNextTip());
		
	}
	
	private String stripHTMLTags(String s) {
	    String bagBegin = s.substring(0,12);
	    String bagEnd = s.substring(12);
	    return bagBegin + "<div style='font-family:MS Sans Serif; font-size:11pt; text-align:center'>" + bagEnd;
	}
	
	private String renderRecentGames() {
		RecentGames rg = new RecentGames();
		ArrayList<RecentGames.RecentGameInfo> rgis = rg.getRecentGameInfo();
		String s = "";
		
		for (RecentGameInfo rgi : rgis) {
			final RecentGameInfo frgi = rgi;
			s += "<br/>"; 
			s += this.renderRecentGameLine(frgi);
		}
		
		return s;
	}
	
	private String renderRecentGameLine(RecentGameInfo frgi) {
		String s = "";
		s += "Game <a href='http://event?recentgame=" + frgi.getFile() + "'>" + frgi.getNumber() + "</a>, orders due by: " + frgi.getDate();
		return s;
	}
}
